package vos;

import java.util.Date;

import com.aton.vo.Page;

/**
 * 零售商检索视图数据结构
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年4月29日 上午1:48:39
 */
public class RetailerSearchVo extends Page {

    /** 名称 */
	public String name;
    /** 淘宝店铺创建时间时间 */
	public Date createTimeStart;
	public Date createTimeEnd;
	
    public static RetailerSearchVo newInstance() {
        RetailerSearchVo vo = new RetailerSearchVo();
        return vo;
    }
}