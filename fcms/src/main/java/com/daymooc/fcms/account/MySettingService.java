package com.daymooc.fcms.account;

import java.awt.image.BufferedImage;
import java.io.File;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.kit.ImageKit;
import com.daymooc.fcms.common.model.User;
import com.daymooc.fcms.login.LoginService;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

public class MySettingService
{
	public static final MySettingService me = new MySettingService();

	// 经测试对同一张图片裁切后的图片 jpg为3.28KB，而 png 为 33.7KB，大了近 10 倍
	public static final String extName = ".jpg";

	/**
	 * 上传图像到临时目录，发回路径供 jcrop 裁切
	 */
	public Ret uploadAvatar(int accountId, UploadFile uf) {
		if (uf == null) {
			return Ret.fail("msg", "上传文件UploadFile对象不能为null");
		}

		try {
			if (ImageKit.notImageExtName(uf.getFileName())) {
				return Ret.fail("msg", "文件类型不正确，只支持图片类型：gif、jpg、jpeg、png、bmp");
			}

			String avatarUrl = "/upload" + getAvatarTempDir() + accountId + "_" + System.currentTimeMillis() + extName;
			String saveFile = PathKit.getWebRootPath() + avatarUrl;
			ImageKit.zoom(500, uf.getFile(), saveFile);
			return Ret.ok("avatarUrl", avatarUrl);
		}
		catch (Exception e) {
			return Ret.fail("msg", e.getMessage());
		} finally {
			uf.getFile().delete();
		}
	}

	public Ret saveAvatar(User loginAccount, String avatarUrl, int x, int y, int width, int height) {
		int accountId = loginAccount.getId();
		// 暂时用的 webRootPath，以后要改成 baseUploadPath，并从一个合理的地方得到
		String webRootPath = PathKit.getWebRootPath() ;
		String avatarFileName = webRootPath + avatarUrl;

		try {
			// 相对路径 + 文件名：用于保存到 account.avatar 字段
			String[] relativePathFileName = new String[1];
			// 绝对路径 + 文件名：用于保存到文件系统
			String[] absolutePathFileName = new String[1];
			buildPathAndFileName(accountId, webRootPath, relativePathFileName, absolutePathFileName);

			BufferedImage bi = ImageKit.crop(avatarFileName, x, y, width, height);
			bi = ImageKit.resize(bi, 200, 200);     // 将 size 变为 200 X 200，resize 不会变改分辨率
			deleteOldAvatarIfExists(absolutePathFileName[0]);
			ImageKit.save(bi, absolutePathFileName[0]);

			AccountService.me.updateUserAvatar(accountId, relativePathFileName[0]);
			LoginService.me.reloadLoginAccount(loginAccount);
			//IndexService.me.clearCache();   // 首页的用户图片需要更新
			return Ret.ok("msg", "头像更新成功，部分浏览器需要按 CTRL + F5 强制刷新看效果");
		} catch (Exception e) {
			return Ret.fail("msg", "头像更新失败：" + e.getMessage());
		} finally {
			new File(avatarFileName).delete();	 // 删除用于裁切的源文件
		}
	}

	/**
	 * 目前该方法为空实现
	 * 如果在 linux 上跑稳了，此方法可以删除，不必去实现，如果出现 bug，
	 * 则尝试实现该方法，即当用户图像存在时再次上传保存，则先删除老的，
	 * 以免覆盖老文件时在 linux 之上出 bug
	 */
	private void deleteOldAvatarIfExists(String oldAvatar) {

	}

	// 用户上传图像最多只允许 1M大小
	public int getAvatarMaxSize() {
		return 1024 * 1024;
	}

	/**
	 * 上传文件，以及上传后立即缩放后的文件暂存目录
	 */
	public String getAvatarTempDir() {
		return "/avatar/temp/";
	}

	/**
	 * 1：生成保存于 account.avatar 字段的：相对路径 + 文件名，存放于 relativePathFileName[0]
	 * 2：生成保存于文件系统的：绝对路径 + 文件名，存放于 absolutePathFileName[0]
	 *
	 * 3：用户头像保存于 baseUploadPath 之下的 /avatar/ 之下
	 * 4：account.avatar 只存放相对于 baseUploadPath + "/avatar/" 之后的路径和文件名
	 *    例如：/upload/avatar/0/123.jpg 只存放 "0/123.jpg" 这部分到 account.avatar 字段之中
	 *
	 * 5："/avatar/" 之下生成的子录为 accountId 对 5000取整，例如 accountId 为 123 时，123 / 5000 = 0，生成目录为 "0"
	 * 6：avatar 文件名为：accountId + ".jpg"
	 * 修改4的描述，数据库保存路径为/upload/avatar/0/123.jpg，为了便于前端统一显示，这里将relativePathFileName加上"/upload/avatar/"
	 */
	private void buildPathAndFileName(int accountId, String webRootPath, String[] relativePathFileName, String[] absolutePathFileName) {
		String relativePath = (accountId / 5000) + "/";
		String fileName = accountId + extName;
		relativePathFileName[0] = "/upload/avatar/" +relativePath + fileName;

		String absolutePath = webRootPath + "/upload/avatar/" + relativePath;   // webRootPath 将来要根据 baseUploadPath 调整，改代码，暂时选先这样用着，着急上线
		File temp = new File(absolutePath);
		if (!temp.exists()) {
			temp.mkdirs();  // 如果目录不存在则创建
		}
		absolutePathFileName[0] = absolutePath + fileName;
	}
	
	//判断老密码是否输入正确
	public boolean isPasswordOk(String oldPws,int userId)
	{
		// 获取数据库里保存的盐值
		String salt = Db.queryStr("select salt from user where id=?", userId);;
		String oldPassword = HashKit.sha256(salt + oldPws);
		
		String psw = Db.queryStr("select password from user where id=?", userId);
		
		if (psw.equals(oldPassword))
		{
			return true;
		}
		
		return false;
	}
}
