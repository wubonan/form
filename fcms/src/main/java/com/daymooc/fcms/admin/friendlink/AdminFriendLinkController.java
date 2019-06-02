package com.daymooc.fcms.admin.friendlink;

import com.daymooc.fcms.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

public class AdminFriendLinkController extends BaseController
{
	private AdminFriendLinkService linkService = AdminFriendLinkService.me;
	public void list()
	{
		setAttr("friendLinks", linkService.getFriendLink());
		render("link_list.html");
	}
	
	public void add()
	{
		render("link_add.html");
	}
	
	public void save()
	{
		UploadFile file = getFile();
		String logo = file.getFileName();
		Ret ret = linkService.addLink(getPara("siteName"), getPara("url"), logo);
		renderJson(ret);
	}
	
	public void delete()
	{
		Ret ret = linkService.delLink(getParaToInt("linkId"));
		
		renderJson(ret);
	}
}
