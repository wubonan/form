
package com.daymooc.fcms.friend;

import java.util.List;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.model.User;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.kit.Ret;

/**
 * FriendController
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class})
public class MyFriendController extends BaseController {

	static final MyFriendService srv = MyFriendService.me;

//	/**
//	 * 关注列表
//	 */
//	@ActionKey("/my/follow")
//	public void follow() {
//		Page<User> followPage = srv.getFollowPage(getLoginAccountId(), getParaToInt("p", 1));
//		setAttr("followPage", followPage);
//		render("follow.html");
//	}
//
//	/**
//	 * 粉丝列表
//	 */
//	@ActionKey("/my/fans")
//	public void fans() {
//		Page<User> fansPage = srv.getFansPage(getLoginAccountId(), getParaToInt("p", 1));
//        //RemindService.me.resetRemindOfNewFans(getLoginAccountId()); // 重置粉丝增加提醒
//		setAttr("fansPage", fansPage);
//		render("fans.html");
//	}

	/**
	 * 加好友
	 */
	@ActionKey("/friend/add")
	@Clear({FrontAuthInterceptor.class, FriendInterceptor.class})// 拦截器中的返回值不符合要求，需要定制返回值，所以 clear 掉
	public void add() {
		if (notLogin()) {
			renderJson(Ret.fail("msg", "登录后才能添加好请先登录"));// 定制返回值
			return ;
		}
		int accountId = getLoginAccountId();
		int friendId = getParaToInt("friendId");
		Ret ret = srv.addFriend(accountId, friendId);
		ret.set("friendRelation", srv.getFriendRelation(accountId, friendId));
		renderJson(ret);
	}

	/**
	 * 删好友
	 */
	@ActionKey("/friend/delete")
	public void delete() {
		int accountId = getLoginAccountId();
		int friendId = getParaToInt("friendId");
		Ret ret = srv.deleteFriend(accountId, friendId);
		ret.set("friendRelation", srv.getFriendRelation(accountId, friendId));
		renderJson(ret);
	}

    /**
     * 获取好友关系，目前用于用户空间关注/粉丝列表页面 ajax 动态获取关系
     */
    @ActionKey("/friend/getFriendRelation")
    @Clear({FrontAuthInterceptor.class, FriendInterceptor.class})
    public void getFriendRelation() {
        if (notLogin()) {
            renderJson(Ret.fail());
            return ;
        }

        int friendRelation = srv.getFriendRelation(getLoginAccountId(), getParaToInt("friendId"));
        renderJson(Ret.ok("friendRelation", friendRelation));
    }
    
    /**
     * 获取好友关系和用户信息，目前用于用户空间关注/粉丝列表页面 ajax 动态获取关系
     */
    @ActionKey("/friend/getFriendRelationAndInfo")
    @Clear({FrontAuthInterceptor.class, FriendInterceptor.class})
    public void getFriendRelationAndInfo() {
    	Ret ret = new Ret();
    	int userId = getParaToInt("friendId");
    	
    	System.out.println(userId);
    	if (notLogin()) {
            renderJson(Ret.fail());
            return ;
        }
    	
    	//获取用户信息
    	User user = srv.getUserInfo(userId);
    	ret.set("friendInfo", user);
 
    	 
    	//获取好友关系
        int friendRelation = srv.getFriendRelation(getLoginAccountId(), userId);
        ret.set("friendRelation", friendRelation);
        
        // 设置关注/粉丝数量
        int[] mRet = MyFriendService.me.getFollowAndFansCount(userId);
        int topicNum = MyFriendService.me.getTopicNum(userId);
        ret.set("followNum", mRet[0]);
        ret.set("fansNum", mRet[1]);
        ret.set("topicNum", topicNum);
        
        ret.setOk();
        
        String cardItem = renderToString("/_view/community/group/_card_item.html", ret);
		
		ret.set("cardItem", cardItem);
        
        renderJson(ret);
    }
}
