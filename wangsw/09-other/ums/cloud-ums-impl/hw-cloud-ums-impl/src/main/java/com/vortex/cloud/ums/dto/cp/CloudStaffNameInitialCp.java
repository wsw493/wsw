package com.vortex.cloud.ums.dto.cp;

import java.util.Comparator;

import com.vortex.cloud.ums.model.CloudStaff;

public class CloudStaffNameInitialCp implements Comparator {
	public int compare(Object object1, Object object2) {// 实现接口中的方法
		CloudStaff p1 = (CloudStaff) object1; // 强制转换
		CloudStaff p2 = (CloudStaff) object2;
		// 为空直接返回
		if (p1 == null && p2 == null) {
			return 0;
		}
		if (p1 == null || p1.getNameInitial() == null) {
			return 1;
		}
		if (p2 == null || p2.getNameInitial() == null) {
			return -1;
		}
		char[] thisarr = p1.getNameInitial().toCharArray(); // 存储被包装
															// 字符串的字符数组

		char[] otherarr = p2.getNameInitial().toCharArray(); // 存储要比较的字符串的字符数组

		/* 取得循环次数，为两个字符串的长度的最小值 */
		int iterate = thisarr.length < otherarr.length ? thisarr.length : otherarr.length;

		boolean mlowercase; // 记录被封装的字符串循环到的字符是否为小写

		boolean olowercase; // 记录要比较的字符串循环到的字符是否为小写

		char thisletter; // 记录被封装的字符串循环到的字符

		char otherletter; // 记录要比较的字符串循环到的字符

		/* 字符串相等，则返回0 */
		if (p1.getNameInitial().equals(p2.getNameInitial())) {
			return 0;
		}

		/* 循环字符串，做比较 */
		for (int i = 0; i < iterate; i++) {
			mlowercase = this.isLowercase(thisarr[i]);
			olowercase = this.isLowercase(otherarr[i]);

			/* 把比较字符变成大写 */
			thisletter = mlowercase ? (char) (thisarr[i] - 32) : thisarr[i];
			otherletter = olowercase ? (char) (otherarr[i] - 32) : otherarr[i];

			/* 比较 */
			if (thisletter != otherletter) { // 比较字母大小，不相等，则取差值，字母小的在前面
				return (thisletter - otherletter);
			} else { // 字母的大写形式相同
				if (mlowercase == olowercase) { // 此位置大小写形式相同,判断下一个字符；
					continue;
				} else if (mlowercase) { // 被封装的字符为小写,则返回负值
					return 32;
				} else if (olowercase) { // 比较字符串的字符为小写，则返回正直
					return -32;
				}
			}

		}

		/* 如果循环好之后还分不出大小，则小的排在后面 */
		return (thisarr.length < otherarr.length ? -1 : 1);
	}

	// 通过码值，来判断字符是否为小写字母
	private boolean isLowercase(char ch) {
		if ((int) ch >= 97 && (int) ch <= 122) {
			return true;
		} else {
			return false;
		}
	}
}