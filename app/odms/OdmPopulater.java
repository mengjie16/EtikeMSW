package odms;

import java.awt.geom.IllegalPathStateException;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.UnknownElementException;
import javax.servlet.UnavailableException;

import org.apache.commons.collections.Transformer;
import org.jaxen.UnresolvableException;

import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.util.reflect.FieldProduceRuler;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * 所有Mongodb中存在的JSON数据类型，在Java类中的映射的实体类都继承于该类<br/>
 * 只支持一层嵌套List对象映射
 * 
 * @author tr0j4n
 * @since v0.4.7
 * @created 2013-12-20 下午8:55:18
 */
public class OdmPopulater<T> extends BasicDBObject {
	// 内置的数据库对象
	private BasicDBObject _dbObject;

	/**
	 * 装饰者模式包裹DB对象，隐藏直接与DB对象交互的细节
	 * 
	 * @since v0.4.7
	 */
	public OdmPopulater(BasicDBObject dbObject) {
		_dbObject = dbObject;

		// ReflectionUtil.injectBean(this, dbObject,
		// ReflectionUtil.CAMEL_STYLE);
	}

	private Object _entity;

	public T getEntity(Class<?> clazz) {
		_entity = ReflectionUtils.construct(clazz);

		ReflectionUtils.injectBean(_entity, _dbObject, ReflectionUtils.SAME_STYLE, TRANSFORM_RULES);

		return (T) _entity;
	}

	// 转换规则
	private FieldProduceRuler TRANSFORM_RULES = new FieldProduceRuler();

	/**
	 * 
	 * 申明List类型的映射
	 * 
	 * @param fieldName 类中的成员名字
	 * @param nodeName json中的子节点名称
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-22 下午12:11:35
	 */
	public void registerListField(final String fieldName, final String nodeName) {
		TRANSFORM_RULES.put(fieldName, new Transformer() {
			public Object transform(Object input) {
				if (!DBObject.class.isAssignableFrom(input.getClass())) {
					return null;
				}
				// 把单个DB对象转成List，这是Order 的 Json对象的List
				BasicDBList orderDbList = (BasicDBList) ((BasicDBObject) input).get(nodeName);
				if (orderDbList == null) {
					throw new IllegalPathStateException("没有能找到JSON子节点 " + nodeName);
				}
				// 取得这个字段上的泛型对象类型
				Class<?> genericType = ReflectionUtils.getFieldGenericType(_entity, fieldName);
				List<Object> objList = new ArrayList<Object>();
				for (Object o : orderDbList) {
					BasicDBObject dbo = (BasicDBObject) o;
					Object cellObj = ReflectionUtils.construct(genericType);
					// 把DB对象注射到Order单个对象中
					ReflectionUtils.injectBean(cellObj, dbo, ReflectionUtils.CAMEL_STYLE);
					objList.add(cellObj);
				}
				return objList;
			}
		});

	}

	// -------------------------如下是覆盖的接口--------------------------

	@Override
	public boolean containsKey(Object key) {
		return _dbObject.containsKey(key);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return _dbObject.entrySet();
	}

	@Override
	public Object get(Object key) {
		return _dbObject.get(key);
	}

	@Override
	public boolean isEmpty() {
		return _dbObject.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return _dbObject.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return _dbObject.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return _dbObject.remove(key);
	}

	@Override
	public int size() {
		return _dbObject.size();
	}

	@Override
	public Collection<Object> values() {
		return _dbObject.values();
	}
}
