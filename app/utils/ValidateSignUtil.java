package utils;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.util.MixHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import play.libs.Codec;

/**
 * 各种签名工具
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月25日 下午5:55:56
 */
public class ValidateSignUtil {

    private static Logger log = LoggerFactory.getLogger("sign");

    /**
     * 将参数按照字母顺序排序
     *
     * @param map
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:56:57
     */
    public static String keyValPairsWithMap(Map map) {
        if (MixHelper.isEmpty(map)) {
            return "";
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(map);
        sortMap = Maps.filterValues(sortMap, new Predicate<Object>() {

            @Override
            public boolean apply(Object input) {
                return input != null;
            }
        });
        String keyValPairs = Joiner.on("&").withKeyValueSeparator("=").join(sortMap);
        return keyValPairs;
    }

    /**
     * 获取md5签名
     *
     * @param map
     * @param key
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:04:30
     */
    public static String signFromMapWithkey(Map map, String key) {
        return Codec.hexMD5(keyValPairsWithMap(map).concat(key));
    }

    /**
     * md5签名并小写
     *
     * @param map
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月25日 下午8:35:46
     */
    public static String signMd5HexToLowerCase(Map map) {
        String keyValPairs = keyValPairsWithMap(map);
        String signStr = Codec.hexSHA1(keyValPairs).toLowerCase();
        return signStr;
    }

    /**
     * sha1签名并小写
     *
     * @param map
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月26日 上午9:55:06
     */
    public static String signSha1HexToLowerCase(Map map) {
        String keyValPairs = keyValPairsWithMap(map);
        String signStr = Codec.hexSHA1(keyValPairs).toLowerCase();
        return signStr;
    }

    /**
     * sha1签名
     *
     * @param str
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月26日 上午10:24:37
     */
    public static String signSha1Hex(String str) {
        return Codec.hexSHA1(str);
    }

    /**
     * sha1签名
     *
     * @param map
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月26日 上午10:23:30
     */
    public static String signSha1Hex(Map map) {
        String keyValPairs = keyValPairsWithMap(map);
        return signSha1Hex(keyValPairs);
    }

}
