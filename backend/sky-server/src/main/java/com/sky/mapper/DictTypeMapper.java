package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoSet;
import com.sky.dto.DictTypePageQueryDTO;
import com.sky.entity.DictType;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictTypeMapper {

    Page<DictType> getDictTypeList(DictTypePageQueryDTO dictTypePageQueryDTO);

    @Insert("insert into dict_type (name, code, description, status, sort, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{code}, #{description}, #{status}, #{sort}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoSet(OperationType.INSERT)
    int addDictType(DictType dictType);

    @AutoSet(OperationType.UPDATE)
    int updateDictType(DictType dictType);

    @Delete("delete from dict_type where id = #{id}")
    int delDictTypeById(Long id);

    @Select("select * from dict_type where id = #{id}")
    DictType getDictTypeById(Long id);

    @Select("select * from dict_type where code = #{code}")
    DictType getDictTypeByCode(String code);

    @Select("select count(1) from dict_type where name = #{name}")
    int getCountByName(String name);

    @Select("select count(1) from dict_type where code = #{code}")
    int getCountByCode(String code);

    @Select("select * from dict_type where status = 1 order by sort desc")
    List<DictType> listAllEnabled();
}
