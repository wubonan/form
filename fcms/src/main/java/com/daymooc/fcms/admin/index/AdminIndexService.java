package com.daymooc.fcms.admin.index;

import java.util.List;

import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.PostsComment;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;
import com.jfinal.plugin.activerecord.Db;

public class AdminIndexService
{
	public static final AdminIndexService me = new AdminIndexService();
	final Posts postsDao = new Posts().dao();
	final User userDao = new User().dao();
	final PostsComment postsComment = new PostsComment().dao();
	final Tags tagDao = new Tags().dao();
	
	/*
	 * 获取发布文章数
	 */
	public long getPostNum()
	{
		long postNum = 0;
		postNum = Db.queryLong("select count(id) from posts");
		
		return postNum;
	}
	
	/**
	 * 获取评论数
	 */
	public long getCommentNum()
	{
		long commentNum = Db.queryLong("select count(id) from posts_comment");
		
		return commentNum;
	}
	
	/**
	 * 获取用户数
	 */
	public long getUserNum()
	{
		long userNum = Db.queryLong("select count(id) from user");
		
		return userNum;
	}
	
	/*
	 * 获取标签数
	 */
	public long getTagNum()
	{
		long tagNum = Db.queryLong("select count(id) from tags");
		
		return tagNum;
	}
	
	/**
	 * 获取对应类型的的post数目
	 */
	public long getPostNumByType(int type)
	{
		long artcielNum = Db.queryLong("select count(*) from posts where post_type=?", type);
		
		return artcielNum;
	}
	
	/*
	 * 获取最新加入的成员
	 */
	public List<User> getUserList(int num)
	{
		List<User> users= userDao.find("select * from user order by createAt desc limit ?", num);
		
		return users;
	}
	
	/**
	 * 获取最新文章
	 */
	public List<Posts> getPostList(int num)
	{
		List<Posts> posts = postsDao.find("select * from posts order by createAt desc limit ?", num);
		
		return posts;
	}
	
}
