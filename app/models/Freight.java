package models;

import java.util.List;
import java.util.Map;

import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.data.binding.As;

/**
 * 模版运费配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月9日 下午4:58:05
 */
public class Freight implements java.io.Serializable {

    @As(",")
    public List<Integer> provinces;
    // 静态模版价格(单位：分)
    public double price;
    // 动态首重
    public double firstWeight;
    // 动态首重价格(单位：分)
    public double fwPrice;
    // 动态续重
    public double addedWeight;
    // 动态续重价格(单位：分)
    public double awPrice;

    public Map toMap() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("provinces", provinces);
        params.put("price", price * 100);
        params.put("firstWeight", firstWeight);
        params.put("fwPrice", (int) (fwPrice * 100));
        params.put("addedWeight", addedWeight);
        params.put("awPrice", (int) (awPrice * 100));
        return params;
    }
}
