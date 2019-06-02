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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import com.daymooc.fcms.util.URLUtils;


/**
 * 视频分析
 * 
 * @author langhsu
 *
 */
public class VideoAnalysis {
	private Map<String, VideoStrategy> strategies = new HashMap<String, VideoStrategy>();
	
	public void init() {
		strategies.put("v.youku.com", new Youku());
	}
	
	public void setStrategies(Map<String, VideoStrategy> strategies) {
		this.strategies = strategies;
	}

	public void addStrategy(String host, VideoStrategy strategy) {
		strategies.put(host, strategy);
	}
	
	public Video take(String url) throws MalformedURLException {
		init();
		String host = URLUtils.getHost(url);
		
		VideoStrategy strategy = strategies.get(host);
		
		if (strategy == null) {
			throw new IllegalArgumentException("地址格式不对, 或不支持该网站" + host);
		}
		
		return strategy.take(url);
	}
	
}
