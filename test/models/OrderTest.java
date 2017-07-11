package models;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.RegexUtils;
import com.aton.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

import enums.constants.RegexConstants;
import vos.OrderVo;


public class OrderTest extends UnitTest{

    @Test
    public void test() {
        String address_deatail_pcra = "广东。广州市。番禺区。广州市。番禺区沙湾镇青云大道锦绣世家11座1304";
//        
        System.out.println(address_deatail_pcra.substring(8));
        Location address = OrderVo.parseAddress(address_deatail_pcra);
        MixHelper.print(JSON.toJSONString(address));
    }
    
    @Test
    public void test_inserts(){
        System.out.println(RegexUtils.isMatch("0571+++++++++85314733", "\\d{4,8}"));
        
    }
    
    @Test
    public void test_provincePeek(){
        OrderVo vo = new OrderVo();
        vo.parseProvinceId("云南");
        MixHelper.print(JSON.toJSONString(vo));
    }

}
