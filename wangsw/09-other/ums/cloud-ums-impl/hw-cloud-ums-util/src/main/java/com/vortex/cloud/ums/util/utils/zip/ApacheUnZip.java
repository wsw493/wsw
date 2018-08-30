package com.vortex.cloud.ums.util.utils.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 可以处理中文文件名
 */
public class ApacheUnZip {
	private static final int buffer = 2048;

	public static void unZip(String path) {
		int count = -1;
		int index = -1;
		String savepath = "";

		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		savepath = path.substring(0, path.lastIndexOf("."));

		try {

			ZipFile zipFile = new ZipFile(path);

			Enumeration<?> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				byte buf[] = new byte[buffer];

				ZipEntry entry = (ZipEntry) entries.nextElement();

				String filename = entry.getName();
				if (filename.indexOf(".") == -1) {
					file = new File(savepath + File.separator + filename);
					if (!file.exists()) {
						file.mkdirs();
					}
					continue;
				}
				index = filename.lastIndexOf("/");
				if (index > -1)
					filename = filename.substring(index + 1);

				filename = savepath + File.separator + filename;

				System.out.println(filename);
				file = new File(filename);
				if (!file.exists()) {
					file.createNewFile();
				}

				is = zipFile.getInputStream(entry);

				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos, buffer);

				while ((count = is.read(buf)) > -1) {
					bos.write(buf, 0, count);
				}

				fos.close();

				is.close();
			}

			zipFile.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (subDirectory == "" && fl.exists() != true)
				fl.mkdir();
			else if (subDirectory != "") {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (subFile.exists() == false)
						subFile.mkdir();
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void unZip(String zipFileName, String outputDirectory) {
		try {
			if (StringUtil.isNullOrEmpty(outputDirectory)) {
				zipFileName = zipFileName.replace('\\', '/');
				outputDirectory = zipFileName.substring(0, zipFileName.lastIndexOf("/"));
			}
			System.out.println("unzip zipFileName = " + zipFileName + ",outputDirectory=" + outputDirectory);
			ZipFile zipFile = new ZipFile(zipFileName);
			Enumeration<ZipEntry> e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			while (e.hasMoreElements()) {
				zipEntry = e.nextElement();
				System.out.println("unziping " + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
					System.out.println("创建目录：" + outputDirectory + File.separator + name);
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					// System.out.println("测试文件1：" +fileName);
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0, fileName.lastIndexOf("/")));
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
					}

					File f = new File(outputDirectory + File.separator + zipEntry.getName());

					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);

					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	public static boolean isPics(String filename) {
		boolean flag = false;

		if (filename.endsWith(".jpg") || filename.endsWith(".gif") || filename.endsWith(".bmp") || filename.endsWith(".png"))
			flag = true;

		return flag;
	}

	public static void main(String[] args) {
		// unZip("F:\\testzip\\szcc-company.zip");
		unZip("F:\\testzip\\testzip.zip", null);
	}
}
