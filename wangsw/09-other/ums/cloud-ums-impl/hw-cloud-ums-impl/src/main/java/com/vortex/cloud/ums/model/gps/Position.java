package com.vortex.cloud.ums.model.gps;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

/**
 * 点表
 * 
 * @author zj
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
public class Position implements Serializable, Cloneable, Comparable<Position> {
	// `id` varchar(255) NOT NULL DEFAULT '' COMMENT '主键',
	private String id;
	// `createTime` datetime DEFAULT NULL COMMENT '上传时间',
	private Date createTime;
	// `devCode` varchar(255) DEFAULT NULL COMMENT '设备编号',
	private String devCode;
	// `gpsValid` bit(1) DEFAULT NULL COMMENT '经纬度是否正常',
	private Boolean gpsValid;
	// `equipmentTime` datetime DEFAULT NULL COMMENT '设备时间',
	private Date equipmentTime;
	// `gpsCount` int(11) DEFAULT NULL COMMENT '定位卫星的数量',
	private Integer gpsCount;
	// `gpsLatitude` double DEFAULT NULL COMMENT '纬度',
	private Double gpsLatitude;
	// `gpsLongitude` double DEFAULT NULL COMMENT '经度',
	private Double gpsLongitude;
	// `gpsAltitude` double DEFAULT NULL COMMENT '海拔',
	private Double gpsAltitude;
	// `gpsSpeed` double(11,0) DEFAULT NULL COMMENT 'GPS信息中的速度',
	private Double gpsSpeed;
	// `gpsDirection` double DEFAULT NULL COMMENT '方向',
	private Double gpsDirection;
	// `gpsMileage` double(11,0) DEFAULT NULL COMMENT '里程',
	private Double gpsMileage;
	// `speed` int(11) DEFAULT NULL COMMENT '速度',
	private Integer speed;
	// `carMileage` double(11,0) DEFAULT NULL COMMENT '里程',
	private Double carMileage;
	// 作业与非作业判断
	// `switching` int(11) DEFAULT NULL COMMENT '作业与非作业',
	private Integer switching;
	// `switching0` bit(1) DEFAULT NULL COMMENT '开关量0',
	private Boolean switching0;
	// `switching1` bit(1) DEFAULT NULL COMMENT '开关量1', 1： 慢扫
	private Boolean switching1;
	// `switching2` bit(1) DEFAULT NULL COMMENT '开关量2',
	private Boolean switching2;
	// `switching3` bit(1) DEFAULT NULL COMMENT '开关量3', 1：快扫
	private Boolean switching3;
	// `analog0` int(11) DEFAULT NULL COMMENT '模拟量0',
	private Integer analog0;
	// `analog1` int(11) DEFAULT NULL COMMENT '模拟量1',
	private Integer analog1;
	// `analog2` int(11) DEFAULT NULL COMMENT '模拟量2',
	private Integer analog2;
	// `analog3` int(11) DEFAULT NULL COMMENT '模拟量3',
	private Integer analog3;
	// -- 车辆/设备状态
	// `stateMike` bit(1) DEFAULT NULL COMMENT '麦克、音响（RS232串口1）状态',
	private Boolean stateMike;
	// `stateObd` bit(1) DEFAULT NULL COMMENT 'OBD接口（RS232串口2）状态',
	private Boolean stateObd;
	// `stateCamera` bit(1) DEFAULT NULL COMMENT '摄像头1（RS232串口3）状态',
	private Boolean stateCamera;
	// `stateCom4` bit(1) DEFAULT NULL COMMENT 'RS232串口4状态',
	private Boolean stateCom4;
	// `stateCom5` bit(1) DEFAULT NULL COMMENT 'RS232串口5状态',
	private Boolean stateCom5;
	// `stateCom6` bit(1) DEFAULT NULL COMMENT 'RS232串口6状态',
	private Boolean stateCom6;
	// `stateIgnition` bit(1) DEFAULT NULL COMMENT '车辆点火状态。置位为已点火。',
	private Boolean stateIgnition;
	// `stateFire` bit(1) DEFAULT NULL COMMENT '常火，置位为有。此标识只有在车辆点火状态为熄火时才有效。',
	private Boolean stateFire;
	// `stateGps` bit(1) DEFAULT NULL COMMENT 'GPS天线状态。置位为有。',
	private Boolean stateGps;
	// `stateSd` bit(1) DEFAULT NULL COMMENT 'SD卡是否正常。 置位为正常。',
	private Boolean stateSd;

	// 原车载四期中，暂时没有什么用
	// `stateBackupBattery` bit(1) DEFAULT NULL COMMENT '主机掉电由后备电池供电。 置位为后备。',
	// private Boolean stateBackupBattery;
	// `stateBatteryRemove` bit(1) DEFAULT NULL COMMENT '电瓶拆除。 置位为拆除。',
	// private Boolean stateBatteryRemove;

	// 是否已经报过警(GPS)
	// `gpsAlarmDone` bit(1) DEFAULT 0 COMMENT '是否已经报过警(GPS)',
	private Boolean gpsAlarmDone;
	/** 是否已经报过警（作业清扫） **/
	// `workAlarmDone` bit(1) DEFAULT 0 COMMENT '是否已经报过警(作业清扫)',
	private Boolean workAlarmDone;
	/** 餐厨新增 */
	// `workAlarmDone` bit(1) DEFAULT 0 COMMENT '是否已经报过警(餐厨)',
	private Boolean ccAlarmDone;
	// -- 附加信息
	// `carId` varchar(255) DEFAULT NULL COMMENT '车辆编号',
	private String carId;
	// 车牌号
	private String carCode;
	// `equipmentId` varchar(255) DEFAULT NULL COMMENT '设备编号',
	private String equipmentId;
	// `done` bit(1) DEFAULT NULL COMMENT '经纬度是否偏转',
	private Boolean done;
	// `longitudeDone` double DEFAULT NULL COMMENT '偏转后的经度',
	private Double longitudeDone;
	// `latitudeDone` double DEFAULT NULL COMMENT '偏转后的纬度',
	private Double latitudeDone;
	// `address` varchar(255) DEFAULT NULL COMMENT '地址',
	private String address;
	// -- 清扫版需求
	// `roadId` varchar(255) DEFAULT NULL COMMENT '所在路段0',
	private String roadId;
	// `roadId2` varchar(255) DEFAULT NULL COMMENT '所在路段1',
	private String roadId2;
	// `roadId3` varchar(255) DEFAULT NULL COMMENT '所在路段2',
	private String roadId3;
	// `roadId4` varchar(255) DEFAULT NULL COMMENT '所在路段3',
	private String roadId4;

	// ---油耗需求
	// `oilLevel` double DEFAULT NULL COMMENT '油位',
	private Double oilLevel;
	// `pullOilType` int(11) DEFAULT NULL COMMENT '获取方式',
	private Integer pullOilType;
	// 升L/高度mm
	// `oilLevelUnit` varchar(255) DEFAULT NULL COMMENT '油位单位',
	private String oilLevelUnit;

	// 是否已用滤波方法过滤过（目前只有上海老港使用）
	private Boolean isFilter;

	// add oil 2014.10.22
	// 是否新值（F9） V1.1.9
	// `newValueOil` int(11) DEFAULT NULL COMMENT '是否新值',
	private Integer newValueOil = 0;
	// 计算次数（F10）V1.1.9
	// `calcCountOil` int(11) DEFAULT NULL COMMENT '计算次数',
	private Integer calcCountOil = 0;
	// 温度（F10） V1.1.9
	// `temperatureOil` int(11) DEFAULT NULL COMMENT '温度',
	private Integer temperatureOil = 0;
	// 油量临时值（F11）V1.1.9
	// `tempOilUnit` varchar(255) DEFAULT NULL COMMENT '油量临时值单位',
	private String tempOilUnit = "mm";
	// 临时值
	// `tempOilHeight` double DEFAULT NULL COMMENT '临时值',
	private Double tempOilHeight = 0d;

	@Id
	@GeneratedValue(generator = "idGenerator")
	@GenericGenerator(name = "idGenerator", strategy = "assigned")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDevCode() {
		return devCode;
	}

	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}

	public Boolean getCcAlarmDone() {
		return ccAlarmDone;
	}

	public void setCcAlarmDone(Boolean ccAlarmDone) {
		this.ccAlarmDone = ccAlarmDone;
	}

	public Boolean getGpsValid() {
		return gpsValid;
	}

	public void setGpsValid(Boolean gpsValid) {
		this.gpsValid = gpsValid;
	}

	public Date getEquipmentTime() {
		return equipmentTime;
	}

	public void setEquipmentTime(Date equipmentTime) {
		this.equipmentTime = equipmentTime;
	}

	public Integer getGpsCount() {
		return gpsCount;
	}

	public void setGpsCount(Integer gpsCount) {
		this.gpsCount = gpsCount;
	}

	public Double getGpsLatitude() {
		return gpsLatitude;
	}

	public void setGpsLatitude(Double gpsLatitude) {
		this.gpsLatitude = gpsLatitude;
	}

	public Double getGpsLongitude() {
		return gpsLongitude;
	}

	public void setGpsLongitude(Double gpsLongitude) {
		this.gpsLongitude = gpsLongitude;
	}

	public Double getGpsAltitude() {
		return gpsAltitude;
	}

	public void setGpsAltitude(Double gpsAltitude) {
		this.gpsAltitude = gpsAltitude;
	}

	public Double getGpsSpeed() {
		return gpsSpeed;
	}

	public void setGpsSpeed(Double gpsSpeed) {
		this.gpsSpeed = gpsSpeed;
	}

	public Double getGpsDirection() {
		return gpsDirection;
	}

	public void setGpsDirection(Double gpsDirection) {
		this.gpsDirection = gpsDirection;
	}

	public Double getGpsMileage() {
		return gpsMileage;
	}

	public void setGpsMileage(Double gpsMileage) {
		this.gpsMileage = gpsMileage;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Double getCarMileage() {
		return carMileage;
	}

	public void setCarMileage(Double carMileage) {
		this.carMileage = carMileage;
	}

	public Boolean getSwitching0() {
		return switching0;
	}

	public void setSwitching0(Boolean switching0) {
		this.switching0 = switching0;
	}

	public Boolean getSwitching1() {
		return switching1;
	}

	public void setSwitching1(Boolean switching1) {
		this.switching1 = switching1;
	}

	public Boolean getSwitching2() {
		return switching2;
	}

	public void setSwitching2(Boolean switching2) {
		this.switching2 = switching2;
	}

	public Boolean getSwitching3() {
		return switching3;
	}

	public void setSwitching3(Boolean switching3) {
		this.switching3 = switching3;
	}

	public Integer getAnalog0() {
		return analog0;
	}

	public void setAnalog0(Integer analog0) {
		this.analog0 = analog0;
	}

	public Integer getAnalog1() {
		return analog1;
	}

	public void setAnalog1(Integer analog1) {
		this.analog1 = analog1;
	}

	public Integer getAnalog2() {
		return analog2;
	}

	public void setAnalog2(Integer analog2) {
		this.analog2 = analog2;
	}

	public Integer getAnalog3() {
		return analog3;
	}

	public void setAnalog3(Integer analog3) {
		this.analog3 = analog3;
	}

	public Boolean getStateMike() {
		return stateMike;
	}

	public void setStateMike(Boolean stateMike) {
		this.stateMike = stateMike;
	}

	public Boolean getStateObd() {
		return stateObd;
	}

	public void setStateObd(Boolean stateObd) {
		this.stateObd = stateObd;
	}

	public Boolean getStateCamera() {
		return stateCamera;
	}

	public void setStateCamera(Boolean stateCamera) {
		this.stateCamera = stateCamera;
	}

	public Boolean getStateCom4() {
		return stateCom4;
	}

	public void setStateCom4(Boolean stateCom4) {
		this.stateCom4 = stateCom4;
	}

	public Boolean getStateCom5() {
		return stateCom5;
	}

	public void setStateCom5(Boolean stateCom5) {
		this.stateCom5 = stateCom5;
	}

	public Boolean getStateCom6() {
		return stateCom6;
	}

	public void setStateCom6(Boolean stateCom6) {
		this.stateCom6 = stateCom6;
	}

	public Boolean getStateIgnition() {
		return stateIgnition;
	}

	public void setStateIgnition(Boolean stateIgnition) {
		this.stateIgnition = stateIgnition;
	}

	public Boolean getStateFire() {
		return stateFire;
	}

	public void setStateFire(Boolean stateFire) {
		this.stateFire = stateFire;
	}

	public Boolean getStateGps() {
		return stateGps;
	}

	public void setStateGps(Boolean stateGps) {
		this.stateGps = stateGps;
	}

	public Boolean getStateSd() {
		return stateSd;
	}

	public void setStateSd(Boolean stateSd) {
		this.stateSd = stateSd;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Double getLongitudeDone() {
		return longitudeDone;
	}

	public void setLongitudeDone(Double longitudeDone) {
		this.longitudeDone = longitudeDone;
	}

	public Double getLatitudeDone() {
		return latitudeDone;
	}

	public void setLatitudeDone(Double latitudeDone) {
		this.latitudeDone = latitudeDone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRoadId() {
		return roadId;
	}

	public void setRoadId(String roadId) {
		this.roadId = roadId;
	}

	public String getRoadId2() {
		return roadId2;
	}

	public void setRoadId2(String roadId2) {
		this.roadId2 = roadId2;
	}

	public String getRoadId3() {
		return roadId3;
	}

	public void setRoadId3(String roadId3) {
		this.roadId3 = roadId3;
	}

	public String getRoadId4() {
		return roadId4;
	}

	public void setRoadId4(String roadId4) {
		this.roadId4 = roadId4;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public Double getOilLevel() {
		return oilLevel;
	}

	public void setOilLevel(Double oilLevel) {
		this.oilLevel = oilLevel;
	}

	public Integer getPullOilType() {
		return pullOilType;
	}

	public void setPullOilType(Integer pullOilType) {
		this.pullOilType = pullOilType;
	}

	public String getOilLevelUnit() {
		return oilLevelUnit;
	}

	public void setOilLevelUnit(String oilLevelUnit) {
		this.oilLevelUnit = oilLevelUnit;
	}

	public Integer getSwitching() {
		return switching;
	}

	public void setSwitching(Integer switching) {
		this.switching = switching;
	}

	public Boolean getGpsAlarmDone() {
		return gpsAlarmDone;
	}

	public void setGpsAlarmDone(Boolean gpsAlarmDone) {
		this.gpsAlarmDone = gpsAlarmDone;
	}

	public Boolean getWorkAlarmDone() {
		return workAlarmDone;
	}

	public void setWorkAlarmDone(Boolean workAlarmDone) {
		this.workAlarmDone = workAlarmDone;
	}

	public Boolean getIsFilter() {
		return isFilter;
	}

	public void setIsFilter(Boolean isFilter) {
		this.isFilter = isFilter;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Integer getNewValueOil() {
		return newValueOil;
	}

	public void setNewValueOil(Integer newValueOil) {
		this.newValueOil = newValueOil;
	}

	public Integer getCalcCountOil() {
		return calcCountOil;
	}

	public void setCalcCountOil(Integer calcCountOil) {
		this.calcCountOil = calcCountOil;
	}

	public Integer getTemperatureOil() {
		return temperatureOil;
	}

	public void setTemperatureOil(Integer temperatureOil) {
		this.temperatureOil = temperatureOil;
	}

	public String getTempOilUnit() {
		return tempOilUnit;
	}

	public void setTempOilUnit(String tempOilUnit) {
		this.tempOilUnit = tempOilUnit;
	}

	public Double getTempOilHeight() {
		return tempOilHeight;
	}

	public void setTempOilHeight(Double tempOilHeight) {
		this.tempOilHeight = tempOilHeight;
	}

	@Override
	public Object clone() {
		Position o = null;
		try {
			o = (Position) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public int compareTo(Position o) {
		if (o.getOilLevel() > this.getOilLevel()) {
			return -1;
		} else {
			return 1;
		}
	}
}
