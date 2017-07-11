package models.mappers;

import java.util.List;

import models.WechatUser;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.WechatUserVo;

public interface WechatUserMapper {

    @Select("select * from " + WechatUser.TABLE_NAME + " where id=#{id}")
    WechatUser selectById(Long id);
    
    @Select("select * from " + WechatUser.TABLE_NAME + " where open_id=#{openId} limit 1")
    WechatUser selectByOpenId(@Param("openId") String openId);
    
    @Select("select * from " + WechatUser.TABLE_NAME + " where ${field}=#{value} limit 1")
    WechatUser selectByField(@Param("field") String field, @Param("value") Object value);

    @Insert("insert into " + WechatUser.TABLE_NAME + "(avatar,nick,open_id,unionid,auth_time,last_auth_time) "
        + "values(#{avatar},#{nick},#{openId},#{unionid},#{authTime},#{lastAuthTime})")
    void insert(WechatUser user);

    void updateById(WechatUser user);

    int countVo(WechatUserVo vo);

    List<WechatUser> selectListByVo(WechatUserVo vo);
    
    @Delete("delete from " + WechatUser.TABLE_NAME + " where id=#{id}")
    int delete(Long id);
    
    void updateLastAuthTimeBatch(@Param("ids") List<Long> ids);
    
    void updateAuthTimeBatch(@Param("ids") List<Long> ids);
}
