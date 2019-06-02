package com.daymooc.fcms.common.interceptor;

import com.daymooc.fcms.common.model.SiteInfo;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class SiteInfoInterceptor implements Interceptor
{
	private SiteInfo siteDao = new SiteInfo().dao();
	@Override
	public void intercept(Invocation inv)
	{
		Controller c = inv.getController();
		SiteInfo siteInfo = siteDao.findFirst("select * from site_info where id=1");
		c.setAttr("siteInfo", siteInfo);
		inv.invoke();
	}

}
