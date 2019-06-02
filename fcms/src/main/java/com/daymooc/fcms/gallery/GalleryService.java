package com.daymooc.fcms.gallery;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.plugin.activerecord.Page;

public class GalleryService
{
	public static final GalleryService me = new GalleryService();
	final Posts postsDao = new Posts().dao();
	int pageSize = 12;
	
	public List<Posts> getGallery()
	{
		List<Posts> posts = postsDao.findByCache("gallery", "gallery", "select * from posts where post_type=3 and status=? limit 12", Posts.STATUS_PUB);
		
		return posts;
	}
	
	public Page<Posts> getImages(int pageNumber)
	{
		String select = "select * ";
		String from   = "from posts where post_type=3 and status=?";
		Page<Posts> posts = postsDao.paginate(pageNumber, pageSize, select, from, Posts.STATUS_PUB);
		
		AccountService.me.join("userId", posts.getList(), "nickName", "avatar");
		
		return posts;
	}
}
