package com.daymooc.fcms.account;

import com.daymooc.fcms.common.controller.BaseController;
import com.daymooc.fcms.common.model.User;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.daymooc.fcms.account.MySettingService;

public class AccountController extends BaseController
{
	public static final MySettingService srv = MySettingService.me;
	
	public void profile()
	{
		render("profile.html");
	}
	
	public void saveInfo()
	{
		User user = getModel(User.class);
		if (user.update())
		{
			renderJson(Ret.ok("msg", "修改基本信息成功"));
			return;
		}
		
		renderJson(Ret.fail("msg", "修改信息失败"));
		return;
	}
	/**
	 * 上传用户图片，为裁切头像做准备
	 */
	public void uploadAvatar() {
		UploadFile uf = null;
		try {
			uf = getFile("avatar", srv.getAvatarTempDir(), srv.getAvatarMaxSize());
			if (uf == null) {
				renderJson(Ret.fail("msg", "请先选择上传文件"));
				return;
			}
		} catch (Exception e) {
			// 经测试，暂时拿不到这个异常，需要改进 jfinal 才可以拿得到
			if (e instanceof com.oreilly.servlet.multipart.ExceededSizeException) {
				renderJson(Ret.fail("msg", "文件大小超出范围"));
			} else {
				if (uf != null) {
					// 只有出现异常时才能删除，不能在 finally 中删，因为后面需要用到上传文件
					uf.getFile().delete();
				}
				renderJson(Ret.fail("msg", e.getMessage()));
			}
			return ;
		}

		Ret ret = srv.uploadAvatar(123456, uf);
		if (ret.isOk()) {   // 上传成功则将文件 url 径暂存起来，供下个环节进行裁切
			setSessionAttr("avatarUrl", ret.get("avatarUrl"));
		}
		renderJson(ret);
	}

	/**
	 * 保存 jcrop 裁切区域为用户头像
	 */
	public void saveAvatar() {
		String avatarUrl = getSessionAttr("avatarUrl");
		int x = getParaToInt("x");
		int y = getParaToInt("y");
		int width = getParaToInt("width");
		int height = getParaToInt("height");
		Ret ret = srv.saveAvatar(getLoginAccount(), avatarUrl, x, y, width, height);
		renderJson(ret);
	}
	
	/**
	 * 设置新密码
	 */
	public void newPsw()
	{
		String oldPassword = getPara("oldPassword");
		if (!srv.isPasswordOk(oldPassword, getLoginAccountId()))
		{
			renderJson(Ret.ok("msg", "旧密码输入不正确！"));
			return;
		}
		
		String newPassword = getPara("password");
		String newPassword2 = getPara("password2");
		
		if (!newPassword.equals(newPassword2))
		{
			renderJson(Ret.ok("msg", "2次输入的密码不相同！"));
			return;
		}
		// 密码加盐 hash
		String salt = HashKit.generateSaltForSha256();
		newPassword = HashKit.sha256(salt + newPassword);
		
		User user = new User();
		user.setPassword(newPassword);
		user.setSalt(salt);
		user.setId(getLoginAccountId());
		
		if (user.update())
		{
			renderJson(Ret.ok("msg", "恭喜您，更新密码成功"));
			return;
		}
		
		renderJson(Ret.fail("msg", "对不起，更新密码失败"));
		
	}

}
