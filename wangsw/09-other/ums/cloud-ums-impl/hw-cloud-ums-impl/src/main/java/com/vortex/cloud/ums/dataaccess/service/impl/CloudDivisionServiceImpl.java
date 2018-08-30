package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

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

import com.vortex.cloud.ums.dataaccess.dao.ICloudDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDivisionService;
import com.vortex.cloud.ums.dto.CloudDivisionDto;
import com.vortex.cloud.ums.enums.CloudDivisionLevelEnum;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;






@SuppressWarnings("all")
@Service("cloudDivisionService")
@Transactional
public class CloudDivisionServiceImpl extends SimplePagingAndSortingService<CloudDivision, String> implements ICloudDivisionService {

	private Logger logger = LoggerFactory.getLogger(CloudDivisionServiceImpl.class);

	@Resource
	private ICloudDivisionDao cloudDivisionDao;

	@Override
	public HibernateRepository<CloudDivision, String> getDaoImpl() {
		return cloudDivisionDao;
	}

	@Override
	public List<CloudDivision> getAllChildren(CloudDivision parent) {
		return cloudDivisionDao.getAllChildren(parent);
	}

	@Override
	public CloudDivision save(CloudDivisionDto dto) {
		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		CloudDivision entity = new CloudDivision();
		BeanUtils.copyProperties(dto, entity);

		return cloudDivisionDao.save(entity);
	}

	@Override
	public CloudDivisionDto getById(String id) {
		CloudDivision entity = cloudDivisionDao.findOne(id);
		if (entity == null) {
			return null;
		}

		CloudDivisionDto dto = new CloudDivisionDto();
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
	private void setLevelText(CloudDivisionDto dto) {
		dto.setLevelText(CloudDivisionLevelEnum.getTextByValue(dto.getLevel()));
	}

	/**
	 * 分离出经纬度
	 * 
	 * @param dto
	 */
	private void setLngLat(CloudDivisionDto dto) {
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

		List<CloudDivision> childList = this.findListByFilter(filterList, null);
		if (CollectionUtils.isNotEmpty(childList)) {
			logger.warn("deleteNotWithDescendant，不是叶子节点，不能删除行政区划：id=" + id);
			return 0;
		}

		cloudDivisionDao.delete(id);
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
			cloudDivisionDao.deleteByIds(childIdList.toArray(new String[childIdList.size()]));
		}

		cloudDivisionDao.delete(id);
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

		List<CloudDivision> sonList = this.findListByFilter(filterList, new Sort(Direction.ASC, "orderIndex"));

		if (CollectionUtils.isEmpty(sonList)) {
			return null;
		}

		List<String> resultList = new ArrayList<String>();
		List<String> grandsonList = null;
		for (CloudDivision son : sonList) {
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
	public CloudDivision update(CloudDivisionDto dto) {
		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		CloudDivision division = cloudDivisionDao.findOne(dto.getId());
		division.setAbbr(dto.getAbbr());
		division.setCommonCode(dto.getCommonCode());
		division.setLevel(dto.getLevel());
		division.setLngLats(dto.getLngLats());
		division.setName(dto.getName());
		division.setStartTime(dto.getStartTime());
		division.setOrderIndex(dto.getOrderIndex());

		return cloudDivisionDao.update(division);
	}
}
