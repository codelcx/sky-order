package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoSet;
import com.sky.dto.DinnerTablePageQueryDTO;
import com.sky.entity.DinnerTable;
import com.sky.enumeration.OperationType;
import com.sky.vo.DinnerTableVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DinnerTableMapper {

    Page<DinnerTableVO> getDinnerTableList(DinnerTablePageQueryDTO dto);

    @Insert("insert into dinner_table (table_number, capacity, status, qr_code_url, create_time, update_time, create_user, update_user) " +
            "values (#{tableNumber}, #{capacity}, #{status}, #{qrCodeUrl}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoSet(OperationType.INSERT)
    int addDinnerTable(DinnerTable dinnerTable);

    @Select("select * from dinner_table where id = #{id}")
    DinnerTable getDinnerTableById(Long id);

    @Select("select * from dinner_table where table_number = #{tableNumber}")
    DinnerTable getDinnerTableByTableNumber(Integer tableNumber);

    @Select("select count(1) from dinner_table where table_number = #{tableNumber}")
    int countByTableNumber(Integer tableNumber);

    @AutoSet(OperationType.UPDATE)
    int updateDinnerTable(DinnerTable dinnerTable);

    @Delete("delete from dinner_table where id = #{id}")
    int delDinnerTableById(Long id);
}
