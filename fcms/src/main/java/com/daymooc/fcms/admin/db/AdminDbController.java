package com.daymooc.fcms.admin.db;

import com.daymooc.fcms.common.contants.AttributeConstant;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.TimeKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

public class AdminDbController extends BaseController
{
	AdminDbService dbService = AdminDbService.me;
	
	public void backupRestore()
	{
		render("backup_restore.html");
	}
	
	public void list()
	{
		render("db_file_list.html");
	}
	
	public void backup()
	{
		if (getPara("backupPath") == null || getPara("backupPath").equals(""))
		{
			renderJson(Ret.fail("msg", "请填写备份地址"));
			return;
		}
		try {  
	          if (dbService.exportDatabaseTool(AttributeConstant.WEBIP, AttributeConstant.mysqlUser, AttributeConstant.mysqlPassword,
	            		getPara("backupPath"), TimeKit.getStringDate()+".sql", "fcms")) {  
	                System.out.println("数据库成功备份！！！");  
	                renderJson(Ret.ok("msg", "数据库成功备份"));
	           } 
	          else 
	          {  
	                System.out.println("数据库备份失败！！！");
	                renderJson(Ret.fail("msg", "数据库备份失败"));
	           }  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        }
	}
	
	public void restore()
	{
		UploadFile file = getFile("dbFile", "db");
		
		String path = file.getUploadPath()+"\\"+file.getFileName();
		System.out.println(path);
		try
		{
			Ret ret = dbService.restore("aa", "fcms");
			renderJson(ret);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
