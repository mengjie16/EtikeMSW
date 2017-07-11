package models;

import static org.junit.Assert.*;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.BrandSearchVo;

import com.aton.test.UnitTest;
import com.aton.util.MixHelper;
import com.aton.vo.Page;

public class BrandTest extends UnitTest {
	
	private static final Logger log = LoggerFactory.getLogger(BrandTest.class);
	
	@Test
	public void test_create() {
		Brand brand = new Brand();
		brand.name = "贝因美";
		brand.secondaryName = "Beingmate";
		brand.company = "贝因美婴童食品公司";
		brand.holder = "贝因美";
		brand.note = "杭州的";
		brand.save();
	}
	
	@Test
	public void test_findByKeyWord(){
		List<Brand> list = Brand.findBykeyWord("贝因");
		MixHelper.print(list);
	}
	
	@Test
	public void test_findPageBrand(){
		BrandSearchVo vo = new BrandSearchVo();
		vo.name = "贝";
		vo.pageNo = 1;
		vo.pageSize = 5;
		Page<Brand> resultPage = Brand.findBrandPage(vo);
		log.info("totalCount = {}",resultPage.totalCount);
		resultPage.items.forEach(new Consumer<Brand>(){
			@Override
			public void accept(Brand obj) {
				log.info("totalCount = {}",obj.name);
			}
		});
	}
}
