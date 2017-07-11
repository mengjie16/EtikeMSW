package odms.context;

import java.util.List;
import java.util.Map;

import models.ItemLocation;

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
 * @since  v1.0
 * @created 2016年6月2日 上午1:34:43
 */
public class ItemLocationContext extends MdbContext<ItemLocation> {
	
    public ItemLocationContext() {
        COLLECTION_NAME = "item_location";
    }

    /**
     * 查找唯一的那条配置的DB对象
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午1:34:31
     */
    private ItemLocation findByItemId_Num(String numStr) {
    	Validate.isTrue(MixHelper.isNotEmpty(numStr), "_id is failed!");
        return selectOneById(numStr);
    }
    
    /**
     * 根据交易id查找数据
     * 
     * @param transId
     * @return
     * @since v1.0
     * @author bwl
     * @created 2016年1月27日 上午11:42:58
     */
    public List<ItemLocation> findByItemId(long itemId) {
       
        Validate.isTrue(itemId != 0, "condition is failed!");
        // 条件DB对象
        DBObject dbObj = new BasicDBObject("itemId",itemId);
        return selectList(dbObj);
    }
    
    
    /**
     * 生成商品id_序号列表
     *
     * @param itemId
     * @param count
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午11:08:43
     */
    private static List<String> generateItem_Num(long itemId, int count){
    	List<String> listStr = Lists.newArrayList();
    	for(int i=0;i<count;i++){
    		listStr.add(itemId+"_"+i);
    	}
    	return listStr;
    }
    
    /**
     * 生成商品地址项主键
     *
     * @param itemId
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午11:44:25
     */
    private String generateOneItem_Num(long itemId){
    	 Validate.isTrue(itemId != 0, "itemId is error !"); 
    	 int index = 0;
    	 String numStr = itemId+"_"+index;
    	 while(findByItemId_Num(numStr)!=null){
    		 index++;
    		 numStr = itemId+"_"+index;
    	 }
    	 return numStr;
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
    public boolean save(ItemLocation itemLocation) {
        Validate.isTrue(itemLocation != null, "itemLocation can't be null!");
        return save(transform(itemLocation));
    }
    
    /**
     * 批量保存
     *
     * @param params
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 下午2:52:28
     */
    public boolean batchSaveIteamLocation(List<ItemLocation> params){
    	Validate.isTrue(MixHelper.isNotEmpty(params), "params can't be null!");
    	List<DBObject> objs =  Lists.transform(params,new Function<ItemLocation,DBObject>(){
			@Override
			public DBObject apply(ItemLocation input) {
				// TODO Auto-generated method stub
				return transform(input);
			}
    	});
    	return saveBatch(objs);
    }
    
    /**
     * 根据宝贝ID删除地址信息
     *
     * @param itemId
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:19:14
     */
    public boolean deleteByItemId(long itemId){
    	Validate.isTrue(itemId!=0, "itemId can't be null!");
    	// 条件DB对象
        DBObject dbObj = new BasicDBObject("itemId",itemId);
        return remove(dbObj);
    }
    
    /**
     * 根据id删除地址信息
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
     * @param ItemLocation
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:22:55
     */
    private DBObject transform(ItemLocation itemLocation) {
        Validate.isTrue(itemLocation != null, "itemLocation can't be null !");
        DBObject dbObj = new BasicDBObject();
        dbObj.putAll(itemLocation.toMap());
        return dbObj;
    }
    
    /**
     * 转换DBOject 为targetDBObject
     *
     * @param dbObj
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:31:40
     */
    private ItemLocation transform(DBObject dbObj) {
        
        Validate.isTrue(dbObj != null, "dbObj is wrong !");
        return parseFromRawObject(dbObj);
    }

}
