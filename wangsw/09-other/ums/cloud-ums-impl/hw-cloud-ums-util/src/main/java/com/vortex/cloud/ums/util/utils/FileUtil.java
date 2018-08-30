package com.vortex.cloud.ums.util.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 文件的操作
 * 
 * @author xiangdj
 * 
 */
public class FileUtil {
	public static final String LINE = "\r\n";

	private static final int BUFFER_SIZE = 50 * 1024;// 缓冲区大小

	private static Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static String getSizeString(long size) {
		String ret = " byte";
		if (size > 1023) {
			ret = " KB";
			size = size / 1024;
			if (size > 1023) {
				ret = " MB";
				size = size / 1024;
				if (size > 1023) {
					size = size / 1024;
					ret = " GB";
				}
			}
		}
		return size + ret;
	}

	/**
	 * 得到文件名字的后缀 不含.
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileExtend(String filename) {
		if (filename == null) {
			return "";
		}
		int index = filename.lastIndexOf(".");
		if (index < 0) {
			return "";
		}
		return filename.substring(index + 1);
	}

	/**
	 * 得到文件名字的后缀 含.
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileSuffix(String filename) {
		String fileSuffix = null;
		if (!(StringUtil.isNullOrEmpty(filename) || filename.indexOf(".") == -1)) {
			fileSuffix = filename.substring(filename.indexOf("."));
		}

		return StringUtil.clean(fileSuffix);
	}

	/**
	 * 得到文件名字的前缀
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFilePreSuffix(String filename) {
		String filePreSuffix = filename;
		if (!(StringUtil.isNullOrEmpty(filename) || filename.indexOf(".") == -1)) {
			filePreSuffix = filename.substring(0, filename.indexOf("."));
		}

		return StringUtil.clean(filePreSuffix);
	}

	/**
	 * 是否是office文件
	 * 
	 * @param extend
	 * @return
	 */
	public static boolean isOfficeByExtend(String extend) {

		if (!StringUtil.isNullOrEmpty(extend)) {
			extend = extend.toLowerCase();
		}
		return "xls".equals(extend) || "xlsx".equals(extend) || "doc".equals(extend) || "docx".equals(extend) || "ppt".equals(extend) || "pptx".equals(extend);
	}

	/**
	 * 是否是office文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isOfficeByFileName(String fileName) {
		String extend = getFileExtend(fileName);
		return isOfficeByExtend(extend);
	}

	public static boolean copyFile(File from, File to) {
		boolean isSuccess = false;

		if ((!from.exists()) || (to.exists())) {
			return isSuccess;
		}

		String destAddress = to.getAbsolutePath();
		String destDir = destAddress.substring(0, destAddress.lastIndexOf(File.separator));

		File dir = new File(destDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(from), BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(to), BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			while (in.read(buffer) > 0) {
				out.write(buffer);
			}
			out.flush();
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}

		}

		return isSuccess;
	}

	public static boolean copyInputStreamToFile(InputStream source, File destination) {
		boolean isSuccess = false;

		try {
			FileUtils.copyInputStreamToFile(source, destination);
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			log.error(e.getMessage());
		}

		return isSuccess;
	}

	private static boolean copyFileByLines(File from, File to) {
		boolean isSuccess = false;

		if ((!from.exists()) || (to.exists())) {
			return isSuccess;
		}

		String destAddress = to.getAbsolutePath();
		String destDir = destAddress.substring(0, destAddress.lastIndexOf(File.separator));

		File dir = new File(destDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new FileReader(from));
			out = new BufferedWriter(new FileWriter(to));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = in.readLine()) != null) {
				// 显示行号

				if (tempString.indexOf("com.vortex.ydhw.dataaccess.service.impl.UploadDataServiceImpl") != -1) {
					out.write(tempString + "\r\n");
					System.out.println("line " + line + ": " + tempString);
				}

				line++;
			}
			out.flush();
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}

		}

		return isSuccess;
	}

	/**
	 * 复制文件到指定的地方
	 * 
	 * @param fromPathname
	 *            源文件
	 * @param toPathname
	 *            指定的文件
	 * @return
	 */
	public static boolean copyFile(String fromPathname, String toPathname) {
		File from = new File(fromPathname);
		File to = new File(toPathname);
		try {

			if (!from.exists()) {
				from.createNewFile();
			}
			if (!to.exists()) {
				to.createNewFile();
			}
			// return copyFile(from, to);
			return copyFileByLines(from, to);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static void copyDir(String srcDir, String destDir) throws IOException {
		File src = new File(srcDir);
		File dest = new File(destDir);

		if ((!src.exists()) || (!src.isDirectory()) || (dest.exists())) {
			return;
		}

		File[] files = src.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				copyDir(f.getAbsolutePath(), destDir + "\\" + f.getName() + "\\");
			} else
				copyFile(f.getAbsolutePath(), destDir + f.getName());
		}
	}

	public static String readFile(String fileAddr) throws IOException {
		File file = new File(fileAddr);

		if (!file.exists()) {
			return "";
		}

		BufferedReader fr = null;

		StringBuffer datas = new StringBuffer();
		try {
			fr = new BufferedReader(new FileReader(fileAddr));
			String line;
			while ((line = fr.readLine()) != null)
				datas.append(line + "\n");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (fr != null) {
				fr.close();
			}
		}

		return datas.toString();
	}

	public static void writeFile(String destFileAddr, String datas) throws IOException {
		String destDir = destFileAddr.substring(0, destFileAddr.lastIndexOf(File.separator));
		File dir = new File(destDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(destFileAddr)));

			pw.print(datas);
			pw.flush();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	public static void deleteFile(String fileAddr) {
		File file = new File(fileAddr);

		if (file.exists()) {
			file.delete();
		}

	}

	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @param folderFullPath
	 *            = null 或 "" 都删除默认临时目录
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteAllFile(String folderFullPath) throws Exception {
		boolean ret = false;
		if (folderFullPath == null || "".equals(folderFullPath)) {
			return ret;
		}
		try {
			File file = new File(folderFullPath);
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] fileList = file.listFiles();
					for (int i = 0; i < fileList.length; i++) {
						String filePath = fileList[i].getPath();
						deleteAllFile(filePath);
					}
				}
				if (file.isFile()) {
					file.delete();
				}
			}
			ret = true;
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}

		return ret;
	}

	public static void main(String[] args) {
		/*
		 * copyFile(
		 * "C:\\Users\\dejunx\\Desktop\\ydhw-deploy\\ydhw-dataserver.log.2012-08-25"
		 * , "D:\\test20120827.txt");
		 */
		System.out.println(findFileBySuffix("D:/vedio", ""));
	}

	/**
	 * 查找指定目录下的文件，返回的是文件的全路径
	 * 
	 * @param findDirectory
	 * @param suffix
	 * @return
	 */
	public static List<String> findFileBySuffix(String findDirectory, String suffix) {
		return findFileBySuffix(new File(findDirectory), suffix);
	}

	/**
	 * 查找指定目录下的文件，返回的是文件的全路径
	 * 
	 * @param file
	 * @param suffix
	 * @return
	 */
	public static List<String> findFileBySuffix(File file, String suffix) {
		List<String> list = new ArrayList<String>();
		File[] fs = file.listFiles();
		if (fs == null || fs.length == 0)
			return list;
		String directory = null;
		for (int i = 0; i < fs.length; i++) {
			directory = fs[i].getAbsolutePath();
			if (fs[i].isDirectory()) {
				list.addAll(findFileBySuffix(fs[i], suffix));
			} else if (fs[i].isFile()) {
				if (fs[i].getName().toUpperCase().indexOf(suffix) != -1) {
					list.add(directory);
				}
			}
		}
		return list;
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}

	/**
	 * 
	 * @Title: GetImageStr @param imgFilePath 图片路径 @return @Description:
	 *         将图片文件转化为字节数组字符串，并对其进行Base64编码处理 @return String @throws
	 */
	public static String getImageStrByBase64(String imgFilePath) {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFilePath);
			data = new byte[in.available()];
			in.read(data);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 对字节数组Base64编码
		return Base64.encodeBase64String(data);// 返回Base64编码过的字节数组字符串
	}

	/**
	 * 根据网络路径，得到文件的字节流
	 * 
	 * @param urlStr
	 *            网络地址
	 * @return
	 */
	public static byte[] getBytesByUrl(String urlStr) {
		return FileUtil.getBytesByUrl(urlStr, null, null);
	}

	/**
	 * 根据网络路径，得到文件的字节流
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 *            连接超时多少秒
	 * @param readTimeout
	 *            读取超时多少秒
	 * @return
	 */
	public static byte[] getBytesByUrl(String urlStr, Integer connectTimeout, Integer readTimeout) {
		URL url;
		byte[] bytes = null;
		URLConnection conn = null;
		if (connectTimeout == null || connectTimeout < 0) {
			connectTimeout = 10;
		}

		if (readTimeout == null || readTimeout < 0) {
			readTimeout = 10;
		}
		try {
			url = new URL(urlStr);
			conn = url.openConnection();
			conn.setConnectTimeout(1000 * connectTimeout);
			conn.setReadTimeout(1000 * readTimeout);
			bytes = IOUtils.toByteArray(conn);
		} catch (MalformedURLException e) {
			log.error("getByteByUrl", e);
		} catch (IOException e) {
			log.error("getByteByUrl", e);
		} finally {
		}

		return bytes;
	}

}
