/*导航栏切换*/
$(document).ready(function() {
	setCurrentNavMenu();
    setHomeNavMenu();
});

//评论区的编辑器
var layedit;
var articleCommentEdit;
/**
 * 设置当前导航菜单
 */
function setCurrentNavMenu() {
	var url = location.pathname, navMenus = $(".navbar-nav-menu li");
    if (!url.indexOf('/home')) {
        navMenus.eq(0).addClass("active");
    }else if (url == '/' || !url.indexOf('/index')) {
		navMenus.eq(1).addClass("active");
	} else if (!url.indexOf('/video')) {
		navMenus.eq(2).addClass("active");
	} else if (!url.indexOf('/question')) {
		navMenus.eq(3).addClass("active");
	} else if (!url.indexOf('/tags')) {
		navMenus.eq(4).addClass("active");
	} else if (!url.indexOf('/gallery')) {
		navMenus.eq(5).addClass("active");
	}
}
function appendReturnUrl(target) {
    var returnUrl;
    var currentUrl = location.pathname;
    if (currentUrl.indexOf("/login") != 0 && currentUrl.indexOf("/reg") != 0) {
        returnUrl = "?returnUrl=" + currentUrl;
        var link = $(target);
        link.attr("href", link.attr("href") + returnUrl);
    }
    //else {
    //  if (location.search) {
    //      returnUrl =  location.search;
    //  } else {
    //      return ;
    //  }
    //}
    //var link = $(target);
    //link.attr("href", link.attr("href") + returnUrl);
}
/**
 * 设置主页侧边栏当前导航菜单
 */
function setHomeNavMenu() {
    var url = location.pathname, navMenus = $("#home-navbar li");
    if ((url == '/home') || !url.indexOf('/home/newfeeds')) {
        navMenus.eq(0).addClass("active");
    }else if (!url.indexOf('/home/posts')) {
        navMenus.eq(1).addClass("active");
    } else if (!url.indexOf('/home/comments')) {
        navMenus.eq(2).addClass("active");
    } else if (!url.indexOf('/home/favors')) {
        navMenus.eq(3).addClass("active");
    } else if (!url.indexOf('/home/follows')) {
        navMenus.eq(4).addClass("active");
    } else if (!url.indexOf('/home/fans')) {
        navMenus.eq(5).addClass("active");
    } else if (!url.indexOf('/home/notifies')) {
        navMenus.eq(6).addClass("active");
    }
}

//评论区文本输入
$(function () {
	//只在view评论页使用
	var url = location.pathname;
	if (!url.indexOf('/view')) {
		layui.use('layedit', function(){
		  layedit = layui.layedit ,$ = layui.jquery;
		 
		  
		  //自定义工具栏
		  articleCommentEdit = layedit.build('articleComment', {
		    tool: ['face', 'link', 'unlink', '|', 'left', 'center', 'right']
		    ,height: 100
		  })
		});
	}
	
});

//点击图片弹出大图预览
function imgShow(outerdiv, innerdiv, bigimg, _this){  
    var src = _this.attr("src");//获取当前点击的pimg元素中的src属性  
    $(bigimg).attr("src", src);//设置#bigimg元素的src属性  

    /*获取当前点击图片的真实大小，并显示弹出层及大图*/  
    $("<img/>").attr("src", src).load(function(){  
        var windowW = $(window).width();//获取当前窗口宽度  
        var windowH = $(window).height();//获取当前窗口高度  
        var realWidth = this.width;//获取图片真实宽度  
        var realHeight = this.height;//获取图片真实高度  
        var imgWidth, imgHeight;  
        var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放  
          
        if(realHeight>windowH*scale) {//判断图片高度  
            imgHeight = windowH*scale;//如大于窗口高度，图片高度进行缩放  
            imgWidth = imgHeight/realHeight*realWidth;//等比例缩放宽度  
            if(imgWidth>windowW*scale) {//如宽度扔大于窗口宽度  
                imgWidth = windowW*scale;//再对宽度进行缩放  
            }  
        } else if(realWidth>windowW*scale) {//如图片高度合适，判断图片宽度  
            imgWidth = windowW*scale;//如大于窗口宽度，图片宽度进行缩放  
                        imgHeight = imgWidth/realWidth*realHeight;//等比例缩放高度  
        } else {//如果图片真实高度和宽度都符合要求，高宽不变  
            imgWidth = realWidth;  
            imgHeight = realHeight;  
        }  
                $(bigimg).css("width",imgWidth);//以最终的宽度对图片缩放  
          
        var w = (windowW-imgWidth)/2;//计算图片与窗口左边距  
        var h = (windowH-imgHeight)/2;//计算图片与窗口上边距  
        $(innerdiv).css({"top":h, "left":w});//设置#innerdiv的top和left属性  
        $(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg  
    });  
      
    $(outerdiv).click(function(){//再次点击淡出消失弹出层  
        $(this).fadeOut("fast");  
    });  
} 
/**
 * 错误信息提示框，需要引入 layer.js
 *  
  */
function showReplyErrorMsg(msg) {
    layer.msg(msg, {
            shift: 6
            , shade: 0.4
            , time: 2000
            // , offset: "140px"
            , closeBtn: 1
            , shadeClose: true
            ,maxWidth: "1000"
        }, function () {}
    );
}
/**
 * 
 * 显示操作成功信息
 */
function showOkMsg(msg) {
    layer.msg(msg, {
            shift: 6
            , shade: 0.4
            , time: 2000
            // , offset: "140px"
            , closeBtn: 1
            , shadeClose: true
            ,maxWidth: "1000"
        }, function () {}
    );
}
/**
 * ajax GET 请求封装，提供了一些默认参数
 */
function ajaxGet(url, options) {
    var defaultOptions = {
        type: "GET"
        , cache: false      // 经测试设置为 false 时，ajax 请求会自动追加一个参数 "&_=nnnnnnnnnnn"
        , dataType: "json"  // "json" "text" "html" "jsonp"，如果设置为"html"，其中的script会被执行
        // , data: {}
        // , timeout: 9000     // 毫秒
        // , beforeSend: function(XHR) {}
        , success: function(ret){
            if (ret.isOk) {
                alert(ret.msg ? ret.msg : "操作成功");
            } else {
                alert("操作失败：" + (ret.msg ? ret.msg : "请告知管理员！"));
            }
        }
        , error: function(XHR, msg) {
            showReplyErrorMsg(msg); // 默认调用
        }
        // , complete: function(XHR, msg){} // 请求成功与失败都调用
    };
    // 用户自定义参数覆盖掉默认参数
    for(var o in options) {
        defaultOptions[o] = options[o];
    }

    $.ajax(url, defaultOptions);
}

/**
 * 确认对话框层，点击确定才真正操作
 * @param msg 对话框的提示文字
 * @param actionUrl 点击确认后请求到的目标 url
 * @param options jquery $.ajax(...) 方法的 options 参数
 */
function confirmAjaxGet(msg, actionUrl, options) {
    layer.confirm(msg, {
        icon: 0
        , title:''                                      // 设置为空串时，title消失，并自动切换关闭按钮样式，比较好的体验
        , shade: 0.4
        , offset: "139px"
    }, function(index) {                                // 只有点确定后才会回调该方法
        // location.href = operationUrl;                // 操作是一个 GET 链接请求，并非 ajax
        // 替换上面的 location.href 操作，改造成 ajax 请求。后端用 renderJson 更方便，不需要知道 redirect 到哪里
        ajaxGet(actionUrl, options);
        layer.close(index);                             // 需要调用 layer.close(index) 才能关闭对话框
    });
}

//reply 删除功能
function deleteReply(deleteBtn, url) {
    confirmAjaxGet("删除后无法恢复，确定要删除？", url, {
        success: function(ret) {
            if (ret.isOk) {
                $(deleteBtn).parents(".jf-reply-list li").remove();
            }
        }
    });
}
// 文章详情页回复功能
//这里的articleId为postId，后续新增功能后可以为其他id。
function reply(url, articleId, map) {
    if (map.isLoading) {
        return ;
    }

    $.ajax(url, {
        type: "POST"
        , cache: false
        , dataType: "json"
        , data: {
            articleId: articleId,
            //replyContent: $('#discussContent').val(),layui这里不能用这种方法，最好官方的方法
            replyContent:layedit.getContent(articleCommentEdit),//文章评论
        }
        , beforeSend: function() {
            map.isLoading = true;
            map.submit_btn.hide();
        }
        , success: function(ret) {
            if (ret.isOk) {
                // 插入刚刚回复的内容 replyItem
                // TODO 考虑用 news feed 模块的定位方案来改进一下，更优雅
                var url = location.pathname;
              
                $("#chat_container > li:first-child").before(ret.replyItem);
                //var replyContent = $('#discussContent');
                $('#articleComment').val("");
                //var noteContent = $('#noteContent');
                // 数据清空后，高度重置一下，注意高度与 css 文件中保持一致
                //replyContent.css({height:"30px"});
                
            } else {
                showReplyErrorMsg(ret.msg);
            }
        }
        , complete: function() {
            map.submit_btn.show();
            map.isLoading = false;
        }
    });
}

// 文章详情页回复链接的 at 功能
function atAndReply(nickName) {
    var replyContent = $('#replyContent');
    var content = replyContent.val() + "@" + nickName + " ";
    replyContent.val(content);
}
/**
 * 点赞
 */
function doLike(refType, refId, isAdd, options) {
    console.log("like");
    var url = "/like?refType=" + refType + "&refId=" + refId;
    if (isAdd != null) {
        url = url + "&isAdd=" + isAdd;
    }
    ajaxGet(url, options);
}

/**
 * 点赞
 */
function like(refType, refId, map) {
    if (map.isLoading) {
        return ;
    } else {
        map.isLoading = true;
    }

    doLike(refType, refId, map.isAdd, {
        success: function(ret){
            if (ret.isOk) {
                //查找子元素span
                var favors;
                var btn = map.btn;
                var url = location.pathname;
                //社区文章点赞数特殊处理，其他的采用统一处理
              
                favors = $("#favors");
                
                
                var num = favors.text();
                num = parseInt(num);
                if (isNaN(num)) {
                    num = 0;
                }
                if (map.isAdd) {
                    num = num + 1;
                    btn.addClass("active");
                    map.isAdd = false;
                } else {
                    num = num - 1;
                    btn.removeClass("active");
                    map.isAdd = true;
                }
                if (num == 0) {
                    num = 0;
                }
                favors.text(num);
                showOkMsg(ret.msg);
            } else {
                showReplyErrorMsg(ret.msg);
            }
            map.isLoading = false;  // 重置 isLoading，允许点击时提交请求
        }
    });
}

/**
 * 收藏
 */
function doFavorite(refType, refId, isAdd, options) {
    var url = "/favorite?refType=" + refType + "&refId=" + refId;
    if (isAdd != null) {
        url = url + "&isAdd=" + isAdd;
    }
    ajaxGet(url, options);
}

/**
 * 收藏
 */
function favorite(refType, refId, map) {
    if (map.isLoading) {
        return ;
    } else {
        map.isLoading = true;
    }

    doFavorite(refType, refId, map.isAdd, {
        success: function(ret){
            if (ret.isOk) {
                var btn = map.btn;
                var next = btn.next();
                var num = next.text();
                num = parseInt(num);
                if (isNaN(num)) {
                    num = 0;
                }
                if (map.isAdd) {
                    num = num + 1;
                    btn.addClass("active");
                    map.isAdd = false;
                } else {
                    num = num - 1;
                    btn.removeClass("active");
                    map.isAdd = true;
                }
                if (num == 0) {
                    num = "";
                }
                next.text(num);
            } else {
                showReplyErrorMsg(ret.msg);
            }
            map.isLoading = false;  // 重置 isLoading，允许点击时提交请求
        }
    });
}

// 添加好友功能，用于关注/粉丝列表页面
function addFriend(btn, friendId) {
    layer.msg("正在加载，请稍后！", {icon: 16, offset: '100px'});
    ajaxGet("/friend/add?friendId=" + friendId, {
        success: function(ret) {
            if (ret.isOk) {
                setFriendBtn($(btn), friendId, ret.friendRelation);
            } else {
                showReplyErrorMsg(ret.msg);
            }
        }
        , complete: function(XHR, msg){
            layer.closeAll();
        }
    });
}
// 删除好友功能，用于关注/粉丝列表页面
function deleteFriend(btn, friendId) {
    confirmAjaxGet("取消关注后，此人的动态消息将不会出现在你的首页，确定要操作？", "/friend/delete?friendId=" + friendId, {
        success: function(ret) {
            if (ret.isOk) {
                setFriendBtn($(btn), friendId, ret.friendRelation);
            } else {
                showReplyErrorMsg(ret.msg);
            }
        }
    });
}

/**
 * 用于关注/粉丝列表页面
 * friendRelation 含义
 * 0： accountId 与 friendId 无任何关系
 * 1： accountId 关注了 friendId
 * 2： friendId 关注了 accountId
 * 3： accountId 与 friendId 互相关注
 */
function setFriendBtn(btn, friendId, friendRelation) {
    if (friendRelation == 0) {
        btn.attr("onclick", "addFriend(this," + friendId + ");");
        btn.text("+关注");
    } else if (friendRelation == 1) {
        btn.attr("onclick", "deleteFriend(this," + friendId + ");");
        btn.text("取消关注");
    } else if (friendRelation == 2) {
        btn.attr("onclick", "addFriend(this," + friendId + ");");
        btn.text("+关注");
    } else if (friendRelation == 3) {
        btn.attr("onclick", "deleteFriend(this," + friendId + ");");
        btn.text("取消互粉");
    }
}

/**
 * 用于个人空间用户头像下方的关注/取消关注功能
 */
function handleFriend(thisBtn, isAdd, friendId) {
    var layerIndex = layer.msg("正在加载，请稍后！", {icon: 16, offset: '100px'});
    var url = isAdd ? "/friend/add?friendId=" + friendId : "/friend/delete?friendId=" + friendId;
    ajaxGet(url, {
        success: function(ret) {
            if (ret.isOk) {
                var parent = $(thisBtn).parent();
                var link;
                if (ret.friendRelation == 0) {
                    link = "未关注<a href='javascript:void(0);' onclick='handleFriend(this, true, " + friendId + ");'>关注</a>";
                } else if (ret.friendRelation == 1) {
                    link = "已关注<a href='javascript:void(0);' onclick='handleFriend(this, false, " + friendId + ");'>取消</a>";
                } else if (ret.friendRelation == 2) {
                    link = "粉丝<a href='javascript:void(0);' onclick='handleFriend(this, true, " + friendId + ");'>+关注</a>";
                } else {
                    link = "互相关注<a href='javascript:void(0);' onclick='handleFriend(this, false, " + friendId + ");'>取消</a>";
                }
                parent.html(link);
            } else {
                showReplyErrorMsg(ret.msg);
            }
        }
        , complete: function(XHR, msg){
            layer.close(layerIndex);
        }
    });
}