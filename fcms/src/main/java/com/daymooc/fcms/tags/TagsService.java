package com.daymooc.fcms.tags;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.kit.StringKit;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class TagsService
{
	public static final TagsService me = new TagsService();
	final Posts postsDao = new Posts().dao();
	final Tags tagsDao = new Tags().dao();
	int pageSize = 15;
	
	public List<Posts> getTags()
	{
		List<Posts> posts = postsDao.findByCache("tag", "tags", "select t.name as tagName, temp.* from "
				+ "(select * from posts order by createAt desc) as temp join tags t on "
				+ "locate(t.name,tags)>0 group by tagName order by createAt desc");
		
		return posts;
	}
	
	//获取指定数量的热门标签
	public List<Tags> getHotTags(int tagNum)
	{
		List<Tags> tags = tagsDao.findByCache("hotTag", "hotTag", "select * from tags order by posts desc limit ?", tagNum);
		
		return tags;
	}
	
	//根据标签获取文章
	public Page<Posts> getPostPageByTag(String tagName, int pageNumber)
	{
		String select = "select * ";
		String from = "from posts where locate(?,tags)>0 order by createAt desc";
		
		Page<Posts> postPage = postsDao.paginate(pageNumber, pageSize, select, from, tagName);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	//根据标签获取 文章，按访问量排名
	public Page<Posts> getHotPostPageByTag(String tagName, int pageNumber)
	{
		String select = "select * ";
		String from = "from posts where locate(?,tags)>0 order by views desc";
		
		Page<Posts> postPage = postsDao.paginate(pageNumber, pageSize, select, from, tagName);
		
		AccountService.me.join("userId", postPage.getList(), "nickName", "avatar");
		
		return postPage;
	}
	
	public Ret saveTags(String tags)
	{
		Ret ret = new Ret();
		ret.setOk();//默认为OK
		if ((tags != null) && (!tags.isEmpty()))
		{
			String mTags[] = StringKit.convertStrToArray(tags, ",");
			
			for (String tag : mTags)
			{
				//数据库中不存在标签才保存
				
				String mTg = Db.queryStr("select name from tags where name=?", tag);
				if (mTg == null)
				{
					Tags mTag = new Tags();
					mTag.setPosts(1);//此时文章数1
					mTag.setName(tag);
					if (!mTag.save())
					{
						ret.setFail();//失败之后设为fail
					}
				}
				else //存在刷新数据
				{
					Db.update("update tags set posts=posts+1 where name=?", tag);
				}
				
			}
		}
		return ret;
	}
	
}
