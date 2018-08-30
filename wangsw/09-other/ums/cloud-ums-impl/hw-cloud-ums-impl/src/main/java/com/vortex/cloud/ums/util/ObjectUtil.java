package com.vortex.cloud.ums.util;

import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.Date;

import com.vortex.cloud.vfs.common.lang.DateUtil;



public class ObjectUtil {
	public static Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			if(value instanceof Date){
				return DateUtil.format((Date)value,DateUtil.DATETIME_FORMAT);
			}
			return value;
		} catch (Exception e) {
			System.out.println("属性不存在");
			return null;
		}
	}
	
	
	
	   /** 
     * 
     * Description:将Clob对象转换为String对象,Blob处理方式与此相同 
     * 
     * @param clob 
     * @return 
     * @throws Exception 
     */ 
    public static String oracleClob2Str(Clob clob) throws Exception { 
        return (clob != null ? clob.getSubString(1, (int) clob.length()) : null); 
    } 

    /** 
     * 
     * Description:将string对象转换为Clob对象,Blob处理方式与此相同 
     * 
     * @param str 
     * @param lob 
     * @return 
     * @throws Exception 
     */ 
    public static Clob oracleStr2Clob(String str, Clob lob) throws Exception { 
        Method methodToInvoke = lob.getClass().getMethod( 
                "getCharacterOutputStream", (Class[]) null); 
        Writer writer = (Writer) methodToInvoke.invoke(lob, (Object[]) null); 
        writer.write(str); 
        writer.close(); 
        return lob; 
    } 



}
