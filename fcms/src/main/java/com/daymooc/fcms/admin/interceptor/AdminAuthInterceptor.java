	package com.daymooc.fcms.admin.interceptor;

import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.login.LoginService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import java.util.HashSet;
import java.util.Set;

/**
 * 后台权限管理拦截器
 * 
 * 暂时做成最简单的判断当前用户是否是管理员账号，后续改成完善的
 * 基于用户、角色、权限的权限管理系统，并且实现角色、权限完全动态化配置
 */
public class AdminAuthInterceptor implements Interceptor {

	private static Set<String> adminAccountSet = initAdmin();

	private static Set<String> initAdmin() {
		Set<String> ret = new HashSet<String>();
		String admin = PropKit.get("admin");        // 从配置文件中读取管理员账号，多个账号用逗号分隔
		String[] adminArray = admin.split(",");
		for (String a : adminArray) {
			ret.add(a.trim());
		}
		return ret;
	}

	public static boolean isAdmin(User loginAccount) {
		return loginAccount != null && adminAccountSet.contains(loginAccount.getEmail());
	}

	public void intercept(Invocation inv) {
		User loginAccount = inv.getController().getAttr(LoginService.loginAccountCacheName);
		if (isAdmin(loginAccount)) {
			inv.invoke();
		} else {
			inv.getController().renderError(404);
		}
	}
}

