package com.daymooc.fcms.admin.system;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.SiteInfo;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

public class AdminSystemController extends BaseController
{
	private AdminSystemService systemService = AdminSystemService.me;
	public void index()
	{
		setAttr("siteInfo", systemService.getSiteInfo());
		render("index.html");
	}
	
	public void save()
	{
		UploadFile file = getFile("logo","logo");
		
		SiteInfo siteInfo = getModel(SiteInfo.class);
		if (file != null)
		{
			String logo = "/upload/logo/"+file.getFileName();
			siteInfo.setLogo(logo);
		}

		siteInfo.setId(1);
		
		if (siteInfo.update())
		{
			renderJson(Ret.ok("msg", "保存网站信息成功"));
			return;
		}
		
		renderJson(Ret.fail("msg", "保存网站信息失败"));
	}
}
