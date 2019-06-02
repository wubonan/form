
package com.daymooc.fcms.like;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.message.MessageService;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 点赞消息业务
 * 当某位用户第一次对某个资源点赞成功后要向资源所有者发送系统消息
 * 发完消息以后需要向 like_message_log 中写入记录
 * 由于点赞可以是一个反复操作，为了避免多次发送系统消息引入此表
 */
public class LikeMessageLogService {

    public static final LikeMessageLogService me = new LikeMessageLogService();

    // 用于标识各种 refType 类型
    public static final int REF_TYPE_POSTS = 1; //目前只有post点赞
    // public static final int REF_TYPE_PROJECT_REPLY = 2;  // 暂时不用
    //public static final int REF_TYPE_SHARE = 3;
    // public static final int REF_TYPE_SHARE_REPLY = 4;    // 暂时不用
    //public static final int REF_TYPE_FEEDBACK = 5;
    // public static final int REF_TYPE_FEEDBACK_REPLY = 6; // 暂时不用

    @SuppressWarnings("serial")
	private static Map<String, Integer> map = new HashMap<String, Integer>(){{
        put("posts", REF_TYPE_POSTS);
        //put("share", REF_TYPE_SHARE);
        //put("feedback", REF_TYPE_FEEDBACK);
    }};

    public void sendSystemMessage(int myId, int userId, String refType, int refId) {
        try {
            doSendSystemMessage(myId, userId, refType, refId);
        } catch (Exception e) {
            LogKit.error(e.getMessage(), e);
        }
    }

    // TODO 考虑在单独的线程中调用，或者做成异步任务调度形式，提升性能
    private void doSendSystemMessage(int myId, int userId, String tableName, int refId) {
        String sql = "select accountId from like_message_log where accountId=? and refType=? and refId=?";
        Integer refType = getRefTypeValue(tableName);
        // 当 like_message_log 没有对应的记录时，才去发私信，否则证明已经发过私信
        if (Db.queryInt(sql, myId, refType, refId) == null) {
            Record r = new Record()
                    .set("accountId", myId)
                    .set("refType", refType)
                    .set("refId", refId)
                    .set("createAt", new Date());
            Db.save("like_message_log", r);

            saveSystemMessage(myId, userId, tableName, refType, refId);
        }
    }

    private void saveSystemMessage(int myId, int userId, String tableName, int refType, int refId) {
        Record ref = Db.findFirst("select id, title, likeCount, post_type from " + tableName + " where id=? limit 1", refId);
        // 被引用的资源存在时才去发私信，资源可能随时会被删除
        if (ref == null) {
            return ;
        }

        User my = AccountService.me.getById(myId);
        String msg = "@" + my.getNickName() + " 刚刚赞了你的";
        String postType = "";
        if (refType == REF_TYPE_POSTS) {
        	if (ref.getInt("post_type") == 1)
			{
				postType = "article";
			}
        	else if (ref.getInt("post_type") == 2)
			{
        		postType = "video";
			}
        	else if (ref.getInt("post_type") == 3)
			{
        		postType = "gallery";
			}
        	else if (ref.getInt("post_type") == 4)
			{
        		postType = "question";
			}
            msg = msg + "文章：<a href='/view/" +postType +"/" + ref.getInt("id") +"' target='_blank'>" + ref.getStr("title");
        } 
//        else if (refType == REF_TYPE_SHARE) {
//            msg = msg + "分享：<a href='/share/" + ref.getInt("id") +"' target='_blank'>" + ref.getStr("title");
//        } 
        else 
        {
            throw new RuntimeException("refType 不正确，请告知管理员");
        }
        msg = msg + "</a>，目前被赞次数为：" + ref.getInt("likeCount");
        MessageService.me.sendSystemMessage(myId, userId, msg);
    }

    private Integer getRefTypeValue(String tableName) {
        Integer refType = map.get(tableName);
        if (refType == null) {
            throw new IllegalArgumentException("tableName 不正确");
        }
        return refType;
    }
}
