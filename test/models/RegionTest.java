package models;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.base.Strings;

public class RegionTest extends UnitTest {

	@Test
	public void test_findRootRegionByKeyword() {
	    List<Region> list = Region.findByParentIdWithCache(1);
        
        System.out.println(JsonUtil.toJson(list));
	}

}
