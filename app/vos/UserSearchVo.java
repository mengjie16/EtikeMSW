package vos;

import java.util.Date;

import com.aton.util.StringUtils;
import com.aton.vo.Page;

import play.data.binding.As;
import play.data.validation.Match;
import utils.SearchEndDateBinder;

/**
 * 用户搜索vo
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月14日 下午3:42:48
 */
public class UserSearchVo extends Page {

    public String name;
    public String email;
    public String phone;
    public boolean isDelete;
    // 零售商就是，供应商就是
    @Match("RETAILER|SUPPLIER")
    public String userType;
    // 零售商或者供应商的id
    public int userId;
    @As("yyyy-MM-dd")
    public Date createTimeStart;
    @As(binder = SearchEndDateBinder.class)
    public Date createTimeEnd;

    public static UserSearchVo newInstance() {
        UserSearchVo vo = new UserSearchVo();
        return vo;
    }

    public String getName() {
        return StringUtils.trimToNull(name);
    }

    public String getEmail() {
        return StringUtils.trimToNull(email);
    }

    public String getPhone() {
        return StringUtils.trimToNull(phone);
    }
}