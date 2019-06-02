package com.daymooc.fcms.comment;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.kit.SensitiveWordsKit;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.common.safe.RestTime;
import com.daymooc.fcms.newsfeed.NewsFeedService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class CommentController extends BaseController
{
	private final CommentService srv = CommentService.me;
	/**
	 * 
	* @Title: saveReplay 
	* @Description: 保存评论
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void saveReply()
	{
		if (notLogin()) {
			renderJson(Ret.fail("msg", "登录后才可以评论"));
			return ;
		}
		String restTimeMsg = RestTime.checkRestTime(getLoginAccount());
		if (restTimeMsg != null) {
			renderJson(Ret.fail("msg", restTimeMsg));
			return ;
		}
		String replyContent = getPara("replyContent");
		if (StrKit.isBlank(replyContent)) {
			renderJson(Ret.fail("msg", "回复内容不能为空"));
			return ;
		}
		if (SensitiveWordsKit.checkSensitiveWord(replyContent) != null) {
			renderJson(Ret.fail("msg", "回复内容不能包含敏感词"));
			return ;
		}

		Ret ret = srv.saveReply(getParaToInt("articleId"), getLoginAccountId(), replyContent);
		

		// 注入 nickName 与 avatar 便于 renderToString 生成 replyItem html 片段
		User loginAccount = getLoginAccount();
		ret.set("loginAccount", loginAccount);
		// 用模板引擎生成 HTML 片段 replyItem
		String replyItem = renderToString("/_view/view/common/_reply_item.html", ret);
		
		ret.set("replyItem", replyItem);
		renderJson(ret);
	}
	
	public void delete()
	{
		int commentId = getParaToInt("commentId");
		int feedId = getParaToInt("feedId");
		int postId = getParaToInt("postId");
		boolean r2 = Db.deleteById("news_feed", feedId);
		
		if (!r2)
		{
			renderJson(Ret.fail("msg", "删除newsFeed失败"));
			return;
		}
		boolean r = Db.deleteById("posts_comment", commentId);
		if (!r)
		{
			renderJson(Ret.fail("msg", "删除评论失败"));
			return;
		}
		//清除cache
		NewsFeedService.me.clearCache();
		//评论数减去1
		Db.update("update posts set comments=comments-1 where id=?",postId);
		renderJson(Ret.ok("msg", "删除评论成功"));
		return;
	}
}
