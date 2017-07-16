package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import models.Address;
import vos.AddressSearchVo;
import vos.AddressVo;


public interface AddressMapper {

    @Select("select * from " + Address.TABLE_NAME + " where id=#{id}")
    Address selectById(long id);

    @Select("select * from " + Address.TABLE_NAME + " where ${field}=#{value} limit 1")
    Address selectByField(@Param("field") String field, @Param("value") Object value);

    int insert(Address address);

    void updateById(Address address);

    void updateByVo(AddressVo vo);

    @Select("select count(id) from " + Address.TABLE_NAME)
    int count();

    @Delete("delete from " + Address.TABLE_NAME + " where id = #{id}")
    void deleteById(long id);

    int countTotalVo(AddressSearchVo vo);

    List<Address> selectListByPage(AddressSearchVo vo);
}
