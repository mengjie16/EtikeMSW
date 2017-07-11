package models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;
import odms.context.FreightTempContext;
import odms.context.HomePageSettingContext;

import org.apache.commons.collections.Predicate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.constants.CacheType;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;
import vos.ItemVo;

/**
 * 运费模版
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月8日 下午5:49:30
 */
public class SupplierFreightTemp implements Serializable {

    public long _id;
    public List<FreightTemp> temps = Lists.newArrayList();

    /**
     * 创建时间
     */
    public Date createTime;

    public Map toMap() {
        Map map = Maps.newHashMap();
        List<Map> temp_maps = Lists.newArrayList();
        // 活动商品
        if (MixHelper.isNotEmpty(temps)) {
            temp_maps = Lists.transform(temps, new Function<FreightTemp, Map>() {

                @Override
                public Map apply(FreightTemp arg0) {
                    return arg0.toMap();
                }

            });

        }
        map.put("_id", _id);
        map.put("temps", temp_maps);
        map.put("createTime", DateTime.now().toDate());
        return map;
    }
}
