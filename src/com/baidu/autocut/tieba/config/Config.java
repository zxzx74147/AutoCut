package com.baidu.autocut.tieba.config;



public class Config {

	public static final int PLATFORM_WINDOWS = 0;
	public static final int PLATFORM_X_NIX = 1;

	public static int mPlatFrom = PLATFORM_X_NIX;
	
	public static String CONNECT = "\\";
	public static String DIVIDER = "/";

	public static ProjectConfig[] mConfigs = null;

	public static final boolean IS_DO_DELETE = false;

	public static boolean init() {
		
		if(DIVIDER.equals("\\")){
			CONNECT = "/";
		}else if(DIVIDER.equals("/")){
			CONNECT = "\\";
		}else{
			System.out.println("Unknown OS platform!!!!!!!!!");
		}
		System.out.println("current pwd:"+System.getProperty("user.dir"));
		//mConfigs = ConfigParser.parserConfig("/Users/zhengxin/Documents/workspace/AutoCut/AutoCut/AutoCutConfig.conf");
		mConfigs = ConfigParser.parserConfig("AutoCutConfig_baobao.conf");
//		mConfigs = ConfigParser.parserConfig("AutoCutConfig_xinyang.conf");
		return true;
	}
	
	
}
