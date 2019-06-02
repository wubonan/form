package com.daymooc.fcms.video;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.daymooc.fcms.blog.BlogService;
import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.interceptor.FrontAuthInterceptor;
import com.daymooc.fcms.common.kit.HtmlKit;
import com.daymooc.fcms.common.kit.StringKit;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.newsfeed.NewsFeedService;
import com.daymooc.fcms.newsfeed.ReferMeKit;
import com.daymooc.fcms.post.PostService;
import com.daymooc.fcms.share.Video;
import com.daymooc.fcms.share.VideoAnalysis;
import com.daymooc.fcms.share.Youku;
import com.daymooc.fcms.tags.TagsService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.jfinal.plugin.activerecord.Db;

@Before({FrontAuthInterceptor.class})
public class VideoController extends BaseController
{
	BlogService blogService = BlogService.me;
	VideoService videoService = VideoService.me;
	TagsService tagsService = TagsService.me;
	private PostService postService = PostService.me;
	VideoAnalysis videoRobot;
	@Clear({FrontAuthInterceptor.class})
	public void index()
	{
		Page<Posts> videoPage = videoService.getVideos(getParaToInt("p", 1));
		setAttr("videoPage", videoPage);
		render("index.html");
	}
	
	public void addVideo()
	{
		setAttr("aTypes", blogService.getArticleTypes());
		render("add_video.html");
	}
	
	public void moveVideo()
	{
		setAttr("aTypes", blogService.getArticleTypes());
		render("move_video.html");
	}
	
	public void save()
	{
		UploadFile uploadFile = getFile("video","/video/"+getLoginAccountId());
		UploadFile uploadFile2 = getFile("image","/video/"+getLoginAccountId());
		System.out.println(uploadFile);
		String videoUrl = "/upload/video/"+getLoginAccountId()+"/"+uploadFile.getFileName();
		String coverUrl = "/upload/video/"+getLoginAccountId()+"/"+uploadFile2.getFileName();
		
		String articleContent = getPara("articleContent");
		if (articleContent == null || articleContent.isEmpty())
		{
			renderJson(Ret.fail("msg", "内容不能为空，写点内容吧"));
			return;
		}
		
		Posts posts = new Posts();
		
		posts.setArticleType(getParaToInt("articleType"));
		posts.setContent(getPara("articleContent"));
		posts.setCreateAt(new Date());
		posts.setPostType(Posts.VIDEO);
		posts.setPrivacy(getParaToInt("privacy"));
		//将内容的html转为纯文本
		String summary = HtmlKit.Html2Text(getPara("articleContent"));
		//取前200个字符串作为概要
		posts.setSummary(StringUtils.substring(summary, 0, 200));
		posts.setTitle(getPara("title"));
		posts.setTags(getPara("tags"));
		//保存tags到数据库
		Ret ret = tagsService.saveTags(getPara("tags"));
		if (ret.isFail())
		{
			renderJson(Ret.fail("msg", "对不起，保存标签失败了！"));
			return;
		}
		posts.setImages(coverUrl);
		posts.setLastImages(coverUrl);
		posts.setUserId(getLoginAccountId());
		posts.setVideo(videoUrl);
		
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
		
		renderJson(Ret.fail("msg", "发布失败"));
		return;
	}
	
	public void takeVideo()
	{
		UploadFile cover = getFile("cover","/cover/"+getLoginAccountId()+"/");
		Posts posts = new Posts();

		posts.setArticleType(getParaToInt("articleType"));
		posts.setContent(getPara("articleContent"));
		posts.setCreateAt(new Date());
		posts.setPostType(Posts.VIDEO);
		posts.setPrivacy(getParaToInt("parivacy"));
		//将内容的html转为纯文本
		String summary = HtmlKit.Html2Text(getPara("articleContent"));
		//取前200个字符串作为概要
		posts.setSummary(StringUtils.substring(summary, 0, 200));
		posts.setTitle(getPara("title"));
		posts.setTags(getPara("tags"));
		
		//保存tags到数据库
		Ret ret = tagsService.saveTags(getPara("tags"));
		if (ret.isFail())
		{
			renderJson(Ret.fail("msg", "对不起，保存标签失败了！"));
			return;
		}
		
		posts.setUserId(getLoginAccountId());
		
		
		String url = getPara("url");
		if (StringUtils.isNotBlank(url)) {
			//indexOf没有该字符串返回-1
			if (url.indexOf("youku")==-1)
			{
				//腾讯视频
				String qqVideoId =  StringKit.getTencentVideoId(url);
				if (cover != null)
				{
					posts.setImages("/upload/cover/"+getLoginAccountId()+"/"+cover.getFileName());
					posts.setLastImages("/upload/cover/"+getLoginAccountId()+"/"+cover.getFileName());
				}
				else 
				{
					posts.setImages("/assets/images/cover.png");
					posts.setLastImages("/assets/images/cover.png");
				}
				//https://v.qq.com/iframe/player.html?vid=v0024zl735r&tiny=0&auto=0
				posts.setVideo("https://v.qq.com/iframe/player.html?vid="+qqVideoId+"&tiny=0&auto=0");
			}
			else 
			{
				//优酷视频
				Youku youku = new Youku();
				Video video = youku.take(url);
				posts.setImages(video.getBigThumbnail());
				posts.setLastImages(video.getBigThumbnail());
				//http://player.youku.com/embed/XMzAyNTc1MDc3Mg==
				posts.setVideo("http://player.youku.com/embed/"+video.getId());
			}
			
		}
		else
		{
			renderJson(Ret.fail("msg", "视频地址不能为空"));
			return;
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
		
		renderJson(Ret.fail("msg", "发布失败"));
		return;
	}
	
	//修改视频
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
