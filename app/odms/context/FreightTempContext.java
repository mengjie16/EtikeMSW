package odms.context;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;

import com.aton.util.MixHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import models.Freight;
import models.FreightTemp;
import odms.OdmPopulater;

/**
 * 供应商运费模板数据配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月9日 下午3:29:23
 */
public class FreightTempContext extends MdbContext<FreightTemp> {

    public FreightTempContext() {
        COLLECTION_NAME = "freight_temp";
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
    public FreightTemp findById(long id) {
        DBObject queryObj = new BasicDBObject("_id", id);
        DBCursor cursor = getCursor(queryObj).limit(1);
        // 取游标第一个
        DBObject dbObj = pickCursor(cursor);
        if (dbObj != null) {
            return parseFromRawObject(dbObj);
        }
        return null;
    }

    /**
     * 检查指定字段是否存在
     *
     * @param id
     * @param field
     * @param obj
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 上午11:41:57
     */
    public boolean checkFieldValueExsit(long id, String field, Object obj) {
        Validate.isTrue(!Strings.isNullOrEmpty(field), "value can't be null!");
        DBObject queryObj = new BasicDBObject();
        queryObj.put("_id", new BasicDBObject("$ne", id));
        queryObj.put(field, obj);
        return getCursor(queryObj).count() > 0;
    }

    /**
     * 查找所有供应商运费模板
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午1:24:31
     */
    public List<FreightTemp> findUserAll(int userId) {
        DBObject queryObj = new BasicDBObject("userId", userId);
        DBCursor cursor = getCursor(queryObj);
        // 取游标第一个
        List<DBObject> dbObjs = iterateCursor(cursor);
        if (MixHelper.isNotEmpty(dbObjs)) {
            List temps = Lists.newArrayList();
            Iterator<DBObject> objs = dbObjs.iterator();
            while (objs.hasNext()) {
                FreightTemp temp = parseFromRawObject(objs.next());
                if (temp != null) {
                    temps.add(temp);
                }
            }
            return temps;
        }
        return Lists.newArrayList();
    }

    /**
     * 查找最后一个运费模板的id
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 下午4:13:03
     */
    private long lastID() {
        DBObject queryObj = new BasicDBObject();
        DBCursor cursor = getCursor(queryObj).sort(new BasicDBObject("createTime", -1)).limit(1);
        // 取游标第一个
        DBObject dbObj = pickCursor(cursor);
        if (dbObj != null) {
            long id = (Long) dbObj.get("_id");
            return id;
        }
        return 0;
    }

    /**
     * 分页查询所有符合条件数据
     *
     * @param ids
     * @param pageNumber start 1...
     * @param pageSize
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 下午1:52:51
     */
    public List<FreightTemp> findAll(List<Long> ids, int pageNumber, int pageSize) {
        DBObject queryObj = new BasicDBObject();
        if (MixHelper.isNotEmpty(ids)) {
            queryObj = this.conditionInLList("_id", ids);
        }
        pageNumber = pageNumber == 0 ? pageNumber : pageNumber - 1;
        DBCursor cursor = getCursor(queryObj).sort(new BasicDBObject("createTime", 1)).skip(pageNumber * pageSize)
            .limit(pageSize);
        List<DBObject> dbObjs = iterateCursor(cursor);
        if (MixHelper.isNotEmpty(dbObjs)) {
            return dbObjs.stream().map(obj -> parseFromRawObject(obj)).filter(f -> f != null)
                .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 保存数据
     *
     * @param temp
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public boolean save(FreightTemp temp) {
        Validate.isTrue(temp != null, "temp can't be null!");
        return save(transform(temp));
    }

    /**
     * 保存后返回ID
     *
     * @param setting
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:43:36
     */
    public boolean saveReturnId(FreightTemp temp) {
        Validate.isTrue(temp != null, "setting can't be null!");
        DBObject obj = transform(temp);
        if (save(obj)) {
            temp._id = (Long) obj.get("_id");
            return true;
        }
        return false;
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
    public boolean updateFieldById(long id, Map<String, Object> params) {
        Validate.isTrue(MixHelper.isNotEmpty(params), "value can't be null!");
        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject needObj = new BasicDBObject();
        for (String key : params.keySet()) {
            needObj.put(key, params.get(key));
        }
        DBObject updatedbObj = new BasicDBObject("$set", needObj);
        return update(dbObj, updatedbObj);
    }

    /**
     * 更新指定域内的值
     *
     * @param id
     * @param field
     * @param index
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 上午11:35:36
     */
    public boolean updateFieldByIndexId(long id, String field, int index, Object value) {
        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updatedbObj = new BasicDBObject("$set", new BasicDBObject(field + "." + index, value));
        return update(dbObj, updatedbObj);
    }

    /**
     * 向数组类型field增加元素
     *
     * @param id
     * @param field 一定是数组类型，如果field不存在，会新增一个
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:37:01
     */
    public boolean pushValueToFieldById(long id, String field, Object value) {
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
    public boolean removeFieldValueByIndex(long id, String field, int index) {
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
    private boolean setFielValueToNULLByIndex(long id, String field, int index) {
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
    public boolean deleteById(long id) {
        Validate.isTrue(id > 0, "id can't be null!");
        // 条件DB对象
        DBObject dbObj = new BasicDBObject("_id", id);
        return remove(dbObj);
    }

    /**
     * 转换自定义obj
     *
     * @param temp
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午3:20:52
     */
    private DBObject transform(FreightTemp temp) {
        Validate.isTrue(temp != null, "temp can't be null !");
        DBObject dbObj = new BasicDBObject();
        long id = lastID();
        dbObj.put("_id", id + 1);
        dbObj.putAll(temp.toMap());
        return dbObj;
    }

    /**
     * 转换DBObject to java Model
     * 
     * @see odms.context.MdbContext#parseFromRawObject(com.mongodb.DBObject)
     * @since v1.0
     * @author Calm
     * @created 2016年6月27日 下午5:30:37
     */
    protected FreightTemp parseFromRawObject(DBObject dbObj) {
        if (dbObj == null) {
            return null;
        }
        FreightTemp freightTemp = new FreightTemp();
        freightTemp._id = (Long) dbObj.get("_id");
        freightTemp.tempName = (String) dbObj.get("tempName");
        freightTemp.userId = (int) dbObj.get("userId");
        freightTemp.tempType = (String) dbObj.get("tempType");
        List<BasicDBObject> freights = (List<BasicDBObject>) dbObj.get("freights");
        if (MixHelper.isNotEmpty(freights)) {
            freightTemp.freights = freights.stream().map((dbj) -> {
                OdmPopulater<Freight> pop = new OdmPopulater<Freight>(dbj);
                Freight freight = pop.getEntity(Freight.class);
                return freight;
            }).collect(Collectors.toList());
        }
        if(dbObj.get("createTime") != null ){
            freightTemp.createTime = (Date) dbObj.get("createTime");
        }
        return freightTemp;
    }

}
