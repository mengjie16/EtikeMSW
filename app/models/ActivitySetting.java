package models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.thrift.Option;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;

/**
 * 活动列表配置
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class ActivitySetting {

  public String imgUrl;
  public String url;
  public boolean isPublic;
  /** 商品配置 */
  public List<ItemSetting> itemSettings;
  
  public Map toMap(){
      List<Map> it_maps = Lists.newArrayList();
      if(MixHelper.isNotEmpty(itemSettings)){
          it_maps = this.itemSettings.stream().map(i->i.toMap()).collect(Collectors.toList());
      };
      return ImmutableMap.of("imgUrl",imgUrl,
                             "url",url,
                             "isPublic",isPublic,
                             "itemSettings",it_maps);
  }  
  
  /**
   * 正式应用价格无需再次转分
   *
   * @return
   * @since  v1.0
   * @author Calm
   * @created 2016年9月19日 上午2:28:20
   */
  public Map toBuildMap(){
      List<Map> it_maps = Lists.newArrayList();
      if(MixHelper.isNotEmpty(itemSettings)){
          it_maps = this.itemSettings.stream().map(i->i.toBuildMap()).collect(Collectors.toList());
      };
      return ImmutableMap.of("imgUrl",imgUrl,
                             "url",url,
                             "isPublic",isPublic,
                             "itemSettings",it_maps);
  }
  
  /**
   *  转为详情
   *
   * @return
   * @since  v1.0
   * @author Calm
   * @created 2016年9月18日 下午11:44:59
   */
  public ActivitySetting detail(){
      if(MixHelper.isNotEmpty(this.itemSettings)){
          this.itemSettings = this.itemSettings.stream().map(i->i.detail()).collect(Collectors.toList());
      }
      return this;
  }
}
