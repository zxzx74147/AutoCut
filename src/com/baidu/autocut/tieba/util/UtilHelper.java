package com.baidu.autocut.tieba.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilHelper {

	public static final Pattern mJavaResPattern = Pattern
			.compile("R.(layout|color|drawable|anim).[a-zA-Z_0-9]*");

	public static final Pattern mJavaStringPattern = Pattern
			.compile("R.string.[a-zA-Z_0-9]*");

	public static final Pattern mXmlDrawablePattern = Pattern
			.compile("@drawable/[a-zA-Z_0-9]*");
	public static final Pattern mXmlResPattern = Pattern
			.compile("@(layout|anim|color)/[a-zA-Z_0-9]*");

	public static final Pattern mXmlStringPattern = Pattern
			.compile("@string/[a-zA-Z_0-9]*");

	public static final Pattern mJavaBlock = Pattern.compile("[a-zA-Z_0-9]*");
	public static final Pattern mJavaPackBlock = Pattern.compile("[a-zA-Z_0-9.]*");
	public static final String JAVA_PACK = "package";
	public static final String JAVA_IMPORT = "import";

	public final static String JAVA_SUFFIX = ".java";

	public final static String XML_SUFFIX = ".xml";
	public final static String JPG_SUFFIX = ".jpg";
	public final static String PNG_SUFFIX = ".png";

	private final static String[] mCom = { "Activity", "Service", "Provider", "Receiver" };

	public interface ICommonFileCallBack {
		public void dealFile(File file);
	}

	public interface IJavaFileCallBack {
		public void dealFile(File file, String pack);
	}

	public static String getFilePath(String key) {
		return HashedPoolMananger.getCodePathByKey(key);
	}

	public static void printSet(Set<String> keys) {
		if (keys == null) {
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		for (String key : keys) {
			list.add(key);
		}
		Collections.sort(list);
		for (String key : list) {
			System.out.println(key);
		}
	}

	public static void commonDft(File file, ICommonFileCallBack callback) {
		if (!file.exists()) {
			new FileNotFoundException(file.getAbsolutePath() + "not found");
		}
		if (file.isDirectory()) {
			File[] child = file.listFiles();
			for (File item : child) {
				commonDft(item, callback);
			}
		} else {
			callback.dealFile(file);
		}
	}

	public static void javaDft(File file, IJavaFileCallBack callback) {
		javaDft(file, "", callback);
	}

	public static void javaDft(File file, String pack, IJavaFileCallBack callback) {
		if (!file.exists()) {
			new FileNotFoundException(file.getAbsolutePath() + "not found");
		}
		if (file.isDirectory()) {
			File[] child = file.listFiles();
			for (File item : child) {
				javaDft(item, pack + item.getName() + ".", callback);
			}
		} else {
			String name = file.getName();
			if (name.endsWith(JAVA_SUFFIX)) {
				pack = pack.substring(0,
						pack.indexOf(UtilHelper.JAVA_SUFFIX));
				callback.dealFile(file, pack);
			}
		}
	}

	public static boolean checkReg(String str, LinkedList<Pattern> patterns) {
		boolean result = false;
		if (str == null || patterns == null) {
			return result;
		}
		for (Pattern pattern : patterns) {
			if (checkReg(str, pattern)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean checkReg(String str, Pattern pattern) {
		boolean result = false;
		if (str == null || pattern == null) {
			return result;
		}

		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {// matcher.matchers() {
			result = true;
		}
		return result;
	}

	public static boolean isComponent(String name) {
		for (int i = 0; i < mCom.length; i++) {
			if (name.endsWith(mCom[i])) {
				return true;
			}
		}
		return false;
	}
}
