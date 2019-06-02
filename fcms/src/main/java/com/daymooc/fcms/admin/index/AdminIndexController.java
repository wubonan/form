package com.daymooc.fcms.admin.index;

import java.util.List;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.Ret;

public class AdminIndexController extends BaseController
{
	private final AdminIndexService adminIndexSrv = AdminIndexService.me;
	
	public void index()
	{
		long postNum = adminIndexSrv.getPostNum();
		long commentNum = adminIndexSrv.getCommentNum();
		long userNum = adminIndexSrv.getUserNum();
		long tagNum = adminIndexSrv.getTagNum();
		long articleNum = adminIndexSrv.getPostNumByType(Posts.ARTICLE);
		long imageNum = adminIndexSrv.getPostNumByType(Posts.IMAGE);
		long questionNum = adminIndexSrv.getPostNumByType(Posts.QUESTION);
		long videoNum = adminIndexSrv.getPostNumByType(Posts.VIDEO);
		
		
		List<User> users = adminIndexSrv.getUserList(8);
		System.err.println(users.get(0).toString());
		List<Posts> posts = adminIndexSrv.getPostList(6);
		
		setAttr("postNum", postNum);
		setAttr("commentNum", commentNum);
		setAttr("userNum", userNum);
		setAttr("tagNum", tagNum);
		setAttr("articleNum", articleNum);
		setAttr("imageNum", imageNum);
		setAttr("questionNum", questionNum);
		setAttr("videoNum", videoNum);
		setAttr("users", users);
		setAttr("posts", posts);
		
		render("index.html");
	}
}
