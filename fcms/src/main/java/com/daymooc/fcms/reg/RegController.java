package com.daymooc.fcms.reg;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.IpKit;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;

public class RegController extends BaseController
{
	private static final RegService srv = RegService.me;
	
	public void index()
	{
		render("index.html");
	}

	@Before(RegValidator.class)
	public void save()
	{
		String ip = IpKit.getRealIp(getRequest());
		
		Ret ret = srv.reg(getPara("email"), getPara("userName"), getPara("password"), getPara("nickName"), ip);
		
		if (ret.isOk())
		{
			ret.set("regEmail",getPara("userName"));
		}
		renderJson(ret);
	}
	
	/**
	 * 
	* @Title: notActivated 
	* @Description: 显示激活页面 
	* @param     无
	* @return void    返回类型 
	* @throws
	 */
	public void notActivated()
	{
		render("not_activated.html");
	}
	
	/**
	 * 重发激活邮件
	 */
	public void reSendActivateEmail()
	{
		Ret ret = srv.reSendActivateEmail(getPara("email"));
		renderJson(ret);
	}
	
	public void activate()
	{
		Ret ret = srv.activate(getPara("authCode"));
		setAttr("ret", ret);
		render("activate.html");
	}
	
	/**
	 * 
	* @Title: captcha 
	* @Description: 获取验证码
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void captcha() {
		renderCaptcha();
	}
}
