package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.enums.CloudDivisionLevelEnum;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@SuppressWarnings("all")
@Service("tenantDivisionService")
@Transactional
public class TenantDivisionServiceImpl extends SimplePagingAndSortingService<TenantDivision, String> implements ITenantDivisionService {

	private Logger logger = LoggerFactory.getLogger(TenantDivisionServiceImpl.class);

	@Resource
	private ITenantDivisionDao tenantDivisionDao;

	@Override
	public HibernateRepository<TenantDivision, String> getDaoImpl() {
		return tenantDivisionDao;
	}

	@Override
	public TenantDivision save(TenantDivisionDto dto) {
		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		TenantDivision entity = new TenantDivision();
		BeanUtils.copyProperties(dto, entity);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_TENANT_DIVISION_SYNC.getKey(), SyncFlagEnum.ADD.getKey(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tenantDivisionDao.save(entity);

	}

	@Override
	public TenantDivisionDto getById(String id) {
		TenantDivision entity = tenantDivisionDao.findOne(id);
		if (entity == null) {
			return null;
		}

		TenantDivisionDto dto = new TenantDivisionDto();
		BeanUtils.copyProperties(entity, dto);

		this.setLevelText(dto);

		// 分离出经纬度
		this.setLngLat(dto);

		return dto;
	}

	/**
	 * 设置行政级别的描述文本
	 * 
	 * @param dto
	 */
	private void setLevelText(TenantDivisionDto dto) {
		dto.setLevelText(CloudDivisionLevelEnum.getTextByValue(dto.getLevel()));
	}

	/**
	 * 分离出经纬度
	 * 
	 * @param dto
	 */
	private void setLngLat(TenantDivisionDto dto) {
		String lngLats = dto.getLngLats();
		if (StringUtils.isNotBlank(lngLats)) {

			String[] lngLatArr = lngLats.split(",");
			if (ArrayUtils.isEmpty(lngLatArr) || lngLatArr.length != 2) {
				logger.error("getById(),经纬度为非法字符串，不能解析");
				throw new ServiceException("经纬度为非法字符串，不能解析");
			}
			String lng = lngLatArr[0];
			String lat = lngLatArr[1];
			if (StringUtils.isNotBlank(lng) && StringUtils.isNotBlank(lat)) {
				dto.setLongitude(Double.parseDouble(lng));
				dto.setLatitude(Double.parseDouble(lat));
			}
		}
	}

	@Override
	public List<TenantDivision> getAllChildren(TenantDivision parent) {
		return tenantDivisionDao.getAllChildren(parent);
	}

	@Override
	public long deleteByIdArr(String[] ids, boolean casecade) {
		long deleted = 0;
		if (ArrayUtils.isEmpty(ids)) {
			return deleted;
		}

		for (String id : ids) {
			if (!casecade) {
				deleted += this.deleteNotWithDescendant(id);
			} else {
				deleted += this.deleteWithDescendant(id);
			}
		}

		return deleted;
	}

	/**
	 * 只删除自身，不级联删除后代节点
	 * 
	 * @param ids
	 * @return
	 */
	private long deleteNotWithDescendant(String id) {

		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("parentId", Operator.EQ, id));

		List<TenantDivision> childList = super.findListByFilter(filterList, null);
		if (CollectionUtils.isNotEmpty(childList)) {
			logger.warn("deleteNotWithDescendant，不是叶子节点，不能删除行政区划：id=" + id);
			return 0;
		}

		tenantDivisionDao.delete(id);
		TenantDivision entity = tenantDivisionDao.findOne(id);
		if (entity != null) {
			try {
				KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_TENANT_DIVISION_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 1;
	}

	/**
	 * 删除自身，同时级联删除后代节点
	 * 
	 * @param ids
	 * @return
	 */
	private int deleteWithDescendant(String id) {
		List<String> childIdList = this.getChildIdList(id);
		if (CollectionUtils.isNotEmpty(childIdList)) {
			tenantDivisionDao.deleteByIds(childIdList.toArray(new String[childIdList.size()]));
		}

		tenantDivisionDao.delete(id);
		TenantDivision entity = tenantDivisionDao.findOne(id);
		if (entity != null) {
			try {
				KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_TENANT_DIVISION_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	/**
	 * 获取后代记录ID
	 * 
	 * @param parentId
	 * @return
	 */
	private List<String> getChildIdList(String parentId) {

		// 获取儿子节点
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));

		List<TenantDivision> sonList = super.findListByFilter(filterList, new Sort(Direction.ASC, "orderIndex"));

		if (CollectionUtils.isEmpty(sonList)) {
			return null;
		}

		List<String> resultList = new ArrayList<String>();
		List<String> grandsonList = null;
		for (TenantDivision son : sonList) {
			// 添加儿子节点
			resultList.add(son.getId());

			// 递归：获取孙子节点
			grandsonList = this.getChildIdList(son.getId());
			if (CollectionUtils.isEmpty(grandsonList)) {
				continue;
			}
			// 添加孙子节点
			resultList.addAll(grandsonList);
		}

		return resultList;
	}

	@Override
	public TenantDivision update(TenantDivisionDto dto) {
		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		TenantDivision division = tenantDivisionDao.findOne(dto.getId());
		division.setAbbr(dto.getAbbr());
		division.setCommonCode(dto.getCommonCode());
		division.setLevel(dto.getLevel());
		division.setLngLats(dto.getLngLats());
		division.setName(dto.getName());
		division.setStartTime(dto.getStartTime());
		division.setScope(dto.getScope());
		division.setOrderIndex(dto.getOrderIndex());
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_TENANT_DIVISION_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), division);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tenantDivisionDao.update(division);
	}

	@Override
	public List<TenantDivision> findTenantDivisionList(TenantDivisionDto tenantDivision) {
		// 取租户下的所有节点
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantDivision.getTenantId()));
		filterList.add(new SearchFilter("enabled", Operator.EQ, TenantDivision.ENABLED_YES));
		// 不传或者传0就不包含root，传1包含root
		if (StringUtils.isBlank(tenantDivision.getContainsRoot()) || TenantDivisionDto.CONTAIN_ROOT_NO.equals(tenantDivision.getContainsRoot())) {
			filterList.add(new SearchFilter("isRoot", Operator.EQ, TenantDivision.ROOT_NOT));
		}
		if (StringUtils.isNotBlank(tenantDivision.getParentId())) {
			filterList.add(new SearchFilter("parentId", Operator.EQ, tenantDivision.getParentId()));
		}
		if (tenantDivision.getLevel() != null) {
			filterList.add(new SearchFilter("level", Operator.EQ, tenantDivision.getLevel()));
		}
		Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

		return this.findListByFilter(filterList, sort);
	}

	@Override
	public List<TenantDivision> findTenantDivisionListWithRoot(TenantDivision tenantDivision) {
		// 取租户下的所有节点
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantDivision.getTenantId()));
		filterList.add(new SearchFilter("enabled", Operator.EQ, TenantDivision.ENABLED_YES));
		if (StringUtils.isNotBlank(tenantDivision.getParentId())) {
			filterList.add(new SearchFilter("parentId", Operator.EQ, tenantDivision.getParentId()));
		}
		if (tenantDivision.getLevel() != null) {
			filterList.add(new SearchFilter("level", Operator.EQ, tenantDivision.getLevel()));
		}
		Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

		return this.findListByFilter(filterList, sort);
	}

	@Override
	public Map<String, String> getDivisionNamesByIds(List<String> ids) {
		Map<String, String> map = Maps.newHashMap();
		List<TenantDivision> list = tenantDivisionDao.findAllByIds(ids.toArray(new String[ids.size()]));
		if (CollectionUtils.isNotEmpty(list)) {
			for (TenantDivision tenantDivision : list) {
				map.put(tenantDivision.getId(), tenantDivision.getName());
			}

		}
		return map;
	}

	@Override
	public Map<String, String> getDivisionIdsByNames(List<String> names, String tenantId) {
		Map<String, String> map = Maps.newHashMap();
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		if (CollectionUtils.isNotEmpty(names)) {
			searchFilters.add(new SearchFilter("name", Operator.IN, names.toArray()));
		}
		List<TenantDivision> list = tenantDivisionDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(list)) {
			for (TenantDivision tenantDivision : list) {
				map.put(tenantDivision.getName(), tenantDivision.getId());
			}
		}
		return map;
	}

	@Override
	public List<TenantDivision> getChildren(String parentId) {
		return this.tenantDivisionDao.getAllChildren(parentId);
	}

	@Override
	public List<TenantDivision> getByLevel(String tenantId, Integer level) throws Exception {
		if (StringUtils.isEmpty(tenantId) || level == null) {
			logger.error("查询租户的某级行政区划列表时，传入的参数非法");
			throw new VortexException("查询租户的某级行政区划列表时，传入的参数非法");
		}
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		filterList.add(new SearchFilter("enabled", Operator.EQ, TenantDivision.ENABLED_YES));
		filterList.add(new SearchFilter("level", Operator.EQ, level));

		Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

		return this.findListByFilter(filterList, sort);
	}

	@Override
	public LinkedHashMap<String, String> getDivisionsByNames(String tenantId, List<String> names) throws Exception {
		if (StringUtils.isEmpty(tenantId) || CollectionUtils.isEmpty(names)) {
			logger.error("根据行政区划名称列表查询id列表时，传入的参数非法");
			throw new VortexException("根据行政区划名称列表查询id列表时，传入的参数非法");
		}
		List<IdNameDto> list = this.tenantDivisionDao.getDivisionsByNames(tenantId, names);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		LinkedHashMap<String, String> rst = Maps.newLinkedHashMap();
		for (int i = 0; i < list.size(); i++) {
			rst.put(list.get(i).getName(), list.get(i).getId());
		}

		return rst;
	}
}
