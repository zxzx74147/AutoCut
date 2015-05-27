package com.baidu.autocut.tieba.processor;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.baidu.autocut.tieba.config.Config;
import com.baidu.autocut.tieba.config.ProjectConfig;
import com.baidu.autocut.tieba.item.BaseItem;
import com.baidu.autocut.tieba.item.JavaItem;
import com.baidu.autocut.tieba.item.XmlItem;
import com.baidu.autocut.tieba.util.HashedPoolMananger;
import com.baidu.autocut.tieba.util.UtilHelper;
import com.baidu.autocut.tieba.util.UtilHelper.ICommonFileCallBack;
import com.baidu.autocut.tieba.util.UtilHelper.IJavaFileCallBack;

public class CodeCleaner extends BaseCleaner {

	private LinkedList<BaseItem> mLinkedItems = null;

	public CodeCleaner() {
		init();
	}

	private void init() {
		mLinkedItems = new LinkedList<BaseItem>();
	}

	@Override
	public boolean readConfig() {
		JavaFileFilter mJavaFilter = new JavaFileFilter();
		XmlFileFilter mXmlFilter = new XmlFileFilter();
		ImgFileFilter mImgFilter = new ImgFileFilter();
		
		for (int i = 0; i < Config.mConfigs.length; i++) {
			ProjectConfig mConfig = Config.mConfigs[i];
			for (int j = 0; j < mConfig.JAVA_KEEP_FILES.size(); j++) {
				JavaItem item = new JavaItem(mConfig.JAVA_KEEP_FILES.get(j));
				mLinkedItems.add(item);
				HashedPoolMananger.putHashedJavaKey(item.getKey(), item);
			}
			for (int k = 0; k < mConfig.JAVA_FOLDERS.size(); k++) {
				String folder = mConfig.JAVA_FOLDERS.get(k);
				File rootJava = new File(mConfig.mPath + folder);
				if (mConfig.JAVA_KEEP_FOLDERS.contains(folder)) {// 需要保留的文件夹全部标记
					continue;
				} else {
					mJavaFilter.mPatterns = mConfig.JAVA_KEEP_RULES;
				}
				UtilHelper.javaDft(rootJava, mJavaFilter);
			}
			for (int k = 0; k < mConfig.JAVA_KEEP_FOLDERS.size(); k++) {
				String folder = mConfig.JAVA_KEEP_FOLDERS.get(k);
				File rootJava = new File(mConfig.mPath + folder);
				mJavaFilter.mPatterns = null;
				UtilHelper.javaDft(rootJava, mJavaFilter);
			}

			

			for (int j = 0; j < mConfig.XML_KEEP_FILES.size(); j++) {
				XmlItem item = new XmlItem(mConfig.XML_KEEP_FILES.get(j));
				mLinkedItems.add(item);
				HashedPoolMananger.putHashedXmlKey(item.getKey(), item);
			}
			for (int k = 0; k < mConfig.XML_FOLDERS.size(); k++) {
				String folder = mConfig.XML_FOLDERS.get(k);
				File rootXML = new File(mConfig.mPath + folder);
				if (mConfig.XML_KEEP_FOLDERS.contains(folder)) {// 需要保留的文件夹全部标记
					continue;
				} else {
					mXmlFilter.mPatterns = mConfig.XML_KEEP_RULES;
				}
				UtilHelper.commonDft(rootXML, mXmlFilter);
			}
			for (int k = 0; k < mConfig.XML_KEEP_FOLDERS.size(); k++) {
				String folder = mConfig.XML_KEEP_FOLDERS.get(k);
				File rootXML = new File(mConfig.mPath + folder);
				mXmlFilter.mPatterns = null;
				UtilHelper.commonDft(rootXML, mXmlFilter);
			}

			for (int j = 0; j < mConfig.IMG_KEEP_FILES.size(); j++) {
				HashedPoolMananger.putHashedImageKey(mConfig.IMG_KEEP_FILES.get(j));
			}
			for (int k = 0; k < mConfig.IMG_FOLDERS.size(); k++) {
				String folder = mConfig.IMG_FOLDERS.get(k);
				File rootJava = new File(mConfig.mPath + folder);
				if (mConfig.IMG_KEEP_FOLDERS.contains(folder)) {// 需要保留的文件夹全部标记
					continue;
				} else {
					mImgFilter.mPatterns = mConfig.IMG_KEEP_RULES;
				}
				UtilHelper.commonDft(rootJava, mImgFilter);
			}
			for (int k = 0; k < mConfig.IMG_KEEP_FOLDERS.size(); k++) {
				String folder = mConfig.IMG_KEEP_FOLDERS.get(k);
				File rootImg = new File(mConfig.mPath + folder);
				mImgFilter.mPatterns = null;
				UtilHelper.commonDft(rootImg, mImgFilter);
			}
		}
		return false;
	}

	private class JavaFileFilter implements IJavaFileCallBack {

		public LinkedList<Pattern> mPatterns = null;

		@Override
		public void dealFile(File file, String pack) {

			if (mPatterns == null || UtilHelper.checkReg(file.getName(), mPatterns)) {
				//if(mPatterns != null)
				// System.out.println(pack);
				JavaItem item = new JavaItem(pack);
				mLinkedItems.add(item);
				HashedPoolMananger.putHashedJavaKey(item.getKey(), item);
			}

		}
	}

	private class XmlFileFilter implements ICommonFileCallBack {

		public LinkedList<Pattern> mPatterns = null;

		@Override
		public void dealFile(File file) {
			if (mPatterns == null || UtilHelper.checkReg(file.getName(), mPatterns)) {
				String filename = file.getName();
				if (filename.endsWith(UtilHelper.XML_SUFFIX)) {
					// System.out.println(filename);
					String name = filename.substring(0, filename.indexOf("."));
					XmlItem item = new XmlItem(name);
					mLinkedItems.add(item);
					HashedPoolMananger.putHashedXmlKey(item.getKey(), item);
				}
			}
		}
	}

	private class ImgFileFilter implements ICommonFileCallBack {

		public LinkedList<Pattern> mPatterns = null;

		@Override
		public void dealFile(File file) {
			if (mPatterns == null || UtilHelper.checkReg(file.getName(), mPatterns)) {
				String filename = file.getName();
				if (filename.endsWith(UtilHelper.JPG_SUFFIX) || filename.endsWith(UtilHelper.PNG_SUFFIX)) {
					// System.out.println(filename);
					String name = filename.substring(0, filename.indexOf("."));
					HashedPoolMananger.putHashedImageKey(name);
				}
			}
		}
	}

	@Override
	public boolean scanf() {
		while (mLinkedItems.size() > 0) {
			BaseItem head = mLinkedItems.removeFirst();

			LinkedList<JavaItem> javaItem = head.getJavaItem();
			LinkedList<XmlItem> xmlItems = head.getXmlItem();
			LinkedList<String> images = head.getImageItem();
			if (javaItem != null) {
				for (JavaItem item : javaItem) {
					if (HashedPoolMananger.checkCodeFile(item.getKey())
							&& HashedPoolMananger.checkHashedJavaKey(item
									.getKey()) == false) {
						if(UtilHelper.isComponent(item.getKey())){
							System.out.println("No statement in AndroidMainifest:"+item.getKey());
							continue;
						}
						HashedPoolMananger
								.putHashedJavaKey(item.getKey(), item);
						mLinkedItems.add(item);
					}
				}
			}

			if (xmlItems != null) {
				for (XmlItem item : xmlItems) {
					if (HashedPoolMananger.checkCodeFile(item.getKey())
							&& HashedPoolMananger.checkAndPutHashedXmlKey(
									item.getKey(), item) == false) {
						mLinkedItems.add(item);
					}
				}
			}
			if (images != null) {
				for (String img : images) {
					HashedPoolMananger.checkAndPutHashedImageKey(img);
				}
			}
		}

		return true;
	}

	@Override
	public boolean clean() {
		if(Config.IS_DO_DELETE == true){
			System.out.println("Warnning，执行删除!!!!");
		}else{
			System.out.println("Don't Worry, 只是标记一下!!!!");
		}
		for (int i = 0; i < Config.mConfigs.length; i++) {
			ProjectConfig mConfig = Config.mConfigs[i];
			System.out.println(mConfig.mProjectName);
			for (int k = 0; k < mConfig.JAVA_FOLDERS.size(); k++) {
				if (mConfig.JAVA_KEEP_FILES.contains(mConfig.JAVA_FOLDERS.get(k)) == false) {
					File rootJava = new File(mConfig.mPath
							+ mConfig.JAVA_FOLDERS.get(k));
					UtilHelper.javaDft(rootJava, mJavaCleaner);
				}
			}

			for (int k = 0; k < mConfig.XML_FOLDERS.size(); k++) {
				if (mConfig.XML_KEEP_FILES.contains(mConfig.XML_FOLDERS.get(k)) == false) {
					File rootXml = new File(mConfig.mPath
							+ mConfig.XML_FOLDERS.get(k));
					UtilHelper.commonDft(rootXml, mXmlCleaner);
				}
			}

			for (int k = 0; k < mConfig.IMG_FOLDERS.size(); k++) {
				if (mConfig.IMG_KEEP_FILES.contains(mConfig.IMG_FOLDERS.get(k)) == false) {
					File rootImg = new File(mConfig.mPath
							+ mConfig.IMG_FOLDERS.get(k));
					UtilHelper.commonDft(rootImg, mImgCleaner);
				}
			}
		}
		return true;
	}

	private IJavaFileCallBack mJavaCleaner = new IJavaFileCallBack() {
		@Override
		public void dealFile(File file, String pack) {

			String filename = file.getName();
			if (filename.endsWith(UtilHelper.JAVA_SUFFIX)) {
				if (HashedPoolMananger.checkHashedJavaKey(pack) == false) {
					System.out.println("delete java:"+pack);
					if (Config.IS_DO_DELETE == true) {
						try {
							file.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	private ICommonFileCallBack mXmlCleaner = new ICommonFileCallBack() {

		@Override
		public void dealFile(File file) {
			String filename = file.getName();
			if (filename.endsWith(UtilHelper.XML_SUFFIX)) {
				String name = filename.substring(0, filename.indexOf("."));
				if (HashedPoolMananger.checkHashedXmlKey(name) == false) {
					System.out.println("delete xml:"+name);
					if (Config.IS_DO_DELETE == true) {
						try {
							file.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	private ICommonFileCallBack mImgCleaner = new ICommonFileCallBack() {

		@Override
		public void dealFile(File file) {
			String filename = file.getName();
			if (filename.endsWith(UtilHelper.JPG_SUFFIX) || filename.endsWith(UtilHelper.PNG_SUFFIX)) {
				String name = filename.substring(0, filename.indexOf("."));
				if (HashedPoolMananger.checkHashedImageKey(name) == false) {
					System.out.println("delete img:"+name);
					if (Config.IS_DO_DELETE == true) {
						try {
							file.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};


}
