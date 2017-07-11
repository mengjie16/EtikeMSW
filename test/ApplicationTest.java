import java.util.List;
import java.util.Map;

import org.junit.*;

import com.aton.config.Config;
import com.aton.util.JsonUtil;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import play.test.*;
import utils.WechatHelper;
import vos.OrderVo;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        // 当前行
        Map<Integer, String> mis = ImmutableMap.of(0,"",1,"",2,"",3,"adaad",4,"");
        // 当前行是否为空
        boolean match = mis.values().stream().allMatch(s -> !Strings.isNullOrEmpty(s));
        System.out.println(match);
    }
    
    @Test
    public void getWxAuthUrl(){
        String wxAuthUrl = Config.getProperty("wechat.connect.url");
        System.out.println(wxAuthUrl);
        String redirectUrl = WechatHelper.getAuthToTargetUrl("http:wwww.taonao", null, "snsapi_userinfo");
        System.out.println(redirectUrl);
    }
    
    @Test
    public void test_java8_stream(){
        Table<Integer, Integer, String> table = HashBasedTable.create();
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 5; column++) {
                table.put(row, column, "value of cell (" + row + "," + column + ")");
            }
        }
        for (int row = 10; row < 20; row++) {
            for (int column = 0; column < 6; column++) {
                table.put(row, column, "value of cell (" + row + "," + column + ")");
            }
        }
        for (int row=0;row<table.rowMap().size();row++) {
            Map<Integer,String> rowData = table.row(row);
            System.out.println(rowData.size());
           
        }
    }
    
    @Test
    public void test_parseAddress(){
//       Map map = OrderVo.parseAddress("江苏省苏州市昆山市花桥镇兆丰路11号都会新峰21栋2002室");
//       System.out.println(JsonUtil.toJson(map));
    }
    
}