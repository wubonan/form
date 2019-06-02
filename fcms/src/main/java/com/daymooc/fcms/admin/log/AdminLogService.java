package com.daymooc.fcms.admin.log;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.LoginLog;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class AdminLogService
{
	public static final AdminLogService me = new AdminLogService();
	final LoginLog logDao = new LoginLog().dao();
	int pageSize = 15;
	
	public Page<LoginLog> getLoginLog(int pageNumber)
	{
		String select = "select * ";
		String from = "from login_log order by id desc";
		
		Page<LoginLog> logPage = logDao.paginate(pageNumber, pageSize, select, from);
		
		AccountService.me.join("accountId", logPage.getList(), "nickName");
		
		return logPage;
	}
	
	public Ret delLog(int id)
	{
		boolean r = Db.deleteById("login_log", id);
		
		if (r)
		{
			return Ret.ok("msg", "删除成功");
		}
		
		return Ret.fail("msg", "删除失败");
	}
	
	public Ret delAllLog()
	{
		int r = Db.update("truncate TABLE login_log");
		
		//执行成功返回0，失败返回-1
		if (r == 0)
		{
			return Ret.ok("msg", "删除成功");
		}
		
		return Ret.fail("msg", "删除失败");
	}
}
