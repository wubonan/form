package com.daymooc.fcms.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class UserService
{
	public final static UserService me = new UserService();
	final String myFollowCacheName = "myFollowList";
	final String myFansCacheName = "myFansList";
	final String followAndFansTotalCacheName = "followAndFansTotal";
	private final User userDao = new User().dao();
	int pageSize = 12;
	


	public User getUserById(int userId)
	{
		User account = userDao.findFirst("select * from user where id=?", userId);
		return account;
	}
	
	/**
	 * 判断是否已经关注该用户
	 */
	
	public boolean isFollowUser(int userId, int friendId)
	{
		User user = userDao.findFirst("select * from friend where userId=? and friendId=?", userId, friendId);
		
		//如果记录已经存在，则表示已经关注
		if (user != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	* @Title: followUser 
	* @Description: 关注或者取消关注用户
	* @param @return    设定文件 
	* @return Ret    返回类型 
	* @throws
	 */
	public Ret followUser(int userId, int friendId)
	{
		User user = userDao.findFirst("select * from friend where userId=? and friendId=?", userId, friendId);
		
		//如果记录已经存在，则表示取消关注操作
		if (user != null)
		{
			int r = Db.update("delete from friend where userId=? and friendId=?", userId, friendId);
			
			if (r>0)
			{
				return Ret.ok("msg", "已取消关注");
			}
			
			return Ret.fail("msg", "取消关注失败");
		}
		
		//如果记录不存在，则插入记录
		int r = Db.update("insert into friend "
				+ "(userId, friendId, createAt) values (?,?,?)", userId, friendId, new Date());
		if (r>0)
		{
			return Ret.ok("msg", "已关注");
		}
		
		return Ret.fail("msg", "关注失败");
	}
	
	//根据用户Id获取关注的用户
	public List<User> getFollowUser(int userId)
	{
		List<User> follows = userDao.find("select f.*,u.avatar,u.nickName,u.signature from friend f join user u on f.friendId=u.id and f.userId=?",userId);
		
		return follows;
	}
	
	public List<User> getFans(int userId)
	{
		List<User> fans = userDao.find("select f.*,u.avatar,u.nickName,u.signature from friend f join user u on f.userId=u.id and f.friendId=?",userId);
		
		return fans;
	}
	
	/**
	 * 这里后续定义为常量
	 * 查询 userId 与 friendId 之间的关系，返回值为 -1、1、2、3、4 表达的含义分别为：
	 * -1：userId 与 friendId 值相同
	 * 0： userId 与 friendId 无任何关系
	 * 1： userId 关注了 friendId
	 * 2： friendId 关注了 userId
	 * 3： userId 与 friendId 互相关注
	 */
	public int getFriendRelation(int userId, int friendId) {
		if (userId == friendId) {
			return -1;                  // userId 与 friendId 相同
		}

		List<Record> list = Db.find(
				"select userId, friendId from friend where userId= ? and friendId= ? union all " +
				"select userId, friendId from friend where userId= ? and friendId= ?",
				userId, friendId, friendId, userId);
		if (list.size() == 0) {
			return 0;                   // 两个账号无任何关系
		}
		if (list.size() == 1) {
			if (list.get(0).getInt("userId") == userId) {
				return 1;               // userId 关注了 friendId
			} else {
				return 2;               // friendId 关注了 userId
			}
		}
		if (list.size() == 2) {
			return 3;                   // userId 与 friendId 互相关注
		}
		throw new RuntimeException("不可能存在的第五种关系，正常情况下该异常永远不可能抛出");
	}
	
	/**
     * 获取关注与粉丝总数
     * 将其缓存起来
     */
    public int[] getFollowAndFansCount(int userId) {
        // 两种 sql 都可以实现功能，注意这里要使用 union all，需要避免去重
        String sql = "select count(*) from friend f1 where userId = ? union all " +
                     "select count(*) from friend f2 where friendId = ? ";
        List<Long> list = Db.query(sql, userId, userId);
        return new int[]{list.get(0).intValue(), list.get(1).intValue()};
        // String sql =  "select * from " +
        //             "   (select count(*) from friend f1 where userId = ?) as t1 ," +
        //             "   (select count(*) from friend f2 where friendId = ?) as t2";
        // List<Object[]> list = Db.query(sql, userId, userId);
        // return new int[]{((Long)list.get(0)[0]).intValue(), ((Long)list.get(0)[1]).intValue()};
    }

    /**
     * 无论是添加还是删除好友，都调用一次该方法，调用的时候 accountId 与 friendId 的次序无关紧要
     * TODO 暂未启用
     * 1：在 getFollowList() 与 getFansList() 中 put 数据，设置一个合理的过期时间
     * 2：在 add(accountId, friendId) 与 delete(accountId, friendId) 中调用此 clearCache 方法
     */
    public void clearCache(int accountId, int friendId) {
        CacheKit.remove(myFollowCacheName, accountId);
        CacheKit.remove(myFollowCacheName, friendId);

        CacheKit.remove(myFansCacheName, accountId);
        CacheKit.remove(myFansCacheName, friendId);

        CacheKit.remove(followAndFansTotalCacheName, accountId);
        CacheKit.remove(followAndFansTotalCacheName, friendId);

        UserFriendService.me.clearCache(accountId);
        UserFriendService.me.clearCache(friendId);
    }
}
