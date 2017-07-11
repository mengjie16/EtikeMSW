package odms.context;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.List;
import java.util.Map;

import models.StarItemSetting;

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
public class StarItemContext extends MdbContext<StarItemSetting> {
	
    public StarItemContext() {
        COLLECTION_NAME = "star_item_setting";
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
    public StarItemSetting findBySettingById(String _id) {
        Validate.isTrue(!Strings.isNullOrEmpty(_id), "_id is failed!");
        return selectOneById(_id);
    }
    
    /**
     *  查找总数
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月17日 上午3:29:59
     */
    private int findCount(){
        DBObject dbObj = new BasicDBObject();
        int result = count(dbObj);
        return result;
    }
    
    /**
     * 查找首个
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:35:59
     */
    public StarItemSetting findFirst(){
        DBObject queryObj = new BasicDBObject();
        DBCursor cursor = getCursor(queryObj).limit(1);
        // 取游标第一个
        DBObject dbObj = pickCursor(cursor);
        if (dbObj != null) {
            return parseFromRawObject(dbObj);
        }
        return null;
    }
    

    
    /**
     * 保存数据
     *
     * @param setting
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public boolean save(StarItemSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null!");
        return save(transform(setting));
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
    public boolean updateFieldById(long id, String field,Object value){
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");

        DBObject dbObj = new BasicDBObject("_id",id);
        DBObject updatedbObj = new BasicDBObject("$set",new BasicDBObject(field,value));
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
    public boolean deleteById(long id){
    	Validate.isTrue(id>0,"id can't be null!");
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
    private DBObject transform(StarItemSetting setting) {
        Validate.isTrue(setting != null, "setting can't be null !");
        DBObject dbObj = new BasicDBObject();
        if(setting._id==0){
           setting._id = findCount()+1; 
        }
        dbObj.put("_id", setting._id);
        dbObj.putAll(setting.toMap());
        return dbObj;
    }
}
