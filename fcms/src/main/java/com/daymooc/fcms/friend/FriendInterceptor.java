
package com.daymooc.fcms.friend;

import com.daymooc.fcms.common.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 关注/粉丝数量拦截器：用于用户空间显示头像下方的关注/粉丝
 */
public class FriendInterceptor implements Interceptor {

    public static final String followNum = "followNum";//关注数
    public static final String fansNum = "fansNum";//粉丝数
    public static final String articleNum = "articleNum";//话题数
    public static final String friendRelation = "friendRelation";//朋友关系

    public void intercept(Invocation inv) {
        inv.invoke();

        BaseController c = (BaseController) inv.getController();
        boolean isUserSpace = inv.getActionKey().startsWith("/user");
        if (isUserSpace) {
            handleUserSpaceFriend(c);
        } else {
            handleMySpaceFriend(c);
        }
    }

    /**
     * 处理用户空间 "/user" 关注/粉丝数量，以及好友关系
     */
    private void handleUserSpaceFriend(BaseController c) {
        int userId = c.getParaToInt();
        // 如果当前访问者已经登录，利用 myId 与 userId 去查询好友关系
        if (c.isLogin()) {
            int myId = c.getLoginAccountId();
            // 业务层获取好友关系
            int friendRelations = MyFriendService.me.getFriendRelation(myId, userId);
            c.setAttr(friendRelation, friendRelations);
        }
        // 如果当前访问者未登录，无法确定好友关系，则认为该访问者与 user 无好友关系
        else {
            // 值为 0 表示无好友关系，详情见 MyFriendService.getFriendRelation() 注释中的说明
            int friendRelations = 0;
            c.setAttr(friendRelation, friendRelations);
        }

        // 设置关注/粉丝数量
        int[] ret = MyFriendService.me.getFollowAndFansCount(userId);
        int topicNum = MyFriendService.me.getTopicNum(userId);
        c.setAttr(followNum, ret[0]);
        c.setAttr(fansNum, ret[1]);
        c.setAttr(articleNum, topicNum);
    }

    /**
     * 处理我的空间 "/my" 关注/粉丝数量，但好友关系不需要处理
     * 因为不需要 friendRelation 变量，直接显示 "更换头像" 链接即可
     */
    private void handleMySpaceFriend(BaseController c) {
        // 个人空间有 FrontAuthInterceptor 保障过登录，不用 isLogin() 来判断
        // 如果抛异常则必须要改代码修正 bug，clear 掉该拦截器就可以了，主要对于 ajax 做个清除
        int myId = c.getLoginAccountId();

        // 设置关注/粉丝数量
        int[] ret = MyFriendService.me.getFollowAndFansCount(myId);
        int topicNum = MyFriendService.me.getTopicNum(myId);
        c.setAttr(followNum, ret[0]);
        c.setAttr(fansNum, ret[1]);
        c.setAttr(articleNum, topicNum);
    }
}