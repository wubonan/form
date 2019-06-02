package com.daymooc.fcms.user;

import java.util.List;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.friend.FriendInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

@Before({FriendInterceptor.class})
public class UserController extends BaseController
{
	public static int userId = 0;//全局变量，用来保存当前登录用户id
	public final static UserService userSrv = new UserService();
	static final UserFriendService ufSrv = UserFriendService.me;
	final BlogService blogService = BlogService.me;
	
	public void index()
	{
		int curPage = 1;
		
		if (null != getParaToInt("p"))
		{
			curPage = getParaToInt("p");
		}
		
		//获取用户id
		if (getParaToInt(0) != null)
		{
			userId = getParaToInt(0);
		}
		
		//获取用户信息
		User account = userSrv.getUserById(userId);

		//获取是否已经关注某用户
		boolean isFollowUser = false;
		if (isLogin())
		{
			isFollowUser = userSrv.isFollowUser(getLoginAccountId(), userId);
		}
		
		
		//获取该用户关注的用户
		//List<User> follows = userSrv.getFollowUser(userId);
		Page<User> followPage = ufSrv.getFollowPage(getParaToInt(), getParaToInt("foP", 1));
        setAttr("followPage", followPage);
		//获取该用户的粉丝
		//List<User> fans = userSrv.getFans(userId);
		Page<User> fansPage = ufSrv.getFansPage(getParaToInt(), getParaToInt("fansP", 1));
		setAttr("fansPage", fansPage);
		
		//获取该用户的所有post
		Page<Posts> postPage = blogService.getPosts(getParaToInt("p", 1), userId);

		setAttr("account", account);
		setAttr("userId", userId);
		setAttr("postPage", postPage);
		setAttr("followUser", isFollowUser);
		setAttr("curPage", curPage);
		render("index.html");
	}
	/**
	* 
	* @Title: follow 
	* @Description:关注与取消关注用户
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
     * 
     */
	@Before({FrontAuthInterceptor.class})
	public void follow()
	{
		//要关注的用户ID
		int friendId = 0;
		if (getParaToInt(0) != null)
		{
			friendId = getParaToInt(0);
		}
		
		System.out.println(friendId);
		//当前用户ID
		int cUserId = getLoginAccountId();
		
		//当前用户Id和朋友ID一样，说明是自己，自己不能关注自己
		if (friendId == cUserId)
		{
			Ret ret = new Ret();
			//true说明是关注操作，而不是已经关注
			ret.set("followUser", true);
			ret.set("msg", "自己不能关注自己");
			renderJson(ret);
			
			return;
		}

		Ret ret = userSrv.followUser(cUserId, friendId);
		
		//获取是否已经关注该用户
		boolean isFollowUser = userSrv.isFollowUser(cUserId, friendId);
		
		System.out.println(isFollowUser);
		
		ret.set("followUser", isFollowUser);
		
		renderJson(ret);
		
	}
    
    /**
     * 用户粉丝列表
     */
    @ActionKey("/user/fans")
    public void fans() {
        Page<User> fansPage = ufSrv.getFansPage(getParaToInt(), getParaToInt("p", 1));
        setAttr("fansPage", fansPage);
        render("fans.html");
    } 
}
