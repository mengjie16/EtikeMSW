package vos;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import models.Brand;
import models.Item;
import models.ItemCate;
import models.ItemLocation;
import models.ItemSku;
import models.KeyValue;
import play.data.binding.As;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import utils.SearchEndDateBinder;

import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import enums.DeliverType;
import enums.ItemStatus;

/**
 * 交易商品信息 vo
 * 
 * @author sun
 *
 */
public class TradeItemVo implements java.io.Serializable {
    // id
    public long id;
    // 主图
    public String picUrl;
   
    public TradeItemVo(){}
    public TradeItemVo(long id,String picUrl){
        this.id = id;
        this.picUrl = picUrl;
    }
}