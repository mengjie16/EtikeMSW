package vos;

import java.util.Date;

import play.data.binding.As;
import play.data.validation.Match;
import utils.SearchEndDateBinder;

import com.aton.util.StringUtils;
import com.aton.vo.Page;

/**
 * 微信用户搜索vo
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月5日 上午10:30:21
 */
public class WechatUserVo extends Page {
	
    public String nick;
    public String openId;
   
    public static WechatUserVo newInstance() {
        WechatUserVo vo = new WechatUserVo();
        return vo;
    }
}