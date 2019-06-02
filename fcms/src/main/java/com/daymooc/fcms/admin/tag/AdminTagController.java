package com.daymooc.fcms.admin.tag;

import com.daymooc.fcms.admin.index.AdminIndexService;
import com.daymooc.fcms.common.controller.BaseController;

public class AdminTagController extends BaseController
{
	private AdminTagService tagService = AdminTagService.me;
	private AdminIndexService indexService = AdminIndexService.me;
	public void index()
	{	
		setAttr("tags", tagService.getAllTag());
		setAttr("postNum", indexService.getPostNum());
		render("index.html");
	}
	
	public void list()
	{
		setAttr("postPage", tagService.getPostPageByTag(getPara("tag"), getParaToInt("p", 1)));
		
		render("post_list.html");
	}
}
