package com.daymooc.fcms.question;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.kit.HtmlKit;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Tags;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.newsfeed.NewsFeedService;
import com.daymooc.fcms.newsfeed.ReferMeKit;
import com.daymooc.fcms.post.PostService;
import com.daymooc.fcms.tags.TagsService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

public class QuestionController extends BaseController
{
	BlogService blogService = BlogService.me;
	private QuestionService questionService = QuestionService.me;
	private TagsService tagsService = TagsService.me;
	private static final AccountService accountSrv = AccountService.me;
	private static final BlogService blogSrv = BlogService.me;
	private PostService postService = PostService.me;
	public void index()
	{
		setAttr("questionPage", questionService.getQuestions(getParaToInt("p", 1)));
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagsService.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		render("index.html");
	}
	
	public void hot()
	{
		setAttr("questionPage", questionService.getHotQuestions(getParaToInt("p", 1)));
		List<User> hotUsers = accountSrv.getHotUsers();
		List<Tags> hotTags = tagsService.getHotTags(20);
		List<Posts> hotPosts = blogSrv.getHotPost(8);
		List<Posts> newPosts = blogSrv.getNewestPost(8);
		setAttr("hotUsers", hotUsers);
		setAttr("hotTags", hotTags);
		setAttr("hotPosts", hotPosts);
		setAttr("newPosts", newPosts);
		render("hot.html");
	}
	
	@Before({FrontAuthInterceptor.class})
	public void addQus()
	{
		setAttr("aTypes", blogService.getArticleTypes());
		render("add_question.html");
	}
	
	@Before({FrontAuthInterceptor.class})
	public void save()
	{
		Posts posts = new Posts();
		String articleContent = getPara("articleContent");
		if (articleContent.isEmpty() || articleContent == null)
		{
			renderJson(Ret.fail("msg", "对不起，内容不能为空，请写点内容吧"));
			return;
		}
		posts.setArticleType(getParaToInt("articleType"));
		posts.setContent(articleContent);
		posts.setCreateAt(new Date());
		posts.setPostType(Posts.QUESTION);
		posts.setPrivacy(getParaToInt("privacy"));
		posts.setTags(getPara("tags"));
		//保存tags到数据库
		Ret ret = tagsService.saveTags(getPara("tags"));
		if (ret.isFail())
		{
			renderJson(Ret.fail("msg", "对不起，保存标签失败了！"));
			return;
		}
		posts.setTitle(getPara("title"));
		posts.setUserId(getLoginAccountId());
		
		//将文章内容的html转为纯文本
		String summary = HtmlKit.Html2Text(articleContent);
		//取前200个字符串作为概要
		posts.setSummary(StringUtils.substring(summary, 0, 200));
		//如果文章中含有图片，则获取文章中图片并存在数据库中
		String [] imgs = HtmlKit.getImgs(articleContent);
		
		//如果包含图片，则提取src地址填入数据库
		if ((imgs != null) && (imgs.length > 0))
		{
			posts.setLastImages(imgs[imgs.length - 1]);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<imgs.length;i++)
			{
				
				sb.append(imgs[i]);
				if (i!=imgs.length-1)
				{
					sb.append(",");
				}
				
			}
			posts.setImages(sb.toString());
		}
		
		if (posts.save())
		{
			Ret ret2 = new Ret();
			ret2.set("postId", posts.getId());
			ret2.set("msg", "恭喜您，发布成功！");
			ret2.setOk();
			renderJson(ret2);
			//增加动态信息
			List<Integer> referAccounts = ReferMeKit.buildAtMeLink(posts);
			NewsFeedService.me.createPostsNewsFeed(getLoginAccountId(), posts, referAccounts);
			return;
		}
		
		renderJson(Ret.fail("msg", "对不起，发布失败"));
		return;
	}
	
	@Before({FrontAuthInterceptor.class})
	//修改提问
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
