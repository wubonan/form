package com.daymooc.fcms.question;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Page;

public class QuestionService
{
	public static final QuestionService me = new QuestionService();
	final Posts postDao = new Posts().dao();
	int pageSize = 20;
	
	public Page<Posts> getQuestions(int pageNum)
	{
		String select = "select * ";
		String from = "from posts where post_type=4 and status=? order by createAt desc";
		
		Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from, Posts.STATUS_PUB);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	public Page<Posts> getHotQuestions(int pageNum)
	{
		String select = "select * ";
		String from = "from posts where post_type=4 and status=? order by views desc";
		
		Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from, Posts.STATUS_PUB);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
}
