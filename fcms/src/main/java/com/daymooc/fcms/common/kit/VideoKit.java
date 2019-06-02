package com.daymooc.fcms.common.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
* <p>Title: VideoKit</p>
* <p>Description: 获取视频的缩略图，视频ID等</p>
* <p>Company: </p> 
* @author liujiaxiang 
* @date 2017年9月16日 上午10:08:29
 */
public class VideoKit
{
	/**
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * 
	* @Title: getYoukuImg 
	* @Description: 获取优酷视频封面
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getYoukuImg() throws IOException
	{
        String url = "https://v.youku.com/v_show/id_XMjU0OTk2OTY0.html?spm=a2h0j.8191423.module_basic_relation.5~5!2~5~5!3~5~5~A";
        //获取id后面的字符串，也就是"XMjU0MjI2NzY0.html"
        int no = url.indexOf("id_");
        //获取id值，也就是"XMjU0MjI2NzY0"
        String videoId = url.substring(no+3, url.indexOf(".html"));
        //获取视频信息数据的URL对象
        URL myurl = new URL("http://v.youku.com/player/getPlayList/VideoIDS/"+videoId+"/timezone/+08/version/5/source/out?password=&ran=2513&n=3");
        //从URL对象中获取输入流
        InputStreamReader isr = new InputStreamReader(myurl.openStream());
        //封装
        BufferedReader br = new BufferedReader(isr);
        //readLine获取文本
        String urls = br.readLine();
        //关闭流
        br.close();
        //获取json对象
        System.out.println(urls);
        JSONObject json = JSONObject.fromObject(urls);
        //获取json数据（data内）
        JSONArray arr = json.getJSONArray("data");
        //获取logo的值并打印
        System.out.println(JSONObject.fromObject(arr.get(0)).get("logo"));
        
        return (String) JSONObject.fromObject(arr.get(0)).get("logo");
	}
	
	public static void main(String[] args)
	{
		try
		{
			getYoukuImg();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
