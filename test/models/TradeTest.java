package models;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.Pandora;
import com.aton.vo.Page;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import enums.TradeStatus;
import utils.TSFileUtil;
import vos.OrderVo;
import vos.TradeSearchVo;


public class TradeTest extends UnitTest{

    @Test
    public void test() {
        // 生成交易
        Trade trade = new Trade().tradeAuiting();
        List<Order> orders = Lists.newArrayList();
        Order order = new Order();
        order.id= 12345678L;
        order.outOrderNo="123123123123123";
        order.caption="test";
//        order.discount = 1;
//        order.retailerId = 1;
//        order.goodsFee = 190;
//        order.shippingFee = 10;
//        order.totalAmount = 200;
//        
//        OrderVo order2 = new OrderVo();
//        order2.itemId = 2;
//        order2.id= 123456780L;
//        order2.caption="test";
//        order2.discount = 2;
//        order2.retailerId = 2;
//        order2.goodsFee = 120;
//        order2.shippingFee = 10;
//        order2.totalAmount = 130;
//        
//        
//        
//        orders.add(order);
//        //orders.add(order2.parseToOrder(1));
//       // int result = Order.creates(orders);
//        orders =  orders.stream().filter(o->o!=null).collect(Collectors.toList());
////        // 交易总金额
//        trade.payment = orders.stream().map(o -> o.totalAmount).reduce(0,Integer::sum).intValue();
////        // 交易总邮费
//        trade.shippingFee = orders.stream().map(o -> o.shippingFee).reduce(Integer::sum).get();
//                trade = trade.createWithOrders(orders);
//        System.out.println(JsonUtil.toJson(trade));
    }
    
    @Test
    public void test_findPage(){
        TradeSearchVo vo =  new TradeSearchVo();
        vo.pageNo = 1;
        vo.pageSize = 10;
        vo.status = TradeStatus.TRADE_AUDITING;
        List<Trade> trades = Trade.findListWithOrdersByVo(vo);
        System.out.println(JsonUtil.toJson(trades));
    }
    
    @Test
    public void test_createId(){
        double profit = new BigDecimal(499 - 360).divide(BigDecimal.valueOf(499), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
       // int   f1   =   b.setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        System.out.println(profit);
    }
    
    @Test
    public void test_findTradeOrderToExcel() throws Exception{
        Trade  trade =  Trade.findWithOrdersById(1609197662069760L);
        if(trade!=null){
            String[] cnHead = OrderVo.parseHeadMap(ImmutableList.of("id","discount","contact","goodsFee"));
            List<Map<Integer, Object>>  results = OrderVo.orderVosToExcelData(trade.orders,ImmutableList.of("id","discount","contact","goodsFee")).stream().filter(m->m!=null).collect(Collectors.toList());
            
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            OutputStream out = new FileOutputStream("/Users/Calm/Downloads/181609128678797312xxxxxxxxxxxxxxxx.xls");
            if(TSFileUtil.exportExcel(byteOut, cnHead, results, "ceshi")){
                System.out.println("导出成功");
                out.write(byteOut.toByteArray());
            }
           
        }
    }
    
    @Test
    public void test_cancelTrade(){
        Trade  trade =  Trade.findWithOrdersById(181609128678797312L);
        if(trade.cancelBySystem()){
            System.out.println("取消成功");
        }
    }
    
    /**
     * 
     * TODO Comment.
     *
     * @since  v1.0
     * @author Calm
     * @created 2016年9月12日 下午5:51:11
     */
    @Test
    public void test_parseProvince(){
        // 解析出省份id
        List<Region> list = Region.findByParentIdWithCache(1);
        String province = "四川";
        int provinceId = 0;
        for(Region region : list){
            if(region.name.indexOf(province)!=-1||province.indexOf(region.name)!=-1){
                provinceId = region.id;
                break;
            }
        }
        System.out.println(provinceId);
    }

}
