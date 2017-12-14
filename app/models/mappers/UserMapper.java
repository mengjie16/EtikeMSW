package models.mappers;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import models.User;
import vos.UserSearchVo;

public interface UserMapper {

    @Select("select * from " + User.TABLE_NAME + " where id=#{id}")
    User selectById(Long id);

    @Select("select * from " + User.TABLE_NAME + " where ${field}=#{value} limit 1")
    User selectByField(@Param("field") String field, @Param("value") Object value);

    @Select("select count(1) from " + User.TABLE_NAME + " where ${field}=#{value} and id!=${id}")
    int countFieldUsedByOthers(@Param("field") String field, @Param("value") Object value, @Param("id") long id);

    @Select("select * from " + User.TABLE_NAME + " where phone=#{phone} and id!=${id} limit 1")
    User checkUserExsit(@Param("phone") String phone, @Param("id") long id);

    @Insert("insert into " + User.TABLE_NAME
            + "(name,email,avatar,phone,password,salt,role,user_id,qq,weixin,is_auth,create_time,last_login_time) "
            + "values(#{name},#{email},#{avatar},#{phone},#{password},#{salt},#{role},#{userId},#{qq},#{weixin},#{isAuth},#{createTime},#{lastLoginTime})")
    void insert(User user);

    /**
     * 这个接口不会涉及到密码的修改
     */
    void updateById(User user);

    @Update("update user u set last_login_time = #{lastLoginTime} where u.id=#{id}")
    void updateLastLoginTimeById(@Param("lastLoginTime") Date lastLoginTime, @Param("id") long id);

    List<User> selectListByVo(UserSearchVo vo);

    @Select("select * from user u where u.name like '%${name}%'  order by u.create_time desc")
    List<User> selectListByNameFuzzy(@Param("name") String name);

    int countVo(UserSearchVo vo);

    @Update("update user u set u.password = #{password},u.salt = #{salt} where u.id=#{id}")
    void updatePassword(@Param("id") long id, @Param("password") String password, @Param("salt") String salt);

    @Delete("delete from user where id=#{id}")
    void delete(@Param("id") long id);

    int updateUserAuthById(User user);
    
    @Update("update user u set u.is_auth = #{auth} where u.id=#{id}")
    int authUser(@Param("id") long id, @Param("auth") boolean auth);
}
