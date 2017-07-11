package odms.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * 用来帮助创建复杂的Mongodb查询逻辑<br/>
 * 官方的QueryBuilder有Bug，坑已踩
 * 
 * @author tr0j4n
 * @since v0.4.7
 * @created 2013-12-22 下午5:17:13
 */
public abstract class Mriteria {

	/**
	 * 
	 * in查询，类似于SQL:select * from A where id in (...).
	 *
	 * @param node
	 * @param value
	 * @return
	 * @since  v0.6.2
	 * @author youblade
	 * @created 2014年3月29日 下午5:24:04
	 */
	public static <T> DBObject in(String node, List<T> value) {
		BasicDBList values = new BasicDBList();  
		values.addAll(value);
		return new BasicDBObject(node, new BasicDBObject("$in", values));
	}

	/**
	 * 
	 * 等于
	 * 
	 * @param node json节点名称
	 * @param value 比较的值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:45:45
	 */
	public static DBObject equal(String node, Object value) {
		return new BasicDBObject(node, value);
	}

	/**
	 * 
	 * 小于
	 * 
	 * @param node json节点名称
	 * @param value 比较的值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:45:57
	 */
	public static DBObject less(String node, Object value) {
		return new BasicDBObject(node, new BasicDBObject("$lt", value));
	}

	/**
	 * 
	 * 小于等于
	 * 
	 * @param node json节点名称
	 * @param value 比较的值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:46:04
	 */
	public static DBObject lessAndEqual(String node, Object value) {
		return new BasicDBObject(node, new BasicDBObject("$lte", value));
	}

	/**
	 * 
	 * 大于
	 * 
	 * @param node json节点名称
	 * @param value 比较的值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:46:15
	 */
	public static DBObject greater(String node, Object value) {
		return new BasicDBObject(node, new BasicDBObject("$gt", value));
	}

	/**
	 * 
	 * 大于等于
	 * 
	 * @param node json节点名称
	 * @param value 比较的值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:46:24
	 */
	public static DBObject greaterAndEqual(String node, Object value) {
		return new BasicDBObject(node, new BasicDBObject("$gte", value));
	}

	/**
	 * 
	 * 开区间的查询
	 * 
	 * @param node json节点名称
	 * @param head 最小值
	 * @param tail 最大值
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:44:48
	 */
	public static DBObject between(String node, Object head, Object tail) {
		DBObject queryObj = and(greater(node, head), less(node, tail));
		return queryObj;
	}

	/**
	 * 
	 * and 连接多个条件
	 * 
	 * @param dbos
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:40:10
	 */
	public static DBObject and(DBObject... dbos) {
		return unionLogical("$and", dbos);
	}

	/**
	 * 
	 * or连接多个查询条件
	 * 
	 * @param dbos
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:39:52
	 */
	public static DBObject or(DBObject... dbos) {
		return unionLogical("$or", dbos);
	}

	/**
	 * 
	 * 连接多个查询条件
	 * 
	 * @param logic 联合逻辑标识
	 * @param dbos dbObject对象
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午8:39:21
	 */
	private static DBObject unionLogical(String logic, DBObject... dbos) {
		List<DBObject> qoList = new ArrayList<DBObject>();
		Collections.addAll(qoList, dbos);
		DBObject unionQueryQo = new BasicDBObject(logic, qoList);
		return unionQueryQo;
	}

}
