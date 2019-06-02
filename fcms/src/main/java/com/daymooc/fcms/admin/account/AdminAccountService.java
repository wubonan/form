package com.daymooc.fcms.admin.account;

import java.util.Date;

import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class AdminAccountService
{
	public static final AdminAccountService me = new AdminAccountService();
	final User userDao = new User().dao();
	int pageSize = 15;
	
	public Page<User> getAllUsers(int pageNumber)
	{
		String select = "select u.id,u.nickName,u.email,u.createAt,u.status ";
		String from = "from user u";
		
		Page<User> userPage = userDao.paginate(pageNumber, pageSize, select, from);
		
		return userPage;
	}
	
	public Ret deleteUser(int id)
	{
		boolean r = Db.deleteById("user", id);
		
		if (r)
		{
			return Ret.ok("msg", "删除用户成功");
		}
		
		return Ret.fail("msg", "删除用户失败");
	}
	
	public Ret lockUser(int id)
	{
		int r = Db.update("update user set status=? where id=?", User.STATUS_LOCK_ID, id);
		
		if (r > 0)
		{
			return Ret.ok("msg", "锁定用户成功");
		}
		
		return Ret.fail("msg", "锁定用户失败");
	}
	
	public Ret unlockUser(int id)
	{
		int r = Db.update("update user set status=? where id=?", User.STATUS_OK, id);
		
		if (r > 0)
		{
			return Ret.ok("msg", "解锁用户成功");
		}
		
		return Ret.fail("msg", "解锁用户失败");
	}
	
	//添加用户
	public Ret addUser(String email, String userName, String nickName, String password, String ip)
	{
		if (StrKit.isBlank(email) || StrKit.isBlank(userName) || StrKit.isBlank(password) || StrKit.isBlank(nickName)) 
		{
			return Ret.fail("msg", "邮箱、用户名、密码或昵称不能为空");
		}

		email = email.toLowerCase().trim();	// 邮件全部存为小写
		//用户名密码去掉空格
		userName = userName.trim();
		password = password.trim();

		// 密码加盐 hash
		String salt = HashKit.generateSaltForSha256();
		password = HashKit.sha256(salt + password);
		
		User user = new User();
		
		user.setAvatar(User.AVATAR_NO_AVATAR);
		user.setCreateAt(new Date());
		user.setEmail(email);
		user.setIp(ip);
		user.setNickName(nickName);
		user.setUserName(userName);
		user.setPassword(password);
		user.setRoleId(2);
		user.setSalt(salt);
		user.setStatus(User.STATUS_OK);
		
		if (user.save())
		{
			return Ret.ok("msg", "添加用户成功");
		}
		
		return Ret.fail("msg", "添加用户失败");
	}
	
	
	public Ret modPassword(int id, String password)
	{
		if(userDao.findById(id) == null)
		{
			return Ret.fail("msg", "要修改的用户不存在！");
		}
		password = password.trim();
		// 密码加盐 hash
		String salt = HashKit.generateSaltForSha256();
		password = HashKit.sha256(salt + password);
		
		int r = Db.update("update user set password=?, salt=? where id=?", password, salt, id);
		
		if (r > 0)
		{
			return Ret.ok("msg", "修改密码成功");
		}
		
		return Ret.fail("msg", "修改密码失败");
	}
}
