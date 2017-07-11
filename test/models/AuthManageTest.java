package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


public class AuthManageTest extends UnitTest{

    @Test
    public void test() {
       AuthManage am = new AuthManage();
       am.moudleId = "1";
       am.type = "album";
       am.preset = ImmutableList.of("Calm","calm");
       am.users = Lists.newArrayList();
       if(am.save()){
           System.out.println("保存成功");
       }
    }
    
    @Test
    public void findByMoubleId(){
       AuthManage am = AuthManage.findByMoubleId("1", "album"); 
       if(am!=null){
           System.out.println(JsonUtil.toJson(am));
//           am.preset.remove("Calm");
//           am.save();
           System.out.println(am.preset);
       }
    }
    @Test
    public void findByID(){
        AuthManage am = AuthManage.findById("1");
        System.out.println(JsonUtil.toJson(am));
    }
    @Test
    public void test_update(){
        AuthManage am = new AuthManage();
        am._id = "1";
        am.moudleId = "7";
        am.type = "album";
        am.preset = ImmutableList.of();
        am.users = Lists.newArrayList();
        am.save();
        
    }
    @Test
    public void test_delete(){
        AuthManage.deleteByCondition("album","7");
    }

}
