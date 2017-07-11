package odms.context;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.List;
import java.util.Map;

import models.ActivityItem;
import models.ActivityPageSetting;

import org.apache.commons.lang.Validate;

import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
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
 * 活动页面配置
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年6月22日 下午3:05:34
 */
public class ActivityPageSettingContext extends MdbContext<ActivityPageSetting> {
	
    public ActivityPageSettingContext() {
        COLLECTION_NAME = "activity_page_setting";
    }
    
    /**
     * 查询数量
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:14:51
     */
    public int findCount(){
        DBObject dbObj = new BasicDBObject();
        int result = count(dbObj);
        return result;
    }
    
    /**
     * 根据id查找数据
     *
     * @param _id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:15:24
     */
    public ActivityPageSetting findBySettingById(String _id) {
        Validate.isTrue(!Strings.isNullOrEmpty(_id), "_id is failed!");
        return selectOneById(_id);
    }
    
    /**
     * 活动列表查询
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月1日 下午2:02:39
     */
    public List<ActivityPageSetting> findPageList(int pageNumber, int pageSize) {
        DBObject dbObj = new BasicDBObject();
        DBCursor cursor = getCursor(dbObj).sort(new BasicDBObject("createTime", 1)).skip(pageNumber * pageSize)
            .limit(pageSize);
        List<DBObject> getObjs = iterateCursor(cursor);
        List<ActivityPageSetting> activityList = Lists.transform(getObjs, new Function<DBObject, ActivityPageSetting>() {
            @Override
            public ActivityPageSetting apply(DBObject input) {
                return parseFromRawObject(input);
            }
        });
        return activityList;
    }
    
    /**
     * 查找默认活动页配置
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月25日 下午4:06:26
     */
    public ActivityPageSetting findCurrentUseSetting(){
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
     * 保存数据
     *
     * @param ItemLocation
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public boolean save(ActivityPageSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null!");
        return save(transform(setting));
    }
    
    /**
     * 保存后返回ID
     *
     * @param setting
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:43:36
     */
    public String saveReturnId(ActivityPageSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null!");
        DBObject obj = transform(setting);
        if(save(obj)){
            return (String)obj.get("_id");
        }
        return null;
    }
    
    
    /**
     * 根据ID更新数据
     *
     * @param id
     * @param field
     * @param value
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月23日 下午5:25:48
     */
    public boolean updateFieldById(String id, String field,Object value){
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");

        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$set",new BasicDBObject(field,value));
        return update(dbObj,updatedbObj);
    }
    
    /**
     * 更新指定域内的值
     *
     * @param id
     * @param field
     * @param index
     * @param value
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月28日 上午11:35:36
     */
    public boolean updateFieldByIndexId(String id, String field,int index,Object value){
        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$set",new BasicDBObject(field+"."+index,value));
        return update(dbObj,updatedbObj);
    }
    
    /**
     * 想数组类型field增加元素
     *
     * @param id
     * @param field 一定是数组类型，如果field不存在，会新增一个
     * @param value
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:37:01
     */
    public boolean pushValueToFieldById(String id, String field,Object value){
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");
        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$push",new BasicDBObject(field,value));
        return update(dbObj,updatedbObj);
    }
    
    /**
     * 删除数组field中指定索引的值 start:0,1,2...
     *
     * @param id
     * @param field
     * @param index
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月25日 下午3:49:42
     */
    public boolean removeFieldValueByIndex(String id, String field,int index){
        Validate.isTrue(!Strings.isNullOrEmpty(field), "field can't be null!");
        // 先置空null
        if(setFielValueToNULLByIndex(id,field,index)){
            DBObject dbObj = new BasicDBObject("_id",id);
            DBObject updatedbObj = new BasicDBObject("$pull",new BasicDBObject(field,null));
            return update(dbObj,updatedbObj);
        }
        return false;
    }
    
    /**
     * unset value to null
     *
     * @param id
     * @param field
     * @param index
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月25日 下午3:44:59
     */
    private boolean setFielValueToNULLByIndex(String id, String field,int index){
        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$unset",new BasicDBObject(field+"."+index,1));
        return update(dbObj,updatedbObj);
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
     * 转换自定义obj
     *
     * @param setting
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:20:52
     */
    private DBObject transform(ActivityPageSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null !");
        DBObject dbObj = new BasicDBObject();
        String id = findLastTimeSettingID();
        long intId = 0;
        if(!Strings.isNullOrEmpty(id)){
            intId = Longs.tryParse(id);
        }
        id = String.valueOf(intId+1);
        dbObj.put("_id",id);
        dbObj.putAll(setting.toMap());
        return dbObj;
    }
    
    /** 
     * 转换DBObject to java Model
     * @see odms.context.MdbContext#parseFromRawObject(com.mongodb.DBObject)
     * @since  v1.0
     * @author Calm
     * @created 2016年6月27日 下午5:30:37
     */
    protected ActivityPageSetting parseFromRawObject(DBObject dbObj){
        if(dbObj==null){
            return null;
        }
        ActivityPageSetting aps = new ActivityPageSetting();
        aps._id = (String)dbObj.get("_id");
        aps.activityItems = Lists.newArrayList();
        List<BasicDBObject> dbAlist = (List<BasicDBObject>)dbObj.get("activityItems");
        if(MixHelper.isNotEmpty(dbAlist)){
            aps.activityItems = Lists.transform(dbAlist, new Function<BasicDBObject,ActivityItem>(){
                @Override
                public ActivityItem apply(BasicDBObject input) {
                    OdmPopulater<ActivityItem> pop = new OdmPopulater<ActivityItem>(input);
                    ActivityItem jObj = pop.getEntity(ActivityItem.class);
                    return jObj;
                }
            });
        }

        aps.title = (String)dbObj.get("title");
        aps.bannerImg = (String)dbObj.get("bannerImg");
        aps.middlePoster = (String)dbObj.get("middlePoster");
        aps.bottomItemIds = Optional.fromNullable((List<Long>)dbObj.get("bottomItemIds")).or(Lists.newArrayList());
        aps.isUse = (Boolean)dbObj.get("isUse");
        return aps;
    }
    

}
