package models;

import java.util.Date;

/**
 * 
 * 公告报道
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月24日 下午7:21:31
 */
public class Bulletin implements java.io.Serializable {

    public int id;
    /** 类型 */
    public String type;
    /** HTML格式的内容 */
    public String content;
    /** 是否对外可见 */
    public boolean isPublic;

    /** 发布时间 */
    public Date createTime;

}
