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
 * 品牌搜索集
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月15日 下午10:05:39
 */
public class BrandResult {

    /**
     * 品牌数量
     */
    public int count;
    /**
     * 品牌id
     */
    public long id;
    /**
     * 品牌名称
     */
    public String name;

    public BrandResult() {
    };

    public BrandResult(int count, long id, String name) {
        this.count = count;
        this.id = id;
        this.name = name;
    }

}