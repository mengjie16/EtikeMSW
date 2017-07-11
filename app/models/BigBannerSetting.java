package models;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.thrift.Option;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;

/**
 * 轮播大Banner配置
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class BigBannerSetting  implements java.io.Serializable{
  

  public String title;
  public String url;
  public String imgUrl;
  
  public BigBannerSetting(){}
  public Map toMap(){
      return ReflectionUtils.beanToMap(this);
  }
}
