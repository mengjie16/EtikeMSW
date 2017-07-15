package models.mappers;

import java.util.List;

import models.Brand;
import models.Item;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import enums.ItemStatus;
import vos.FreightSearchVo;
import vos.ItemSearchVo;
import vos.UserSearchVo;

/**
 * 商品数据操纵
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年4月25日 上午12:39:22
 */
public interface ItemMapper {

    Item selectById(@Param("id") long id);
    
    Item selectBaseById(@Param("id") long id);
    
    @Select("select supplier_id from " + Item.TABLE_NAME+" where id = #{id}  limit 1")
    int selectSupplierIdById(long id);
    
    @Select("select count(id) from " + Item.TABLE_NAME+" where supplier_id = #{supplierId} and status = #{status}")
    int countBySupplierIdWithStatus(@Param("supplierId") int supplierId,@Param("status") ItemStatus status);
    
    @Select("select * from " + Item.TABLE_NAME + " where ${field}=#{value} limit 1")
    Item selectByField(@Param("field") String field, @Param("value") Object value);
    
    @Select("select count(id) from " + Item.TABLE_NAME + " where brand_id=#{brandId}")
    int countByBrandId(@Param("brandId") long brandId);
    
    @Select("select count(id) from " + Item.TABLE_NAME + " where freight_temp=#{freightTemp}")
    int countByFreigthTempKey(@Param("freightTemp") String freightTemp);
    
    int insert(Item item);
    
    void updateById(Item item);
    
    @Select("select count(id) from " + Item.TABLE_NAME)
    int count();
    
    @Delete("delete from "+Item.TABLE_NAME+" where id = #{id}")
    void deleteById(long id);
    
    List<Item> selectListByPage(ItemSearchVo vo);
    
    List<Item> selectList(ItemSearchVo vo);
    
    List<Item> selectListAll();
    
    List<Item> selectListOnline();
    
    List<Item> selectListBySupplier(@Param("supplierId") int supplierId);
    // 根据条件查询商品运费模板
    List<Long> selectFreightTempByVo(FreightSearchVo vo);
    
    int countTotalVo(ItemSearchVo vo);
    
    int updateStatusByIds(@Param("ids") List<Long> ids,@Param("status") ItemStatus status);
    
    // 商品列表页搜索使用
    // 根据ids 过滤总数中额外的信息，并分页
    List<Item> selectListVoByIds(ItemSearchVo vo);
    // 根据品牌关键字查询数据(品牌id集合)
    List<Brand> selectbIdsByKeyWordInIds(@Param("keyWords") List<String> keyWords);
    // 根据商品关键字查询数据
    List<Item> selectIdsInAllIdsOrBids(@Param("keyWords") List<String> keyWords,@Param("brandIds") List<Long> brandIds,@Param("itemIds") List<Long> itemIds);
    // 过滤基本条件后的商品id集合，除关键字过滤
    List<Long> selectListFilterByVo(ItemSearchVo vo);
    // ---------------  
    
    List<Item> selectListAllByCreateTime();
}
