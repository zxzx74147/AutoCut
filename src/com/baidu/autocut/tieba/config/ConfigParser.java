package com.baidu.autocut.tieba.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigParser {
	
	// AndroidMainifest保留字段
	private static final String ACTIVITY = "activity";
	private static final String SERVICE = "service";
	private static final String RECEIVER = "receiver";
	private static final String PROVIDER = "provider";

	private static final String APPLICATION = "application";

	private static final String MAINIFEST = "manifest";

	private static final String ANDROIR_NAME = "android:name";
	private static final String PACKAGE = "package";

	public static ProjectConfig[] parserConfig(String mPath) {

		LinkedList<ProjectConfig> mConfigs = new LinkedList<ProjectConfig>();
		ProjectConfig mProjectConfig = null;
		File mFile = null;
		boolean parsered = true;

		mFile = new File(mPath);
		if (!mFile.exists()) {
			return null;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(mFile));
			String line = "";
			String block = null;
			while ((block = br.readLine()) != null) {
				block = block.trim();
				if (block.startsWith("#")) {// 注释
					continue;
				} else if (block.endsWith(Config.CONNECT)) {// 连接配置
					block = block.substring(0, block.length() - 1);
					line += block + " ";
					continue;
				} else if (block.startsWith("[Project")) {// 新工程配置
					if (mProjectConfig != null) {
						fillConfig(line, mProjectConfig);
						parserMainifest(mProjectConfig);
						mConfigs.add(mProjectConfig);
					}
					mProjectConfig = new ProjectConfig();
					line += block + " ";
					fillConfig(line, mProjectConfig);
					line = "";
				} else {
					line += block + " ";
					fillConfig(line, mProjectConfig);
					line = "";
				}
			}

		} catch (Exception e) {
			parsered = false;
			e.printStackTrace();
			return null;
		} finally {
			try {
				br.close();
			} catch (Exception e) {

			}
		}

		if (parsered) {
			parserMainifest(mProjectConfig);
			mConfigs.add(mProjectConfig);
			return mConfigs.toArray(new ProjectConfig[mConfigs.size()]);
		} else {
			return null;
		}
	}

	public static void fillConfig(String line, ProjectConfig config) {
		// System.out.println(line);
		if (line == null || config == null || line.length() == 0) {
			return;
		}
		line = line.trim();

		if (line.startsWith("[Project")) {
			config.mProjectName = line;
		} else {
			String[] item = line.split("=");
			if (item.length > 2) {
				throw new IllegalArgumentException(item[0] + " Argument Error");
			}
			if (item.length < 2) {
				return;
			}
			String key = item[0].trim();
			String value = item[1].trim();
			if (key.equals("PATH")) {
				if(!value.endsWith(Config.DIVIDER)){
					value = value+Config.DIVIDER;
				}
				config.mPath = value;
			} else if (key.equals("JAVA_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.JAVA_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("JAVA_KEEP_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.JAVA_KEEP_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("JAVA_KEEP_RULES")) {
				String[] values = value.split(" ");
				for (String rule : values) {
					config.JAVA_KEEP_RULES.add(Pattern.compile(rule.trim()));
				}
			} else if (key.equals("JAVA_KEEP_FILES")) {
				String[] values = value.split(" ");
				for (String file : values) {
					config.JAVA_KEEP_FILES.add(file.trim());
				}
			}

			else if (key.equals("XML_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.XML_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("XML_KEEP_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.XML_KEEP_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("XML_KEEP_RULES")) {
				String[] values = value.split(" ");
				for (String rule : values) {
					config.XML_KEEP_RULES.add(Pattern.compile(rule.trim()));
				}
			} else if (key.equals("XML_KEEP_FILES")) {
				String[] values = value.split(" ");
				for (String file : values) {
					config.XML_KEEP_FILES.add(file.trim());
				}
			}

			else if (key.equals("IMG_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.IMG_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("IMG_KEEP_FOLDERS")) {
				String[] values = value.split(" ");
				for (String folder : values) {
					config.IMG_KEEP_FOLDERS.add(folder.trim());
				}
			} else if (key.equals("IMG_KEEP_RULES")) {
				String[] values = value.split(" ");
				for (String rule : values) {
					config.IMG_KEEP_RULES.add(Pattern.compile(rule.trim()));
				}
			} else if (key.equals("IMG_KEEP_FILES")) {
				String[] values = value.split(" ");
				for (String file : values) {
					config.IMG_KEEP_FILES.add(file.trim());
				}
			}

		}
	}

	private static boolean parserMainifest(ProjectConfig config) {

		BufferedReader reader = null;
		try {
			File file = new File(config.mPath + "AndroidManifest.xml");
			if(!file.exists()){
				file = new File(config.mPath + "src/main/AndroidManifest.xml");
			}
			InputStream stream = new FileInputStream(file);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();

			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			dfs(root, config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return true;
	}

	private static String packageName = null;

	private static void dfs(Element ele, ProjectConfig config) {
		String tag = ele.getTagName();

		if (tag.contains(MAINIFEST)) {
			String pack = ele.getAttribute(PACKAGE);
			if (pack != null && pack.length() > 0) {
				packageName = pack;
			}
		}
		if (tag.contains(ACTIVITY) || tag.contains(SERVICE) || tag.contains(PROVIDER)
				|| tag.contains(RECEIVER) || tag.contains(APPLICATION)) {

			String temp = ele.getAttribute(ANDROIR_NAME);
			if (temp != null) {
				if (temp.startsWith(".")) {
					temp = packageName + temp;
				}
				config.JAVA_KEEP_FILES.add(temp);
				//System.out.println(temp);
			}
		}

		NodeList childs = ele.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node item = childs.item(i);
			if (item instanceof Element) {
				dfs((Element) item, config);
			}
		}
	}

}
