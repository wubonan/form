package com.daymooc.fcms.newsfeed;

import java.util.Date;
import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.NewsFeed;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.PostsComment;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 
* <p>Title: NewsFeedService</p>
* <p>Description: 动态消息业务，包括所有动态，热门，@提到我
* 	其中，动态消息为自己的动态及关注的人的动态，热门为学习点击量最多的课程，@提到我分为@我的消息以及评论/回复我
* </p>
* <p>Company: greennet</p> 
* @author liujiaxiang 
* @date 2017年6月29日 上午10:28:14
 */
public class NewsFeedService
{
	public static final NewsFeedService me = new NewsFeedService();
	final String newsFeedPageCacheName = "newsFeedPage";
	final int pageSize = 15;
	final NewsFeed dao = new NewsFeed().dao();
	final Posts postDao = new Posts().dao();
	final PostsComment commentDao = new PostsComment().dao();
	
	/**
	 * 共用的分页查询
	 */
	private Page<NewsFeed> doPaginate(String cacheName, Object cacheKey, int pageNum, String select, String from, Object... paras) {
        Page<NewsFeed> newsFeedPage = dao.paginate(pageNum, pageSize, select, from, paras);
        AccountService.me.joinNickNameAndAvatar(newsFeedPage.getList());
        loadRefData(newsFeedPage);
        CacheKit.put(cacheName, cacheKey, newsFeedPage);
		return newsFeedPage;
	}
	
	/**
     * 个人空间模块的动态消息，显示自己以及所有关注用户的动态消息
     */
    public Page<NewsFeed> paginate(int accountId, int pageNum)
    {
        String cacheKey =  accountId + "_" + pageNum;
        Page<NewsFeed> newsFeedPage = CacheKit.get(newsFeedPageCacheName, cacheKey);
        if (newsFeedPage == null) {
            String select = "select nf.*";
            StringBuilder from = new StringBuilder()
                    .append("from ( ")
                    .append("       select ft.userId, ft.friendId from friend ft union all (select ").append(accountId).append(", ").append(accountId).append(") ")
                    .append(") as f inner join news_feed nf on f.friendId=nf.accountId and f.userId=? order by id desc");
            newsFeedPage = doPaginate(newsFeedPageCacheName, cacheKey, pageNum, select, from.toString(), accountId);
        }
        return newsFeedPage;
    }
    
    /**
     * 所有动态消息，不添加任何条件,管理员使用该方法
     */
    Page<NewsFeed> paginateForAllNewsFeed(int pageNum) {
        String cacheKey = "all_" + pageNum;
        Page<NewsFeed> newsFeedPage = CacheKit.get(newsFeedPageCacheName, cacheKey);
        if (newsFeedPage == null) {
            String select = "select nf.*";
            String from = "from news_feed nf order by id desc";
            newsFeedPage = doPaginate(newsFeedPageCacheName, cacheKey, pageNum, select, from);
        }
        return newsFeedPage;
    }
    
	/**
	 * 创建posts动态
	 */
	public void createPostsNewsFeed(int accountId, Posts posts, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_POSTS);
		nf.setRefId(posts.getId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加项目 @提到我 消息，以及 remind 记录
		ReferMeService.me.createPostsReferMe(referAccounts, nf.getId(), posts);
	}
	/**
	 * 创建posts动态
	 */
	public void createCommentsNewsFeed(int accountId, PostsComment comments, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_POSTS_COMMENTS);
		nf.setRefId(comments.getId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加项目 @提到我 消息，以及 remind 记录
		ReferMeService.me.createPostsCommentReferMe(referAccounts, nf.getId(), comments);
	}
	
	void loadRefData(Page<NewsFeed> newsFeedPage) {
		List<NewsFeed> list = newsFeedPage.getList();
		for (NewsFeed nf : list) {
			System.out.println("nf"+nf);
			//post类型
			if (nf.getRefType() == NewsFeed.REF_TYPE_POSTS) {
				String sql = "select p.id, p.title, p.userId, p.summary,p.post_type,p.likeCount,p.comments "
						+ "from posts p where p.id = ? limit 1";
				Posts ref = postDao.findFirst(sql, nf.getRefId());
				System.out.println(ref);
				if (ref != null) {
					System.out.println("ref is not null");
					if (ref.getPostType() == Posts.ARTICLE)
					{
						ref.put("href", "/view/article/" + ref.getId());
					}
					else if (ref.getPostType() == Posts.IMAGE) {
						ref.put("href", "/view/gallery/" + ref.getId());
					}
					else if (ref.getPostType() == Posts.QUESTION) {
						ref.put("href", "/view/question/" + ref.getId());
					}
					else if (ref.getPostType() == Posts.VIDEO) {
						ref.put("href", "/view/video/" + ref.getId());
					}
					
				}
				ref.put("feedType",NewsFeed.REF_TYPE_POSTS);
				nf.put("ref", ref);
			}
			//post评论
			else if (nf.getRefType() == NewsFeed.REF_TYPE_POSTS_COMMENTS) {
				String sql = "select pc.userId,pc.content as mContent,pc.createAt,p.* from posts_comment pc join"
						+ " posts p on pc.postId=p.id where pc.id=? limit 1";
				PostsComment ref = commentDao.findFirst(sql, nf.getRefId());
				
				ref.put("feedType",NewsFeed.REF_TYPE_POSTS_COMMENTS);
				nf.put("ref", ref);
			}
			else {
				throw new IllegalStateException("错误的news_feed type 值：" + nf.getRefType());
			}
		}
	}
	
	public void clearCache() {
		CacheKit.removeAll(newsFeedPageCacheName);
	}
}
