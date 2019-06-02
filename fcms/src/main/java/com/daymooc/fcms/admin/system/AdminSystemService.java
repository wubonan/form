package com.daymooc.fcms.admin.system;

import com.daymooc.fcms.common.model.SiteInfo;
import com.jfinal.kit.Ret;

public class AdminSystemService
{
	public static final AdminSystemService me = new AdminSystemService();
	final SiteInfo siteDao = new SiteInfo().dao();
	
	public SiteInfo getSiteInfo()
	{
		SiteInfo siteInfo = siteDao.findFirst("select * from site_info where id=1");
		
		return siteInfo;
	}
	
}
