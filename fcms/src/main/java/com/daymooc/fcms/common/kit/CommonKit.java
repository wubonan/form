package com.daymooc.fcms.common.kit;

import java.util.List;

import com.daymooc.fcms.common.model.FriendLink;
import com.daymooc.fcms.common.model.PostsComment;
import com.jfinal.plugin.activerecord.Db;

/*
 * 
* <p>Title: CommonKit</p>
* <p>Description: 这里可以放一些通用方法，比如用户是否相互关注，用户文章数，用户评论数等等</p>
* <p>Company: </p> 
* @author liujiaxiang 
* @date 2017年9月9日 下午3:20:44
 */
public class CommonKit
{
	final PostsComment commentDao = new PostsComment().dao();
	final FriendLink linkDao = new FriendLink().dao();
	//获取某人评论数
	public static long getCommentNum(int userId)
	{
		long commentNum = Db.queryLong("select count(id) from posts_comment where userId=?", userId);
		
		return commentNum;
	}
	//获取某人文章数
	public static long getPostNum(int userId)
	{
		long postNum = Db.queryLong("select count(id) from posts where userId=?", userId);
		
		return postNum;
	}
	
	//获取通知数
	public static int getMsgNum(int userId)
	{
		int msgNum = 0;
		if(Db.queryNumber("select sum(referMe+message+fans) as msgNum from remind where accountId=?",userId) != null)
		{
			msgNum = Db.queryNumber("select sum(referMe+message+fans) as msgNum from remind where accountId=?",userId).intValue();
			
		}
		return msgNum;
	}
	
	//获取私信数
	public static int getMessageNum(int userId)
	{
		int msgNum = 0;
		if(Db.queryNumber("select sum(referMe+message) as msgNum from remind where accountId=?",userId) != null)
		{
			msgNum = Db.queryNumber("select sum(referMe+message) as msgNum from remind where accountId=?",userId).intValue();
			
		}
		return msgNum;
	}
	
	//获取友情链接
	public List<FriendLink> getFriendLink()
	{
		List<FriendLink> friendLinks = linkDao.find("select * from friend_link");
		
		return friendLinks;
	}
}
