package models.mappers;

import java.util.List;

import models.ItemCate;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.CateSearchVo;

public interface ItemCateMapper {

	
	public List<ItemCate> selectByKeyWord(@Param("keyWord") String keyWord);

	@Select("select *  from " + ItemCate.TABLE_NAME + " where level=#{level} order by ordinal desc")
	public List<ItemCate> selectWithLevel(@Param("level") int level);

    @Select("select * from " + ItemCate.TABLE_NAME + "  where parent_cid=#{parentCid} order by ordinal desc")
    public List<ItemCate> selectSubCates(@Param("parentCid") long parentCid);
   
    @Select("select * from " + ItemCate.TABLE_NAME + " where id=#{id} limit 1")
    public ItemCate selectOne(long id);
    
    @Select("select count(id) from " + ItemCate.TABLE_NAME + " where name=#{name} limit 1")
    public int countName(@Param("name") String name);
    
    int updateIsParent(ItemCate cate);
    /**
     * 
     * 一直向上找，找到最顶级的0级类目
     *
     * @param cid
     * @return
     * @since  v0.5.1
     * @author tr0j4n
     * @created 2014-1-26 下午3:33:05
     */
    @Select("select *  from " + ItemCate.TABLE_NAME     + " where id=CATE_GRAND_FATHER(#{cid}) limit 1")
    public ItemCate selectAncestorCate(@Param("cid") long cid);
    
	//@Insert("insert into "+ItemCate.TABLE_NAME+" (id,name,parent_cid,is_parent,ordinal,level) values (#{id},#{name},#{parentCid},#{isParent},#{ordinal},#{level})")
    int insert(ItemCate cate);
	
	int updateOrdinal(ItemCate cate);
	
	int updateById(ItemCate cate);
	
	public void inserts(@Param("cats") List<ItemCate> cates);
	
	int countByVo(CateSearchVo vo);
	
	List<ItemCate> selectByVo(CateSearchVo vo);
	
	@Delete("delete from " + ItemCate.TABLE_NAME+ " where id=${id}")
    int deleteById(@Param("id") long id);
	
	@Select("select * from "+ItemCate.TABLE_NAME+" order by create_time desc")
	List<ItemCate> selectList();
}