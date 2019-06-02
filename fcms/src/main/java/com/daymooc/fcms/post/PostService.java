package com.daymooc.fcms.post;

import com.daymooc.fcms.common.model.Posts;

public class PostService
{
	public static final PostService me = new PostService();
	final Posts postDao = new Posts().dao();
	
	public Posts getPostById(int id)
	{
		Posts posts = postDao.findById(id);
		
		return posts;
	}
}
