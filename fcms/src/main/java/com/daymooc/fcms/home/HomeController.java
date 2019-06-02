package com.daymooc.fcms.home;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.comment.CommentService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.model.Friend;
import com.daymooc.fcms.common.model.Message;
import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.PostsComment;
import com.daymooc.fcms.message.MessageService;
import com.daymooc.fcms.newsfeed.NewsFeedService;
import com.daymooc.fcms.newsfeed.ReferMeService;
import com.daymooc.fcms.newsfeed.RemindService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

@Before({FrontAuthInterceptor.class})
public class HomeController extends BaseController
{
	final NewsFeedService feedSrv = NewsFeedService.me;
	final BlogService blogSrv = BlogService.me;
	final CommentService commentSrv = CommentService.me;
	final HomeService homeSrv = HomeService.me;
	final MessageService msgSrv = MessageService.me;
	final RemindService remindSrv = RemindService.me;
	
	public void index()
	{
		if (getPara(0) == null)
		{
			setAttr("page", "newfeeds");
		}
		else 
		{
			setAttr("page", getPara(0));
		}
		String page = getPara(0);
		int cUerId = getLoginAccountId();
		//getPara(0)包括newfeeds,posts,comments,favors,follows,fans,notifies
		//动态
		if ((page == null) || (page.equals("newfeeds")))
		{
			//包括自己和自己关注的人的动态
			Page<NewsFeed> newsFeedPage = feedSrv.paginate(cUerId, getParaToInt("p", 1));
			setAttr("newsFeedPage", newsFeedPage);
			setAttr("page", "newfeeds");//用于标识当前页面为动态页
		}
		//我的文章
		else if (page.equals("posts")) 
		{
			Page<Posts> postPage = blogSrv.getPosts(getParaToInt("p", 1), cUerId);
			setAttr("postPage", postPage);
		}
		//我的评论
		else if (page.equals("comments")) {
			Page<PostsComment> commentPage = commentSrv.getCommentAndPost(getParaToInt("p", 1), cUerId);
			setAttr("commentPage", commentPage);
		}
		//我的收藏
		else if (page.equals("favors"))
		{
			Page<Posts> postPage = homeSrv.getFavorPosts(cUerId, getParaToInt("p", 1));
			setAttr("postPage", postPage);
		}
		//我的关注
		else if(page.equals("follows"))
		{
			Page<Friend> followPage = homeSrv.getFollows(cUerId, getParaToInt("p", 1));
			setAttr("followPage", followPage);
		}
		//我的粉丝
		else if (page.equals("fans")) 
		{
			Page<Friend> fansPage = homeSrv.getFans(cUerId, getParaToInt("p", 1));
			setAttr("fansPage", fansPage);
		}
		//@到我
		else if (page.equals("referMe"))
		{
			Page<NewsFeed> newsFeedPage = ReferMeService.me.paginate(getLoginAccountId(), getParaToInt("p", 1));
			RemindService.me.resetRemindOfReferMe(getLoginAccountId()); // 重置提醒 remind 的 referMe 字段
			setAttr("newsFeedPage", newsFeedPage);
		}
		//通知
		else if (page.equals("notifies"))
		{
			Page<Message> msgPage = msgSrv.paginate(getParaToInt("p", 1), cUerId);
			//用户查看消息之后重置消息数量,这里暂时只有message
			//TODO fans,referMe后面需要增加
			remindSrv.resetRemindOfMessage(cUerId);
			remindSrv.resetRemindOfNewFans(cUerId);
			remindSrv.resetRemindOfReferMe(cUerId);
			
			setAttr("msgPage", msgPage);
		}
		//私信
		else if (page.equals("message"))
		{
			int userId = getLoginAccountId();
			Page<Message> messagePage = msgSrv.paginate(getParaToInt("p", 1), userId);
			RemindService.me.resetRemindOfMessage(userId);
			setAttr("messagePage", messagePage);
		}
		
		render("index.html");
	}
	

}
