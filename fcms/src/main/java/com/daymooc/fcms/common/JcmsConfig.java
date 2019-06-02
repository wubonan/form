
package com.daymooc.fcms.common;

import java.sql.Connection;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.daymooc.fcms.admin.common.AdminRoutes;
import com.daymooc.fcms.common.interceptor.LoginSessionInterceptor;
import com.daymooc.fcms.common.interceptor.SiteInfoInterceptor;
import com.daymooc.fcms.common.model._MappingKit;
import com.daymooc.fcms.login.LoginService;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.JsonRender;
import com.jfinal.template.Engine;

/**
 * JcmsObsConfig
 */
public class JcmsConfig extends JFinalConfig {   //该类用于对整个web项目进行配置。
	
	private static Prop p = loadConfig();
	private WallFilter wallFilter;
	
	/**
	 * 启动入口，运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 * 
	 * 使用本方法启动过第一次以后，会在开发工具的 debug、run configuration 中自动生成
	 * 一条启动配置项，可对该自动生成的配置再继续添加更多的配置项，例如 VM argument 可配置为：
	 * -XX:PermSize=64M -XX:MaxPermSize=256M
	 * 上述 VM 配置可以缓解热加载功能出现的异常
	 */
	public static void main(String[] args) {
		/**
		 * 特别注意：Eclipse 之下建议的启动方式
		 */
		//System.out.println(System.getProperty("java.io.tmpdir"));
		JFinal.start("src/main/webapp", 8080, "/", 5);
		
		
		/**
		 * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
		 */
		// JFinal.start("src/main/webapp", 80, "/");
	}
	
	private static Prop loadConfig() {
		try {
			// 优先加载生产环境配置文件
			return PropKit.use("fcms_config_pro.txt");
		} catch (Exception e) {
			// 找不到生产环境配置文件，再去找开发环境配置文件
			return PropKit.use("fcms_config_dev.txt");
		}
	}
	
	
	//此方法用来配置JFinal常量值，如开发模式常量devMode的配置，
    public void configConstant(Constants me) {  
        me.setDevMode(p.getBoolean("devMode", false));
		me.setJsonFactory(MixedJsonFactory.me());
		//TODO 这里暂时设为2000m，后面需要改为更大值，由于 这里Jfinal使用的是int类型，所以后续需要修改为long
		me.setMaxPostSize(1024*1024*2000);//2000M
		//设置404,500页面
		me.setError404View("/_view/common/error_404.html");
		me.setError500View("/_view/common/error_500.html");
    }
    
    /**
     * 路由拆分到 FrontRutes 与 AdminRoutes 之中配置的好处：
     * 1：可分别配置不同的 baseViewPath 与 Interceptor
     * 2：避免多人协同开发时，频繁修改此文件带来的版本冲突
     * 3：避免本文件中内容过多，拆分后可读性增强
     * 4：便于分模块管理路由
     */
    public void configRoute(Routes me) {
	    me.add(new FrontRoutes());
	    me.add(new AdminRoutes());
    }
    
    /**
     * 配置模板引擎，通常情况只需配置共享的模板函数
     */
    public void configEngine(Engine me) {
    	me.addSharedFunction("/_view/common/_layout.html");
    	me.addSharedFunction("/_view/common/_paginate.html");
    	me.addSharedFunction("/_view/_admin/common/_admin_layout.html");
    	//配置共享对象
    	me.addSharedObject("sk", new com.daymooc.fcms.common.kit.StringKit());
    	me.addSharedObject("timeKit", new com.daymooc.fcms.common.kit.TimeKit());
    	me.addSharedObject("comKit", new com.daymooc.fcms.common.kit.CommonKit());
    }
    
    /**
     * 抽取成独立的方法，例于 _Generator 中重用该方法，减少代码冗余
     */
	public static DruidPlugin getDruidPlugin() {
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}
	
    public void configPlugin(Plugins me) {
	    DruidPlugin druidPlugin = getDruidPlugin();
	    wallFilter = new WallFilter();              // 加强数据库安全
	    wallFilter.setDbType("mysql");
	    druidPlugin.addFilter(wallFilter);
	    druidPlugin.addFilter(new StatFilter());    // 添加 StatFilter 才会有统计数据
	    me.add(druidPlugin);
	    
	    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
	    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
	    
	    //添加fcms.sql
	    //arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
	    //arp.addSqlTemplate("fcms.sql");
	    
	    //重要
	    _MappingKit.mapping(arp);
	    // 强制指定复合主键的次序，避免不同的开发环境生成在 _MappingKit 中的复合主键次序不相同
	    arp.setPrimaryKey("document", "mainMenu,subMenu");
	    me.add(arp);
        if (p.getBoolean("devMode", false)) {
            arp.setShowSql(true);
        }
        
	    me.add(new EhCachePlugin());	//缓存
	    me.add(new Cron4jPlugin(p));
	    
    }
    
    /**
     * 本方法会在 jfinal 启动过程完成之后被回调，详见 jfinal 手册
     */
	public void afterJFinalStart() {
		// 调用不带参的 renderJson() 时，排除对 loginAccount、remind 的 json 转换
		JsonRender.addExcludedAttrs(
                LoginService.loginAccountCacheName,
                LoginSessionInterceptor.remindKey
        );
				
		// 让 druid 允许在 sql 中使用 union
		// https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE-wallfilter
		wallFilter.getConfig().setSelectUnionCheck(false);
		
	}

	@Override
	public void configInterceptor(Interceptors me)
	{
		//全局拦截器
		me.add(new LoginSessionInterceptor());
		me.add(new SiteInfoInterceptor());
		
	}

	@Override
	public void configHandler(Handlers me)
	{
		//me.add(new WebSocketHandler("^/websocket"));
	}
}






