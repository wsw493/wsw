/*   
\ * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudMenuDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudMenuSearchDto;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudSystemFunctionDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:29:40
 * @Description History <author> <time> <desc>
 */
@Repository("cloudMenuDao")
public class CloudMenuDaoImpl extends SimpleHibernateRepository<CloudMenu, String> implements ICloudMenuDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JsonMapper jsonMapper = new JsonMapper();
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;
	@Resource
	private ICloudSystemDao cloudSystemDao;
	@Resource
	private ICloudFunctionDao cloudFunctionDao;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(super.getPersistentClass());
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	/**
	 * 重写。 实现：nodeCode的写入。nodeCode一级为2个整数位，即一级最多含99个记录。
	 */
	@Override
	public <S extends CloudMenu> S save(S entity) {
		CloudMenu parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("systemId", Operator.EQ, entity.getSystemId()));
			List<CloudMenu> siblingList = super.findListByFilter(filterList, null);
			int siblingListSize = 0;
			if (CollectionUtils.isNotEmpty(siblingList)) {
				siblingListSize = siblingList.size();
			}

			entity.setNodeCode(StringUtils.EMPTY + new DecimalFormat("00").format(siblingListSize + 1));
		} else {
			parent.setChildSerialNumer(parent.getChildSerialNumer() + 1);
			super.update(parent);

			entity.setNodeCode(parent.getNodeCode() + new DecimalFormat("00").format(parent.getChildSerialNumer()));
		}

		entity.setChildSerialNumer(0);
		CloudMenu _entity = super.save(entity);
		// 同步菜单
		syncRedisMenu(_entity.getSystemId());
		return (S) _entity;
	}

	@Override
	public CloudMenu update(CloudMenu entity) {
		CloudMenu _entity = super.update(entity);
		// 同步菜单
		syncRedisMenu(_entity.getSystemId());
		return _entity;
	}

	@Override
	public void delete(CloudMenu entity) {
		super.delete(entity);
		// 同步菜单
		syncRedisMenu(entity.getSystemId());
	}

	private void syncRedisMenu(String systemId) {
		if (StringUtil.isNullOrEmpty(systemId)) {
			return;
		}
		CloudSystem sys = cloudSystemDao.findOne(systemId);
		if (null == sys) {
			return;
		}
		String tenantId = ManagementConstant.CLOUD_TENANT_ID;
		if (!StringUtil.isNullOrEmpty(sys.getTenantId())) {
			tenantId = sys.getTenantId();
		}
		syncMenuByTenant(tenantId);
	}

	private void syncMenuByTenant(String tenantId) {
		// 同步系统菜单
		long t0 = System.currentTimeMillis();

		long t1 = System.currentTimeMillis();
		// 根据租户id获取所有系统信息
		Map<String, CloudSystem> system_map = getSystemMap(tenantId);
		long t_system = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 根据系统id获取所有菜单
		List<CloudMenu> menu_list = getMenuList(Lists.newArrayList(system_map.keySet()));
		long t_menu = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 获取功能信息
		Map<String, CloudSystemFunctionDto> function_map = getFunctionMap(menu_list);
		long t_function = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 遍历菜单，组合新的数据 key:租户id_系统编号,value:[菜单]
		Map<String, List<MenuTreeDto>> system_menu_map = formatMenuInfo(menu_list, system_map, function_map);
		long t_format = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 为每个租户管理员添加管理员菜单
		addTenantMenu(tenantId, system_menu_map);
		long t_tenant_menu = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 更新缓存数据
		syncSystemMenu(tenantId, system_menu_map);
		long t_save = System.currentTimeMillis() - t1;

		// 统计菜单数量
		Integer log_menuNum = 0;
		for (String set_key : system_menu_map.keySet()) {
			log_menuNum = log_menuNum + system_menu_map.get(set_key).size();
		}
		logger.error(String.format(
				"[同步redis,系统完整菜单,菜单变动]，总耗时：%sms，系统数量：%s，菜单数量：%s，获取所有系统耗时：%sms，获取所有菜单耗时：%sms，获取对应功能耗时：%sms，封装菜单耗时：%sms，添加租户管理员菜单耗时：%sms，存入缓存耗时：%sms",
				(System.currentTimeMillis() - t0), system_menu_map.keySet().size(), log_menuNum, t_system, t_menu,
				t_function, t_format, t_tenant_menu, t_save));
	}

	/**
	 * @Title: getSystemMap @Description: 根据租户id获取所有系统信息 @return
	 *         Map<String,CloudSystem> @throws
	 */
	private Map<String, CloudSystem> getSystemMap(String tenantId) {
		Map<String, CloudSystem> returnValue = Maps.newHashMap();
		if (StringUtil.isNullOrEmpty(tenantId)) {// 無租戶信息，返回空
			return returnValue;
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		List<CloudSystem> system_list = cloudSystemDao.findListByFilter(searchFilters, null);

		if (ManagementConstant.CLOUD_TENANT_ID.equals(tenantId)) {// 若包含云平台租户id，则添加云平台租户系统
			searchFilters.clear();
			searchFilters.add(new SearchFilter("tenantId", Operator.NULL, ManagementConstant.CLOUD_TENANT_ID));
			system_list.addAll(cloudSystemDao.findListByFilter(searchFilters, null));
		}
		if (CollectionUtils.isEmpty(system_list)) {// 未查到数据，返回空
			return returnValue;
		}
		for (CloudSystem entity : system_list) {// key:系统id,value:系统信息
			returnValue.put(entity.getId(), entity);
		}
		return returnValue;
	}

	/**
	 * @Title: getMenuList @Description: 根据系统id获取所有菜单 @return
	 *         List<CloudMenu> @throws
	 */
	private List<CloudMenu> getMenuList(List<String> systemIds) {
		List<CloudMenu> returnValue = Lists.newArrayList();
		if (CollectionUtils.isEmpty(systemIds)) {// 無系統信息，返回空
			return returnValue;
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		Sort sort = new Sort(Direction.ASC, "orderIndex");
		searchFilters.add(
				new SearchFilter("systemId", Operator.IN, (String[]) systemIds.toArray(new String[systemIds.size()])));
		// 未隐藏的菜单
		searchFilters.add(new SearchFilter("isHidden", Operator.EQ, 0));
		return this.findListByFilter(searchFilters, sort);
	}

	/**
	 * @Title: getTenantMap @Description: 获取功能信息 @return
	 *         Map<String,Tenant> @throws
	 */
	private Map<String, CloudSystemFunctionDto> getFunctionMap(List<CloudMenu> menu_list) {
		Map<String, CloudSystemFunctionDto> returnValue = Maps.newHashMap();
		if (CollectionUtils.isEmpty(menu_list)) {
			return returnValue;
		}
		List<String> functionId_list = Lists.newArrayList();
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getFunctionId())) {
				continue;
			}
			if (functionId_list.contains(entity.getFunctionId())) {
				continue;
			}
			functionId_list.add(entity.getFunctionId());
		}

		if (CollectionUtils.isEmpty(functionId_list)) {
			return returnValue;
		}
		List<CloudSystemFunctionDto> function_list = (List<CloudSystemFunctionDto>) cloudFunctionDao
				.getFunctionsByIds(StringUtils.join(functionId_list, ","));
		if (CollectionUtils.isEmpty(function_list)) {
			return returnValue;
		}
		for (CloudSystemFunctionDto entity : function_list) {// key:功能id,value:功能信息
			returnValue.put(entity.getId(), entity);
		}
		return returnValue;
	}

	/**
	 * @Title: formatMenuInfo @Description: 遍历菜单，组合新的数据
	 *         key:租户id_系统编号,value:[菜单] @return
	 *         Map<String,List<CloudMenu>> @throws
	 */
	private Map<String, List<MenuTreeDto>> formatMenuInfo(List<CloudMenu> menu_list,
			Map<String, CloudSystem> system_map, Map<String, CloudSystemFunctionDto> function_map) {
		Map<String, List<MenuTreeDto>> returnValue = Maps.newHashMap();
		if (CollectionUtils.isEmpty(menu_list) || MapUtils.isEmpty(system_map)) {// 無菜单信息或系统信息，返回空
			return returnValue;
		}
		List<MenuTreeDto> ts_menu_list = null;
		CloudSystem sys = null;
		String key = null;
		String tenantId = null;
		MenuTreeDto dto = null;
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getSystemId())) {// 菜单未绑定系统
				continue;
			}
			if (!system_map.containsKey(entity.getSystemId())) {// 无指定系统信息
				continue;
			}
			sys = system_map.get(entity.getSystemId());
			tenantId = sys.getTenantId();
			if (StringUtil.isNullOrEmpty(tenantId)) {// 若无租户id，则为云平台租户id
				tenantId = ManagementConstant.CLOUD_TENANT_ID;
			}
			key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
					+ ManagementConstant.REDIS_SEPARATOR + sys.getSystemCode();
			if (!returnValue.containsKey(key)) {// 未找到key，则新建
				ts_menu_list = Lists.newArrayList();
			} else {
				ts_menu_list = returnValue.get(key);
			}
			dto = new MenuTreeDto().transfer(entity);
			if (!StringUtil.isNullOrEmpty(dto.getFunctionId()) && function_map.containsKey(dto.getFunctionId())) {
				// 菜单uri
				dto.setUri(function_map.get(dto.getFunctionId()).getUrl());
			}
			if (!StringUtil.isNullOrEmpty(entity.getNodeCode())) {
				// 树形层级，根节点为0
				dto.setLevel(entity.getNodeCode().length() / 2);
			}
			ts_menu_list.add(dto);
			returnValue.put(key, ts_menu_list);
		}
		return returnValue;
	}

	/**
	 * @Title: addTenantMenu @Description: //为每个租户管理员添加管理员菜单 @return
	 *         void @throws
	 */
	private void addTenantMenu(String tenantId, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return;
		}
		String roleCode = ManagementConstant.TENANT_ROOT_ROLE;

		List<CloudSystemDto> sys_list = cloudSystemDao.getCloudSystemByRoleCode(roleCode);
		if (CollectionUtils.isEmpty(sys_list)) {
			return;
		}
		String sys_id = sys_list.get(0).getId();
		String sys_code = sys_list.get(0).getSystemCode();
		// 根据系统id获取所有菜单
		List<CloudMenu> menu_list = getMenuList(Lists.newArrayList(sys_id));
		// 获取功能信息
		Map<String, CloudSystemFunctionDto> function_map = getFunctionMap(menu_list);
		List<MenuTreeDto> ts_menu_list = Lists.newArrayList();

		if (ManagementConstant.CLOUD_TENANT_ID.equals(tenantId)) {// 云平台租户不添加
			return;
		}
		String key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
				+ ManagementConstant.REDIS_SEPARATOR + sys_code;
		MenuTreeDto dto = null;
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getSystemId())) {// 菜单未绑定系统
				continue;
			}
			dto = new MenuTreeDto().transfer(entity);
			if (!StringUtil.isNullOrEmpty(dto.getFunctionId()) && function_map.containsKey(dto.getFunctionId())) {
				// 菜单uri
				dto.setUri(function_map.get(dto.getFunctionId()).getUrl());
			}
			if (!StringUtil.isNullOrEmpty(entity.getNodeCode())) {
				// 树形层级，根节点为0
				dto.setLevel(entity.getNodeCode().length() / 2);
			}
			ts_menu_list.add(dto);
		}
		system_menu_map.put(key, ts_menu_list);
	}

	/**
	 * @Title: syncSystemMenu @Description: 更新缓存数据 @return void @throws
	 */
	private void syncSystemMenu(String tenantId, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (StringUtil.isNullOrEmpty(tenantId)) {// 无租户信息，不同步
			return;
		}
		Set<String> mapKey_set = system_menu_map.keySet();
		Map<String, List<MenuTreeDto>> one_tenant_map = Maps.newHashMap();
		String base_key = ManagementConstant.REDIS_PRE_KEY_SYS_MENU + ManagementConstant.REDIS_SEPARATOR;
		// 获取缓存中关于系统菜单key的信息
		for (String key : mapKey_set) {
			if (key.indexOf(tenantId) != -1) {// 包含该租户信息
				one_tenant_map.put(key, system_menu_map.get(key));
			}
		}
		// 刪除原有数据
		List<String> rsm_list = centralCacheRedisService.getObject(base_key + tenantId, List.class);
		if (CollectionUtils.isNotEmpty(rsm_list)) {
			centralCacheRedisService.removeObjects(rsm_list);
		}
		if (MapUtils.isEmpty(one_tenant_map)) {// 无数据则继续，刪除key
			centralCacheRedisService.removeObject(base_key + tenantId);
			return;
		}
		// 更新缓存中关于系统菜单key的信息
		centralCacheRedisService.putObject(base_key + tenantId, Lists.newArrayList(one_tenant_map.keySet()));
		// 更新数据
		for (String key : one_tenant_map.keySet()) {
			centralCacheRedisService.putObject(key, jsonMapper.toJson(one_tenant_map.get(key)));
		}
	}

	@Override
	public Page<CloudMenuDto> findPage(Pageable pageable, CloudMenuSearchDto searchDto) {
		String systemId = searchDto.getSystemId(); // 系统id
		String parentId = searchDto.getParentId();
		String code = searchDto.getCode(); // 编码
		String name = searchDto.getName(); // 名称

		List<SearchFilter> sfList = new ArrayList<SearchFilter>();

		sfList.add(new SearchFilter("systemId", Operator.EQ, systemId));
		sfList.add(new SearchFilter("parentId", Operator.EQ, parentId));
		if (StringUtils.isNotBlank(code)) {
			sfList.add(new SearchFilter("code", Operator.LIKE, code));
		}

		if (StringUtils.isNotBlank(name)) {
			sfList.add(new SearchFilter("name", Operator.LIKE, name));
		}

		Page<CloudMenu> page = super.findPageByFilter(pageable, sfList);
		if (page == null) {
			return null;
		}

		// 将记录由CloudMenu类型转为CloudMenuDto类型
		List<CloudMenu> content = page.getContent();
		List<CloudMenuDto> dtoList = new ArrayList<CloudMenuDto>();
		if (CollectionUtils.isNotEmpty(content)) {
			CloudMenuDto dto = null;
			for (CloudMenu entity : content) {
				dto = new CloudMenuDto();
				BeanUtils.copyProperties(entity, dto);
				dtoList.add(dto);
			}
		}

		return new PageImpl<CloudMenuDto>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public List<CloudMenu> getMenuList(String systemId) {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();

		sfList.add(new SearchFilter("systemId", Operator.EQ, systemId));

		Sort sort = new Sort(Direction.DESC, "orderIndex");

		return super.findListByFilter(sfList, sort);
	}

	@Override
	public List<MenuTreeDto> getUserMenuList(String systemCode, String userId) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append(
				" select distinct code,name,description,id,parentId,photoIds,uri,isLeaf,level,orderIndex,isWelcomeMenu from ( ");

		// 第一部分，受控的未隐藏的菜单
		sql.append(
				" SELECT f.name,f.description,f.id,f.code,f.parentId,f.photoIds,1 isLeaf,LENGTH(f.nodeCode)/2 level,f.orderIndex,f.isWelcomeMenu,CONCAT(g.website,'/',e.uri) uri ");
		sql.append(
				"  from cloud_system a,cloud_role b,cloud_user_role c,cloud_function_role d,cloud_function e,cloud_menu f,cloud_system g ");
		sql.append(" where a.systemCode=? ");
		sql.append("   and a.id=b.systemId ");
		sql.append("   and b.id=c.roleId ");
		sql.append("   and c.userId=? ");
		sql.append("   and c.roleId=d.roleId ");
		sql.append("   and d.functionId=e.id ");
		sql.append("   and e.id=f.functionId ");
		sql.append("   and e.goalSystemId=g.id ");
		sql.append("   and f.isControlled=? ");
		sql.append("   and f.isHidden=? ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");
		sql.append("   and d.beenDeleted=? ");
		sql.append("   and e.beenDeleted=? ");
		sql.append("   and f.beenDeleted=? ");
		sql.append("   and g.beenDeleted=? ");

		sql.append(" UNION ALL ");

		// 第二部分，该系统下不受控且未隐藏的菜单
		sql.append(
				" SELECT a.name,a.description,a.id,a.code,a.parentId,a.photoIds,1 isLeaf,LENGTH(a.nodeCode)/2 level,a.orderIndex,a.isWelcomeMenu,CONCAT(d.website,'/',c.uri) uri ");
		sql.append(" from cloud_menu a,cloud_system b,cloud_function c,cloud_system d ");
		sql.append(" where a.systemId=b.id ");
		sql.append("   and b.systemCode=? ");
		sql.append("   and a.functionId=c.id ");
		sql.append("   and c.goalSystemId = d.id ");
		sql.append("   and a.isControlled=? ");
		sql.append("   and a.isHidden=? ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");
		sql.append("   and d.beenDeleted=? ");

		sql.append(" ) a order by level,orderIndex ");

		args.add(systemCode);
		args.add(userId);
		args.add(CloudMenu.CONTROLLED_YES);
		args.add(CloudMenu.HIDDEN_NOT);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		args.add(systemCode);
		args.add(CloudMenu.CONTROLLED_NOT);
		args.add(CloudMenu.HIDDEN_NOT);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(MenuTreeDto.class));
	}

	@Override
	public MenuTreeDto getMenuTreeDtoById(String id) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append(
				" select a.name,a.description,a.id,a.code,a.parentId,a.photoIds,'' uri,0 isLeaf,LENGTH(a.nodeCode)/2 level,a.isWelcomeMenu,a.orderIndex ");
		sql.append(" from cloud_menu a ");
		sql.append(" where a.id=? ");

		args.add(id);

		List<MenuTreeDto> list = jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(MenuTreeDto.class));

		return list.get(0);
	}

	@Override
	public List<CloudMenu> getMenusByFunctionId(String functionId) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("functionId", Operator.EQ, functionId));
		return super.findListByFilter(filterList, null);
	}

	@Override
	public List<CloudMenu> getMenusByParentId(String systemId, String parentId) {
		if (StringUtils.isEmpty(systemId) || StringUtils.isEmpty(parentId)) {
			return null;
		}

		String sql = "";
		if (parentId.equals("-1")) { // 第一层需要去掉预设的菜单组
			sql = "select * from cloud_menu t where t.beenDeleted=0 and t.systemId='" + systemId + "' and t.parentId='"
					+ parentId + "' and t.code<>'" + ManagementConstant.SYS_ROOT_MENU_GROUP + "'";
		} else {
			sql = "select * from cloud_menu t where t.beenDeleted=0 and t.systemId='" + systemId + "' and t.parentId='"
					+ parentId + "'";
		}

		return jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(CloudMenu.class));
	}
}
