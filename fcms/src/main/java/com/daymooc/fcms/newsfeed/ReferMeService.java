package com.daymooc.fcms.newsfeed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.PostsComment;
import com.daymooc.fcms.common.model.ReferMe;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 提到我业务，分为 @我 以及评论/回复我
 */
public class ReferMeService
{
	public static final ReferMeService me = new ReferMeService();
	final String referMePageCacheName = "referMePage";
	final ReferMe dao = new ReferMe().dao();
	final NewsFeed newsFeedDao = new NewsFeed().dao();
	final int pageSize = 15;

	/**
	 * 
	* @Title: createCourseReferMe 
	* @Description: 创建文章保存时的refer_me 以及remind记录
	* @param @param referAccounts
	* @param @param id
	* @param @param course    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void createPostsReferMe(List<Integer> referAccounts, Integer newsFeedId, Posts post)
	{
		//针对posts中的@我   生成   refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		clearCache();
	}
	
	public void createPostsCommentReferMe(List<Integer> referAccounts, Integer newsFeedId, PostsComment comment)
	{
		//针对comment中的@我   生成   refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		clearCache();
	}
	
	
	private void createReferMe(List<Integer> referAccounts, int newsFeedId, int type) {
		for (Integer referAccountId : referAccounts) {
			createReferMe(referAccountId, newsFeedId, type);
		}
	}

	/**
	 * 生成一条 refer_me，同时也生成一条 remind 提醒用户
	 */
	private void createReferMe(int referAccountId, int newsFeedId, int type) {
		ReferMe rm = new ReferMe();
		rm.setReferAccountId(referAccountId);       // 接收者
		rm.setNewsFeedId(newsFeedId);                 // newsFeedId
		rm.setType(type);                                        // @我 类型的 referType
		rm.setCreateAt(new Date());
		rm.save();

		// 每一条 refer_me 都创建 remind 提醒
		RemindService.me.createRemindOfReferMe(referAccountId);
	}
	
	/**
	 * 个人空间模块的 @提到我 消息
	 */
	public Page<NewsFeed> paginate(int accountId, int pageNum) {
		String cacheKey = accountId + "_" + pageNum;
		Page<NewsFeed> newsFeedPage = CacheKit.get(referMePageCacheName, cacheKey);
		if (newsFeedPage == null) {
			// 先获取 refer_me 的数据
			String s = "select newsFeedId";
			String f = "from refer_me where referAccountId=? order by id desc";
			Page<ReferMe> referMePage = dao.paginate(pageNum, pageSize, s, f, accountId);
			if (referMePage.getList().size() == 0) {
				newsFeedPage =  new Page<NewsFeed>(new ArrayList<NewsFeed>(), pageNum, pageSize, 0, 0);
				CacheKit.put(referMePageCacheName, cacheKey, newsFeedPage);
				return newsFeedPage;
			}

            // TODO 将来用 AccountService.me.join(...) 代替 inner join 关联操作
			StringBuilder sql = new StringBuilder("select nf.*, a.avatar, a.nickName ");
			sql.append("from news_feed nf inner join user a on nf.accountId=a.id where nf.id in(");
			apppendNewsFeedIds(referMePage.getList(), sql);
			sql.append(") order by id desc");

			List<NewsFeed> newsFeedList = newsFeedDao.find(sql.toString());
			newsFeedPage = new Page<NewsFeed>(newsFeedList, pageNum, pageSize, referMePage.getTotalPage(), referMePage.getTotalRow());

			// 重用 NewsFeed 模块的 loadRefData 功能
			NewsFeedService.me.loadRefData(newsFeedPage);
			CacheKit.put(referMePageCacheName, cacheKey, newsFeedPage);
		}
		return newsFeedPage;
	}
	
	private void apppendNewsFeedIds(List<ReferMe> list, StringBuilder ret) {
		for (int i=0, size=list.size(); i<size; i++) {
			if (i > 0) {
				ret.append(", ");
			}
			ret.append(list.get(i).getNewsFeedId());
		}
	}
	
	public void clearCache() {
		CacheKit.removeAll(referMePageCacheName);
	}

}
