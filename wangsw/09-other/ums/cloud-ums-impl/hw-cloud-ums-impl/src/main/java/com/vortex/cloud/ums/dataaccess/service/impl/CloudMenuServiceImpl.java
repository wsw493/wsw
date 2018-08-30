/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudMenuDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudMenuSearchDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.tree.CloudMenuTree;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;






/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:34:27
 * @Description History <author> <time> <desc>
 */
@Service("cloudMenuService")
@Transactional
public class CloudMenuServiceImpl extends SimplePagingAndSortingService<CloudMenu, String> implements ICloudMenuService {

	private static final Logger logger = LoggerFactory.getLogger(CloudMenuServiceImpl.class);
	private static final String ROOT_ID = "-1"; // 根节点id

	@Resource
	private ICloudMenuDao cloudMenuDao;

	@Resource
	private ICloudFunctionDao cloudFunctionDao;

	@Override
	public HibernateRepository<CloudMenu, String> getDaoImpl() {
		return cloudMenuDao;
	}

	/**
	 * 校验编码是否租户+业务系统唯一
	 * 
	 * @param systemId
	 * @param id
	 *            菜单Id，修改记录时才存在
	 * @param code
	 * @return
	 */
	@Override
	public boolean isCodeExistForSystem(String systemId, String id, String code) {
		SearchFilter filter = null;
		List<SearchFilter> searchFilters = Lists.newArrayList();

		filter = new SearchFilter("systemId", SearchFilter.Operator.EQ, systemId);
		searchFilters.add(filter);

		if (StringUtils.isNotBlank(id)) {
			filter = new SearchFilter("id", SearchFilter.Operator.NE, id.trim());
			searchFilters.add(filter);
		}

		filter = new SearchFilter("code", SearchFilter.Operator.EQ, code.trim());
		searchFilters.add(filter);

		// 查询列表
		List<CloudMenu> list = super.findListByFilter(searchFilters, null);

		if (CollectionUtils.isNotEmpty(list)) {
			return true;
		}

		return false;
	}

	@Override
	public CloudMenu saveBusinessSystem(CloudMenuDto dto) {
		this.validateOnSaveForBusinessSystem(dto);

		this.refreshWelcomePageForAdd(dto);

		CloudMenu entity = new CloudMenu();
		BeanUtils.copyProperties(dto, entity);

		return cloudMenuDao.save(entity);
	}

	private void validateOnSaveForBusinessSystem(CloudMenuDto dto) {
		if (StringUtils.isBlank(dto.getSystemId())) {
			throw new ServiceException("业务系统ID为空");
		}

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isCodeExistForSystem(dto.getSystemId(), dto.getId(), dto.getCode())) {
			throw new ServiceException("编号已存在！");
		}

		// 如果父节点存在绑定的功能，则不允许添加
		boolean isBound = this.isBoundFunction(dto.getParentId());
		if (isBound) {
			logger.error("validateOnUpdate(), 选择的父菜单已绑定功能！");
			throw new ServiceException("选择的父菜单已绑定功能！");
		}
	}

	private void validateForm(CloudMenuDto dto) {

		if (StringUtils.isBlank(dto.getCode())) {
			throw new ServiceException("编码为空");
		}

		if (StringUtils.isBlank(dto.getName())) {
			throw new ServiceException("名称为空");
		}

		if (dto.getOrderIndex() == null) {
			throw new ServiceException("排序号为空");
		}

		if (StringUtils.isBlank(dto.getParentId())) {
			throw new ServiceException("请选择父菜单");
		}

		if (dto.getIsHidden() == null) {
			throw new ServiceException("是否隐藏为空");
		}

		if (dto.getIsControlled() == null) {
			throw new ServiceException("是否受控制为空");
		}

		// 功能码不是必填的
	}

	/**
	 * 判断指定记录是否绑定了功能
	 * 
	 * @return
	 */
	private boolean isBoundFunction(String id) {
		if (CloudMenuTree.ROOT_NODE_ID.equals(id)) {
			return false;
		}

		CloudMenu menu = cloudMenuDao.findOne(id);
		if (menu == null) {
			logger.error("isBoundFunction(), 根据id(" + id + ")未能找到菜单记录");
			throw new ServiceException("未能找到菜单记录");
		}

		boolean isBound = false;
		if (StringUtils.isBlank(menu.getFunctionId())) {
			isBound = false;
		} else {
			isBound = true;
		}
		return isBound;
	}

	private void refreshWelcomePageForAdd(CloudMenuDto dto) {
		Integer isWelcomeMenu = dto.getIsWelcomeMenu();
		if (!CloudMenu.IS_WELCOME_MENU_YES.equals(isWelcomeMenu)) {
			return;
		}

		this.removeWelcomePageOfSystem(dto);
	}

	private void removeWelcomePageOfSystem(CloudMenuDto dto) {
		String SystemId = dto.getSystemId();
		if (StringUtils.isBlank(SystemId)) {
			return;
		}

		CloudMenu welcomeMenu = this.getWelcomePageOfSystem(SystemId);
		if (welcomeMenu == null) {
			return;
		}

		this.removeWelcomePage(welcomeMenu);
	}

	/**
	 * 保证一个系统只有一个主页
	 * 
	 * @param dto
	 */
	private void removeWelcomePage(CloudMenu welcomeMenu) {
		if (welcomeMenu == null) {
			return;
		}

		welcomeMenu.setIsWelcomeMenu(CloudMenu.IS_WELCOME_MENU_NOT);
		super.update(welcomeMenu);
	}

	private CloudMenu getWelcomePageOfSystem(String systemId) {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("systemId", Operator.EQ, systemId));
		sfList.add(new SearchFilter("isWelcomeMenu", Operator.EQ, CloudMenu.IS_WELCOME_MENU_YES));

		List<CloudMenu> list = super.findListByFilter(sfList, null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	@Override
	public CloudMenuDto getById(String id) {
		CloudMenu menu = cloudMenuDao.findOne(id);

		CloudMenuDto dto = new CloudMenuDto();
		BeanUtils.copyProperties(menu, dto);

		this.setParentName(dto);
		this.setFunctionName(dto);
		this.setLeafNode(dto);

		return dto;
	}

	private void setParentName(CloudMenuDto dto) {
		String parentName = null;
		String parentId = dto.getParentId();
		if (CloudMenuTree.ROOT_NODE_ID.equals(parentId)) {
			parentName = CloudMenuTree.ROOT_NODE_TEXT;
		} else {
			CloudMenu parent = cloudMenuDao.findOne(dto.getParentId());
			parentName = parent.getName();
		}
		dto.setParentName(parentName);
	}

	private void setFunctionName(CloudMenuDto dto) {
		String functionId = dto.getFunctionId();
		if (StringUtils.isBlank(functionId)) {
			return;
		}

		CloudFunction function = cloudFunctionDao.findOne(functionId);
		if(function == null) {
			return;
		}
		
		dto.setFunctionName(function.getName());
	}

	/**
	 * 设置是否为叶子节点，如果非叶子节点，更新时不允许选择function
	 * 
	 * @param dto
	 */
	private void setLeafNode(CloudMenuDto dto) {
		List<CloudMenu> list = this.getSonList(dto.getId());
		if (CollectionUtils.isEmpty(list)) {
			dto.setLeafNode(true);
		} else {
			dto.setLeafNode(false);
		}
	}

	/**
	 * 获取指定记录的儿子记录
	 * 
	 * @param id
	 */
	private List<CloudMenu> getSonList(String id) {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("parentId", Operator.EQ, id));
		List<CloudMenu> list = super.findListByFilter(sfList, null);

		return list;
	}

	@Override
	public void updateForBusinessSystem(CloudMenuDto dto) {
		// 入参数校验
		this.validateOnUpdateForBusinessSystem(dto);

		this.refreshWelcomePageForUpdate(dto);

		CloudMenu old = cloudMenuDao.findOne(dto.getId());
		BeanUtils.copyProperties(dto, old);
		cloudMenuDao.update(old);
	}

	private void validateOnUpdateForBusinessSystem(CloudMenuDto dto) {

		if (StringUtils.isBlank(dto.getSystemId())) {
			throw new ServiceException("业务系统ID为空");
		}

		if (StringUtils.isBlank(dto.getId())) {
			throw new ServiceException("ID为空");
		}

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isCodeExistForSystem(dto.getSystemId(), dto.getId(), dto.getCode())) {
			throw new ServiceException("编号已存在！");
		}

		// 如果父节点存在绑定的功能，则不允许添加
		boolean isBound = this.isBoundFunction(dto.getParentId());
		if (isBound) {
			logger.error("validateOnUpdate(), 选择的父菜单已绑定功能！");
			throw new ServiceException("选择的父菜单已绑定功能！");
		}

		// 如果存在子记录，则不允许绑定功能
		if (StringUtils.isNotBlank(dto.getFunctionId())) {
			List<CloudMenu> sonList = this.getSonList(dto.getId());
			if (CollectionUtils.isNotEmpty(sonList)) {
				logger.error("validateOnUpdate(), 存在子菜单，不允许绑定功能！");
				throw new ServiceException("存在子菜单，不允许绑定功能！");
			}
		}
	}

	private void refreshWelcomePageForUpdate(CloudMenuDto dto) {
		Integer isWelcomeMenu = dto.getIsWelcomeMenu();
		if (!CloudMenu.IS_WELCOME_MENU_YES.equals(isWelcomeMenu)) {
			return;
		}

		String systemId = dto.getSystemId();
		CloudMenu welcomeMenu = this.getWelcomePageOfSystem(systemId);
		if (welcomeMenu != null) {
			if (!dto.getId().equals(welcomeMenu.getId())) {
				this.removeWelcomePage(welcomeMenu);
			}
		}
	}

	@Override
	public Page<CloudMenuDto> findPage(Pageable pageable, CloudMenuSearchDto searchDto) {
		return cloudMenuDao.findPage(pageable, searchDto);
	}

	@Override
	public List<CloudMenu> getMenuList(String systemId) {
		return cloudMenuDao.getMenuList(systemId);
	}

	@Override
	public void deletes(List<String> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}

		for (String id : idList) {
			super.delete(id);
		}
	}

	@Override
	public MenuTreeDto getMenuTree(String systemCode, String userId) throws Exception {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemCode)) {
			logger.error("得到用户菜单列表时，传入的参数不足！");
			throw new ServiceException("得到用户菜单列表时，传入的参数不足！");
		}

		// 得到左右叶节点菜单
		List<MenuTreeDto> list = cloudMenuDao.getUserMenuList(systemCode, userId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		int maxLevel = 0; // nodeCode树形字段的最大长度，最深level的叶节点所在
		for (MenuTreeDto dto : list) {
			if (dto.getLevel() > maxLevel) {
				maxLevel = dto.getLevel();
			}
		}
		if (maxLevel == 0) { // 最大深度为0，直接返回空
			return null;
		}

		Map<String, List<MenuTreeDto>> treeMap = new LinkedHashMap<String, List<MenuTreeDto>>();

		// 组合树形结构
		this.doFil(list, maxLevel, treeMap);

		// 递归完毕后，返回根节点
		MenuTreeDto rst = new MenuTreeDto();
		rst.setName("根节点");
		rst.setDescription("");
		rst.setId(ROOT_ID);
		rst.setCode("");
		rst.setParentId("");
		rst.setPhotoIds("");
		rst.setLevel(0);
		rst.setIsLeaf(0);
		rst.setChildren(treeMap.get(ROOT_ID));

		return rst;
	}

	@SuppressWarnings("unchecked")
	private void doFil(List<MenuTreeDto> list, int level, Map<String, List<MenuTreeDto>> treeMap) {

		if (level == 0) {
			return;
		}

		// 得到当前层级的菜单列表
		Map<String, List<MenuTreeDto>> map = new LinkedHashMap<String, List<MenuTreeDto>>();
		List<MenuTreeDto> parentList = new ArrayList<MenuTreeDto>();
		for (MenuTreeDto dto : list) {
			if (dto.getLevel().intValue() == level) {
				MenuTreeDto tree = new MenuTreeDto();
				BeanUtils.copyProperties(dto, tree);

				tree.setChildren(treeMap.get(tree.getId())); // 将上次封装好的子节点列表设置好

				// 封装上层需要用到的treeMap
				if (map.get(tree.getParentId()) == null) {
					List<MenuTreeDto> mlist = new ArrayList<MenuTreeDto>();
					mlist.add(tree);
					map.put(tree.getParentId(), mlist);

					if (level != 1) {
						// map中未找到列表，代表是第一次用到该父节点，需要加载父节点到全体节点列表list中
						MenuTreeDto parent = cloudMenuDao.getMenuTreeDtoById(tree.getParentId());
						parentList.add(parent);
					}
				} else {
					map.get(tree.getParentId()).add(tree);
				}
			}
		}

		// 将父节点加入集合
		list.addAll(parentList);

		// 将map中list排序
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Collections.sort(map.get(key));
		}

		treeMap.clear();
		treeMap.putAll(map);

		// 级别调至上一层
		level--;

		// 递归调用
		this.doFil(list, level, treeMap);

		return;
	}
}
