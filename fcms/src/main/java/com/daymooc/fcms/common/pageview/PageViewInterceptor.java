
package com.daymooc.fcms.common.pageview;

import com.daymooc.fcms.common.kit.IpKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 用于记录文章详情页的页面访问量 page view，用于热门文章排序
 */
public class PageViewInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		inv.invoke();

		Controller c = inv.getController();

		if (c.isParaExists(0)) {
			String actionKey = inv.getActionKey();
			Integer id = c.getParaToInt();
			String ip = IpKit.getRealIp(c.getRequest());
			PageViewService.me.processPageView(actionKey, id, ip);
		}
	}
}
