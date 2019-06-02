package com.daymooc.fcms.share;


public interface VideoStrategy
{
	/**
	 * 获取视频信息
	 * 
	 * @param url 视频地址
	 * @return
	 */
	Video take(String url);
}
