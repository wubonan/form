package com.daymooc.fcms.admin.post;

import com.daymooc.fcms.common.model.Posts;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class AdminPostService
{
	public static final AdminPostService me = new AdminPostService();
	final Posts postDao = new Posts().dao();
	int pageSize = 15;
	
	public Page<Posts> getPostByType(int type, int pageNumber)
	{
		String select = "select * ";
		String from = "from posts where post_type=? order by id desc";
		Page<Posts> postPage = postDao.paginate(pageNumber, pageSize, select, from, type);
		
		return postPage;
	}
	
	public Ret deletePost(int id)
	{
		//先获取该文章的tag，用于更新tags表
		String tags=Db.queryStr("select tags from posts where id=?",id);
		String tag[] = tags.split(",");
		
		//更新tags
		for(int i=0; i<tag.length; i++)
		{
			Db.update("update tags set posts=posts-1 where name=?", tag[i]);
		}
		
		boolean r = Db.deleteById("posts", id);
		
		if (r)
		{
			return Ret.ok("msg", "删除文章成功");
		}
		
		return Ret.fail("msg", "删除文章失败");
	}
	
	public Ret lockPost(int id)
	{
		int r = Db.update("update posts set status=? where id=?",Posts.STATUS_LOCK, id);
		
		if (r > 0)
		{
			return Ret.ok("msg", "锁定文章成功");
		}
		
		return Ret.fail("msg", "锁定文章失败");
	}
	
	public Ret unlockPost(int id)
	{
		int r = Db.update("update posts set status=? where id=?",Posts.STATUS_PUB, id);
		
		if (r > 0)
		{
			return Ret.ok("msg", "解锁文章成功");
		}
		
		return Ret.fail("msg", "解锁文章失败");
	}
	
}
