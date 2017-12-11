package vos;

import java.util.Date;

import com.aton.vo.Page;

import enums.TradeStatus;
import play.data.binding.As;
import utils.SearchEndDateBinder;

/**
 * 品牌信息检索
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年5月30日 下午6:23:33
 */
public class TradeSearchVo extends Page {
    // 交易id
    public long id;
    /** 交易创建时间 */
    @As("yyyy-MM-dd")
    public Date createTimeStart;
    @As(binder = SearchEndDateBinder.class)
    public Date createTimeEnd;
    // 交易状态
    public TradeStatus status;
    // 交易标题
    public String caption;
    // 零售商id
    public int retailerId;
    // 手机号
    // public String phone;
    // 姓名
    public String name;
}