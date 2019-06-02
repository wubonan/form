
package com.daymooc.fcms.like;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.Ret;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * 个人空间、用户空间的粉丝数量旁边添加一个赞数量： 关注(19)  粉丝(999)  赞(9999)
 *
 *
 * 点赞功能，每篇post都可以被人点赞
 * project、project_like、share、share_like ...
 *
 */
public class LikeService {

    public static final LikeService me = new LikeService();

    //对应的表，如果还要给其他表点赞，在这里添加即可
    final String REF_TYPE_ARTCILE = "article";//文章
    final String REF_TYPE_VIDEO = "video";//视频
    final String REF_TYPE_GALLERY = "gallery";//图片
    final String REF_TYPE_POST = "posts";//posts表，目前只有这个

    // 用户前端传入非法参数引发安全问题
    @SuppressWarnings("serial")
	private final Set<String> permissionTables = new HashSet<String>(){{
        add(REF_TYPE_ARTCILE);
        add(REF_TYPE_VIDEO);
        add(REF_TYPE_GALLERY);
        add(REF_TYPE_POST);
    }};

    private void check(String refType) {
        if ( !permissionTables.contains(refType) ) {
            throw new IllegalArgumentException("refType 不正确");
        }
    }

    /**
     * 点赞
     * @param myId 点赞的用户 id，即当前登录用户
     * @param refType 被点赞的表名
     * @param refId 被点赞的表名中的相应的 id 值
     * @param isAdd true 为点赞，false 为取消点赞，null 需要判断是否已被点赞
     */
    public Ret like(int myId, String refType, int refId, Boolean isAdd) {
        check(refType);
        if (isAdd != null) {
            if (isAdd) {
                return save(myId, refType, refId);
            } else {
                return delete(myId, refType, refId);
            }
        } else {
            return like(myId, refType, refId);
        }
    }

    private Ret like(int myId, String refType, int refId) {
        if (isLike(myId, refType, refId)) {
            return delete(myId, refType, refId);
        } else {
            return save(myId, refType, refId);
        }
    }

    // 获取被点赞资源的创建者
    private Integer getUserIdOfRef(String refType, int refId) {
    	String userId = "userId";
    	if (refType.equals("video"))
		{
			userId = "user_id";
		}
 
        return Db.queryInt("select "+userId+" from " + refType + " where id=? limit 1", refId);
    }

    /**
     * 点赞
     */
    private Ret save(final int myId, final String refType, final int refId) {
        final Integer userId = getUserIdOfRef(refType, refId);
        if (userId == null) {
            return Ret.fail("msg", "未找到资源，可能已经被删除");
        }
        if (myId == userId) {
            return Ret.fail("msg", "不能给自己点赞");
        }
        // 如果已经点过赞，则直接退出
        if (isLike(myId, refType, refId)) {
            return Ret.fail("msg", "已经点赞，请刷新页面");
        }
        boolean isOk = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                int n = Db.update("insert into " + refType + "_like(userId, refId, createAt) value(?, ?, now())", myId, refId);
                if (n > 0) {
                    n = Db.update("update " + refType + " set likeCount=likeCount+1 where id=? limit 1", refId);
                    if (n > 0) {
                    	AccountService.me.addLikeCount(userId);
                    }
                }
                
                return n > 0;
            }
        });
        if (isOk) {
            // 向被赞的人发送私信，鼓励创造更多资源
            LikeMessageLogService.me.sendSystemMessage(myId, userId, refType, refId);
        }
        return isOk ? Ret.ok("msg", "点赞成功") : Ret.fail("msg", "点赞失败");
    }

    /**
     * 取消点赞
     */
    private Ret delete(final int myId, final String refType, final int refId) {
        boolean isOk = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                int n = Db.update("delete from " + refType + "_like where userId=? and refId=? limit 1", myId, refId);
                if (n > 0) {
                    n = Db.update("update " + refType + " set likeCount=likeCount-1 where id=? and likeCount>0 limit 1", refId);
                    Integer userId = getUserIdOfRef(refType, refId);
                    if (userId != null) {
                        AccountService.me.minusLikeCount(userId);
                    }
                }
                return n > 0;
            }
        });
        return isOk ? Ret.ok("msg", "取消点赞成功") : Ret.fail("msg", "取消点赞失败");
    }

    /**
     * 对 refType + refId 指向的资源，是否已点赞
     */
    public boolean isLike(int UserId, String refType, int refId) {
        String sql = "select UserId from " + refType + "_like where userId=? and refId=? limit 1";
        return Db.queryInt(sql, UserId, refId) != null;
    }

    /**
     * 设置 article detail 页面的点赞状态
     */
    @SuppressWarnings("rawtypes")
	public void setLikeStatus(User loginUser, String refType, Model refObj, Ret ret) {
        if (loginUser != null) {
            boolean isLike = isLike(loginUser.getId(), refType, refObj.getInt("id"));
            ret.set("isLikeActive", isLike ? "active" : "");
            ret.set("isLikeAdd", isLike ? "false" : "true");
        } else {
            ret.set("isLikeActive", "");
            ret.set("isLikeAdd", "true");
        }
        System.out.println(refObj);
        int likeCount = refObj.getInt("likeCount");
        ret.set("likeCount", likeCount > 0 ? likeCount : "");
    }

    /**
     * 删除被引用的资源时，要删除相关的点赞记录
     */
    private void deleteByRefDeleted(String refType, int refId) {
        Db.update("delete from " + refType + "_like where refId=?", refId);
    }

    /**
     * 删除 article 时，要删除相关的点赞记录
     */
    public void deleteByArticleDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_ARTCILE, refId);
    }

    /**
     * 删除 Video 时，要删除相关的点赞记录
     */
    public void deleteByVideoDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_VIDEO, refId);
    }
    
    /**
     * 删除 gallery 时，要删除相关的点赞记录
     */
    public void deleteByGalleryDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_GALLERY, refId);
    }
    
    /**
     * 删除 post 时，要删除相关的点赞记录
     */
    public void deleteByPostDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_POST, refId);
    }
}

