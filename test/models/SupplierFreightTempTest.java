package models;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.vo.Page;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import vos.FreightSearchVo;

public class SupplierFreightTempTest extends UnitTest {

    @Test
    public void test_find() {
        FreightSearchVo  vo = new FreightSearchVo();
        vo.pageNo = 1;
        vo.pageSize = 10;
        vo.aboutBrand = "费雪";
        vo.aboutSupplier = "18657157520";
        Page<FreightTemp> temp = FreightTemp.findFreightTempByVo(vo);
        System.out.println(JsonUtil.toJson(temp));
    }

//    @Test
//    public void test_save() {
//        SupplierFreightTemp temp = new SupplierFreightTemp();
//        temp._id = 6;
//        // 1
//        Freight fre1 = new Freight();
//        fre1.price = 8;
//        fre1.provinces = ImmutableList.of(1, 2, 3, 4);
//
//        // 模版1
//        FreightTemp ft = new FreightTemp();
//        ft.tempName = "测试运费模版";
//        ft.freights.add(fre1);
//
//        temp.temps.add(ft);
//
//        System.out.println(JsonUtil.toJson(temp.toMap()));
//        if (temp.save()) {
//            System.out.println("save success ");
//        } else {
//            System.out.println("save failed ");
//        }
//
//    }
//
//    @Test
//    public void test_pushFreightTemp() {
//        int id = 6;
//        SupplierFreightTemp temp = SupplierFreightTemp.findById(id);
//        if (temp == null) {
//            temp = new SupplierFreightTemp();
//            temp._id = id;
//            temp.save();
//        }
//        
//        Freight fre1 = new Freight();
//        fre1.price = 8;
//        fre1.provinces = ImmutableList.of(1, 2, 3, 4);
//        
//        // 模版
//        FreightTemp ft = new FreightTemp();
//        ft.tempName = "xxxxx测试运费模版xxxxxxx";
//        ft.freights.add(fre1);
//        
//        temp.pushValueToField("temps", ft.toMap());
//    }
//
//    @Test
//    public void test_updateFreightTemp() {
//        int id = 6;
//        SupplierFreightTemp temp = SupplierFreightTemp.findById(id);
//        if (temp == null) {
//            temp = new SupplierFreightTemp();
//            temp._id = id;
//            temp.save();
//        }
//        Freight fre1 = new Freight();
//        fre1.price = 8;
//        fre1.provinces = ImmutableList.of(1, 2, 3, 4);
//        
//        // 模版
//        FreightTemp ft = new FreightTemp();
//        ft.tempName = "测试运费模版12341234132";
//        ft.freights.add(fre1);
//        
//        temp.updateValueByFieldIndex("temps", 1, ft.toMap());
//    }
//
//    @Test
//    public void test_removeFreightTemp() {
//        int id = 6;
//        SupplierFreightTemp temp = SupplierFreightTemp.findById(id);
//        temp.removeValueByFieldIndex("temps", 1);
//    }
//    
//    @Test
//    public void test_deleteSupplierFreightTemp(){
//        int id = 6;
//        SupplierFreightTemp temp = SupplierFreightTemp.findById(id);
//        temp.delete();
//    }
//    
//    @Test
//    public void test_findMap(){
////        Map result = SupplierFreightTemp.parseFreightTempToMapByIdWithTempKey(21,"9330fd692e93fd416465648d43c9d0b9");
////        System.out.println(JsonUtil.toJson(result));
//    }
//    
//    @Test
//    public void test_findAll(){
//        FreightTemp freightTemp = SupplierFreightTemp.findFreightTempByTempkeyInAll("9330fd692e93fd416465648d43c9d0b9");
//       // List<FreightTemp> temps = SupplierFreightTemp.findAllFreightTemp();
//      //  List<Freight> allTemps = temps.stream().flatMap(s->s.freights.stream()).collect(Collectors.toList());
//        System.out.println(JsonUtil.toJson(freightTemp));
//    }
//
//
}
