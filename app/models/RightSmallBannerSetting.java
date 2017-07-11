package models;

import java.util.Date;
import java.util.Map;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.thrift.Option;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.collect.Maps;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;

/**
 * 右侧小Banner配置
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class RightSmallBannerSetting {
  
  public String title;
  public String url;
  public String imgUrl;
  
  public Map toMap(){
      return ReflectionUtils.beanToMap(this);
  }
    
}
