package com.daymooc.fcms.common.account;


import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.ehcache.CacheKit;
import java.util.List;

/**
 * 账户业务
 */
@SuppressWarnings("rawtypes")
public class AccountService {

	public static final AccountService me = new AccountService();
	private final User dao = new User().dao();
	private final String allUsersCacheName = "allUsers";

	public void updateUserAvatar(int UserId, String relativePathFileName) {
		Db.update("update user set avatar=? where id=? limit 1", relativePathFileName, UserId);
        clearCache(UserId);
	}

	/**
	 * 通过nickName获取User对象，仅返回指定的字段，多字段用逗号分隔
	 */
	public User getByNickName(String nickName, String columns) {
		if (StrKit.isBlank(nickName)) {
			return null;
		}
		return dao.findFirst("select " + columns +" from user where nickName=?  and status=?  limit 1", nickName, User.STATUS_OK);
	}

	/**
	 * 通过 id 获取User对象，只能获取到未被 block 的 User
	 */
	public User getUsefulById(int UserId) {
		// return dao.findFirst("select " + columns +" from User where id=? and status=? limit 1", UserId, User.STATUS_OK);
        User User = getById(UserId);
        return User.isStatusOk() ? User : null;
	}

    /**
     * 优先从缓存中获取 User 对象，可获取到被 block 的 User
     */
    public User getById(int UserId) {
        // 优先从缓存中取，未命中缓存则从数据库取
        User User = CacheKit.get(allUsersCacheName, UserId);
        if (User == null) {
            // 考虑到可能需要 join 状态不合法的用户，先放开 status 的判断
            // User = dao.findFirst("select * from User where id=? and status=? limit 1", UserId, User.STATUS_OK);
            User = dao.findFirst("select * from user where id=? limit 1", UserId);
            if (User != null) {
                User.removeSensitiveInfo();
                CacheKit.put(allUsersCacheName, UserId, User);
            }
        }
        return User;
    }

	public void joinNickNameAndAvatar(List<? extends Model> modelList) {
		join("accountId", modelList, "nickName", "avatar");
	}

	public void joinNickNameAndAvatar(Model model) {
		join("accountId", model, "nickName", "avatar");
	}

	public void join(String joinOnField, List<? extends Model> modelList, String... joinAttrs) {
		if (modelList != null) {
			for (Model m : modelList) {
				join(joinOnField, m, joinAttrs);
			}
		}
	}

	/**
	 * 在Posts 模块，需要关联查询获取 User 对象的 nickName、avatar 时使用此方法
	 * 避免使用关联查询，优化性能，在使用中更关键的地方在于缓存的清除
	 * @param joinOnField join 使用的字段名，User 这端使用 id
	 * @param model 需要 join 的 model
	 * @param joinAttrs 需要 join 到 model 中的的属性名称
	 */
	public void join(String joinOnField, Model model, String... joinAttrs) {
		
		Integer UserId = model.getInt(joinOnField);
		if (UserId == null) {
			throw new RuntimeException("Model 中的 \"" + joinOnField  + "\" 属性值不能为 null");
		}

        User User = getById(UserId);

        // join 真正开始的地方，前面是准备工作
		if (User != null) {
			for (String attr : joinAttrs) {
				model.put(attr, User.get(attr));
			}
		} else {
			throw new RuntimeException("未找到 User 或者 User 状态不合法，User 的 id 值为：" + UserId + " 可能是数据库数据不一致");
		}
	}

    /**
     * 更新 likeCount 字段
     * TODO 未来做成延迟更新模式
     */
    private void updateLikeCount(int UserId, boolean isAdd) {
        String sql = isAdd ?
                "update user set likeCount=likeCount+1 where id=? limit 1" :
                "update user set likeCount=likeCount-1 where id=? and likeCount > 0 limit 1";
        int n = Db.update(sql, UserId);
        if (n > 0) {
            // 直接更新缓存中的 likeCount 值
            User User = CacheKit.get(allUsersCacheName, UserId);
            if (User != null) {
                User.setLikeCount(User.getLikeCount() + (isAdd ? 1 : -1));
            }
        }
    }

    /**
     * likeCount 增加 1
     */
    public void addLikeCount(int UserId) {
        updateLikeCount(UserId, true);
    }

    /**
     * likeCount 减去 1
     */
    public void minusLikeCount(int UserId) {
        updateLikeCount(UserId, false);
    }

	/**
	 * 根据 UserId 值移除缓存
	 */
	public void clearCache(int UserId) {
		CacheKit.remove(allUsersCacheName, UserId);
	}
	
	/**
	 * 获取热门用户,暂时获取15个
	 */
	public List<User> getHotUsers()
	{
		List<User> users = dao.findByCache("hotUser", "hotUser", "select u.id,u.nickName,u.avatar from user u join posts p on u.id=p.userId group by "
				+ "u.nickName order by count(p.userId) desc limit 15");
		
		return users;
	}
}



