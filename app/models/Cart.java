package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.apache.ibatis.session.SqlSession;

import com.aton.db.SessionFactory;

import models.mappers.CartMapper;
import models.mappers.FavoriteMapper;

public class Cart implements Serializable {

    public static final String TABLE_NAME = "cart";
    /** 自增主键 */
    public long id;
    
    public int retailerId;

    public long itemId;
    
    /* 品牌信息 */
    public long brandId;
    
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
          
    public static boolean save(Cart cart) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            CartMapper mapper = ss.getMapper(CartMapper.class);
            Cart existCart= mapper.selectByItemIdAndColor(cart.itemId, cart.skuColor);
            if(existCart != null){
                mapper.updateCount(cart.cartCount+existCart.cartCount, existCart.id);
            }else{
                mapper.insert(cart);
            }
            return true;
        }  finally {
            ss.close();
        }
        
    }
    
    public static boolean delete(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            CartMapper mapper = ss.getMapper(CartMapper.class);
            mapper.deleteById(id);
            return true;
        }  finally {
            ss.close();
        }
    }
    
    
    public static List<Cart> findList(long retailerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            CartMapper mapper = ss.getMapper(CartMapper.class);
            List<Cart> res = mapper.selectListByRetailer(retailerId);
            return res;
        }  finally {
            ss.close();
        }
       
    }
    
    public static Cart findById(long itemId, long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            CartMapper mapper = ss.getMapper(CartMapper.class);
            return mapper.selectById(itemId);
        }  finally {
            ss.close();
        }
    }
    
    
    public static boolean updateCount(int count, long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            CartMapper mapper = ss.getMapper(CartMapper.class);
            
            mapper.updateCount(count, id);
            return true;
        }  finally {
            ss.close();
        }
        
    }

    
    public int getCartPrice() {
        return cartPrice;
    }

    
    public void setCartPrice(int cartPrice) {
        this.cartPrice = cartPrice;
    }
    
    
    
    
    
    




}
