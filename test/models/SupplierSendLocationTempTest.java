package models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;

import play.test.UnitTest;


public class SupplierSendLocationTempTest extends UnitTest{

    @Test
    public void test() {
        SupplierSendLocationTemp sst =  new SupplierSendLocationTemp();
        sst.supplierId = 6;
        sst.country = "中国";
        sst.countryId = 0;
        sst.provinceId = 330000;
        sst.province = "浙江省";
        sst.city = "杭州市";
        sst.region = "上城区";
        sst.address = "某某地方";
        if(sst.save()){
            System.out.println("保存成功");
        }
    }
    
    @Test
    public void test_findListBySupplierId(){
        List<SupplierSendLocationTemp> list = SupplierSendLocationTemp.findListBySupplierId(6);
        if(MixHelper.isNotEmpty(list)){
            System.out.println(JsonUtil.toJson(list));
        }
    }
}
