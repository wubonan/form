
/*导航栏切换*/
$(document).ready(function() {
	setPostCurrentNavMenu();
});

/**
 * 设置发布管理当前导航菜单
 */
function setPostCurrentNavMenu() {
	var url = location.pathname, navMenus = $(".post-man li"), navUl=$(".post-man");
    if (!url.indexOf('/admin/post/article')) {
    	navUl.addClass("menu-open");
    	navUl.css("display","block");
        navMenus.eq(0).addClass("active");
    }else if (!url.indexOf('/admin/post/video')) {
    	navUl.addClass("menu-open");
    	navUl.css("display","block");
		navMenus.eq(1).addClass("active");
	} else if (!url.indexOf('/admin/post/question')) {
		navUl.addClass("menu-open");
		navUl.css("display","block");
		navMenus.eq(2).addClass("active");
	} else if (!url.indexOf('/admin/post/gallery')) {
		navUl.addClass("menu-open");
		navUl.css("display","block");
		navMenus.eq(3).addClass("active");
	}
}

/**
 * 设置账号管理当前导航菜单
 */
function setPostCurrentNavMenu() {
	var url = location.pathname, navMenus = $(".account-man li"), navUl=$(".account-man");
    if (!url.indexOf('/admin/account/list')) {
    	navUl.addClass("menu-open");
    	navUl.css("display","block");
        navMenus.eq(0).addClass("active");
    }else if (!url.indexOf('/admin/account/add')) {
    	navUl.addClass("menu-open");
    	navUl.css("display","block");
		navMenus.eq(1).addClass("active");
	} else if (!url.indexOf('/admin/account/mod')) {
		navUl.addClass("menu-open");
		navUl.css("display","block");
		navMenus.eq(2).addClass("active");
	}
}