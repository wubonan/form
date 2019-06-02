

package com.daymooc.fcms.like;

import com.daymooc.fcms.common.controller.BaseController;
import com.jfinal.kit.Ret;

/**
 * 点赞控制器
 */
public class LikeController extends BaseController {

    static LikeService srv = LikeService.me;

    public void index() {
        if (notLogin()) {
            renderJson(Ret.fail("msg", "请先登录"));
            return ;
        }
        
        System.out.println(getPara("refType")+" "+getParaToInt("refId")+" " +getParaToBoolean("isAdd"));
        Ret ret = srv.like(getLoginAccountId(), getPara("refType"), getParaToInt("refId"), getParaToBoolean("isAdd"));
        renderJson(ret);
    }
}
