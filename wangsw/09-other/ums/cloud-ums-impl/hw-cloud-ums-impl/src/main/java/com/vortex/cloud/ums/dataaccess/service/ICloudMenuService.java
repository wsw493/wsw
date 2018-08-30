/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudMenuSearchDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:33:34
 * @Description History <author> <time> <desc>
 */
public interface ICloudMenuService extends PagingAndSortingService<CloudMenu, String> {
	/**
	 * 校验编码是系统唯一
	 * 
	 * @param systemId
	 * @param id
	 *            菜单Id，修改记录时才存在
	 * @param code
	 * @return
	 */
	boolean isCodeExistForSystem(String systemId, String id, String code);

	/**
	 * 保存
	 * 
	 * @param dto
	 * @return
	 */
	public CloudMenu saveBusinessSystem(CloudMenuDto dto);

	/**
	 * 根据指定的id，获取记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudMenuDto getById(String id);

	void updateForBusinessSystem(CloudMenuDto dto);

	/**
	 * 获取指定系统下的菜单列表分页
	 * 
	 * @param pageable
	 * @param searchDto
	 * @return
	 */
	Page<CloudMenuDto> findPage(Pageable pageable, CloudMenuSearchDto searchDto);

	/**
	 * 获取指定系统下的菜单列表
	 * 
	 * @param systemId
	 * @return
	 */
	List<CloudMenu> getMenuList(String systemId);

	/**
	 * 删除1~N条记录
	 * 
	 * @param idList
	 */
	void deletes(List<String> idList);

	/**
	 * 得到某人在某系统中的菜单树形结构
	 * 
	 * @param systemCode
	 *            业务系统编码
	 * @param userId
	 *            用户id
	 * @return
	 * @throws Exception
	 */
	public MenuTreeDto getMenuTree(String systemCode, String userId) throws Exception;
}
