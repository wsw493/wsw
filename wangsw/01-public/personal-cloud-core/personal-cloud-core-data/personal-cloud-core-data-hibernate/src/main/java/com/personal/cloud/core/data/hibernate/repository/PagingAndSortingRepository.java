package com.personal.cloud.core.data.hibernate.repository;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilters;




public interface PagingAndSortingRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

	/**
	 * Returns all entities sorted by the given options.
	 * 
	 * @param sort
	 * @return all entities sorted by the given options
	 */
	List<T> findAll(Sort sort);

	List<T> findListByProperty(Map<String, Object> filterPropertyMap, Sort sort);

	List<T> findListByFilter(Iterable<SearchFilter> searchFilter, Sort sort);

	/**
	 * Returns a {@link Page} of entities meeting the paging restriction
	 * provided in the {@code Pageable} object.
	 * 
	 * @param pageable
	 * @return a page of entities
	 */
	Page<T> findPageByProperty(Pageable pageable, String hql, Map<String, Object> filterPropertyMap);

	Page<T> findPageByFilter(Pageable pageable, Iterable<SearchFilter> searchFilter);

	/**
	 * 
	 * @Title: findListByFilter
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<T>
	 * @throws
	 */
	public List<T> findListByFilters(SearchFilters searchFilters, Sort sort);

	/**
	 * 
	 * @Title: findPageByFilter
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return Page<T>
	 * @throws
	 */
	public Page<T> findPageByFilters(Pageable pageable, SearchFilters searchFilters);

	public List<T> findListByFilters(SearchFilters searchFilters, Sort sort, Iterable<String> colunms);

	public Page<T> findPageByFilters(Pageable pageable, SearchFilters searchFilters, Iterable<String> colunms);

	public List<T> findListByFilter(Iterable<SearchFilter> searchFilter, Sort sort, Iterable<String> colunms);

	public Page<T> findPageByFilter(Pageable pageable, Iterable<SearchFilter> searchFilter, Iterable<String> colunms);

	//查询某一条记录不包含“beenDeleted=1”的数据
	public T findOneNoDeleted(String id);
}
