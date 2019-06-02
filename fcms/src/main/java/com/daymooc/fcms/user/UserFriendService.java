package com.daymooc.fcms.user;

import com.daymooc.fcms.common.model.User;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 非自己的其他用户的好友业务
 * 关键点：其他用户的好友与自己的好友关系是不同的，所以好友下方的链接文字以及 js 方法调用都将不同
 */
public class UserFriendService {

    public static final UserFriendService me = new UserFriendService();
    final String userFollowCacheName = "userFollowList";
    final String userFansCacheName = "userFansList";
    final User UserDao = new User().dao();
    final int pageSize = 16;

    /**
     * 获取非自己的某位用户关注列表
     * 与 MyFriendService 中不同，非自己用户所关注的人与自己的好友关系需要单独计算
     * MyFriendService 的 getFollowList 中的目标用户列表已经具备了被关注的条件
     */
    public Page<User> getFollowPage(int userId, int pageNum) {
        String select = "select f.friendId, a.id, a.nickName, a.avatar, a.signature";
        StringBuilder sql = new StringBuilder();
        sql.append("from friend f inner join User a ");
        sql.append("on f.friendId = a.id where f.userId = ? order by f.createAt desc");
        return UserDao.paginate(pageNum, pageSize, select, sql.toString(), userId);
    }

    /**
     * 获取非自己的某位用户粉丝列表
     * 与 MyFriendService 中不同，非自己用户的粉丝与自己的好友关系需要单独计算
     * MyFriendService 的 getFansList 中的目标用户列表已经具备了关注了自己的条件
     */
    public Page<User> getFansPage(int userId, int pageNum) {
        String select = "select f.userId, a.id, a.nickName, a.avatar, a.signature";
        StringBuilder sql = new StringBuilder();
        sql.append("from friend f inner join User a ");
        sql.append("on f.userId = a.id where f.friendId = ? order by f.createAt desc");
        return UserDao.paginate(pageNum, pageSize, select, sql.toString(), userId);
    }

    /**
     * MyFriendService 中的 clearCache(int, int) 会调用该方法，其它地方不使用
     * TODO 暂未启用
     * 1：在 getFollowList() 与 getFansList() 中 put 数据，设置一个合理的过期时间
     * 2：在 add(userId, friendId) 与 delete(userId, friendId) 中调用此 clearCache 方法
     */
    public void clearCache(int userId) {
        CacheKit.remove(userFollowCacheName, userId);
        CacheKit.remove(userFansCacheName, userId);
    }
}