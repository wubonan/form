/*
+--------------------------------------------------------------------------
|   Mblog [#RELEASE_VERSION#]
|   ========================================
|   Copyright (c) 2014, 2015 mtons. All Rights Reserved
|   http://www.mtons.com
|
+---------------------------------------------------------------------------
*/
package com.daymooc.fcms.share;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.daymooc.fcms.util.HttpUtils;


/**
 * 优酷视频信息抓取
 * 
 * @author langhsu
 *
 */
public class Youku implements VideoStrategy {
	private String source = "优酷";
	private String apiBasic = "https://openapi.youku.com/v2/videos/show_basic.json";
	private String api = "https://openapi.youku.com/v2/videos/show.json";
	private String clientId = "7c068d0cb01cb88c";
	
	@Override
	public Video take(String url) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", clientId);
		params.put("video_url", url);
		
		Video ret = null;
		try {
			
			String body = HttpUtils.post(apiBasic, params);
			
			ret = JSON.parseObject(body, Video.class);

			params.put("video_id", ret.getId());
			body = HttpUtils.post(api, params);
			ret = JSON.parseObject(body, Video.class);

			ret.setSource(source);
			ret.setBody(getHtmlBody(ret));
		} catch (Exception e) {
			System.out.println("error.....");
			e.printStackTrace();
		}
		return ret;
	}
	
	private String getHtmlBody(Video video) {
		/*
		<div id="youkuplayer" style="width:480px;height:400px"></div>
        <script type="text/javascript" src="http://player.youku.com/jsapi">
        player = new YKU.Player('youkuplayer',{
        styleid: '0',
        client_id: '7c068d0cb01cb88c',
        vid: 'XMTI4MTIzMDQ1Ng=='
        });
        </script>
		 */
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div id='youkuplayer' class='player'></div>");
		buf.append("<script type=\"text/javascript\" src=\"http://player.youku.com/jsapi\">");
		buf.append("player = new YKU.Player('youkuplayer',{");
		buf.append("styleid: '0',");
		buf.append("client_id: '").append(clientId).append("',");
		buf.append("vid: '").append(video.getId()).append("' }); </script>");
		return buf.toString();
	}

//	public static void main(String[] args)
//	{
//		Youku youku = new Youku();
//		Video ret = youku.take("https://v.youku.com/v_show/id_XMzAyNTc1MDc3Mg==.html?spm=a2hww.20023042.m_223465.5~5~5~5~5~5~A");
//	
//		System.out.println(ret.getId());
//	}
}
