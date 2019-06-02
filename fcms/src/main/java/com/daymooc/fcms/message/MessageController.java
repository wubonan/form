package com.daymooc.fcms.message;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.kit.SensitiveWordsKit;
import com.daymooc.fcms.common.model.Message;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.friend.FriendInterceptor;
import com.daymooc.fcms.newsfeed.RemindService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

@Before({FrontAuthInterceptor.class, FriendInterceptor.class})
public class MessageController extends BaseController
{
	MessageService msgSrv = MessageService.me;
	/**
	 * 所有私信往来
	 */
	@ActionKey("/home/message")
	public void message()
	{
		int userId = getLoginAccountId();
		Page<Message> messagePage = msgSrv.allPaginate(getParaToInt("p", 1), userId);
		RemindService.me.resetRemindOfMessage(userId);
		setAttr("messagePage", messagePage);
		setAttr("page", "message");
		
		render("index.html");
	}
	
	public void sysMsg()
	{
		setAttr("page", "sysMsg");
		render("index.html");
	}
	
	/**
     * 与某一用户的私信
     */
    public void friend() {
        int friendId = getParaToInt();
        Page<Message> messagePage = msgSrv.paginate(getParaToInt("p", 1), getLoginAccountId(), friendId);

        User friend = new User().set("id", friendId);
        AccountService.me.join("id", friend, "nickName", "avatar");

        setAttr("messagePage", messagePage);
        setAttr("friend", friend);
        setAttr("page", "friendMessage");
        render("index.html");
    }

    /**
     * 发送私信
     */
    public void send() {
        // RestTime 的调用可以考虑移到业务层中去，发送私信暂时不开启 testTime 的 check
//        String restTimeMsg = RestTime.checkRestTime(getLoginAccount());
//        if (restTimeMsg != null) {
//            renderJson(Ret.error("msg", restTimeMsg).getData());
//            return ;
//        }
        String replyContent = getPara("replyContent");
        if (StrKit.isBlank(replyContent)) {
            renderJson(Ret.fail("msg", "私信内容不能为空"));
            return ;
        }
        if (SensitiveWordsKit.checkSensitiveWord(replyContent) != null) {
            renderJson(Ret.fail("msg", "私信内容不能包含敏感词"));
            return ;
        }

        Ret ret = msgSrv.send(getLoginAccountId(), getParaToInt("friendId"), replyContent);
        if (ret.isFail()) {
            renderJson(ret);
            return ;
        }

        ret.set("loginAccount", getLoginAccount());     // 放入 loginAccount 供 renderToString 使用

        // 用模板引擎生成 HTML 片段 replyItem
        String replyItem = renderToString("/_view/home/common/_one_friend_message_reply_item.html", ret);
        
        ret.set("replyItem", replyItem);
        renderJson(ret);
    }

    /**
     * 删除某一条私信
     */
    public void deleteByMessageId() {
        Ret ret = msgSrv.deleteByMessageId(getLoginAccountId(), getParaToInt("messageId"));
        renderJson(ret);
    }

    /**
     * 删除某一个用户的所有私信往来
     */
    public void deleteByFriendId() {
        Ret ret = msgSrv.deleteByFriendId(getLoginAccountId(), getParaToInt("friendId"));
        renderJson(ret);
    }
}
