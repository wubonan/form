package com.daymooc.fcms.reptile;

import java.util.List;

import com.daymooc.fcms.common.account.AccountService;
import com.daymooc.fcms.common.model.Posts;
import com.daymooc.fcms.common.model.Reptile;
import com.daymooc.fcms.common.model.Tags;
import com.jfinal.plugin.activerecord.Page;

public class ReptileService {
	public static final ReptileService me = new ReptileService();
	final Reptile reptileDao = new Reptile().dao();
	public List<Reptile> getReptile()
	{

		//String select = "select * ";
		//String from = "from posts where post_type=? and status=? order by createAt desc";
		List<Reptile> reptile = reptileDao.find("select * from reptile");
		//Page<Posts> postPage = postDao.paginate(pageNum, pageSize, select, from, Posts.STATUS_PUB);
		
	
		return reptile;
	}
}
