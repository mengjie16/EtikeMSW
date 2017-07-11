package models;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.thrift.Option;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;

/**
 * 活动列表配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class ItemSetting {

    public long id;
    // 活动商品(单位：分)
    public int price;
    // --- 只是展示使用
    public int currentPrice;

    public String img;
    public String title;

    public Map toMap() {
        return ImmutableMap.of("id", id, "price", price * 100);
    }

    /**
     * 正式应用价格无需再次转分
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月19日 上午2:27:10
     */
    public Map toBuildMap() {
        return ImmutableMap.of("id", id, "price", price);
    }

    /**
     * 转换为详情
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月18日 下午11:36:44
     */
    public ItemSetting detail() {
        Item item = Item.findBaseInfoById(this.id);
        if (item != null) {
            this.img = item.picUrl;
            this.title = item.title;
            this.currentPrice = item.itemLastFee();
        }
        return this;
    }
}
