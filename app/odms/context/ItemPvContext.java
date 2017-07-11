package odms.context;

import java.util.List;
import java.util.Map;

import models.ItemPv;

import org.apache.commons.lang.Validate;

import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import odms.DataStore;

/**
 * 商品地址保存
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月2日 上午1:34:43
 */
public class ItemPvContext extends MdbContext<ItemPv> {

    public ItemPvContext() {
        COLLECTION_NAME = "item_pv";
    }

    /**
     * 查找唯一的那条配置的DB对象
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午1:34:31
     */
    public ItemPv findByItemId(long itemId) {
        Validate.isTrue(itemId > 0, "itemId is failed!");
        // 条件DB对象
        DBObject queryObj = new BasicDBObject("_id", itemId);
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
     * @param ItemPv
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:34:25
     */
    public boolean save(ItemPv itemPv) {
        Validate.isTrue(itemPv != null, "ItemPv can't be null!");
        return save(transform(itemPv));
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

    public boolean updateFieldByIncrement(long id, String field, long incrNum) {
        Validate.isTrue(MixHelper.isNotEmpty(field) && id > 0, "feild or id can't be null!");
        // 条件DB对象
        DBObject dbObj = new BasicDBObject("_id", id);
        DBObject updateObj = new BasicDBObject("$inc", new BasicDBObject(field, incrNum));
        return updateInsert(dbObj, updateObj);
    }

    /**
     * 转换自定义obj
     *
     * @param ItemPv
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:22:55
     */
    private DBObject transform(ItemPv itemPv) {
        Validate.isTrue(itemPv != null, "ItemPv can't be null !");
        DBObject dbObj = new BasicDBObject();
        dbObj.putAll(itemPv.toMap());
        return dbObj;
    }

}
