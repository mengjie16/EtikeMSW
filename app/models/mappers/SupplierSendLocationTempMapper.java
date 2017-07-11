package models.mappers;

import java.util.List;

import models.SupplierSendLocationTemp;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SupplierSendLocationTempMapper {
    
    void insert(SupplierSendLocationTemp locationTemp);
    
    @Select("select * from " + SupplierSendLocationTemp.TABLE_NAME + "  where supplier_id=#{supplierId} order by create_time desc")
    public List<SupplierSendLocationTemp> selectList(@Param("supplierId") int supplierId);
  
	@Delete("delete from " + SupplierSendLocationTemp.TABLE_NAME+ " where id=${id}")
    int deleteById(@Param("id") long id);
	
	void updateById(SupplierSendLocationTemp locationTemp);
}