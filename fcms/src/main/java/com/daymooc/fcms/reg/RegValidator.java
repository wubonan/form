package com.daymooc.fcms.reg;

import com.daymooc.fcms.common.kit.SensitiveWordsKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

/**
*注册校验
*/
public class RegValidator extends Validator
{

	/**
	 * 
	* @Title: validate 
	* @Description: 前台已经做了部分校验，这里校验用户是否已被注册 
	* @param @param c    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	@Override
	protected void validate(Controller c)
	{
		setShortCircuit(true);
		
		if(RegService.me.isEmaiExists(c.getPara("email"))) {
			addError("emailMsg", "邮箱已被注册");
		}
		
		if (RegService.me.isPasswordOK(c.getPara("password"), c.getPara("rePassword")))
		{
			addError("passwordMsg", "2次输入密码不相同");
		}

		if (SensitiveWordsKit.checkSensitiveWord(c.getPara("userName")) != null) {
			addError("userNameMsg", "用户名不能包含敏感词");
		}

		String userName = c.getPara("userName").trim();
		if (userName.contains("@") || userName.contains("＠")) { // 全角半角都要判断
			addError("userNameMsg", "用户名不能包含 \"@\" 字符");
		}
		if (userName.contains(" ") || userName.contains("　")) {
			addError("userNameMsg", "用户名不能包含空格");
		}
		if (RegService.me.isUserNameExists(c.getPara("userName"))) {
			addError("userNameMsg", "用户名已被注册");
		}
		if (RegService.me.isNickNameExists(c.getPara("nickName"))) {
			addError("userNameMsg", "用户昵称已被使用");
		}
		Ret ret = validateNickName(userName);
		if (ret.isFail()) {
			addError("userNameMsg", ret.getStr("msg"));
		}

		
	}

	@Override
	protected void handleError(Controller c)
	{
		c.renderJson();
	}
	
	/**
	 * TODO 用正则来匹配这些不能使用的字符，而不是用这种 for + contains 这么土的办法
	 *    初始化的时候仍然用这个数组，然后用 StringBuilder 来个 for 循环拼成如下的形式：
	 *    regex = "( |`|~|!|......|\(|\)|=|\[|\]|\?|<|>\。|\,)"
	 *    直接在数组中添加转义字符
	 * 
	 * TODO 找时间将所有 nickName 的校验全部封装起来，供 Validattor 与 RegService 中重用，目前先只补下缺失的校验
	 * TODO RegService 中的 nickName 校验也要重用同一份代码，以免代码重复
	 */
	public static Ret validateNickName(String nickName) {
		if (StrKit.isBlank(nickName)) {
			return Ret.fail("msg", "昵称不能为空");
		}

		// 放开了 _-.  三个字符的限制
		String[] arr = {" ", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "=", "+",
							"[", "]", "{", "}", "\\", "|", ";", ":", "'", "\"", ",", "<", ">", "/", "?",
							"　", "＠", "＃", "＆", "，", "。", "《", "》", "？" };   // 全角字符
		for (String s : arr) {
			if (nickName.contains(s)) 
			{
				return Ret.fail("msg", "昵称不能包含字符: \"" + s +"\"");
			}
		}

		return Ret.ok();
	}
}
