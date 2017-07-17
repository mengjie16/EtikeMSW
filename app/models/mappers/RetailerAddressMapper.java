package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import models.RetailerAddress;
import models.SupplierSendLocationTemp;
import vos.AddressSearchVo;
import vos.AddressVo;


public interface RetailerAddressMapper {

    @Select("select * from " + RetailerAddress.TABLE_NAME + " where id=#{id}")
    RetailerAddress selectById(long id);

    @Select("select * from " + RetailerAddress.TABLE_NAME + " where ${field}=#{value} limit 1")
    RetailerAddress selectByField(@Param("field") String field, @Param("value") Object value);

    int insert(RetailerAddress address);

    void update(RetailerAddress address);

    void updateByVo(AddressVo vo);

    @Select("select count(id) from " + RetailerAddress.TABLE_NAME)
    int count();

    @Delete("delete from " + RetailerAddress.TABLE_NAME + " where id = #{id}")
    void deleteById(long id);

    int countTotalVo(AddressSearchVo vo);

    List<RetailerAddress> selectListByPage(AddressSearchVo vo);
    
    
    @Select("select * from " + RetailerAddress.TABLE_NAME + "  where retailer_id=#{retailerId} order by create_time desc")
    public List<RetailerAddress> selectList(@Param("retailerId") int retailerId);
}
