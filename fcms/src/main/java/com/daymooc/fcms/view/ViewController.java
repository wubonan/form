package com.daymooc.fcms.view;

import java.util.List;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.comment.CommentService;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.contants.AttributeConstant;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.like.LikeService;
import com.daymooc.fcms.tags.TagsService;
import com.daymooc.fcms.user.UserService;
import com.jfinal.kit.Ret;

public class ViewController extends BaseController
{
	private ViewService viewSrv = ViewService.me;
	private CommentService commentSrv = CommentService.me;
	private static final BlogService blogSrv = BlogService.me;
	private static final AccountService accountSrv = AccountService.me;
	private static final UserService userSrv = UserService.me;
	private static final TagsService tagSrv = TagsService.me;
	public void article()
	{
		int id = getParaToInt(0);
		Posts post = viewSrv.getPost(id);
		viewSrv.updateViews(id);
		setLikeAndFavoriteStatus(post);
		//右边栏begin
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		//右边栏end
		//获取是否已经关注某用户
		boolean isFollowUser = false;
		if (isLogin())
		{
			isFollowUser = userSrv.isFollowUser(getLoginAccountId(), post.getUserId());
		}
		setAttr("followUser", isFollowUser);
		setAttr("post", post);//post详情
		setAttr("replyPage", commentSrv.getReplyPage(id, getParaToInt("p", 1)));
		setAttr("commentNum", commentSrv.getComments(id));//评论数
		setAttr("postNum", viewSrv.getPostNum(post.getUserId()));//发布文章数
		setAttr("totalCommentNum", viewSrv.getCommentNum(post.getUserId()));//发布评论数
		render("article.html");
	}
	
	public void gallery() 
	{
		int id = getParaToInt(0);
		Posts post = viewSrv.getPost(id);
		viewSrv.updateViews(id);
		setLikeAndFavoriteStatus(post);
		//右边栏begin
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		//右边栏end
		//获取是否已经关注某用户
		boolean isFollowUser = false;
		if (isLogin())
		{
			isFollowUser = userSrv.isFollowUser(getLoginAccountId(), post.getUserId());
		}
		
		setAttr("followUser", isFollowUser);
		setAttr("post", post);
		setAttr("replyPage", commentSrv.getReplyPage(id, getParaToInt("p", 1)));
		setAttr("commentNum", commentSrv.getComments(id));
		setAttr("postNum", viewSrv.getPostNum(post.getUserId()));//发布文章数
		setAttr("totalCommentNum", viewSrv.getCommentNum(post.getUserId()));//发布评论数
		render("gallery.html");
	}
	
	public void video()
	{
		int id = getParaToInt(0);
		Posts post = viewSrv.getPost(id);
		viewSrv.updateViews(id);
		setLikeAndFavoriteStatus(post);
		//右边栏begin
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		//右边栏end
		//获取是否已经关注某用户
		boolean isFollowUser = false;
		if (isLogin())
		{
			isFollowUser = userSrv.isFollowUser(getLoginAccountId(), post.getUserId());
		}
		setAttr("followUser", isFollowUser);
		setAttr("serverIp", AttributeConstant.ServerIP);
		setAttr("replyPage", commentSrv.getReplyPage(id, getParaToInt("p", 1)));
		setAttr("commentNum", commentSrv.getComments(id));
		setAttr("postNum", viewSrv.getPostNum(post.getUserId()));//发布文章数
		setAttr("totalCommentNum", viewSrv.getCommentNum(post.getUserId()));//发布评论数
		setAttr("post", post);
		render("video.html");
	}
	
	public void question()
	{
		int id = getParaToInt(0);
		Posts post = viewSrv.getPost(id);
		viewSrv.updateViews(id);
		setLikeAndFavoriteStatus(post);
		//右边栏begin
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagSrv.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		//右边栏end
		//获取是否已经关注某用户
		boolean isFollowUser = false;
		if (isLogin())
		{
			isFollowUser = userSrv.isFollowUser(getLoginAccountId(), post.getUserId());
		}
		setAttr("followUser", isFollowUser);
		setAttr("post", post);
		setAttr("replyPage", commentSrv.getReplyPage(id, getParaToInt("p", 1)));
		setAttr("commentNum", commentSrv.getComments(id));
		setAttr("postNum", viewSrv.getPostNum(post.getUserId()));//发布文章数
		setAttr("totalCommentNum", viewSrv.getCommentNum(post.getUserId()));//发布评论数
		render("article.html");
	}
	
	  /**
     * 如果用户已登录，则需要显示当前视频是否已经被该用户点赞了
     */
	private void setLikeAndFavoriteStatus(Posts post) {
        Ret ret = Ret.create();
        LikeService.me.setLikeStatus(getLoginAccount(), "posts", post, ret);
        setAttr("ret", ret);
    }
	
}
