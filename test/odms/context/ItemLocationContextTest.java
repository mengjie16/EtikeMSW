package odms.context;

import static org.junit.Assert.*;

import java.util.List;
import java.util.function.Consumer;

import models.ItemLocation;
import models.Location;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.MixHelper;
import com.google.common.collect.ImmutableList;

public class ItemLocationContextTest extends UnitTest {

	@Test
	public void test_SaveLocation() {
		ItemLocationContext icontext = new ItemLocationContext();
		ItemLocation location = new ItemLocation();
		location.index = 1;
		location.itemId = 12345678999999L;
		location.address = "浦东新区";
		location.city = "上海";
		location.province = "上海";
		location.provinceId = 1;
		boolean result = icontext.save(location);
		System.out.println(result);
	}
	
	@Test
	public void test_batchSave(){
		//int index,long itemId,String country,int countryId,String province,int provinceId,String city,String address
//		List<ItemLocation> params = ImmutableList.of(
//				new ItemLocation(0,10000,"",0,"浙江省",330000,"杭州市","拱墅区某某某"),
//				new ItemLocation(0,10003,"",0,"上海",330000,"上海","浦东区某某某"),
//				new ItemLocation(1,10003,"",0,"广东省",440100,"广州市","拱墅区某某某"),
//				new ItemLocation(0,10002,"",0,"北京",110100,"北京市","东城区某某某"),
//				new ItemLocation(1,10000,"",0,"海南省",330000,"三亚市","拱墅区某某某"),
//				new ItemLocation(0,10001,"",0,"内蒙古自治区",150000,"呼和浩特市","新城区某某某"));
//		ItemLocationContext icontext = new ItemLocationContext();
//		boolean result = icontext.batchSaveIteamLocation(params);
//		System.out.println(result);
	}
	
	@Test
	public void test_deleteByItemId(){
		ItemLocationContext icontext = new ItemLocationContext();
		boolean result = icontext.deleteByItemId(10001L);
		System.out.println(result);
	}
	
	@Test
	public void test_findLocationListByItemId(){
		ItemLocationContext icontext = new ItemLocationContext();
		List<ItemLocation> list = icontext.findByItemId(10003L);
		if(!MixHelper.isEmpty(list)){
			list.forEach(new Consumer<ItemLocation>(){

				@Override
				public void accept(ItemLocation arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0._id);
				}
				
			});
		}
	}

}
