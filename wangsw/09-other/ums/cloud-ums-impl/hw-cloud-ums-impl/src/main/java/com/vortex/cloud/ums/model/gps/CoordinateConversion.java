package com.vortex.cloud.ums.model.gps;


/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：实现坐标转换功能 -- 经纬度投影到高斯坐标系
 * @说明： http://blog.sina.com.cn/s/blog_5656b0020100chbv.html
 */
public class CoordinateConversion {
	/**
	 * @author lxw
	 * @功能：由经纬度反算成高斯投影坐标【最终结果以米为单位】
	 * @说明：
	 */
	public static APoint BLToGauss(double longitude, double latitude) {
		int ProjNo = 0;
		int ZoneWide; // //带宽
		@SuppressWarnings("unused")
		double longitude1, latitude1, longitude0, latitude0, X0, Y0, xval, yval;
		double a, f, e2, ee, NN, T, C, A, M, iPI;
		iPI = 0.0174532925199433; // //3.1415926535898/180.0;
		ZoneWide = 6; // //6度带宽
		a = 6378245.0;
		f = 1.0 / 298.3; // 54年北京坐标系参数
		// //a=6378140.0; f=1/298.257; //80年西安坐标系参数
		ProjNo = (int) (longitude / ZoneWide);
		longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
		longitude0 = longitude0 * iPI;
		latitude0 = 0;
		// System.out.println(latitude0);
		longitude1 = longitude * iPI; // 经度转换为弧度
		latitude1 = latitude * iPI; // 纬度转换为弧度
		e2 = 2 * f - f * f;
		ee = e2 * (1.0 - e2);
		NN = a
				/ Math.sqrt(1.0 - e2 * Math.sin(latitude1)
						* Math.sin(latitude1));
		T = Math.tan(latitude1) * Math.tan(latitude1);
		C = ee * Math.cos(latitude1) * Math.cos(latitude1);
		A = (longitude1 - longitude0) * Math.cos(latitude1);
		M = a
				* ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256)
						* latitude1
						- (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2
								/ 1024) * Math.sin(2 * latitude1)
						+ (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024)
						* Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072)
						* Math.sin(6 * latitude1));
		xval = NN
				* (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72
						* C - 58 * ee)
						* A * A * A * A * A / 120);
		yval = M
				+ NN
				* Math.tan(latitude1)
				* (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61
						- 58 * T + T * T + 600 * C - 330 * ee)
						* A * A * A * A * A * A / 720);
		X0 = 1000000L * (ProjNo + 1) + 500000L;
		Y0 = 0;
		xval = xval + X0;
		yval = yval + Y0;

		APoint point = new APoint();
		point.setX(xval);
		point.setY(yval);
		return point;
	}

	/**
	 * @author lxw
	 * @功能：由WGS84经纬度反算成高斯投影坐标【最终结果以米为单位】
	 * @说明：
	 */
	public static double[] BLToGauss(double longitude, double latitude, CoordinateEnum coordinate) {
		double[] output = new double[2];

		int ProjNo = 0;
		int ZoneWide; // //带宽
		@SuppressWarnings("unused")
		double longitude1, latitude1, longitude0, latitude0, X0, Y0, xval, yval;
		double a, f, e2, ee, NN, T, C, A, M, iPI;
		iPI = 0.0174532925199433; // //3.1415926535898/180.0;
		ZoneWide = 6; // //6度带宽
		if (coordinate == CoordinateEnum.Beijing54){
			a = 6378245.0;
			f = 1.0 / 298.3; // 54年北京坐标系参数
		} else if (coordinate == CoordinateEnum.Xian80){
			a=6378140.0; f=1/298.257; //80年西安坐标系参数
		} else {
			a = 6378245.0;
			f = 1.0 / 298.3; // 54年北京坐标系参数
		}
		
		
		ProjNo = (int) (longitude / ZoneWide);
		longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
		longitude0 = longitude0 * iPI;
		latitude0 = 0;
		// System.out.println(latitude0);
		longitude1 = longitude * iPI; // 经度转换为弧度
		latitude1 = latitude * iPI; // 纬度转换为弧度
		e2 = 2 * f - f * f;
		ee = e2 * (1.0 - e2);
		NN = a
				/ Math.sqrt(1.0 - e2 * Math.sin(latitude1)
						* Math.sin(latitude1));
		T = Math.tan(latitude1) * Math.tan(latitude1);
		C = ee * Math.cos(latitude1) * Math.cos(latitude1);
		A = (longitude1 - longitude0) * Math.cos(latitude1);
		M = a
				* ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256)
						* latitude1
						- (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2
								/ 1024) * Math.sin(2 * latitude1)
						+ (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024)
						* Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072)
						* Math.sin(6 * latitude1));
		xval = NN
				* (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72
						* C - 58 * ee)
						* A * A * A * A * A / 120);
		yval = M
				+ NN
				* Math.tan(latitude1)
				* (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61
						- 58 * T + T * T + 600 * C - 330 * ee)
						* A * A * A * A * A * A / 720);
		X0 = 1000000L * (ProjNo + 1) + 500000L;
		Y0 = 0;
		xval = xval + X0;
		yval = yval + Y0;

		output[0] = xval;
		output[1] = yval;
		return output;
	}

	// 由高斯投影坐标反算成经纬度  没测试过
	public static double[] GaussToBL(double X, double Y, CoordinateEnum coordinate)// , double *longitude,
														// double *latitude)
	{
		int ProjNo;
		int ZoneWide; // //带宽
		double[] output = new double[2];
		double longitude1, latitude1, longitude0, X0, Y0, xval, yval;// latitude0,
		double e1, e2, f, a, ee, NN, T, C, M, D, R, u, fai, iPI;
		iPI = 0.0174532925199433; // //3.1415926535898/180.0;
		
		if (coordinate == CoordinateEnum.Beijing54){
			a = 6378245.0;
			f = 1.0 / 298.3; // 54年北京坐标系参数
		} else if (coordinate == CoordinateEnum.Xian80){
			a=6378140.0; f=1/298.257; //80年西安坐标系参数
		} else {
			a = 6378245.0;
			f = 1.0 / 298.3; // 54年北京坐标系参数
		}
		
		// a = 6378245.0; f = 1.0/298.3; //54年北京坐标系参数
//		a = 6378140.0;
//		f = 1 / 298.257; // 80年西安坐标系参数
		ZoneWide = 6; // //6度带宽
		ProjNo = (int) (X / 1000000L); // 查找带号
		longitude0 = (ProjNo - 1) * ZoneWide + ZoneWide / 2;
		longitude0 = longitude0 * iPI; // 中央经线

		X0 = ProjNo * 1000000L + 500000L;
		Y0 = 0;
		xval = X - X0;
		yval = Y - Y0; // 带内大地坐标
		e2 = 2 * f - f * f;
		e1 = (1.0 - Math.sqrt(1 - e2)) / (1.0 + Math.sqrt(1 - e2));
		ee = e2 / (1 - e2);
		M = yval;
		u = M / (a * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));
		fai = u + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * u)
				+ (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32)
				* Math.sin(4 * u) + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * u)
				+ (1097 * e1 * e1 * e1 * e1 / 512) * Math.sin(8 * u);
		C = ee * Math.cos(fai) * Math.cos(fai);
		T = Math.tan(fai) * Math.tan(fai);
		NN = a / Math.sqrt(1.0 - e2 * Math.sin(fai) * Math.sin(fai));
		R = a
				* (1 - e2)
				/ Math.sqrt((1 - e2 * Math.sin(fai) * Math.sin(fai))
						* (1 - e2 * Math.sin(fai) * Math.sin(fai))
						* (1 - e2 * Math.sin(fai) * Math.sin(fai)));
		D = xval / NN;
		// 计算经度(Longitude) 纬度(Latitude)
		longitude1 = longitude0
				+ (D - (1 + 2 * T + C) * D * D * D / 6 + (5 - 2 * C + 28 * T
						- 3 * C * C + 8 * ee + 24 * T * T)
						* D * D * D * D * D / 120) / Math.cos(fai);
		latitude1 = fai
				- (NN * Math.tan(fai) / R)
				* (D * D / 2 - (5 + 3 * T + 10 * C - 4 * C * C - 9 * ee) * D
						* D * D * D / 24 + (61 + 90 * T + 298 * C + 45 * T * T
						- 256 * ee - 3 * C * C)
						* D * D * D * D * D * D / 720);
		// 转换为度 DD
		output[0] = longitude1 / iPI;
		output[1] = latitude1 / iPI;
		return output;
		// *longitude = longitude1 / iPI;
		// *latitude = latitude1 / iPI;
	}

	/**
	 * 由经纬度反算成高斯投影坐标【最终结果以米为单位】
	 * 
	 * @param ap0
	 * @return
	 */
	public static APoint BLToGauss(APoint ap0) {
		return BLToGauss(ap0.getX(), ap0.getY());
	}

//	/**
//	 * 
//	 * @Title: wgs2GcjWeb
//	 * @Description: 原始坐标系转化为盐都天地图坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static APoint wgs2GcjWeb(double longitude, double latitude) {
//		APoint ap0 = new APoint(longitude, latitude);
//		return GcjWebGpsCorrecter.wgs2gcjweb(ap0);
//	}

//	/**
//	 * 
//	 * @Title: wgs2GcjWeb
//	 * @Description: 原始坐标系转化为盐都天地图坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static APoint wgs2GcjWeb(APoint ap0) {
//		return GcjWebGpsCorrecter.wgs2gcjweb(ap0);
//	}

//	/**
//	 * 
//	 * @Title: wgs2GcjWeb
//	 * @Description: 原始坐标系转化为盐都天地图坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static List<APoint> wgs2GcjWeb(List<APoint> ap0s) {
//		List<APoint> ap1s = Lists.newArrayList();
//		for (APoint ap0 : ap0s) {
//			ap1s.add(wgs2GcjWeb(ap0));
//		}
//		return ap1s;
//	}

//	/**
//	 * 
//	 * @Title: gcjWeb2wgs
//	 * @Description: 盐都天地图坐标系=>原始坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static APoint gcjWeb2wgs(double longitude, double latitude) {
//		APoint ap0 = new APoint(longitude, latitude);
//		return gcjWeb2wgs(ap0);
//	}

//	/**
//	 * 
//	 * @Title: gcjWeb2wgs
//	 * @Description: 盐都天地图坐标系=>原始坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static APoint gcjWeb2wgs(APoint ap0) {
//		APoint ap1 = GcjWebGpsCorrecter.gcjweb2wgs(ap0);
//		return ap1;
//	}

//	/**
//	 * 
//	 * @Title: gcjWeb2wgs
//	 * @Description: 盐都天地图坐标系=>原始坐标系
//	 * @return APoint
//	 * @throws
//	 */
//	public static List<APoint> gcjWeb2wgs(List<APoint> ap0s) {
//		List<APoint> ap1s = Lists.newArrayList();
//		for (APoint ap0 : ap0s) {
//			ap1s.add(gcjWeb2wgs(ap0));
//		}
//		return ap1s;
//	}
}
