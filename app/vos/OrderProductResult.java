package vos;

import java.util.Date;
import java.util.List;
import java.util.Map;

import play.data.binding.As;
import utils.SearchEndDateBinder;

import com.aton.util.StringUtils;
import com.aton.vo.Page;


/**
 * 订单商品信息结果集
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月15日 下午10:05:39
 */
public class OrderProductResult {
    
    /**
     * 商品＋sku 的md5
     */
    public List<Map<String,String>> keyStrs;
    /**
     * 商品名称
     */
    public String name;
    public String nameMd5;
    /**
     * 商品sku集合
     */
    public List<String> skus;

    public OrderProductResult() {
    };

    public OrderProductResult(List<Map<String,String>> keyStrs,String name,String nameMd5) {
        this.keyStrs = keyStrs;
        this.name = name;
        this.nameMd5 = nameMd5;
    }

}