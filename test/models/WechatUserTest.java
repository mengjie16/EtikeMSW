package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.util.JsonUtil;
import com.aton.vo.Page;

import play.test.UnitTest;
import vos.WechatUserVo;


public class WechatUserTest extends UnitTest{

    @Test
    public void test_save() {
        WechatUser wu = new WechatUser();
        wu.openId = "12356478909-";
        wu.avatar = "http://ssadfasd";
        wu.nick = "测试";
        if(wu.save()){
            System.out.println("保存成功");
        }
    }
    
    @Test
    public void test_findByVo(){
        WechatUserVo wuv = new WechatUserVo();
        wuv.nick = "测试";
        wuv.pageNo = 1;
        wuv.pageSize = 5;
        Page<WechatUser>  wechatUsers = WechatUser.findByVo(wuv);
        System.out.println(JsonUtil.toJson(wechatUsers));
        
    }

}
