package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.IParamTypeDao;
import com.vortex.cloud.ums.dataaccess.service.IParamSettingService;
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Service("paramTypeService")
@Transactional
public class ParamTypeServiceImpl extends SimplePagingAndSortingService<PramType, String> implements IParamTypeService {

	protected static final Logger logger = LoggerFactory.getLogger(ParamTypeServiceImpl.class);

	@Resource
	private IParamTypeDao paramTypeDao = null;

	@Resource
	private IParamSettingService paramSettingService;

	@Override
	public HibernateRepository<PramType, String> getDaoImpl() {
		return paramTypeDao;
	}

	@Override
	public PramType findByCode(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		filters.add(new SearchFilter("code", Operator.EQ, code));
		filters.add(new SearchFilter("beenDeleted", Operator.EQ, 0));

		List<PramType> types = this.findListByFilter(filters, null);

		if (types == null || types.size() == 0) {
			return null;
		}
		if (types.size() > 1) {
			logger.warn("Code:" + code + "对应有多个不同的参数分类，请删除多余的");
		}
		return types.get(0);
	}

	@Override
	public long deletes(String[] idArr) {

		if (ArrayUtils.isEmpty(idArr)) {
			return 0;
		}

		long deleted = 0;
		List<SearchFilter> filterList = null;
		for (String id : idArr) {

			filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("typeId", Operator.EQ, id));

			List<PramSetting> list = paramSettingService.findListByFilter(filterList, null);

			if (CollectionUtils.isNotEmpty(list)) {
				continue;
			}

			this.delete(id);
			deleted++;
		}

		return deleted;
	}
}
