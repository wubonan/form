package com.daymooc.fcms.blog;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.ArticleType;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Page;

public class BlogService
{
	public static final BlogService me = new BlogService();
	final Posts postDao = new Posts().dao();
	final ArticleType articleTypeDao = new ArticleType().dao();
	final int pageSize = 12;
	
	public Page<Posts> getArticles(int pageNum)
	{
		//post_type!=4,在文章页不显示问答
		String select = "select *,TIMESTAMPDIFF(DAY,p.createAt,now()) as days,"
				+ "TIMESTAMPDIFF(MONTH,p.createAt,now()) as months,TIMESTAMPDIFF(YEAR,p.createAt,now()) as years ";
		String from = "from posts p where post_type!=4 and status!=? order by createAt desc";
		Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from, Posts.STATUS_LOCK);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	//获取热门文章
	public Page<Posts> getHotArticles(int pageNum)
	{
		//post_type!=4,在文章页不显示问答
		String select = "select *,TIMESTAMPDIFF(DAY,p.createAt,now()) as days,"
				+ "TIMESTAMPDIFF(MONTH,p.createAt,now()) as months,TIMESTAMPDIFF(YEAR,p.createAt,now()) as years ";
		String from = "from posts p where post_type!=4 order by views desc";
		Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");

		return postPage;
	}
	
	//获取某个用户的所有post
	public Page<Posts> getPosts(int pageNum,int userId)
	{
		String select = "select * ";
		String from = "from posts where userId=? order by createAt desc";
		Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from, userId);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	public List<ArticleType> getArticleTypes()
	{
		List<ArticleType> aTypes = articleTypeDao.find("select * from article_type");
		
		return aTypes;
	}
	
	public List<Posts> getHotPost(int postNum)
	{
		List<Posts> hotPosts = postDao.findByCache("hotArticle", "hotArticle", "select * from posts order by views desc limit ?", postNum);
		
		return hotPosts;
	}
	
	public List<Posts> getNewestPost(int postNum)
	{
		List<Posts> newPosts = postDao.find("select * from posts order by createAt desc limit ?", postNum);
		
		return newPosts;
	}
}
