package models;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.collect.Lists;

import vos.ItemSearchResult;
import vos.ItemSearchVo;
import vos.ItemVo;

public class ItemTest extends UnitTest {

    @Test
    public void test() {
        List<Integer> ints = Lists.newArrayList();
        ints.add(18);
        ints.add(12);
        ints.add(13);
        int price = ints.stream().map(p -> p).min((r1, r2) -> {
            return Integer.compare(r1, r2);
        }).get();
        double p = new BigDecimal(11059).divide(BigDecimal.valueOf(100)).doubleValue();
        System.out.println(p);
    }

    @Test
    public void test_copy() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Item item = new Item();

        item.id = 0;
        item.title = "adsfas";
        ItemVo iv = new ItemVo();

        PropertyUtils.copyProperties(item, iv);
        System.out.println(iv.title);
    }

    @Test
    public void test_findAlisa() {
        Item item = Item.findById(2);
        List<Item> listItem = Item.findAlisaItemBySupplierId(item.supplierId);
        if (listItem != null) {
            MixHelper.print(listItem);
        }
    }

    @Test
    public void test_findById() {
        Item item = Item.findById(472);
        System.out.println(JsonUtil.toJson(item));
        double fee = item.itemLastFee(12);
        System.out.println(fee);
        // if(item!=null){
        // Map<Integer, Integer> shippFee = item.calculateFreightTempFee(2);
        // MixHelper.print(shippFee);
        // }
    }

    @Test
    public void test_checkLocation() {
        List<ItemLocation> locations = Lists.newArrayList();
        ItemLocation location1 = new ItemLocation();
        location1.province = "zhejiang";
        location1.city = "hangzhou";

        ItemLocation location2 = new ItemLocation();
        location2.province = "zhejiang";
        location2.city = "hangzhou";

        locations.add(location1);
        locations.add(location2);

        Item item = new Item();
        item.sendGoodLocations = locations;
        System.out.println(item.checkItemLocationRepeat());
    }

    @Test
    public void test_findByKw() {
        ItemSearchVo vo = new ItemSearchVo();
        vo.kw("");

        ItemSearchResult result = Item.findByVoPage(vo);
        System.out.println(JsonUtil.toJson(result));
    }

    @Test
    public void test_parseToPoint() {
        // List<Item> items = Item.findAllItem();
        // for(Item item : items){
        // if(item!=null){
        // item.parseFeePoint();
        // if(item.updateById()){
        // // System.out.println("item");
        // }else{
        // System.out.println("item,id="+item.id+",更新失败");
        // }
        // }
        // }
    }

}
