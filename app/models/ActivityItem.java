package models;

import java.util.List;
import java.util.Map;

import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 活动页面商品
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年7月1日 下午12:13:10
 */
public class ActivityItem {
    public long itemId;
    public String mainTitle;
    public String subTitle;
    // 单位：分
    public int price;
    // 单位：分
    public int distPrice;
    public String itemImg;
    public String content;
    
    public Map toMap(){
        Map map = Maps.newHashMap();
        map.put("itemId", itemId);
        map.put("mainTitle", mainTitle);
        map.put("subTitle", subTitle);
        map.put("price", price*100);
        map.put("distPrice", distPrice*100);
        map.put("itemImg", itemImg);
        map.put("content", content);
        return map;
    }  
}
