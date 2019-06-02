package com.daymooc.fcms.admin.friendlink;

import java.util.List;

import com.daymooc.fcms.common.model.FriendLink;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

public class AdminFriendLinkService
{
	public static final AdminFriendLinkService me = new AdminFriendLinkService();
	final FriendLink linkDao = new FriendLink().dao();
	
	public List<FriendLink> getFriendLink()
	{
		List<FriendLink> friendLinks = linkDao.find("select * from friend_link");
		
		return friendLinks;
	}
	
	public Ret delLink(int id)
	{
		boolean r = Db.deleteById("friend_link", id);
		
		if (r)
		{
			return Ret.ok("msg", "删除成功");
		}
		
		return Ret.fail("msg", "删除失败");
	}
	
	public Ret addLink(String siteName, String url, String logo)
	{
		FriendLink link = new FriendLink();
		
		link.setSiteName(siteName);
		link.setUrl(url);
		link.setLogo("/upload/"+logo);
		if (link.save())
		{
			return Ret.ok("msg", "增加成功");
		}
		
		return Ret.fail("msg", "增加失败");
	}
}
