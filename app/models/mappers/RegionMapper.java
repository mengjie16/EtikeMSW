package models.mappers;

import java.util.List;

import models.Region;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 地区数据操纵
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年4月28日 上午12:14:14
 */
public interface RegionMapper {

	@Select("select * from " + Region.TABLE_NAME + " where parent_id = #{id} and id!=990000")
	List<Region> selectByParentId(int id);
	
	@Select("select * from " + Region.TABLE_NAME + " where name like '%${keyWord}%' and (type = 1 or type= 2) order by name desc")
	List<Region> selectRootBykeyWord(@Param("keyWord") String keyWord);

	@Insert("insert into " + Region.TABLE_NAME + " values(#{id},#{name},#{parent_id},#{type},#{zip})")
    void insert(Region region);
}
