package vos;

import java.util.Date;

import play.data.binding.As;
import utils.SearchEndDateBinder;

import com.aton.util.StringUtils;
import com.aton.vo.Page;

import enums.DeliverType;
import enums.ItemStatus;

/**
 * 文章搜索vo
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月18日 上午2:49:19
 */
public class ArticleSearchVo extends Page {

    public String title;
	/** 供应商名称 */
	public String content;
    @As("yyyy-MM-dd")
    public Date createTimeStart;
    @As(binder = SearchEndDateBinder.class)
    public Date createTimeEnd;
    public boolean isPublic;
    public boolean isList;
    public boolean isDelete;
}