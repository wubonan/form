package com.daymooc.fcms.common.kit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlKit
{
	//从html中提取纯文本  
	public static String StripHT(String strHtml) {  
	     String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签    
	        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符    
	        return txtcontent;  
	} 
	
	public static String Html2Text(String inputString) {  
	       String htmlStr = inputString; // 含html标签的字符串  
	       String textStr = "";  
	       java.util.regex.Pattern p_script;  
	       java.util.regex.Matcher m_script;  
	       java.util.regex.Pattern p_style;  
	       java.util.regex.Matcher m_style;  
	       java.util.regex.Pattern p_html;  
	       java.util.regex.Matcher m_html;  
	  
	       try {  
	           String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>  
	                                                                                                       // }  
	           String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>  
	                                                                                                   // }  
	           String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
	  
	           p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
	           m_script = p_script.matcher(htmlStr);  
	           htmlStr = m_script.replaceAll(""); // 过滤script标签  
	  
	           p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
	           m_style = p_style.matcher(htmlStr);  
	           htmlStr = m_style.replaceAll(""); // 过滤style标签  
	  
	           p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
	           m_html = p_html.matcher(htmlStr);  
	           htmlStr = m_html.replaceAll(""); // 过滤html标签  
	  
	           textStr = htmlStr;  
	  
	       } catch (Exception e) {  
	           System.err.println("Html2Text: " + e.getMessage());  
	       }  
	  
	       return textStr;// 返回文本字符串  
	   }  
	
	public static String[] getImgs(String content) {  
	    String img = "";  
	    Pattern p_image;  
	    Matcher m_image;  
	    String str = "";  
	    String[] images = null;  
	    String regEx_img = "(<img.*src\\s*=\\s*(.*?)[^>]*?>)";  
	    p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);  
	    m_image = p_image.matcher(content);  
	    while (m_image.find()) {  
	        img = m_image.group();  
	        Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);  
	        while (m.find()) {  
	            String tempSelected = m.group(1);  
	  
	            if ("".equals(str)) {  
	                str = tempSelected;  
	            } else {  
	                String temp = tempSelected;  
	                str = str + "," + temp;  
	            }  
	        }  
	    }  
	    if (!"".equals(str)) {  
	        images = str.split(",");  
	    }  
	    return images;  
	}  
}
