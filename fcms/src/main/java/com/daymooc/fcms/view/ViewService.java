package com.daymooc.fcms.view;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Db;

public class ViewService
{
	public static final ViewService me = new ViewService();
	final Posts postDao = new Posts().dao();
	
	public Posts getPost(int id)
	{
		Posts post = postDao.findById(id);
		AccountService.me.join("userId", post, "nickName", "avatar");
		return post;
	}
	//更新浏览数
	public void updateViews(int id)
	{
		Db.update("update posts set views=views+1 where id=?", id);
		return;
	}
	
	//获取对应用户的发布文章数
	public int getPostNum(int userId)
	{
		Number postNum = Db.queryNumber("select count(*) from posts where userId=?", userId);
		
		return postNum.intValue();
	}
	
	//获取用户评论数
	public int getCommentNum(int userId)
	{
		Number commentNum = Db.queryNumber("select count(*) from posts_comment where userId=?", userId);
		
		return commentNum.intValue();
	}
}
