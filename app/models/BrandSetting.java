package models;

import java.util.Date;
import java.util.Map;

import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 品牌列表配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class BrandSetting {

    public long bid;
    public String title;
    public String imgUrl;

    public Map toMap() {
        return ImmutableMap.of("bid", bid);
    }
    
    /**
     *  转换为详情
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月18日 下午11:38:13
     */
    public BrandSetting detail() {
        // TODO Auto-generated method stub
        Brand brand = Brand.findBrandWithCacheMap(this.bid);
        if (brand != null) {
            this.imgUrl = brand.picUrl;
            this.title = brand.name;
        }
        return this;
    }
}
