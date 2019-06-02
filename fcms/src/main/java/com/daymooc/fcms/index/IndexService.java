package com.daymooc.fcms.index;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.ArticleType;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class IndexService
{
	public static final IndexService me = new IndexService();
	final ArticleType articleTypeDao = new ArticleType().dao();
	final Posts postsDao = new Posts().dao();
	int pageSize = 15;
	
	public List<ArticleType> getArticleTypes()
	{
		List<ArticleType> types = articleTypeDao.findByCache("articleType", "articleType", "select * from article_type");
		
		return types;
	}
	
	public Page<Posts> getPostPageByType(String typeName, int pageNumber)
	{
		int typeId = Db.queryInt("select id from article_type where article_type=?",typeName);
		String select = "select * ";
		String from = "from posts where article_type=? order by createAt desc";
		
		Page<Posts> postPage = postsDao.paginate(pageNumber, pageSize, select, from, typeId);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	public Page<Posts> getHotPostPageByType(String typeName, int pageNumber)
	{
		int typeId = Db.queryInt("select id from article_type where article_type=?",typeName);
		String select = "select * ";
		String from = "from posts where article_type=? order by views desc";
		
		Page<Posts> postPage = postsDao.paginate(pageNumber, pageSize, select, from, typeId);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
}
