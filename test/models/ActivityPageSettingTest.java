package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;


public class ActivityPageSettingTest extends UnitTest {

    @Test
    public void test() {
        ActivityPageSetting aps = ActivityPageSetting.findCurrentUse();
        if(aps!=null){
            System.out.println( aps.title);
            if(aps.delete()){
                System.out.println("删除成功");
            }
        }
    }
    
    @Test
    public void test_find(){
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById("1");
        if(activityPageSetting!=null){
            System.out.println(activityPageSetting.title);
        }
    }
    
    @Test
    public void test_delete(){
        ActivityPageSetting activityPageSetting = ActivityPageSetting.findById("1");
        if(activityPageSetting!=null){
            System.out.println(activityPageSetting.delete());
        }
    }

}
