package com.daymooc.fcms.blog;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.kit.HtmlKit;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.newsfeed.NewsFeedService;
import com.daymooc.fcms.newsfeed.ReferMeKit;
import com.daymooc.fcms.post.PostService;
import com.daymooc.fcms.tags.TagsService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

@Before({FrontAuthInterceptor.class})
public class BlogController extends BaseController
{
	private BlogService blogService = BlogService.me;
	private TagsService tagsService = TagsService.me;
	private PostService postService = PostService.me;
	public void add()
	{
		setAttr("aTypes", blogService.getArticleTypes());
		render("add_blog.html");
	}
	
	public void post()
	{
		if (StringUtils.isBlank(getPara("articleTitle")))
		{
			renderJson(Ret.fail("msg", "标题不能为空"));
			return;
		}
		if (StringUtils.isBlank(getPara("articleContent")))
		{
			renderJson(Ret.fail("msg", "内容不能为空，请写点内容吧"));
			return;
		}
		Posts post = new Posts();
		String articleContent = getPara("articleContent");
		post.setTitle(getPara("articleTitle"));
		post.setContent(articleContent);
		post.setCreateAt(new Date());
		post.setPrivacy(getParaToInt("privacy"));
		post.setArticleType(getParaToInt("articleType"));
		post.setPostType(Posts.ARTICLE);
		post.setUserId(getLoginAccountId());
		post.setTags(getPara("tags"));
		//保存tags到数据库
		Ret ret = tagsService.saveTags(getPara("tags"));
		if (ret.isFail())
		{
			renderJson(Ret.fail("msg", "对不起，保存标签失败了！"));
			return;
		}
		
		//将文章内容的html转为纯文本
		String summary = HtmlKit.Html2Text(articleContent);
		//取前200个字符串作为概要
		post.setSummary(StringUtils.substring(summary, 0, 200));
		//如果文章中含有图片，则获取文章中图片并存在数据库中
		String [] imgs = HtmlKit.getImgs(articleContent);
		
		//如果包含图片，则提取src地址填入数据库
		if ((imgs != null) && (imgs.length > 0))
		{
			post.setLastImages(imgs[imgs.length - 1]);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<imgs.length;i++)
			{
				
				sb.append(imgs[i]);
				if (i!=imgs.length-1)
				{
					sb.append(",");
				}
				
			}
			post.setImages(sb.toString());
		}
		
		if (post.save())
		{
			Ret ret2 = new Ret();
			ret2.set("postId", post.getId());
			ret2.set("msg", "恭喜您，发布成功！");
			ret2.setOk();
			renderJson(ret2);
			//增加动态信息
			List<Integer> referAccounts = ReferMeKit.buildAtMeLink(post);
			NewsFeedService.me.createPostsNewsFeed(getLoginAccountId(), post, referAccounts);
			return;
		}
		
		renderJson(Ret.fail("msg", "发布失败"));
		return;
		
	}
	
	public void mod()
	{
		if (getParaToInt(0) != null)
		{
			int userId = Db.queryInt("select userId from posts where id=?", getParaToInt(0));
			if (getLoginAccountId() == userId)
			{
				int postId = getParaToInt(0);
				Posts post = postService.getPostById(postId);
				setAttr("aTypes", blogService.getArticleTypes());
				setAttr("post", post);
				render("mod_post.html");
			}
			else
			{
				renderError(404);
			}
		}	
	}
}
