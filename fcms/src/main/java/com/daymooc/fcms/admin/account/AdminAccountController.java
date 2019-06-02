package com.daymooc.fcms.admin.account;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.IpKit;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.reg.RegService;
import com.daymooc.fcms.reg.RegValidator;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class AdminAccountController extends BaseController
{
	private AdminAccountService accountService = AdminAccountService.me;
	public void list()
	{
		Page<User> userPage = accountService.getAllUsers(getParaToInt("p", 1));
		
		setAttr("userPage", userPage);
		render("account_list.html");
	}
	
	public void add()
	{
		render("account_add.html");
	}
	
	public void mod()
	{
		render("account_mod.html");
	}
	
	public void lock()
	{
		renderJson(accountService.lockUser(getParaToInt("userId")));
	}
	
	public void unlock()
	{
		renderJson(accountService.unlockUser(getParaToInt("userId")));
	}
	
	public void delete()
	{
		int id = getParaToInt("userId");
		boolean r = Db.deleteById("user", id);
		if (r)
		{
			renderJson(Ret.ok("msg", "删除成功。"));
			return;
		}
		
		renderJson(Ret.fail("msg", "删除失败"));
	}
	
	@Before(RegValidator.class)
	public void addUser()
	{
		String ip = IpKit.getRealIp(getRequest());
		Ret ret = accountService.addUser(getPara("email"), getPara("userName"), 
				getPara("nickName"), getPara("password"), ip);
		
		renderJson(ret);
	}
	
	public void modUser()
	{
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if(RegService.me.isPasswordOK(password, rePassword))
		{
			renderJson(Ret.fail("msg", "两次输入的密码不相同,请检查"));
			return;
		}
		
		Ret ret = accountService.modPassword(getParaToInt("id"), password);
		
		renderJson(ret);
	}
}
