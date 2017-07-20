package models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.DeliverType;
import enums.ItemStatus;
import enums.constants.CacheType;
import models.mappers.FavoriteMapper;
import models.mappers.ItemMapper;
import models.mappers.RetailerMapper;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import utils.ExchangeRateUtil;
import vos.BrandResult;
import vos.CateResult;
import vos.FreightSearchVo;
import vos.ItemPropertieVo;
import vos.ItemSearchResult;
import vos.ItemSearchVo;
import vos.ItemVo;
import vos.PriceRangeVo;

public class Favorite implements Serializable {

    public static final String TABLE_NAME = "favorite";
    /** 自增主键 */
    public long id;

    public int retailerId;
    
    public long itemId;
    
    @Min(0)
    public int retailPrice;

    public String title;    
    
    public String picUrl;
     
    public static boolean save(Favorite favorite) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FavoriteMapper mapper = ss.getMapper(FavoriteMapper.class);
            mapper.insert(favorite);
            return true;
        }  finally {
            ss.close();
        }
        
    }
    
    public static boolean delete(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FavoriteMapper mapper = ss.getMapper(FavoriteMapper.class);
            mapper.delete(id);
            return true;
        }  finally {
            ss.close();
        }
    }
    
    
    public static List<Favorite> findList(long retailerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FavoriteMapper mapper = ss.getMapper(FavoriteMapper.class);
            List<Favorite> res = mapper.selectList(retailerId);
            return res;
        }  finally {
            ss.close();
        }
       
    }
    
    public static int findById(long itemId, long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FavoriteMapper mapper = ss.getMapper(FavoriteMapper.class);
            return mapper.selectById(itemId, id);
        }  finally {
            ss.close();
        }
    }
    

   
}
