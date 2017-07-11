package odms.context;

import java.util.List;
import java.util.Map;

import models.ActivityPageSetting;
import models.ActivitySetting;
import models.BigBannerSetting;
import models.BrandSetting;
import models.HomePageSetting;
import models.ItemLocation;
import models.ItemSetting;
import models.RightSmallBannerSetting;

import org.apache.commons.lang.Validate;

import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import odms.DataStore;
import odms.OdmPopulater;

/**
 * 首页配置数据操纵
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月22日 下午3:05:34
 */
public class HomePageSettingBuildsContext extends MdbContext<HomePageSetting> {
	
    public HomePageSettingBuildsContext() {
        COLLECTION_NAME = "home_page_setting_builds";
    }
  
    
    /**
     * 统计指定feild中的数量
     *
     * @param feild
     * @param value
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月18日 下午11:00:01
     */
    public long findCountByFeild(String feild,String value){
        DBObject dbObj = new BasicDBObject(feild,value);
        long result = count(dbObj);
        return result;
    }
       
    /**
     * 查找主页设置列表
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:36:43
     */
    public List<HomePageSetting> findList() {
        DBObject dbObj = new BasicDBObject();
        DBCursor cursor = getCursor(dbObj).sort(new BasicDBObject("createTime", 1));
        List<DBObject> getObjs = iterateCursor(cursor);
        List<HomePageSetting> homePageSettings = Lists.newArrayList();
        if(!MixHelper.isEmpty(getObjs)){
           homePageSettings = Lists.transform(getObjs, new Function<DBObject, HomePageSetting>() {
                @Override
                public HomePageSetting apply(DBObject input) {
                    return parseFromRawObject(input);
                }
            });
        }
        return homePageSettings;
    }
    
    /**
     * 查找默认主页主题配置
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月25日 下午4:06:26
     */
    public HomePageSetting findDefaultSetting(){
        DBObject queryObj = new BasicDBObject("isUse", true);
        DBCursor cursor = getCursor(queryObj).sort(new BasicDBObject("createTime", -1)).limit(1);
        // 取游标第一个
        DBObject dbObj = pickCursor(cursor);
        if(dbObj!=null){
            return parseFromRawObject(dbObj);
        }
        return null;
    }
    
    /**
     * 查找最后一个创建的配置信息的ID
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月13日 下午3:56:53
     */
    public String findLastTimeSettingID(){
        DBObject queryObj = new BasicDBObject();
        DBCursor cursor = getCursor(queryObj).sort(new BasicDBObject("createTime", -1)).limit(1);
        // 取游标第一个
        DBObject dbObj = pickCursor(cursor);
        if(dbObj!=null){
           String id = (String)dbObj.get("_id");
           return id;
        }
        return null;
    }
    
    /**
     * 保存数据,成功返回id
     *
     * @param ItemLocation
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public String saveCurrent(HomePageSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null!");
        DBObject obj = transform(setting);
        if(save(obj)){
            return (String)obj.get("_id");
        }
        return null;
    }
    
    /**
     * 更新当前配置为使用状态，其他配置为不使用状态
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月13日 下午3:34:13
     */
    public boolean updateCurrentUseOtherUnUse(String id){
        Validate.isTrue(!Strings.isNullOrEmpty(id), "id can't be null!");
        // 执行更新使用状态为true
        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$set",new BasicDBObject("isUse",true));
        if(update(dbObj,updatedbObj)){
            // 执行更新其他的设置使用状态为false
            DBObject otherObj = new BasicDBObject("_id",new BasicDBObject("$ne",id));
            DBObject otherdbObj = new BasicDBObject("$set",new BasicDBObject("isUse",false));
            updateMulti(otherObj,otherdbObj);
            return true;
        }
        return false;
    }
    
    /**
     * 根据id删除信息
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:49:59
     */
    public boolean deleteById(String id){
    	Validate.isTrue(!Strings.isNullOrEmpty(id),"id can't be null!");
    	// 条件DB对象
        DBObject dbObj = new BasicDBObject("_id",id);
        return remove(dbObj);
    }

    /**
     * 转换自定义obj
     *
     * @param setting
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:20:52
     */
    private DBObject transform(HomePageSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null !");
        DBObject dbObj = new BasicDBObject();
        String id = findLastTimeSettingID();
        long intId = 0;
        if(!Strings.isNullOrEmpty(id)){
            intId = Longs.tryParse(id);
        }
        id = String.valueOf(intId+1);
        dbObj.put("_id", id);
        dbObj.putAll(setting.toBuildMap());
        return dbObj;
    }
    
    /** 
     * 转换DBObject to java Model
     * @see odms.context.MdbContext#parseFromRawObject(com.mongodb.DBObject)
     * @since  v1.0
     * @author Calm
     * @created 2016年6月27日 下午5:30:37
     */
    protected HomePageSetting parseFromRawObject(DBObject dbObj){
        if(dbObj==null){
            return null;
        }
        HomePageSetting hsp = new HomePageSetting();
        hsp._id = (String)dbObj.get("_id");
        hsp.activitySettings = Lists.newArrayList();
        List<BasicDBObject> dbAlist = (List<BasicDBObject>)dbObj.get("activitySettings");
        if(MixHelper.isNotEmpty(dbAlist)){
            hsp.activitySettings = Lists.transform(dbAlist, new Function<BasicDBObject,ActivitySetting>(){

                @Override
                public ActivitySetting apply(BasicDBObject input) {
                    OdmPopulater<ActivitySetting> pop = new OdmPopulater<ActivitySetting>(input);
                    ActivitySetting jObj = pop.getEntity(ActivitySetting.class);
                    List<BasicDBObject> iSets = (List<BasicDBObject>) input.get("itemSettings");
                    if(MixHelper.isNotEmpty(iSets)){
                        jObj.itemSettings = Lists.transform( iSets, new Function<BasicDBObject,ItemSetting>(){
    
                            @Override
                            public ItemSetting apply(BasicDBObject input) {
                                OdmPopulater<ItemSetting> pop = new OdmPopulater<ItemSetting>(input);
                                ItemSetting item = pop.getEntity(ItemSetting.class);
                                return item;
                            }
                            
                        });
                    }
                    return jObj;
                }
                
            });
        }
        hsp.big_BannerSettings = Lists.newArrayList();
        List<BasicDBObject> bBlist = (List<BasicDBObject>)dbObj.get("big_BannerSettings");
        if(MixHelper.isNotEmpty(bBlist)){
            hsp.big_BannerSettings = Lists.transform( bBlist,new Function<BasicDBObject,BigBannerSetting>(){
    
                @Override
                public BigBannerSetting apply(BasicDBObject input) {
                    OdmPopulater<BigBannerSetting> pop = new OdmPopulater<BigBannerSetting>(input);
                    BigBannerSetting jObj = pop.getEntity(BigBannerSetting.class);
                    return jObj;
                }
                
            });
        }
        hsp.right_SmallBannerSettings = Lists.newArrayList();
        List<BasicDBObject> rSlist = (List<BasicDBObject>)dbObj.get("right_SmallBannerSettings");
        if(MixHelper.isNotEmpty(rSlist)){
            hsp.right_SmallBannerSettings = Lists.transform(rSlist, new Function<BasicDBObject,RightSmallBannerSetting>(){
    
                @Override
                public RightSmallBannerSetting apply(BasicDBObject input) {
                    OdmPopulater<RightSmallBannerSetting> pop = new OdmPopulater<RightSmallBannerSetting>(input);
                    RightSmallBannerSetting jObj = pop.getEntity(RightSmallBannerSetting.class);
                    return jObj;
                }
                
            });
        }
        hsp.brandSettings = Lists.newArrayList();
        List<BasicDBObject> bSlist = (List<BasicDBObject>)dbObj.get("brandSettings");
        if(MixHelper.isNotEmpty(bSlist)){
            hsp.brandSettings = Lists.transform(bSlist, new Function<BasicDBObject,BrandSetting>(){
    
                @Override
                public BrandSetting apply(BasicDBObject input) {
                    OdmPopulater<BrandSetting> pop = new OdmPopulater<BrandSetting>(input);
                    BrandSetting jObj = pop.getEntity(BrandSetting.class);
                    return jObj;
                }
                
            });
        }
        hsp.name = (String)dbObj.get("name");
        hsp.type = (String)dbObj.get("type");
        hsp.createTime = (String)dbObj.get("createTime");
        hsp.isUse = (Boolean)dbObj.get("isUse");
        return hsp;
    }
    

}
