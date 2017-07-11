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
public class CateChangeVo {
	/**
	 * 当前类目ID
	 */
	public long id;
	/**
	 * 类目名称
	 */
	public String name;
	/**
	 * 改变类型match(top|bottom|del|move|save)
	 */
	public String changeType;
	/**
	 * 关联类目ID
	 */
	public long correId;
}