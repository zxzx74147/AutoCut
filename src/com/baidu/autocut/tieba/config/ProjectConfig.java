package com.baidu.autocut.tieba.config;

import java.util.LinkedList;
import java.util.regex.Pattern;



public class ProjectConfig {

	public String mProjectName = null;
	public boolean mNeedClean = true;
	public String mPath;
	public String packageName = null;
	public LinkedList<String> JAVA_FOLDERS = new LinkedList<String>();

	public LinkedList<String> JAVA_KEEP_FOLDERS = new LinkedList<String>();
	
	public LinkedList<String> JAVA_KEEP_FILES = new LinkedList<String>();
	
	public LinkedList<Pattern> JAVA_KEEP_RULES = new LinkedList<Pattern>();

	public LinkedList<String> XML_FOLDERS = new LinkedList<String>();

	public LinkedList<String> XML_KEEP_FOLDERS = new LinkedList<String>();
	
	public LinkedList<String> XML_KEEP_FILES = new LinkedList<String>();
	
	public LinkedList<Pattern> XML_KEEP_RULES = new LinkedList<Pattern>();
	
	public LinkedList<String> IMG_FOLDERS = new LinkedList<String>();

	public LinkedList<String> IMG_KEEP_FOLDERS = new LinkedList<String>();
	
	public LinkedList<String> IMG_KEEP_FILES = new LinkedList<String>();
	
	public LinkedList<Pattern> IMG_KEEP_RULES = new LinkedList<Pattern>();

	

}
