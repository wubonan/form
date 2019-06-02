package com.daymooc.fcms.reg;

import java.util.Date;

import com.daymooc.fcms.common.authcode.AuthCodeService;
import com.daymooc.fcms.common.contants.AttributeConstant;
import com.daymooc.fcms.common.kit.EmailKit;
import com.daymooc.fcms.common.model.AuthCode;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.message.MessageService;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class RegService
{
	public static final RegService me = new RegService();
	private final User userDao = new User().dao();
	
	/*
	 * 用户是否存在
	 */
	public boolean isEmaiExists(String email) {
		return Db.queryInt("select id from user where email = ? limit 1", email) != null;
	}
	
	public boolean isNickNameExists(String nickName) {
		return Db.queryInt("select id from user where nickName = ? limit 1", nickName) != null;
	}
	
	/**
	 * 2次密码是否相同
	 */
	public boolean isPasswordOK(String ps1, String ps2)
	{
		if (!ps1.equals(ps2))
		{
			return true;
		}
		
		return false;
	}
	/**
	 * 昵称是否已被注册，昵称不区分大小写，以免存在多个用户昵称看起来一个样的情况
	 *
	 *  mysql 的 where 字句与 order by 子句默认不区分大小写，区分大小写需要在
	 *  字段名或字段值前面使用 binary 关键字例如：
	 *  where nickName = binary "jfinal" 或者 where binary nickName = "jfinal"，前者性能要高
	 *
	 *  为了避免不同的 mysql 配置破坏掉 mysql 的 where 不区分大小写的行为，这里在 sql 中使用
	 *  lower(...) 来处理，参数 nickName 也用 toLowerCase() 方法来处理，再次确保不区分大小写
	 */
	public boolean isUserNameExists(String userName) {
		userName = userName.toLowerCase().trim();
		return Db.queryInt("select id from user where lower(userName) = ? limit 1", userName) != null;
	}
	
	
	/**
	 * 账户注册，hashedPass = sha256(32字符salt + pass)
	 */
	public Ret reg(String email, String userName, String password, String nickName, String ip) {
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

		// 创建账户
		User account = new User();
		account.setEmail(email);
		account.setUserName(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setSalt(salt);
		account.setStatus(User.STATUS_REG);
		account.setCreateAt(new Date());
		account.setIp(ip);
		account.setAvatar(User.AVATAR_NO_AVATAR);  // 注册时设置默认头像
		account.setRoleId(2);//默认为2，1表示超级管理员，2表示普通成员
		
		System.err.println(account.toString());

		if (account.save()) 
		{
			String authCode =  AuthCodeService.me.createRegActivateAuthCode(account.getInt("id"));
			if (sendRegActivateAuthEmail(authCode, account)) 
			{
				return Ret.ok("msg", "注册成功，激活邮件已发送，请查收并激活账号：" + userName);
			} else
			{
				return Ret.fail("msg", "注册成功，但是激活邮件发送失败，可能是邮件服务器出现故障，请去MB官方QQ群留言给群主，多谢！");
			}
		} 
		else 
		{
			return Ret.fail("msg", "注册失败，account 保存失败，请告知管理员");
		}
	}
	
	/**
	 * 发送账号激活授权邮件
	 */
	private boolean sendRegActivateAuthEmail(String authCode, User reg) {
		String title = "MB会员激活邮件";
		String content = "欢迎加入MB,在这里，你可以分享你的生活。在浏览器地址栏里输入并访问下面激活链接即可完成账户激活：\n\n"
				+ "http://" +AttributeConstant.WEBIP+":8080/reg/activate?authCode="
				+ authCode;

		String emailServer = PropKit.get("emailServer");
		System.out.println("email:"+emailServer);
		String fromEmail = PropKit.get("fromEmail");
		String emailPass = PropKit.get("emailPass");
		String toEmail = reg.getEmail();
		System.out.println("toEmail:"+toEmail);
		try {
			EmailKit.sendEmail(emailServer, fromEmail, emailPass, toEmail, title, content);
			System.out.println("send email ok.");
			return true;
		} catch (Exception e) {
			System.out.println("send email failed,error:"+e);
			return false;
		}
	}
	
	/**
	 * 激活账号，返回 false 表示激活码已过期或者不存在
	 * 	激活账号不要去自动登录，激活邮件如果发错到了别人的邮箱，会有别人冒用的可能
	 * 并且登录功能还有额外有选择过期时间的功能
	 */
	public Ret activate(String authCodeId) {
		AuthCode authCode = AuthCodeService.me.getAuthCode(authCodeId);
		if (authCode != null && authCode.isValidRegActivateAuthCode()) {
			// 更新账户状态为已激活， status 的 where 条件必须为 reg，以防被锁定账户重新激活
			int n = Db.update("update user set status = ? where id = ? and status = ?", User.STATUS_OK, authCode.get("accountId"), User.STATUS_REG);
			if (n > 0) {
                sendWelcomeMessage(authCode.getInt("accountId"));
				return Ret.ok("msg", "账号激活成功，欢迎加入MB！");
			} else {
				return Ret.fail("msg", "未找到需要激活的账号，可能是账号已经激活或已经被锁定，请联系管理员");
			}
		} else {
			return Ret.fail("msg", "authCode 不存在或已经失效，可以尝试在登录页再次发送激活邮件");
		}
	}

    /**
     * 激活成功后立即发送欢迎系统私信
     */
    private void sendWelcomeMessage(Integer accountId) {
        try {   // try catch 确保主流程一定成功
            String sysMsg =
                    "您好，我是MB的站长 Jiaxiang，非常欢迎您的加入。" +
                    "<br/><br/>MS是一个分享交流平台，" +
                    "欢迎分享你的文章及视频。" +
                    "<br/><br/>我们倡议：文明发言，热爱生活！";
            MessageService.me.sendSystemMessage(1, accountId, sysMsg);
        } catch (Exception e) {
            //
       LogKit.error("发送激活欢迎系统消息异常：" + e.getMessage(), e);
		}
	}

	public Ret reSendActivateEmail(String userName) {
		if (StrKit.isBlank(userName) || userName.indexOf('@') == -1) {
			return Ret.fail("msg", "email 格式不正确，请重新输入");
		}
		userName = userName.toLowerCase().trim();   // email 转成小写
		if ( ! isUserNameExists(userName)) {
			return Ret.fail("msg", "email 没有被注册，无法收取激活邮件，请先去注册");
		}

		// 根据 userName 查找未激活的账户：Account.STATUS_REG
		User account = userDao.findFirst("select * from user where name=? and status = ? limit 1", userName, User.STATUS_REG);
		if (account == null) {
			return Ret.fail("msg", "该账户已经激活，可以直接登录");
		}

		String authCode = AuthCodeService.me.createRegActivateAuthCode(account.getId());
		if (sendRegActivateAuthEmail(authCode, account)) {
			return Ret.ok("msg", "激活码已发送至邮箱，请收取激活邮件并进行激活");
		} else {
			return Ret.fail("msg", "激活邮件发送失败，可能是邮件服务器出现故障，请联系我们，多谢！");
		}
	}
}
