package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoSet;
import com.sky.dto.DictDataPageQueryDTO;
import com.sky.entity.DictData;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictDataMapper {

    Page<DictData> getDictDataList(DictDataPageQueryDTO dictDataPageQueryDTO);

    @Insert("insert into dict_data (dict_type_id, label, value, is_default, status, sort, remark, create_time, update_time, create_user, update_user) " +
            "values (#{dictTypeId}, #{label}, #{value}, #{isDefault}, #{status}, #{sort}, #{remark}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoSet(OperationType.INSERT)
    int addDictData(DictData dictData);

    @AutoSet(OperationType.UPDATE)
    int updateDictData(DictData dictData);

    @Delete("delete from dict_data where id = #{id}")
    int delDictDataById(Long id);

    @Select("select * from dict_data where id = #{id}")
    DictData getDictDataById(Long id);

    @Select("select * from dict_data where dict_type_id = #{dictTypeId} order by sort desc")
    List<DictData> listByTypeId(Long dictTypeId);

    @Select("select count(1) from dict_data where dict_type_id = #{dictTypeId} and value = #{value}")
    int getCountByTypeIdAndValue(Long dictTypeId, String value);
}
