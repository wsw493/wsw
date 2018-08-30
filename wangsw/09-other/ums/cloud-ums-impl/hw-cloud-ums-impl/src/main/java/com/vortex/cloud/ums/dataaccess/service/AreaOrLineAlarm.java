package com.vortex.cloud.ums.dataaccess.service;

import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.gps.Position;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：图元部分提供给报警模块的接口
 * @说明：
 */
public interface AreaOrLineAlarm {

	/**
	 * @author lxw
	 * @功能：获取一个点和一个图元的距离
	 * @说明：
	 */
	public double overMeter(Position position, WorkElement element) throws Exception;

	/**
	 * @author lxw
	 * @功能：判断两个图元是否相交
	 * @说明：
	 */
	public boolean hasOverlapBetweenElement(WorkElement e1, WorkElement e2) throws Exception;

}
