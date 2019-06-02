package com.daymooc.fcms.search;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class SearchService
{
	public static final SearchService me = new SearchService();
	final Posts postDao = new Posts().dao();
	int pageSize = 15;

	// 根据关键字搜索文章
	public Page<Posts> searchPosts(String keyWord, int pageNum)
	{
		// !!!!!!前方有坑，注意，Mysql的CONCAT中只要有一个为null,那么查询结果就为null，所以要加IFNULL判断
		/*
		 * List<Course> courses = courseDao.find(
		 * "select * from course where CONCAT" +
		 * "(course_name,IFNULL(course_des,''),IFNULL(course_detail,'')) LIKE  ?;"
		 * , "%"+keyWord+"%");
		 */
		// 搜索改进，上面的方法，一次只能搜索一个关键词，使用下面的方法来进行搜索，可匹配多个关键词
		// 这里的参数格式为：语言|基础|留学
		// student|girl|boy
		String select = "select * ";
		String from = "from posts where privacy=0 and CONCAT"
						+"(title,IFNULL(summary,''),IFNULL(content,'')) REGEXP ?";
		Page<Posts> posts = postDao.paginate(pageNum, pageSize, select, from, keyWord);
		
		AccountService.me.join("userId", posts.getList(), "nickName", "avatar");
		return posts;
	}

	public int getSearchNum(String keyWord)
	{
		Number num = Db.queryNumber("select count(*) from posts where privacy=0 and CONCAT"
				+ "(title,IFNULL(summary,''),IFNULL(content,'')) REGEXP ?;", keyWord);

		return num.intValue();
	}
}
