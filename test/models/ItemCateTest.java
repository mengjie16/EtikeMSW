package models;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import sun.awt.image.VolatileSurfaceManager;
import vos.CateChangeVo;
import vos.CateSearchVo;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.RegexUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * 宝贝类目单元测试
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月28日 下午12:34:39
 */
public class ItemCateTest extends UnitTest {
	
	@Test
	public void test_create() {
		
		ItemCate iCate1 = new ItemCate();
		iCate1.name = "现货专区";
		iCate1.parentCid = 0;
		iCate1.isParent = true;
		iCate1.ordinal = 1;
		
		ItemCate iCate2 = new ItemCate();
		iCate2.name = "洗护用品";
		iCate2.parentCid = 0;
		iCate2.isParent = true;
		iCate2.ordinal = 1;
		
		ItemCate iCate3 = new ItemCate();
		iCate3.name = "儿童玩具";
		iCate3.parentCid = 0;
		iCate3.isParent = true;
		iCate3.ordinal = 1;
		
		ItemCate.batchSave(ImmutableList.of(iCate1,iCate2,iCate3));
		
	}
	
	@Test
	public void test_find(){
		List<ItemCate> list = ItemCate.findByKeyWord("备'");
		MixHelper.print(JsonUtil.toJson(list));
	}
	
	@Test
	public void test_find_map(){
		List<ItemCate> list = ItemCate.findByKeyWord("必备");
		List<Map> listMap = Lists.transform(list, new Function<ItemCate,Map>(){
			@Override
			public Map apply(ItemCate obj) {
				// TODO Auto-generated method stub
				if(obj!=null){
					return ImmutableMap.of("id",obj.id,"name",obj.name);
				}
				return null;
			}
		});
		MixHelper.print(JsonUtil.toJson(listMap));
	}
	
	@Test
	public void test_reg(){
		boolean result = RegexUtils.isMatch("备 '", "^[a-zA-Z0-9_\u4e00-\u9fa5(\\S|\\s)*]+$");
		System.out.println(result);
	}
	
	@Test
	public void saveCurrent(){
		ItemCate iCate = new ItemCate();
		iCate.name = "测试类目";
		iCate.parentCid = 0;
		iCate.isParent = true;
		iCate.ordinal = 99;
		iCate.saveCurrent();
		System.out.println(iCate.id);
		
	}
	
	@Test
	public void otherTest(){
		List<CateChangeVo> vol = Lists.newArrayList();
		// 逻辑处理后列表
		List<CateChangeVo> copyVol = Lists.newArrayList();
		for(CateChangeVo catecVo : vol){
			// 有需求会保存
			if(Objects.equal(catecVo.changeType, "del")){
				ItemCate.delete(catecVo.id);
			}
			// 无论如何更新或保存一次
			ItemCate cateObj = new ItemCate();
			cateObj.name = catecVo.name;
			cateObj.id = catecVo.id;
			cateObj.saveCurrent();
			catecVo.id = cateObj.id;
			copyVol.add(catecVo);
		}
		// 查询本级类目列表(此列表是降序排序的)
		List<ItemCate> currCates = ItemCate.findList(0);
		int startIndex = currCates.size()+1;
		int endIndex = 1;
		List<Long> ids = Lists.newArrayList();
		// 已经处理过的
		List<Long> cIds = Lists.newArrayList();
		for(CateChangeVo ic : copyVol){
			// 处理置顶
			if(Objects.equal(ic.changeType, "top")){
				ItemCate cateObj = new ItemCate();
				cateObj.id = ic.id;
				cateObj.ordinal = startIndex;
				cateObj.updateOrdinal();
				cIds.add(cateObj.id);
				startIndex--;
			}
			// 处理置底
			else if(Objects.equal(ic.changeType, "bottom")){
				ItemCate cateObj = new ItemCate();
				cateObj.id = ic.id;
				cateObj.ordinal = endIndex;
				cateObj.updateOrdinal();
				cIds.add(cateObj.id);
				endIndex++;
			}
			else{
				ids.add(ic.id);
			}
		}
		
		// 处理剩余正常剩下排序
		for(int i =0; i<currCates.size();i++){
			
			ItemCate nCate = currCates.get(i);
			if(cIds.contains(nCate.id)){
				continue;
			}
			if(ids.contains(nCate.id)){
				// 到了开始排序
				if(nCate.id==ids.get(0)){
					nCate.ordinal = startIndex;
					nCate.updateOrdinal();
					startIndex--;
					ids.remove(0);
					for(Long id : ids){
						ItemCate cate = new ItemCate();
						cate.id = id;
						cate.ordinal = startIndex;
						cate.updateOrdinal();
						startIndex--;
					}
				}
			}else{
				nCate.ordinal = startIndex;
				nCate.updateOrdinal();
				startIndex--;
			}
		}
		
	}
	
	@Test
	public void test_findList(){
		Page<ItemCate> list = ItemCate.findPageByVo(new CateSearchVo());
		System.out.println(list.items.size());
		
	}
	
	@Test
	public void test_findAllAliasCate(){
		List<ItemCate> itemCates = ItemCate.findParentsCateWithSort(17L);
		for(ItemCate ic :itemCates){
			System.out.println(ic.name);
		}
	
		
	}
	
	@Test
	public void test_findSubCates(){
	    List<ItemCate> cates = ItemCate.findCatesWithChildrenByLevel(1);
	    
	    System.out.println(JsonUtil.toJson(cates));
	}
}
