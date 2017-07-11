package odms.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.joda.time.DateTime;

import com.aton.util.DateUtils;
import com.aton.util.RegexUtils;
import com.mongodb.DBObject;

/**
 * 
 * Object Document 操作实用工具类
 * 
 * @author tr0j4n
 * @since v0.4.7
 * @created 2013-12-24 下午2:54:56
 */
public class OdmUtils {

	//日期时间格式匹配正则
	private static final String TIME_REGEX = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

	/**
	 * 
	 * 层层遍历db对象
	 * 
	 * @param fatherObj
	 * @param path
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-24 下午2:52:03
	 */
	public static DBObject deepIn(DBObject fatherObj, String path) {
		String[] nodePaths = path.split("/");
		DBObject result = fatherObj;
		for (String nodeName : nodePaths) {
			result = (DBObject) result.get(nodeName);
		}
		return result;
	}

	/**
	 * 
	 * 批量解析出一批字段，如果是日期类型，会自动进行转换，在处理JSON对象的多个字段时使用<br/>
	 * 避免逻辑丑陋
	 * 
	 * @param dbo
	 * @param nodeNames
	 * @return
	 * @since v0.4.7
	 * @author tr0j4n
	 * @created 2013-12-24 下午3:02:30
	 */
	public static Map<String, Object> draw(DBObject dbo, String... nodeNames) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String nodeName : nodeNames) {
			Object raw = dbo.get(nodeName);
			if (raw == null) {
				continue;
			}
			String str = raw.toString();
			// 发现是日期格式
			if (RegexUtils.isMatch(str, TIME_REGEX)) {
				Date dt = DateTime.parse(str).toDate();
				resultMap.put(nodeName, dt);
			} else {
				resultMap.put(nodeName, str);
			}
		}
		return resultMap;
	}
}
