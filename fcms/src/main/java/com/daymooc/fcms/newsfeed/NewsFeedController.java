package com.daymooc.fcms.newsfeed;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.friend.FriendInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;

@Before({FrontAuthInterceptor.class, FriendInterceptor.class})
public class NewsFeedController extends BaseController
{

	static NewsFeedService srv = NewsFeedService.me;
	static NewsFeedReplyService newsFeedReplyService = NewsFeedReplyService.me;
	
	public void referMe() {
		Page<NewsFeed> newsFeedPage = ReferMeService.me.paginate(getLoginAccountId(), getParaToInt("p", 1));
		RemindService.me.resetRemindOfReferMe(getLoginAccountId()); // 重置提醒 remind 的 referMe 字段
		setAttr("newsFeedPage", newsFeedPage);
		setAttr("paginateLink", "/my/referMe?p=");      // 用于指定重用页面分页宏所使用的 link
		render("index.html");
	}
}
