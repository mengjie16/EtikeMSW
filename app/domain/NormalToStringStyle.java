package domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import com.aton.util.DateUtils;

/**
 * 
 * TODO Comment.
 * 
 * @author Tr0j4n
 * @since v0.7.3
 * @created 2015年6月8日 上午12:35:32
 */
public class NormalToStringStyle extends ToStringStyle {

    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if(value == null){
            value = "NULL";
            buffer.append(value);
            return;
        }
        if (value instanceof Date) {
            value = new DateTime(value).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        buffer.append(value);
    }
}
