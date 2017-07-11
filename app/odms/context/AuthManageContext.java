package odms.context;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.List;
import java.util.Map;

import models.AuthManage;

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
 * @since v1.0
 * @created 2016年6月22日 下午3:05:34
 */
public class AuthManageContext extends MdbContext<AuthManage> {

    public AuthManageContext() {
        COLLECTION_NAME = "auth_manage";
    }
    
    /**
     * 查询数量
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:14:51
     */
    private int findCount(){
        DBObject dbObj = new BasicDBObject();
        int result = count(dbObj);
        return result;
    }

    /**
     * 根据id查找数据
     *
     * @param _id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:15:24
     */
    public AuthManage findById(String _id) {
        Validate.isTrue(!Strings.isNullOrEmpty(_id), "_id is failed!");
        return selectOneById(_id);
    }
    
    /**
     * 查找指定条件的数据
     *
     * @param field
     * @param value
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月5日 下午1:03:41
     */
    public AuthManage findByField(String field,String value,String type) {
        Validate.isTrue(!Strings.isNullOrEmpty(field) || !Strings.isNullOrEmpty(type), "field is failed!");

        DBObject queryObj = new BasicDBObject(field, value);
        queryObj.put("type", type);
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
     * @param ItemLocation
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public boolean save(AuthManage auth) {
        Validate.isTrue(auth != null, "setting can't be null!");
        return save(transform(auth));
    }

    /**
     * 根据ID更新数据
     *
     * @param id
     * @param field
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月23日 下午5:25:48
     */
    public boolean updateFieldById(String id, String field, Object value) {
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");

        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updatedbObj = new BasicDBObject("$set", new BasicDBObject(field, value));
        return update(dbObj, updatedbObj);
    }
    
    /**
     * 批量更新字段
     *
     * @param id
     * @param params
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:32:22
     */
    public boolean updateFieldByParams(String id, String field, Object value) {
        Validate.isTrue(!MixHelper.isEmpty(field), "value can't be null!");

        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updatedbObj = new BasicDBObject("$set",new BasicDBObject(field, value));
        return update(dbObj, updatedbObj);
    }

    /**
     * 数组类型field增加元素
     *
     * @param id
     * @param field 一定是数组类型，如果field不存在，会新增一个
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:37:01
     */
    public boolean pushValueToFieldById(String id, String field, Object value) {
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");
        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updatedbObj = new BasicDBObject("$push", new BasicDBObject(field, value));
        return update(dbObj, updatedbObj);
    }

    /**
     * 删除数组field中指定索引的值 start:0,1,2...
     *
     * @param id
     * @param field
     * @param index
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午3:49:42
     */
    public boolean removeFieldValueByIndex(String id, String field, int index) {
        Validate.isTrue(!Strings.isNullOrEmpty(field), "field can't be null!");
        // 先置空null
        if (setFielValueToNULLByIndex(id, field, index)) {
            DBObject dbObj = new BasicDBObject("_id", id);
            DBObject updatedbObj = new BasicDBObject("$pull", new BasicDBObject(field, null));
            return update(dbObj, updatedbObj);
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
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午3:44:59
     */
    private boolean setFielValueToNULLByIndex(String id, String field, int index) {
        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updatedbObj = new BasicDBObject("$unset", new BasicDBObject(field + "." + index, 1));
        return update(dbObj, updatedbObj);
    }

    /**
     * 根据id删除信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:49:59
     */
    public boolean deleteById(String id) {
        Validate.isTrue(!Strings.isNullOrEmpty(id), "id can't be null!");
        // 条件DB对象
        DBObject dbObj = new BasicDBObject("_id", id);
        return remove(dbObj);
    }
    
    /**
     * 删除信息
     *
     * @param type
     * @param moudleId
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月5日 下午11:43:03
     */
    public boolean deleteByTypeMoudle(String type, String moudleId) {
        Validate.isTrue(!Strings.isNullOrEmpty(type), "id can't be null!");
        // 条件DB对象
        DBObject dbObj = new BasicDBObject("type", type);
        dbObj.put("moudleId", moudleId);
        return remove(dbObj);
    }
    
    /**
     * 转换自定义obj
     *
     * @param setting
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:20:52
     */
    private DBObject transform(AuthManage setting) {
        Validate.isTrue(setting != null, "setting can't be null !");
        DBObject dbObj = new BasicDBObject();
        int count = findCount();
        dbObj.put("_id", count+1+"");
        dbObj.putAll(setting.toMap());
        return dbObj;
    }
}
