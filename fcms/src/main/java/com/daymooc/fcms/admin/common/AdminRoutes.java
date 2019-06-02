
package com.daymooc.fcms.admin.common;

import com.daymooc.fcms.admin.account.AdminAccountController;
import com.daymooc.fcms.admin.db.AdminDbController;
import com.daymooc.fcms.admin.friendlink.AdminFriendLinkController;
import com.daymooc.fcms.admin.index.AdminIndexController;
import com.daymooc.fcms.admin.interceptor.AdminAuthInterceptor;
import com.daymooc.fcms.admin.log.AdminLogController;
import com.daymooc.fcms.admin.post.AdminPostController;
import com.daymooc.fcms.admin.system.AdminSystemController;
import com.daymooc.fcms.admin.tag.AdminTagController;
import com.jfinal.config.Routes;


/**
 * 后台管理路由
 * 注意：自 jfinal 3.0 开始，baesViewPath 改为在 Routes 中独立配置
 *      并且支持 Routes 级别的 Interceptor，这类拦截器将拦截所有
 *      在此 Routes 中添加的 Controller，行为上相当于 class 级别的拦截器
 *      Routes 级别的拦截器特别适用于后台管理这样的需要统一控制权限的场景
 *      减少了代码冗余
 */
public class AdminRoutes extends Routes {

	public void config() {
		// 添加后台管理拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new AdminAuthInterceptor());
		
		setBaseViewPath("/_view/_admin");
		
		//add(controllerKey, controllerClass);
		add("/admin", AdminIndexController.class, "/index");
		add("/admin/post", AdminPostController.class, "/post");
		add("/admin/sys", AdminSystemController.class, "/system");
		add("/admin/account", AdminAccountController.class, "/account");
		add("/admin/db", AdminDbController.class, "/db");
		add("/admin/log", AdminLogController.class, "/log");
		add("/admin/tag", AdminTagController.class, "/tag");
		add("/admin/friendlink", AdminFriendLinkController.class, "/friendlink");
		
	}
}





