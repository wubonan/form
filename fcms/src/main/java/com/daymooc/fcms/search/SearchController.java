package com.daymooc.fcms.search;

import com.daymooc.fcms.search.SearchService;
import com.daymooc.fcms.tags.TagsService;

import java.util.List;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.StringKit;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;
import com.jfinal.plugin.activerecord.Page;

public class SearchController extends BaseController
{
	final SearchService searchSrv = SearchService.me;
	private static final BlogService blogSrv = BlogService.me;
	private static final AccountService accountSrv = AccountService.me;
	private static final TagsService tagSrv = TagsService.me;
	public void index()
	{
		String keyWord = "";
		
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		
		if (getPara("q") == null || getPara("q").isEmpty())
		{
			setAttr("postPage", null);
			render("index.html");
			return;
		}
		
		keyWord = getPara("q");

		// 根据空格拆分关键字，进行多关键字搜索
		String kw = StringKit.conactString(keyWord);
	
		Page<Posts> postPage = searchSrv.searchPosts(kw, getParaToInt("p", 1));
		int resultNum = searchSrv.getSearchNum(keyWord);

		setAttr("postPage", postPage);
		setAttr("keyWord", keyWord);
		setAttr("resultNum", resultNum);
		render("index.html");
	}
}
