package com.daymooc.fcms.reptile;

import java.util.List;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;

import com.daymooc.fcms.tags.TagsService;
import com.jfinal.core.Controller;

public class ReptileController extends Controller{
	
	private ReptileService reptileService = ReptileService.me;
	private static final BlogService blogSrv = BlogService.me;
	private static final AccountService accountSrv = AccountService.me;
	private static final TagsService tagSrv = TagsService.me;

	public void index() {
		System.out.println("test");
		System.out.println("test");
		System.out.println("test");
		System.out.println("test");
		
		
		setAttr("reptiles", reptileService.getReptile());
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		
		render("index.html");
	}
}
