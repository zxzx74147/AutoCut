package com.baidu.autocut.tieba.util;

import java.io.File;
import java.util.HashMap;

import com.baidu.autocut.tieba.config.Config;
import com.baidu.autocut.tieba.config.ProjectConfig;
import com.baidu.autocut.tieba.item.BaseItem;
import com.baidu.autocut.tieba.util.UtilHelper.ICommonFileCallBack;
import com.baidu.autocut.tieba.util.UtilHelper.IJavaFileCallBack;

public class HashedPoolMananger {
	// 代码行数
	public static int line = 0;

	// 存储代码与文件的映射
	public static HashMap<String, String> mCodeKeyFileHash = null;
	// 存储图片与文件的映射
	public static HashMap<String, String> mImageKeyFileHash = null;

	// 存储索引的java文件
	private static HashMap<String, BaseItem> mHashedJavaItems = null;
	// 存储索引的xml文件
	private static HashMap<String, BaseItem> mHashedXmlItems = null;
	// 存储索引的图片文件
	private static HashMap<String, Integer> mHashedImageItems = null;
	// 存储索引的string字段
	private static HashMap<String, Integer> mHashedSringItems = null;

	// 计数器
	private static int mJavaNum = 0;
	private static int mXmlNum = 0;
	private static int mImageNum = 0;

	

	// 通过key查找文件路径
	public static String getImagePathByKey(String key) {
		if (mImageKeyFileHash.containsKey(key)) {
			return mImageKeyFileHash.get(key);
		}
		return null;
	}

	public static boolean checkImageFile(String key) {
		return mImageKeyFileHash.containsKey(key);
	}

	public static String getCodePathByKey(String key) {
		if (mCodeKeyFileHash.containsKey(key)) {
			return mCodeKeyFileHash.get(key);
		}
		return null;
	}

	public static boolean checkCodeFile(String key) {
		return mCodeKeyFileHash.containsKey(key);
	}

	public static boolean checkHashedJavaKey(String key) {
		return mHashedJavaItems.containsKey(key);
	}

	public static void putHashedJavaKey(String key, BaseItem item) {
		mHashedJavaItems.put(key, item);
	}

	public static boolean checkAndPutHashedJavaKey(String key, BaseItem item) {
		boolean hasHashed = checkHashedJavaKey(key);
		
		if (hasHashed == false) {
			putHashedJavaKey(key, item);
		}
		return hasHashed;
	}

	public static HashMap<String, BaseItem> getHashedJavaItem() {
		return mHashedJavaItems;
	}

	public static boolean checkHashedXmlKey(String key) {
		return mHashedXmlItems.containsKey(key);
	}

	public static void putHashedXmlKey(String key, BaseItem item) {
		mHashedXmlItems.put(key, item);
	}

	public static boolean checkAndPutHashedXmlKey(String key, BaseItem item) {
		boolean hasHashed = checkHashedXmlKey(key);
		if (hasHashed == false) {
			putHashedXmlKey(key, item);
		}
		return hasHashed;
	}

	public static HashMap<String, BaseItem> getHashedXmlItem() {
		return mHashedXmlItems;
	}

	public static boolean checkHashedImageKey(String key) {
		return mHashedImageItems.containsKey(key);
	}

	public static void putHashedImageKey(String key,int value) {
		mHashedImageItems.put(key, value);
	}
	
	public static void putHashedImageKey(String key) {
		mHashedImageItems.put(key, 1);
	}

	public static boolean checkAndPutHashedImageKey(String key) {
		boolean hasHashed = checkHashedImageKey(key);
		if (hasHashed == false) {
			putHashedImageKey(key,1);
		}else{
			putHashedImageKey(key,mHashedImageItems.get(key)+1);
		}
		return hasHashed;
	}

	public static boolean checkHashedStringKey(String key) {
		return mHashedSringItems.containsKey(key);
	}

	public static void putHashedStringKey(String key) {
		mHashedSringItems.put(key, 0);
	}

	public static boolean init() {
		boolean result = true;

		mHashedJavaItems = new HashMap<String, BaseItem>();
		mHashedXmlItems = new HashMap<String, BaseItem>();
		mHashedImageItems = new HashMap<String, Integer>();
		mHashedSringItems = new HashMap<String, Integer>();

		mCodeKeyFileHash = new HashMap<String, String>();
		mImageKeyFileHash = new HashMap<String, String>();

		for (int i = 0; i < Config.mConfigs.length; i++) {
			ProjectConfig mConfig = Config.mConfigs[i];
			for (int j = 0; j < mConfig.JAVA_FOLDERS.size(); j++) {
				File rootJava = new File(mConfig.mPath
						+ mConfig.JAVA_FOLDERS.get(j));
				UtilHelper.javaDft(rootJava, mJavaCallBack);
			}
			for (int j = 0; j < mConfig.XML_FOLDERS.size(); j++) {
				File rootXml = new File(mConfig.mPath
						+ mConfig.XML_FOLDERS.get(j));
				UtilHelper.commonDft(rootXml, mCommonCallBack);
			}
		}

		mHashedImageItems.put("icon", 0);
		System.out.println("All java:" + mJavaNum);
		System.out.println("All xml:" + mXmlNum);
		System.out.println("All drawable:" + mImageNum);
		return result;
	}

	private static IJavaFileCallBack mJavaCallBack = new IJavaFileCallBack() {
		@Override
		public void dealFile(File file, String pack) {
			String filename = file.getName();
			if (filename.endsWith(UtilHelper.JAVA_SUFFIX)) {
				mCodeKeyFileHash.put(pack, file.getAbsolutePath());
				mJavaNum++;
				// System.out.println(name+"|"+file.getAbsolutePath());\
			}
		}
	};
	
	private static ICommonFileCallBack mCommonCallBack = new ICommonFileCallBack() {
		
		@Override
		public void dealFile(File file) {
			String filename = file.getName();
			if (filename.endsWith(UtilHelper.XML_SUFFIX)) {
				String name = filename.substring(0, filename.indexOf("."));
				mCodeKeyFileHash.put(name, file.getAbsolutePath());
				mXmlNum++;
			} else if (filename.endsWith(UtilHelper.JPG_SUFFIX)
					|| filename.endsWith(UtilHelper.PNG_SUFFIX)) {
				String name = filename.substring(0, filename.indexOf("."));
				mImageKeyFileHash.put(name, file.getAbsolutePath());
				mImageNum++;
			}
		}
	};
	

	public static void printAll() {
		System.out.println("总行数" + HashedPoolMananger.line);
	}

}
