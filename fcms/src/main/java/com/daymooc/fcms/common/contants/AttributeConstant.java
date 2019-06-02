package com.daymooc.fcms.common.contants;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class AttributeConstant {
	
	private static Prop propKit = PropKit.use("server_config.txt");
	
	
    public static final String ABOUT = propKit.get("about");
    public static final String CONTENT_PAGE = propKit.get("ContentPage");
    public static final String MAIN_PAGE =  propKit.get("MainPage");
    public static final Integer DAY_TIME = 1 * 60 * 60 * 24;
    public static final Integer PAGE_SIZE = 8;
    public static final String mysqlPath = propKit.get("mysqlPath");
    public static final String mysqlUser = propKit.get("user");
    public static final String mysqlPassword = propKit.get("password");
    public static final String WEBIP = propKit.get("WEBIP");
    //图片大小,2M
    public static final Integer IMG_SIZE = 1024 * 1024 * 2;
    
    
    public static final String SESSION_KEY_LOGINED_USER  = "user";
    public static final String SESSION_KEY_LOGINED_FLAG = "logined_flag";
    public static final String SESSION_KEY_LOGIN_SUCESS = "true";
    public static final String SESSION_KEY_LOGIN_FAIL = "false";
	public static final String TARGET_URL=propKit.get("TARGET_URL");
	public static final String ServerIP=propKit.get("ServerIP");
	
}