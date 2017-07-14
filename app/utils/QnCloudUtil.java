package utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Time;
import play.libs.WS;

import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.resumableio.ResumeableIoApi;
import com.qiniu.api.rs.PutPolicy;

import enums.constants.CacheType;

/**
 * 七牛云储存工具类
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月28日 下午12:12:43
 */
public class QnCloudUtil {

    public static final Logger log = LoggerFactory.getLogger(QnCloudUtil.class);

    static {
        Config.ACCESS_KEY = com.aton.config.Config.getProperty("qiniu.access_key");
        Config.SECRET_KEY = com.aton.config.Config.getProperty("qiniu.secret_key");
    }

    /**
     * 七牛图片空间.
     * 
     * @author Calm
     * @since  v1.0
     * @created 2016年5月28日 下午12:13:09
     */
    public enum QnFileBucket {
        /** 正式空间：普通图片 */
        ONLINE_PUBLIC("eitak","//cdn"), 
        DEV("eitak","//cdn"), 
        TEST("eitak","//cdn");
        // bucket的名字
        public String bucket;
        // 自定义域名前缀
        public String domain;

        private QnFileBucket(String name, String domain) {
            this.bucket = name;
            this.domain = domain;
        }
    }

    /**
     * 生成默认的上传token.
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年5月28日 下午12:14:45
     */
    public static String generateUploadToken() {
        if (AppMode.get().mode == Mode.TEST) {
            return generateUploadToken(QnFileBucket.TEST);
        }
        if (AppMode.get().mode == Mode.DEV) {
            return generateUploadToken(QnFileBucket.DEV);
        }
        return generateUploadToken(QnFileBucket.ONLINE_PUBLIC);
    }
    
    /**
     * 生成qini文件上传token
     *
     * @param bucket
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年5月28日 下午12:11:08
     */
    public static String generateUploadToken(QnFileBucket bucket) {
        /*
         * uptoken的过期时间为：其在本系统中的缓存过期时间+3分钟（避免时间误差）
         */
        int seconds = Time.parseDuration(CacheType.FILE_UPTOKEN.expiredTime);
        long millis = DateTime.now().plusSeconds(seconds).plusMinutes(3).getMillis();

        PutPolicy putPolicy = new PutPolicy(bucket.bucket);
        putPolicy.expires = TimeUnit.MILLISECONDS.toSeconds(millis);

        putPolicy.insertOnly = 1;
        // putPolicy.returnBody
        // TODO 自定义上传文件名
        // putPolicy.saveKey

        // 最大文件限制：2MB
        putPolicy.fsizeLimit = 2 * 1024 * 1024;
        // 只允许上传图片类型
        putPolicy.mimeLimit = "image/*";
        try {
            return putPolicy.token(new Mac(Config.ACCESS_KEY, Config.SECRET_KEY));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 通过图片原url上传至七牛并返回七牛上该图片地址
     *
     * @param url
     * @return
     * @throws IOException
     * @since  v1.0
     * @author Calm
     * @created 2016年5月28日 下午12:15:05
     */
    public static String uploadImg(String url) throws IOException {

        // 获取图片源数据流
        InputStream inputStream = WS.url(url).get().getStream();

        // 图片地址url头部
        String DOMAIN = "http://cdn{0}.eitak.cn/";

        // 获取upToken
        String uptoken = QnCloudUtil.generateUploadToken();
        if (AppMode.get().mode == Mode.TEST) {
            DOMAIN = MessageFormat.format(DOMAIN, "-test");
        } else if (AppMode.get().mode == Mode.DEV) {
            DOMAIN = MessageFormat.format(DOMAIN, "-dev");
        } else {
            DOMAIN = MessageFormat.format(DOMAIN, "");
        }

        // 上传
        PutRet ret = ResumeableIoApi.put(inputStream, uptoken, null, null);
        inputStream.close();

        return DOMAIN + ret.getKey();
    }
}
