package odms.context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import odms.DataStore;
import odms.OdmPopulater;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 
 * MongoDB实体类上下文对象
 * 
 * @author tr0j4n
 * @since v0.4.2
 * @created 2013-12-24 下午2:15:00
 */
public class MdbContext<T> {

    private static Logger log = LoggerFactory.getLogger(MdbContext.class);

    public String COLLECTION_NAME = "";

    private Class<T> entityClass;

    public MdbContext() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    /**
     * 
     * 从Mdb中取，可以使用各种支持的复杂查询
     * 
     * @param dbObj
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-24 下午2:45:01
     */
    protected DBCursor getCursor(DBObject dbObj) {
        return DataStore.dbCollection(COLLECTION_NAME).find(dbObj);
    }

    /**
     * 查询表的数据总量
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:04:02
     */
    protected long count() {
        return DataStore.dbCollection(COLLECTION_NAME).count();
    }

    /**
     * 条件数据总量
     *
     * @param dbObj
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:12:23
     */
    protected int count(DBObject dbObj) {
        int count = getCursor(dbObj).count();
        return count;
    }

    /**
     * 
     * 从游标中获得第一个原始元素
     * 
     * @param cursor
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-25 下午4:30:29
     */
    protected DBObject pickCursor(DBCursor cursor) {
        List<DBObject> objList = iterateCursor(cursor);
        if (objList.size() > 0) {
            return objList.get(0);
        }
        return null;
    }

    /**
     * 
     * 将游标中的所有元素都取出
     * 
     * @param cursor
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-5-1 下午4:31:53
     */
    protected List<DBObject> iterateCursor(DBCursor cursor) {
        List<DBObject> objList = Lists.newArrayList();
        try {
            while (cursor.hasNext()) {
                DBObject dbRecord = cursor.next();
                objList.add(dbRecord);
            }
        } finally {
            cursor.close();
        }

        return objList;
    }

    /**
     * 
     * 反序列化DBObject对象
     * 
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-25 下午4:40:09
     */
    protected T parseFromRawObject(DBObject dbObj) {
        Validate.isTrue(dbObj != null, "dbObj == null");
        OdmPopulater<T> pop = new OdmPopulater<T>((BasicDBObject) dbObj);
        T sObj = pop.getEntity(entityClass);
        return sObj;
    }

    /**
     * 
     * 通过id取一个对象
     * 
     * @param id
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-24 下午2:22:01
     */
    protected T selectOneById(String id) {
        Validate.isTrue(!Strings.isNullOrEmpty(id), "parameter id invalid");

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
     * 
     * 复杂查询多个对象
     * 
     * @param queryObj
     * @return
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-24 下午4:46:49
     */
    protected List<T> selectList(DBObject queryObj) {
        Validate.isTrue(queryObj != null, "queryObj == null");
        DBCursor cursor = getCursor(queryObj);
        List<DBObject> dbObjList = iterateCursor(cursor);
        List<T> objList = Lists.newArrayList();
        // 遍历进行反序列化
        for (DBObject dbObj : dbObjList) {
            T obj = parseFromRawObject(dbObj);
            objList.add(obj);
        }
        return objList;
    }

    /**
     * 
     * 保存到Mdb
     * 
     * @param dbObj
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-24 下午2:44:36
     */
    protected boolean save(DBObject dbObj) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.save(dbObj);
        if (!Strings.isNullOrEmpty(wr.getError())) {
            log.error("Writing object to Mdb failed, id= {} ", dbObj.get("_id"));
            return false;
        }
        return true;
    }

    /**
     * 
     * 批量写入对象
     * 
     * @param dboList
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-25 上午10:31:54
     */
    protected boolean saveBatch(List<DBObject> dboList) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.insert(dboList, WriteConcern.SAFE);
        if (!Strings.isNullOrEmpty(wr.getError())) {
            log.error("Writing object to Mdb failed,err:{}", wr.getError());
            return false;
        }
        return true;
    }

    /**
     * 
     * 每个DbContext对象都需要有这样一个方法<br/>
     * 该方法规定了DB对象的解析为实体的规则
     * 
     * @param dbObj 某条数据对象
     * @return 实体对象的列表
     * @since v0.4.7
     * @author tr0j4n
     * @created 2013-12-22 下午4:21:30
     */
    protected List<T> fetch(DBObject dbObj) {
        List<T> objList = Lists.newArrayList();

        return objList;
    }

    /**
     * 删除符合条件记录
     *
     * @param dbObj
     * @return
     * @since v1.5.0316
     * @author CalmLive
     * @created 2016年3月24日 上午11:36:19
     */
    protected boolean remove(DBObject dbObj) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.remove(dbObj);
        if (!Strings.isNullOrEmpty(wr.getError())) {
            log.error("remove object from Mdb failed,err:{}", wr.getError());
            return false;
        }
        return true;
    }

    /**
     * 根据条件更新数据
     *
     * @param queryObj
     * @param updateObj
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月23日 下午5:22:46
     */
    protected boolean update(DBObject queryObj, DBObject updateObj) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.update(queryObj, updateObj);
        if (wr.getN() == 0) {
            log.error("update object from Mdb failed,err:{}", wr.getError());
            return false;
        }
        return true;
    }

    /**
     * 根据符合条件个更新多条数据()
     *
     * @param queryObj
     * @param updateObj
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:08:38
     */
    protected boolean updateMulti(DBObject queryObj, DBObject updateObj) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.update(queryObj, updateObj, false, true);
        if (wr.getN() == 0) {
            log.error("update object from Mdb failed,err:{}", wr.getError());
            return false;
        }
        return true;
    }

    /**
     * 更新指定条目数据，没有自动新增
     *
     * @param queryObj
     * @param updateObj
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:16:34
     */
    protected boolean updateInsert(DBObject queryObj, DBObject updateObj) {
        DBCollection coll = DataStore.dbCollection(COLLECTION_NAME);
        WriteResult wr = coll.update(queryObj, updateObj, true, false);
        if (wr.getN() == 0) {
            log.error("update object from Mdb failed,err:{}", wr.getError());
            return false;
        }
        return true;

    }

    /**
     * 返回查询in field 条件
     *
     * @param field
     * @param values
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:59:00
     */
    protected DBObject conditionIn(String field, List<String> values) {
        return new BasicDBObject(field, new BasicDBObject("$in", values));
    }

    /**
     * 返回查询not in field 条件
     *
     * @param field
     * @param values
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午3:20:59
     */
    protected DBObject conditionNIn(String field, List<String> values) {
        return new BasicDBObject(field, new BasicDBObject("$nin", values));
    }
    
    /**
     * 返回查询not in field 条件
     *
     * @param field
     * @param values
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午3:20:59
     */
    protected DBObject conditionInLList(String field, List<Long> values) {
        return new BasicDBObject(field, new BasicDBObject("$in", values));
    }
}
