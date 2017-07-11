package models;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 优惠信息
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月28日 下午3:24:15
 */
public class PromotionInfo implements Serializable {

    public long id;
    /** 商品id */
    public long itemId;
    /** 打折的钱数，单位分 */
    public int discount;
    /** 优惠开始时间 */
    public Date startTime;
    /** 优惠结束时间 */
    public Date endTime;
    /** 创建时间 */
    public Date createTime;
}
