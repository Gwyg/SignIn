package com.huang.mapper;

import com.huang.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    void insertUser(User user);

    @Update("update user set session = #{session} where unicode = #{unicode}")
    void updatesession(String unicode,String session);
}
