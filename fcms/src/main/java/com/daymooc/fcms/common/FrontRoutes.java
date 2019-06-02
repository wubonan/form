package com.daymooc.fcms.common;

import com.daymooc.fcms.account.AccountController;
import com.daymooc.fcms.blog.BlogController;
import com.daymooc.fcms.comment.CommentController;
import com.daymooc.fcms.common.upload.UeUploadController;
import com.daymooc.fcms.friend.MyFriendController;
import com.daymooc.fcms.gallery.GalleryController;
import com.daymooc.fcms.home.HomeController;
import com.daymooc.fcms.index.IndexController;
import com.daymooc.fcms.like.LikeController;
import com.daymooc.fcms.login.LoginController;
import com.daymooc.fcms.message.MessageController;
import com.daymooc.fcms.post.PostController;
import com.daymooc.fcms.question.QuestionController;
import com.daymooc.fcms.reg.RegController;
import com.daymooc.fcms.reptile.ReptileController;
import com.daymooc.fcms.search.SearchController;
import com.daymooc.fcms.tags.TagsController;
import com.daymooc.fcms.user.UserController;
import com.daymooc.fcms.video.VideoController;
import com.daymooc.fcms.view.ViewController;
import com.jfinal.config.Routes;

public class FrontRoutes extends Routes
{

	@Override
	public void config()
	{
		setBaseViewPath("/_view");
		
		add("/", IndexController.class, "/index");
		add("/index", IndexController.class);
		add("/upload", UeUploadController.class);//ueditor上传接口
		add("/login", LoginController.class);
		add("/reg", RegController.class);
		add("/video", VideoController.class);
		add("/question", QuestionController.class);
		add("/tags", TagsController.class);
		add("/gallery", GalleryController.class);
		add("/view", ViewController.class);
		add("/blog", BlogController.class);
		add("/post", PostController.class);
		add("/account", AccountController.class);
		add("/user", UserController.class);
		//我的消息
		add("/home/message", MessageController.class, "/home");
		//点赞
		add("/like", LikeController.class);
		//评论
		add("/comment", CommentController.class);
		//主页
		add("/home", HomeController.class);
		add("/friend", MyFriendController.class);
		//搜索
		add("/search", SearchController.class);
		add("/reptile",ReptileController.class);
	}

}
