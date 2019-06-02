package com.daymooc.fcms.gallery;

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
import com.daymooc.fcms.tags.TagsService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.jfinal.plugin.activerecord.Db;

@Before({FrontAuthInterceptor.class})
public class GalleryController extends BaseController
{
	BlogService blogService = BlogService.me;
	TagsService tagsService = TagsService.me;
	GalleryService galleryService = GalleryService.me;
	private PostService postService = PostService.me;
	
	@Clear({FrontAuthInterceptor.class})
	public void index()
	{
		setAttr("posts", galleryService.getGallery());
		render("index.html");
	}
	
	public void getImgs()
	{
		int pageNum = getParaToInt("p", 2);
		Page<Posts> posts = galleryService.getImages(pageNum);
		
		Ret ret = new Ret();
		ret.setOk();
		ret.set("posts", posts.getList());
		renderJson(ret);
		
	}
	
	public void addImg()
	{
		setAttr("aTypes", blogService.getArticleTypes());
		render("add_image.html");
	}
	
	public void uploadImg() 
	{
		//getOriginalFileName和getFileName的区别：前者获取上传时的名称，或者获取保存时的名称
		List<UploadFile> uploadFiles = getFiles("/gallery/"+getLoginAccountId()+"/");
		
		int fileNum = uploadFiles.size();
		String images = null;
		for(int i=0;i < fileNum;i++)
		{
			String fileName ="/upload/gallery/" +getLoginAccountId() + "/" + uploadFiles.get(i).getFileName();
			images = StringKit.conactString(fileName,",");
		}
		Ret ret = new Ret();
		ret.setOk();
		ret.set("path", images);
		ret.set("msg","图片上传成功，请继续完善其他内容！");
		renderJson(ret);
	}
	
	public void save()
	{
		Posts posts = new Posts();

		//Parameter   : name=a1.jpg  id=WU_FILE_0  privacy=2323  lastModifiedDate=Wed Oct 21 2015 10:40:56 GMT+0800 (中国标准时间)  type=image/jpeg  title=2323  articleType=1  size=17306  tags=  articleContent=23434

		if (getPara("articleContent") == null)
		{
			renderJson(Ret.fail("msg", "内容不能为空，添加点内容吧"));
			return;
		}
		posts.setTitle(getPara("title"));
		posts.setArticleType(getParaToInt("articleType"));
		posts.setContent(getPara("articleContent"));
		posts.setCreateAt(new Date());
		posts.setImages(getPara("images"));
		System.out.println("img:"+getPara("images"));
		String images[] = StringKit.convertStrToArray(getPara("images"), ",");
		for (int i = 0; i < images.length; i++) 
		{
			//最后一张图片
			if (i == (images.length - 1)) 
			{
				posts.setLastImages(images[i]);
			}
		}
		posts.setPostType(Posts.IMAGE);
		posts.setPrivacy(getParaToInt("privacy"));
		//将内容的html转为纯文本
		String summary = HtmlKit.Html2Text(getPara("articleContent"));
		//取前200个字符串作为概要
		posts.setSummary(StringUtils.substring(summary, 0, 200));
		posts.setTags(getPara("tags"));
		//保存tags到数据库
		Ret ret = tagsService.saveTags(getPara("tags"));
		if (ret.isFail())
		{
			renderJson(Ret.fail("msg", "对不起，保存标签失败了！"));
			return;
		}
		posts.setUserId(getLoginAccountId());
		List<Integer> referAccounts = ReferMeKit.buildAtMeLink(posts);
		if (posts.save()) {
			Ret ret2 = new Ret();
			ret2.set("postId", posts.getId());
			ret2.set("msg", "恭喜您，发布成功！");
			ret2.setOk();
			renderJson(ret2);
			//增加动态信息
			NewsFeedService.me.createPostsNewsFeed(getLoginAccountId(), posts, referAccounts);
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
