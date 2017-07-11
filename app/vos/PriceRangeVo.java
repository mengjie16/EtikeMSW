package vos;

import java.math.BigDecimal;
import java.util.List;

import play.mvc.Util;

/**
 * 价格区间视图表现
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年4月29日 上午1:32:53
 */
public class PriceRangeVo {

    /**
     * 最小起订量
     */
    public int range;
    /**
     * 价格
     */
    public int price;

    public double d_price;

    /**
     * 存在个数
     *
     * @param objList
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月1日 下午4:44:38
     */
    @Util
    public int count(List<PriceRangeVo> objList) {
        int count = 0;
        for (PriceRangeVo vo : objList) {
            if (vo.range == this.range) {
                count++;
            }
        }
        return count;
    }

    /**
     * 转换为分
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 上午10:52:07
     */
    public PriceRangeVo parseToPoint() {
        this.price = new BigDecimal(this.d_price).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        return this;
    }
}
