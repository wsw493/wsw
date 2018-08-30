package com.vortex.cloud.ums.dataaccess.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudUserRoleDto;
import com.vortex.cloud.ums.dto.CloudUserRoleSearchDto;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 *  用户角色dao 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudUserRoleDao extends HibernateRepository<CloudUserRole, String> {

	Page<CloudUserRoleDto> findPageBySearchDto(Pageable pageable, CloudUserRoleSearchDto searchDto);
}
