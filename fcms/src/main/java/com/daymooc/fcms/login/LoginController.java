package com.daymooc.fcms.login;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.IpKit;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;

public class LoginController extends BaseController
{
	static final LoginService srv = LoginService.me;
	public void index()
	{
		keepPara("returnUrl");// 保持住 returnUrl 这个参数，以便在登录成功后跳转到该参数指向的页面
		render("index.html");
	}
	
	public void doLogin()
	{
		boolean keepLogin = getParaToBoolean("keepLogin",false);
		String loginIp = IpKit.getRealIp(getRequest());
		Ret ret = srv.login(getPara("userName"), getPara("password"), keepLogin, loginIp);
		
		if (ret.isOk())
		{
			String sessionId = ret.getStr(LoginService.sessionIdName);
			int maxAgeInSeconds = ret.getAs("maxAgeInSeconds");
			setCookie(LoginService.sessionIdName, sessionId, maxAgeInSeconds, true);
			setAttr(LoginService.loginAccountCacheName, ret.get(LoginService.loginAccountCacheName));

			ret.set("returnUrl", getPara("returnUrl", "/"));    // 如果 returnUrl 存在则跳过去，否则跳去首页
		}
		renderJson(ret);
		
	}
	
	/**
	 * 退出登录
	 */
	@ActionKey("/logout")
	public void logout() {
		srv.logout(getCookie(LoginService.sessionIdName));
		removeCookie(LoginService.sessionIdName);
		redirect("/");
	}

	/**
	 * 显示忘记密码页面
	 */
	public void forgetPassword() {
		render("forget_password.html");
	}

	/**
	 * 发送找回密码邮件
	 */
	public void sendRetrievePasswordEmail() {
		Ret ret = srv.sendRetrievePasswordAuthEmail(getPara("email"));
		renderJson(ret);
	}

	/**
	 * 1：keepPara("authCode") 将密码找回链接中问号挂参的 authCode 传递到页面
	 * 2：在密码找回页面中与用户输入的新密码一起传回给 doPassReturn 进行密码修改
	 */
	public void retrievePassword() {
		keepPara("authCode");
		render("retrieve_password.html");
	}

	/**
	 * ajax 密码找回
	 * 1：判断 authCode 是否有效
	 * 2：有效则更新密码
	 */
	public void doRetrievePassword() {
		Ret ret = srv.retrievePassword(getPara("authCode"), getPara("newPassword"));
		renderJson(ret);
	}

	public void captcha() {
		renderCaptcha();
	}
}
