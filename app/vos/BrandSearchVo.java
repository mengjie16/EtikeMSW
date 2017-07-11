package vos;

import com.aton.vo.Page;

/**
 * 品牌信息检索
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月30日 下午6:23:33
 */
public class BrandSearchVo extends Page {

    public String name;
	/** 供应商名称 */
	public String secondaryName;
	/** 类目id */
	public String company;
	/** 备注 */
	public String note;
}