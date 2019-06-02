package com.daymooc.fcms.tags;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;
import com.jfinal.plugin.activerecord.Page;

public class TagsController extends BaseController
{
	private TagsService tagsService = TagsService.me;
	private static final AccountService accountSrv = AccountService.me;
	private static final BlogService blogSrv = BlogService.me;
	
	public void index()
	{
		setAttr("postTags", tagsService.getTags());
		render("index.html");
	}

	public void view() throws UnsupportedEncodingException
	{
		//注意，url里的参数是get传过来的，默认转码为unicode，这里需要转为utf-8
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagsService.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		String tagName = URLDecoder.decode(getPara(0),"UTF-8");
		Page<Posts> postPage = tagsService.getPostPageByTag(tagName, getParaToInt("p", 1));
		setAttr("postPage", postPage);
		setAttr("tagName", tagName);
		render("view_tag.html");
	}
	
	public void hot() throws UnsupportedEncodingException
	{
		//注意，url里的参数是get传过来的，默认转码为unicode，这里需要转为utf-8
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagsService.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		String tagName = URLDecoder.decode(getPara(0),"UTF-8");
		Page<Posts> postPage = tagsService.getHotPostPageByTag(tagName, getParaToInt("p", 1));
		setAttr("postPage", postPage);
		setAttr("tagName", tagName);
		render("view_tag.html");
	}
}
