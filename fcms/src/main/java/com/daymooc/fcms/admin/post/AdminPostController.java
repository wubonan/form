package com.daymooc.fcms.admin.post;

import java.util.List;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

public class AdminPostController extends BaseController
{
	private AdminPostService adminPostService = AdminPostService.me;
	private NewsFeed feedDao = new NewsFeed().dao();
	public void article()
	{
		setAttr("postPage", adminPostService.getPostByType(Posts.ARTICLE, getParaToInt("p", 1)));
		render("post.html");
	}
	
	public void video()
	{
		setAttr("postPage", adminPostService.getPostByType(Posts.VIDEO, getParaToInt("p", 1)));
		render("post.html");
	}
	
	public void gallery()
	{
		setAttr("postPage", adminPostService.getPostByType(Posts.IMAGE, getParaToInt("p", 1)));
		render("post.html");
	}
	
	public void question()
	{
		setAttr("postPage", adminPostService.getPostByType(Posts.QUESTION, getParaToInt("p", 1)));
		render("post.html");
	}
	
	public void delete()
	{
		int postId = getParaToInt("postId");
		Ret ret = adminPostService.deletePost(postId);
		
		List<NewsFeed> newsFeeds = feedDao.find("select id from news_feed where refId=?", postId);
		
		for (int i = 0; i < newsFeeds.size(); i++)
		{
			boolean r2 = Db.deleteById("news_feed", newsFeeds.get(i).getId());
			
			if (!r2)
			{
				renderJson(Ret.fail("msg", "删除news_feed失败"));
				return;
			}
		}
		
		renderJson(ret);
	}
	
	public void lock()
	{
		Ret ret = adminPostService.lockPost(getParaToInt("postId"));
		
		renderJson(ret);
	}
	
	public void unlock()
	{
		Ret ret = adminPostService.unlockPost(getParaToInt("postId"));
		
		renderJson(ret);
	}
}
