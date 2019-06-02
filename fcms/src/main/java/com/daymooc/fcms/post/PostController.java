package com.daymooc.fcms.post;

import java.util.List;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.common.model.Posts;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

@Before({FrontAuthInterceptor.class})
public class PostController extends BaseController
{
	private NewsFeed feedDao = new NewsFeed().dao();
	public void delete()
	{
		int postId = getParaToInt("postId");
		boolean r = Db.deleteById("posts", postId);
		List<NewsFeed> newsFeeds = feedDao.find("select id from news_feed where refId=?", postId);
		
		for (int i = 0; i < newsFeeds.size(); i++)
		{
			boolean r2 = Db.deleteById("news_feed", newsFeeds.get(i).getId());
			
			if (!r2)
			{
				renderJson(Ret.fail("msg", "删除news_feed失败"));
				return;
			}
		}

		if (r)
		{
			renderJson(Ret.ok("msg", "删除成功"));
			return;
		}
		
		renderJson(Ret.fail("msg", "删除失败"));
	}
	
	
	public void update()
	{
		UploadFile cover = getFile("cover","/cover/"+getLoginAccountId()+"/");
		Posts posts = getModel(Posts.class);
		
		//只有视频才会修改cover
		if (cover != null)
		{
			System.out.println("sssss");
			posts.setImages("/upload/cover/"+getLoginAccountId()+"/"+cover.getFileName());
			posts.setLastImages("/upload/cover/"+getLoginAccountId()+"/"+cover.getFileName());
			
		}
		
		
		if (posts.update())
		{
			renderJson(Ret.ok("msg", "更新成功"));
			return;
		}
		
		renderJson(Ret.fail("msg", "更新失败"));
	}
}
