package vos;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import models.Brand;
import models.Cart;

public class CartVo {
    public long id;
    
    public int retailerId;

    public long cartId;
    
    /* 品牌信息 */
    public Brand brand;
    
    /* 购物车价格 */
    @Transient
    public int cartPrice;
 
    /* 商品规格 */
    public String skuColor;
    
    public int skuQuantity;
    
    /* 购物车数量(购物车功能会用到) */
    public int cartCount;
    
    @Min(0)
    public int retailPrice;

    public String title;    
    
    public String picUrl;
    
    public long brandId;
    
    public long itemId;
    
    
    public Cart parseTocart() {
        Cart cart = new Cart();
        cart.id = this.id;
        cart.title = this.title;
        cart.brandId = this.brandId;
        cart.picUrl = this.picUrl;
        cart.cartCount = this.cartCount;
        cart.itemId = this.itemId;
        cart.retailerId = this.retailerId;
        cart.skuColor = this.skuColor;
        cart.skuQuantity = this.skuQuantity;
        cart.cartPrice = this.cartPrice;
        cart.retailPrice = this.retailPrice;
        // 价格处理(小数保留2位，并转换为分)
        cart.retailPrice = new BigDecimal(this.retailPrice).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        return cart;
    }



    /**
     * 转换vo
     *
     * @param cart
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 下午5:31:00
     */
    public static CartVo valueOfCart(Cart cart) {
        if (cart == null)
            return null;
        CartVo cartVo = new CartVo();
        cartVo.id = cart.id;
        cartVo.title = cart.title;
        cartVo.picUrl = cart.picUrl;
        cartVo.brand = Brand.findById(cart.brandId);
        cartVo.retailPrice = cart.retailPrice;
        cartVo.cartCount = cart.cartCount;
        cartVo.itemId = cart.itemId;
        cartVo.retailerId = cart.retailerId;
        cartVo.skuColor = cart.skuColor;
        cartVo.skuQuantity = cart.skuQuantity;
        cartVo.cartPrice = cart.cartPrice;
        return cartVo;
    }

    /**
     * 转换vo
     *
     * @param carts
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 下午5:31:19
     */
    public static List<CartVo> valueOfcartList(List<Cart> carts) {
        if (MixHelper.isEmpty(carts)) {
            return Lists.newArrayList();
        }
        List<CartVo> ivs = Lists.transform(carts, new Function<Cart, CartVo>() {

            @Override
            public CartVo apply(Cart arg0) {
                return CartVo.valueOfCart(arg0);
            }

        });
        return ivs;
    }
}
