package models;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * 
 * 描述一个物理地址
 * 
 * @author Tr0j4n
 * @since v1.0
 * @created 2016年4月13日 下午11:45:48
 */
public class ReferUrl {

    private static final Logger log = LoggerFactory.getLogger(ReferUrl.class);

    /**
     * 参考关键字
     */
    public String key;

    /**
     * 参考中文描述
     */
    public String name;

    /**
     * 参考规则校验
     */
    public String checkRule;

    public ReferUrl() {

    }

    public ReferUrl(String key, String name) {
        this.key = key;
        this.name = name;
    }
    
    /**
     * 获取默认的参数链接列表
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月23日 上午11:12:37
     */
    public static List<ReferUrl> getDefaultList() {
        List<ReferUrl> refers = ImmutableList.of(
            new ReferUrl("taobao|tmall", "淘宝"), 
            new ReferUrl("jd", "京东"),
            new ReferUrl("vip", "唯品会"),
            new ReferUrl("1688", "阿里巴巴")
            );
        return refers;
    }

}
