package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


public class StarItemSettingTest extends UnitTest{

    @Test
    public void test() {
       StarItemSetting sis = new StarItemSetting();
       sis.itemIds = ImmutableList.of(1L,2L,3L,4L);
       if(sis.save()){
           System.out.println("save success");
           System.out.println(JsonUtil.toJson(sis));
       }
    }
    
    @Test
    public void test_update() {
       StarItemSetting sis = new StarItemSetting();
       sis._id = 1;
       sis.itemIds = ImmutableList.of(1L,2L,3L,5L);
       if(sis.save()){
           System.out.println("save success");
           System.out.println(JsonUtil.toJson(sis));
       }
    }
    
    @Test
    public void test_find(){
        StarItemSetting sis = StarItemSetting.findFirst();
        System.out.println(JsonUtil.toJson(sis));
    }

}
