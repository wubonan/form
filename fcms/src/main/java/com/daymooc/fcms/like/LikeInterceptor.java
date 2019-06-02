
package com.daymooc.fcms.like;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 用于显示 "/my" 个人空间与 "/user" 空间的点赞数量
 */
public class LikeInterceptor implements Interceptor {

    public static final String likeNum = "_likeNum";

    public void intercept(Invocation inv) {
        inv.invoke();

        BaseController c = (BaseController) inv.getController();
        boolean isUserSpace = inv.getActionKey().startsWith("/user");
        if (isUserSpace) {
            handleUserSpaceLikeCount(c);
        } else {
            handleMySpaceLikeCount(c);
        }
    }

    private void handleUserSpaceLikeCount(BaseController c) {
        User account = AccountService.me.getById(c.getParaToInt());
        c.setAttr(likeNum, account.getLikeCount());
    }

    private void handleMySpaceLikeCount(BaseController c) {
    	User account = AccountService.me.getById(c.getLoginAccountId());
        c.setAttr(likeNum, account.getLikeCount());
    }
}
