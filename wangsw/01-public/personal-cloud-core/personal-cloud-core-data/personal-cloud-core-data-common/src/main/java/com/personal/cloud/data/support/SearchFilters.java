package com.personal.cloud.data.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchFilters {

	/** 字段操作类型. */
	public static enum Operator {
		AND, OR
	}

	private List<SearchFilter> searchFilterCollection = new ArrayList<SearchFilter>();

	private Operator operator = Operator.OR;

	private List<SearchFilters> searchFiltersCollection = new ArrayList<SearchFilters>();

	/**
	 * 构造函数
	 */
	public SearchFilters() {

	}

	public SearchFilters(Collection<SearchFilter> searchFilterCollection) {
		addSearchFilter(searchFilterCollection);
	}

	public SearchFilters(SearchFilter searchFilter) {
		add(searchFilter);
	}

	public SearchFilters(Collection<SearchFilter> searchFilterCollection, Operator operator) {
		setOperator(operator);
		addSearchFilter(searchFilterCollection);
	}

	public SearchFilters(Operator operator, SearchFilter... searchFilters) {
		setOperator(operator);
		for (SearchFilter searchFilter : searchFilters) {
			add(searchFilter);
		}
	}

	public SearchFilters(Collection<SearchFilter> searchFilterCollection, Operator operator, Collection<SearchFilters> searchFiltersCollection) {
		setOperator(operator);
		addSearchFilter(searchFilterCollection);
		addSearchFilters(searchFiltersCollection);
	}

	// ////////////////////////////////////////////////////////////////////
	public SearchFilters add(SearchFilter searchFilter) {
		searchFilterCollection.add(searchFilter);
		return this;
	}

	public SearchFilters addSearchFilter(Collection<SearchFilter> searchFilterCollection) {
		this.searchFilterCollection.addAll(searchFilterCollection);
		return this;
	}

	public SearchFilters add(SearchFilters searchFilters) {
		searchFiltersCollection.add(searchFilters);
		return this;
	}

	public SearchFilters addSearchFilters(Collection<SearchFilters> searchFiltersCollection) {
		searchFiltersCollection.addAll(searchFiltersCollection);
		return this;
	}

	public List<SearchFilter> getSearchFilterCollection() {
		return searchFilterCollection;
	}

	public void setSearchFilterCollection(List<SearchFilter> searchFilterCollection) {
		this.searchFilterCollection = searchFilterCollection;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public List<SearchFilters> getSearchFiltersCollection() {
		return searchFiltersCollection;
	}

	public void setSearchFiltersCollection(List<SearchFilters> searchFiltersCollection) {
		this.searchFiltersCollection = searchFiltersCollection;
	}

}
