package models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;


public class ItemPvTest extends UnitTest{

    @Test
    public void test_save() {
        ItemPv pv = new ItemPv();
        pv._id = 1;
        pv.pcInAlbum = 1;
        boolean result = pv.save();
        System.out.println("保存结果："+result);
    }
    
    @Test
    public void test_findById(){
        ItemPv pv = ItemPv.findById(1);
        System.out.println(JsonUtil.toJson(pv));
    }
    
    @Test
    public void test_updateFieldIncr(){
        boolean result = ItemPv.incrementPc(2, 1);
        System.out.println(result);
    }

}
