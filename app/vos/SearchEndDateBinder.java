package vos;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import play.data.binding.TypeBinder;

import com.aton.util.DateUtils;
import com.google.common.base.Strings;

/**
 * 用于格式化截止时间+1
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年9月20日 上午10:24:23
 */
public class SearchEndDateBinder implements TypeBinder<Date> {

    @Override
    public Object bind(String name, Annotation[] annotations, String value, Class actualClass, Type genericType)
        throws Exception {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        String[] patterns = { DateUtils.YYYYMMDD, DateUtils.YYYY_MM_DD };
        Date date = DateUtils.tryParse(value, patterns);
        Date result = DateUtils.addDays(date, 1);
        return result;
    }

}
