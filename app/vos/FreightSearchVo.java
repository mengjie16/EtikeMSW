package vos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import enums.DeliverType;
import enums.ItemStatus;
import models.Brand;
import models.ItemCate;
import play.data.binding.As;
import play.libs.Codec;

/**
 *  运费模版信息检索视图数据结构
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年4月29日 上午1:48:39
 */
public class FreightSearchVo extends Page {
    
    /** 这里是供应商的name，公司名，手机号都可以搜索 */
    public String aboutSupplier;
    /** 品牌主次名字，模糊搜索，凡是这个品牌的商品所用到的模板 */
    public String aboutBrand;

    public static FreightSearchVo newInstance() {
        FreightSearchVo vo = new FreightSearchVo();
        return vo;
    }
    
    public String getAboutSupplier(){
        if(Strings.isNullOrEmpty(aboutSupplier)){
            return "";
        }
        return StringUtils.trim(this.aboutSupplier);
    }
    
    public String getAboutBrand(){
        if(Strings.isNullOrEmpty(aboutBrand)){
            return "";
        }
        return StringUtils.trim(this.aboutBrand);
    }
}