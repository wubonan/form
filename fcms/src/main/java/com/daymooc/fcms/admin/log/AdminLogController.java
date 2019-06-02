package com.daymooc.fcms.admin.log;

import com.daymooc.fcms.common.controller.BaseController;
import com.jfinal.kit.Ret;

public class AdminLogController extends BaseController
{
	private AdminLogService logService = AdminLogService.me;
	public void index()
	{
		setAttr("logPgae", logService.getLoginLog(getParaToInt("p", 1)));
		render("index.html");
	}
	
	public void delete()
	{
		Ret ret = logService.delLog(getParaToInt("logId"));
		renderJson(ret);
	}
	
	public void deleteAll()
	{
		Ret ret = logService.delAllLog();
		renderJson(ret);
	}
}
