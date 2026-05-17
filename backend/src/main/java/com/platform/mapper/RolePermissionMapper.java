package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
