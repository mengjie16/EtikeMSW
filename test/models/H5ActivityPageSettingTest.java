package models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.collect.Lists;


public class H5ActivityPageSettingTest extends UnitTest {

    @Test
    public void test_create() {
        H5ActivityPageSetting h5aps = new H5ActivityPageSetting();
        h5aps.groupItems = Lists.newArrayList();
        List<Long> groupItem = Lists.newArrayList();
        groupItem.add(2L);
        groupItem.add(5L);
       // h5aps.groupItems.add(groupItem);
        h5aps.isUse = true;
        if(h5aps.save()){
            System.out.println("保存成功");
        }
    }
    
    @Test
    public void test_findList(){
        List<H5ActivityPageSetting> h5apsList = H5ActivityPageSetting.findList(0, 2);
        if(!MixHelper.isEmpty(h5apsList)){
            System.out.println(JsonUtil.toJson(h5apsList));
        }
    }
    
    @Test
    public void test_pushValueToFeild(){
       boolean result =  H5ActivityPageSetting.pushValueToField("1", "groupItems", 1L);
       System.out.println(result);
    }
    
    @Test
    public void test_removeValueFromFeildIndex(){
        boolean result =  H5ActivityPageSetting.removeValueByFieldIndex("1", "groupItems", 1);
        System.out.println(result);
    }
    
    @Test
    public void test_updateValueFromFeildIndex(){
        Long[] ids = new Long[]{4L,7L};
        boolean result =  H5ActivityPageSetting.updateValueByFieldIndex("1", "groupItems", 1,ids);
        System.out.println(result);
    }
    
    @Test
    public void test_updateValueFromFeild(){
        boolean result =  H5ActivityPageSetting.updateValueByField("2", "title", "旗舰店-帕琦BACiUZZi婴儿推车专场");
        System.out.println(result);
    }
    
}
