package vos;

import java.util.Date;

import play.data.binding.As;
import utils.SearchEndDateBinder;

import com.aton.util.StringUtils;
import com.aton.vo.Page;

import enums.DeliverType;
import enums.ItemStatus;

/**
 * 类目信息检索
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月4日 下午7:15:13
 */
public class CateSearchVo extends Page {
	/**
	 * 类目ID
	 */
	public long id;
	/** 类目名称  */
    public String name;
	/** 类目等级 */
	public int level;
	/** 是否父级类目 */
	public boolean isParent;	
	/** 是否查询类目级别区别 */
	public boolean queryIsParent;
	/** 是否查询类目等级 */
	public boolean queryLevel;
	/** 是否查询ID下的子类目  */
	public boolean querySub;
}