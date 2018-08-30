package com.vortex.cloud.ums.dto;
/**
 * 租户url
 * @author ll
 *
 */
public class TenantUrlDto {
	
	private String domain; // 租户访问url
	private String menuUrl; // 租户菜单url
	private String navigationUrl; // 导航url
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getMenuUrl() {
		return menuUrl;
	}
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	public String getNavigationUrl() {
		return navigationUrl;
	}
	public void setNavigationUrl(String navigationUrl) {
		this.navigationUrl = navigationUrl;
	}
}
