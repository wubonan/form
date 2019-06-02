package com.daymooc.fcms.admin.tag;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class AdminTagService
{
	public static final AdminTagService me = new AdminTagService();
	final Tags tagDao = new Tags().dao();
	final Posts postDao = new Posts().dao();
	int pageSize = 15;
	
	public List<Tags> getAllTag()
	{
		List<Tags> tags = tagDao.find("select * from tags");
		
		return tags;
	}
	
	public Page<Posts> getPostPageByTag(String tagName, int pageNumber)
	{
		String select = "select * ";
		String from = "from posts where locate(?,tags)>0 order by createAt desc";
		
		Page<Posts> postPage = postDao.paginate(pageNumber, pageSize, select, from, tagName);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
}
