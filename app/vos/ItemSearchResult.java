package vos;

import java.util.Date;
import java.util.List;

import play.data.binding.As;
import utils.SearchEndDateBinder;

import com.aton.util.StringUtils;
import com.aton.vo.Page;

import enums.DeliverType;
import enums.ItemStatus;
import models.Brand;
import models.ItemCate;

/**
 * 商品搜索结果视图
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月15日 下午1:06:45
 */
public class ItemSearchResult extends Page {
    /**
     * 类目集
     */
    public List<CateResult> cates;
    
    public List<BrandResult> brands;
    
    /**
     * 是否品牌搜索
     */
    public boolean is_brand;
    
    /**
     * 没有搜索结果时候的描述
     */
    public String result_empty_desc;
    
    public static ItemSearchResult newInstance(int pageNumber,int pageSize,int totalCount) {
        ItemSearchResult vo = new ItemSearchResult();
        vo.pageSize = pageSize;
        vo.pageNo = pageNumber;
        vo.totalCount = totalCount;
        return vo;
    }
}