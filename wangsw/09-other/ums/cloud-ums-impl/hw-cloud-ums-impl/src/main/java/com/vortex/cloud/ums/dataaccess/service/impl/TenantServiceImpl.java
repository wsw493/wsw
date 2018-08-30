package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDivision1Dao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDivisionService;
import com.vortex.cloud.ums.dataaccess.service.IDeflectService;
import com.vortex.cloud.ums.dataaccess.service.ITenantBusinessService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.dto.TenantUrlDto;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.support.TenantConstant;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;







@Transactional
@Service("tenantService")
public class TenantServiceImpl extends SimplePagingAndSortingService<Tenant, String> implements ITenantService {

	private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);

	@Resource
	private ITenantDao tenantDao;

	@Resource
	private ICloudDivision1Dao cloudDivision1Dao;

	@Resource
	private ITenantDivisionDao tenantDivisionDao;

	@Resource
	private ICloudDivisionService cloudDivisionService;

	@Resource
	private ITenantBusinessService tenantBusinessService;

	@Resource
	private IDeflectService deflectService;

	@Override
	public HibernateRepository<Tenant, String> getDaoImpl() {
		return tenantDao;
	}

	@Override
	public Tenant saveTenant(TenantDto tenantDto) {
		// 检查必要信息
		if (StringUtils.isEmpty(tenantDto.getTenantName())) {
			throw new ServiceException("请填写租户名！");
		}

		if (StringUtils.isEmpty(tenantDto.getUserName())) {
			throw new ServiceException("请填写用户名！");
		}

		if (StringUtils.isEmpty(tenantDto.getPassword())) {
			throw new ServiceException("请填写密码！");
		}

		// 保存租户信息
		Tenant tenant = new Tenant();
		BeanUtils.copyProperties(tenantDto, tenant);

		tenant.setEnabled(TenantConstant.TENANT_ENABLE); // 默认启用

		// 设置原始经纬度
		this.setLonLat(tenant);

		tenant = tenantDao.save(tenant);

		String tenantId = tenant.getId();
		tenantDto.setId(tenantId);

		// 添加行政区域树
		this.addDivisionTree(tenant.getDivisionId(), tenantId);

		// copy参数表
		tenantBusinessService.copyPrams(tenantId);

		// 为租户设置一个默认root管理员
		tenantBusinessService.setTenantRootUser(tenantDto);

		return tenant;
	}

	/**
	 * 设置经纬度
	 * 
	 * @param tenant
	 */
	private void setLonLat(Tenant tenant) {
		String lonLat = "";
		// 经纬度没有空
		if (tenant.getLatitudeDone() != null && tenant.getLongitudeDone() != null) {
			try {
				lonLat = deflectService.deflect(tenant.getLongitudeDone().toString() + "," + tenant.getLatitudeDone().toString());
			} catch (Exception e) {
				logger.error("偏转服务异常");
			}

		}
		if (StringUtils.isNotEmpty(lonLat)) {
			String[] lonLatArray = lonLat.split(",");
			if (ArrayUtils.isNotEmpty(lonLatArray)) {
				tenant.setLongitude(Double.parseDouble(lonLatArray[0]));
				tenant.setLatitude(Double.parseDouble(lonLatArray[1]));

			}
		}

	}

	/**
	 * 添加行政区域树
	 * 
	 * @param rootDivisionId
	 * @param tenantId
	 */
	private void addDivisionTree(String rootDivisionId, String tenantId) {
		this.copyDivisionTree(rootDivisionId, tenantId);
	}

	@Transactional(readOnly = true)
	@Override
	public TenantDto loadTenant(String id) {
		// 检查必要信息
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException("请传入租户id！");
		}

		Tenant tenant = tenantDao.findOne(id);
		if (tenant == null) {
			throw new ServiceException("根据租户id(" + id + ")未找到租户信息！");
		}

		TenantDto result = new TenantDto();
		BeanUtils.copyProperties(tenant, result);

		// 设置行政区划名称
		this.setDivision(result);

		return result;
	}

	/**
	 * 设置行政区划名称
	 * 
	 * @param result
	 */
	private void setDivision(TenantDto result) {
		String divisionId = result.getDivisionId();
		if (StringUtil.isNullOrEmpty(divisionId)) {
			return;
		}

		CloudDivision division = cloudDivisionService.findOne(divisionId);
		if (division != null) {
			result.setDivisionName(division.getName());
		}
	}

	@Override
	public TenantDto getTenantByCode(String code) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("tenantCode", Operator.EQ, code));

		List<Tenant> list = super.findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			logger.error("getTenantByCode(),根据[" + code + "]未能获取到租户记录");
			throw new ServiceException("根据[" + code + "]未能获取到租户记录");
		}

		Tenant entity = list.get(0);

		TenantDto dto = new TenantDto();
		BeanUtils.copyProperties(entity, dto);
		setDivision(dto);
		return dto;
	}

	@Override
	public void updateTenant(TenantDto tenantDto) {
		// 检查必要信息
		if (StringUtils.isEmpty(tenantDto.getId())) {
			throw new ServiceException("请传入租户id！");
		}

		if (StringUtils.isEmpty(tenantDto.getTenantName())) {
			throw new ServiceException("请填写租户名！");
		}

		Tenant tenant = tenantDao.findOne(tenantDto.getId());

		// 更新租户信息
		tenant.setTenantCode(tenantDto.getTenantCode());
		tenant.setTenantName(tenantDto.getTenantName());
		tenant.setDomain(tenantDto.getDomain());
		tenant.setMenuUrl(tenantDto.getMenuUrl());//租户菜单url
		tenant.setNavigationUrl(tenantDto.getNavigationUrl());//导航url
		tenant.setContact(tenantDto.getContact());
		tenant.setPhone(tenantDto.getPhone());
		tenant.setEmail(tenantDto.getEmail());
		tenant.setLatitudeDone(tenantDto.getLatitudeDone());
		tenant.setLongitudeDone(tenantDto.getLongitudeDone());
		tenant.setDivisionId(tenantDto.getDivisionId());

		// 设置原始经纬度
		this.setLonLat(tenant);

		tenantDao.update(tenant);
	}

	@Override
	public void removeTeant(String id) {
		if (!this.isRemoveAllowed(id)) {
			throw new ServiceException("该租户不允许被删除！");
		}

		Tenant tenant = tenantDao.findOne(id);

		if (tenant == null) {
			throw new ServiceException("租户不存在！");
		}

		// 删除tenant
		tenantDao.delete(id);
	}

	/**
	 * 检查该租户是否可以被删除
	 * 
	 * @param tenantId
	 * @return
	 */
	private boolean isRemoveAllowed(String tenantId) {
		// TODO 检查该租户是否可以被删除，暂时不让删除，后续业务实现后再完善此方法
		return false;
	}

	@Override
	public void enableTenant(String[] idArr) {
		if (ArrayUtils.isEmpty(idArr)) {
			throw new ServiceException("请传入租户id！");
		}

		for (int i = 0; i < idArr.length; i++) {
			Tenant tenant = tenantDao.findOne(idArr[i]);
			if (tenant == null) {
				throw new ServiceException("根据租户id(" + idArr[i] + ")未找到租户信息！");
			}

			if (TenantConstant.TENANT_ENABLE.equals(tenant.getEnabled())) {
				throw new ServiceException("租户已经启用，无需重复操作！");
			}

			tenant.setEnabled(TenantConstant.TENANT_ENABLE);
			tenantDao.update(tenant);
		}
	}

	@Override
	public void disableTenant(String[] idArr) {
		if (ArrayUtils.isEmpty(idArr)) {
			throw new ServiceException("请传入租户id！");
		}

		for (int i = 0; i < idArr.length; i++) {
			Tenant tenant = tenantDao.findOne(idArr[i]);
			if (tenant == null) {
				throw new ServiceException("根据租户id(" + idArr[i] + ")未找到租户信息！");
			}

			if (TenantConstant.TENANT_DISABLE.equals(tenant.getEnabled())) {
				throw new ServiceException("租户已经禁用，无需重复操作！");
			}

			tenant.setEnabled(TenantConstant.TENANT_DISABLE);
			tenantDao.update(tenant);
		}
	}

	@Override
	public void copyDivisionTree(String rootId, String tenantId) {
		if (StringUtils.isEmpty(rootId) || StringUtils.isEmpty(tenantId)) {
			return;
		}

		CloudDivision cd = cloudDivision1Dao.findOne(rootId);
		if (cd == null) {
			return;
		}

		// 处理根节点
		TenantDivision td = new TenantDivision();
		td.setTenantId(tenantId);
		td.setCommonCode(cd.getCommonCode());
		td.setName(cd.getName());
		td.setAbbr(cd.getAbbr());
		td.setLevel(cd.getLevel());
		td.setParentId(cd.getParentId());
		td.setLngLats(cd.getLngLats());
		td.setStartTime(cd.getStartTime());
		td.setEndTime(cd.getEndTime());
		td.setEnabled(cd.getEnabled());
		td.setIsRoot(ManagementConstant.ROOT_YES); // 标识为根节点
		td.setNodeCode(cd.getNodeCode());
		td.setChildSerialNumer(cd.getChildSerialNumer());
		td.setOrderIndex(cd.getOrderIndex());
		td = tenantDivisionDao.save(td);

		// 拷贝子节点
		this.copyChildren(cd, td, tenantId);
	}

	/**
	 * 递归拷贝下级节点
	 * 
	 * @param cd
	 * @param td
	 * @param tenantId
	 */
	private void copyChildren(CloudDivision cd, TenantDivision td, String tenantId) {
		List<CloudDivision> cdList = cloudDivision1Dao.getListByParentId(cd.getId());

		if (CollectionUtils.isEmpty(cdList)) {
			return;
		}

		for (CloudDivision cloudDivision : cdList) {
			TenantDivision tenantDivision = new TenantDivision();
			tenantDivision.setTenantId(tenantId);
			tenantDivision.setCommonCode(cloudDivision.getCommonCode());
			tenantDivision.setName(cloudDivision.getName());
			tenantDivision.setAbbr(cloudDivision.getAbbr());
			tenantDivision.setLevel(cloudDivision.getLevel());
			tenantDivision.setParentId(td.getId());
			tenantDivision.setLngLats(cloudDivision.getLngLats());
			tenantDivision.setStartTime(cloudDivision.getStartTime());
			tenantDivision.setEndTime(cloudDivision.getEndTime());
			tenantDivision.setEnabled(cloudDivision.getEnabled());
			tenantDivision.setIsRoot(ManagementConstant.ROOT_NO); // 标识为非根节点
			tenantDivision.setNodeCode(cloudDivision.getNodeCode());
			tenantDivision.setChildSerialNumer(cloudDivision.getChildSerialNumer());
			tenantDivision.setOrderIndex(cloudDivision.getOrderIndex());
			tenantDivision = tenantDivisionDao.save(tenantDivision);

			copyChildren(cloudDivision, tenantDivision, tenantId);
		}
	}

	@Override
	public Tenant getByCode(String tenantCode) {
		if (StringUtils.isEmpty(tenantCode)) {
			return null;
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("tenantCode", SearchFilter.Operator.EQ, tenantCode);
		searchFilters.add(filter);
		List<Tenant> list = tenantDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public TenantUrlDto getTenantUrl(String tenantId) {
		return tenantDao.getTenantUrl(tenantId);
	}

	@Override
	public void delete(String[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}

		for (String id : ids) {
			this.removeTeant(id);
		}
	}

	@Override
	public Map<String, String> findTenantNameById(List<String> idList) {
		return tenantDao.findTenantNameById(idList);
	}

	@Override
	public Object getTenantCodesByIds(List<String> idList) {
		Map<String, String> resultMap = Maps.newHashMap();
		List<Tenant> tenants = null;
		if (CollectionUtils.isNotEmpty(idList)) {
			tenants = tenantDao.findAllByIds(idList.toArray(new String[idList.size()]));
			for (Tenant tenant : tenants) {
				resultMap.put(tenant.getId(), tenant.getTenantCode());
			}
		}

		return resultMap;
	}

	@Override
	public Object getAllTenant() {
		List<Map<String, String>> maps = Lists.newArrayList();
		List<Tenant> tenants = tenantDao.findAll();
		if (CollectionUtils.isEmpty(tenants)) {
			return maps;
		}
		Map<String, String> tempTenant;
		for (Tenant tenant : tenants) {
			tempTenant = Maps.newHashMap();
			tempTenant.put("tenantId", tenant.getId());
			tempTenant.put("tenantCode", tenant.getTenantCode());
			tempTenant.put("tenantName", tenant.getTenantName());
			maps.add(tempTenant);
		}
		return maps;
	}
}
