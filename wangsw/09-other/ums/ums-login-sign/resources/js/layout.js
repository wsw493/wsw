$(function () {
	ContentLayout.init();
    App.initHelpers('slick');
    changePwdPanel.init();
   
    $(window).resize(function (){
    	$("#contentFrame").height($(window).height()-60);
    });

    window.addEventListener('message',function(e){   
	    if(e.data==='logout'){
	        window.location.href = 'login.html';
	    }else if(e.data === 'noauthorize'){
	    	$("#contentFrame").attr("src",'error403.html');
	    }
	    if(typeof(e.data)=='string' && e.data.indexOf('ACCESS-TOKEN')>-1 && e.data.indexOf('ACCESS-TOKEN-GETTIME')>-1){
            var tempArr = e.data.split("ACCESS-TOKEN-GETTIME");
            localStorage.setItem("ACCESS-TOKEN",tempArr[0].substring('ACCESS-TOKEN'.length,tempArr[0].length));
            localStorage.setItem("ACCESS-TOKEN-GETTIME",tempArr[1]);
        }
	},false);
	$('#contentFrame')[0].onload=function(){
		window.frames[0].postMessage("ACCESS-TOKEN"+localStorage.getItem("ACCESS-TOKEN")+
				"ACCESS-TOKEN-GETTIME"+localStorage.getItem("ACCESS-TOKEN-GETTIME"),'*');
	}
});

var ctx = window.location.href.substring(0,window.location.href.lastIndexOf("/")+1);
var menuJson = '';
var userId = '';
var systemCode = '';
var tenantId = '';
var welcomeMenuId = '';
var defaultWelcomeId = '';
var curr_menu = '';
var test_url = VortexConstant.gateWay;
var token = '';

var ContentLayout = {
	init: function (){
		curr_menu = window.location.hash;
		curr_menu = curr_menu.substring(1,curr_menu.length);
		
		if(window.location.search.indexOf('?') >-1){
			args = window.location.search.split('?')[1].split('&');
			args.map(function(item) {
				if(item.indexOf('systemCode')>-1){
					systemCode = item.split('=')[1];
				}else if(item.indexOf('token')>-1){
					token = item.split('=')[1];
				}
			});
		}

		ContentLayout.loadData({
			url: test_url+"/casServer/user"||"/cloud/management/util/logininfo.sa",
			type: 'post',
			successCallBack:function (data){
				if(data && data.data.name != ""){
					$("#username").html(data.data.name);
				}else {
					$("#username").html("未知用户");
				}

				if(data && data.data.userId != ""){
					userId = data.data.userId;
				}

				if(data && data.data.tenantId != ""){
					tenantId = data.data.tenantId;
				}

				if(data.data.photoId && data.data.photoId!='[]'){
					// $("#photoId").attr("src",VortexConstant.fileServer + "/cloudFile/common/downloadFile?id=" + JSON.parse(data.data.photoId)[0].id)
					if(data.data.photoId.indexOf('id')>-1){
						$("#photoId").attr("src",VortexConstant.fileServer + "/cloudFile/common/downloadFile?id=" + JSON.parse(data.data.photoId)[0].id)
					}else {
						$("#photoId").attr("src",VortexConstant.fileServer + "/cloudFile/common/downloadFile?id=" + data.data.photoId)
					}
				}

				ContentLayout.initChangeSystemSelect(data.data.systemList);

				ContentLayout.loadData({
					url: test_url+"/cloud/management/util/getMenuJson.sa",
					params: {
						'userId': userId,
						"systemCode": systemCode
					},
					type: 'get',
					async: false,
					successCallBack: menuJsonHandler
				});
			}
		});
	},
	/**
	 * 加载数据
	 * config: {url:'',params:{},successCallBack:function()}加载数据配置参数
	 * */
	loadData: function(config){
		jQuery.support.cors = true;
		$.ajax({
			url : config.url,
			type: config.type,
			data : config.params,
			async: config.async,
			dataType : "json",
			headers: {
                'Content-Type': 'application/json',
                'Authorization' : 'Bearer '+ token
            },
			success: config.successCallBack,
			complete: function (data) {
				if(data.responseJSON){
					if(data.responseJSON.result == '10001' || data.responseJSON.result == '10002'){
						window.location.href = 'login.html';
					}
					if(data.responseJSON.result == '10003'){
						$("#contentFrame").attr("src",'error403.html');
					}
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				if(XMLHttpRequest.status == '401'){
					window.location.href = 'login.html';
				}
			}
		});
	},
	exitSystem: function(){		//退出系统
		ContentLayout.loadData({
			url: test_url+"/cas/logout",
			type: 'post',
			successCallBack:function (data){
				if(data.result == 0){
					window.location.href='login.html';
				}else {
					alert('登出失败');
				}
			}
		});
	},
	iFrameHeight: function (){
		$("#contentFrame").height($("main").height());
	},
	initChangeSystemSelect : function (data){
		var flag = false; //子系统列表中是否包含默认系统码
		var tempsystemCode = '';//如果不包含，默认子系统
		data = JSON.parse(data);
		var title = '';
		if(data){
			for(var i=0;i<data.length;i++){
				var argg = data[i].split('||');
				if(systemCode==''&&i==0){
					systemCode = argg[0];
				}
				$("#changeSystem").append(new Option(argg[1],argg[0]));
				if(systemCode == argg[0]){
					flag = true;
					title = argg[1];
				}
				tempsystemCode = argg[0];
			}
		}
		if(!flag ){
			if(tempsystemCode != ""){
				if(window.location.href.indexOf("?")>-1){
					window.location.href = window.location.href.split("?")[0]+'?systemCode=' + tempsystemCode + "&token=" + token + window.location.hash;
				}else {
					window.location.href = window.location.href.split("#")[0]+'?systemCode=' + tempsystemCode + "&token=" + token + window.location.hash;
				}
			}
		}else {
			$("#changeSystem").val(systemCode);
			document.title = title;
			$("#changeSystem").on("change",function (){
				if(window.location.href.indexOf("?")>-1){
					window.location.href = window.location.href.split("?")[0]+'?systemCode=' + $(this).val() + "&token=" + token;
				}else {
					window.location.href = window.location.href.split("#")[0]+'?systemCode=' + $(this).val() + "&token=" + token;
				}
			});
		}
	},
	setWelcomPage: function (){
		if(curr_menu != ''){
			welcomeMenuId = curr_menu;
		}else {
			if(welcomeMenuId == ''){
				welcomeMenuId = defaultWelcomeId;
			}
		}
		var $element = $("li[menuid="+ welcomeMenuId +"]");
		var welcomeUrl = $element.attr("uri");
		if(welcomeUrl){
			if(welcomeUrl.indexOf("?") > -1){
	    		welcomeUrl = welcomeUrl + "&tenantId=" + tenantId + "&userId=" + userId + "&systemCode=" + systemCode + "&token=" + token;
	    	}else {
	    		welcomeUrl = welcomeUrl + "?tenantId=" + tenantId + "&userId=" + userId + "&systemCode=" + systemCode + "&token=" + token;
	    	}
	    	
			$("#contentFrame").attr("src",welcomeUrl);
		}
		/*$element.parent("ul").parent("li").addClass("open");
		$element.addClass("checkedMenu");*/
		if($element.parentsUntil("div")){
			for(var i=0;i<$element.parentsUntil("div").length;i++){
				if($($element.parentsUntil("div")[i]).attr("menuid")){
					$($element.parentsUntil("div")[i]).addClass("open");
				}
			}
		}
//		$element.parentsUntil("div").find("li").addClass("open");
		$element.addClass("checkedMenu");
		
	}
}
/**
 * 菜单JSON返回后的处理函数
 *rst: 返回的JSON对象
 * */
function menuJsonHandler(rst) {
	menuJson =JSON.parse(rst.data);
	var config = {
		menuObj : menuJson,
		container : "nav-main",
		parentId : "",
		menuLevel : "0"
	}
	var menu = new MenuJson(config);
	
	$(".no_child").click(function () {
		window.location.hash = $(this).attr("menuid");
    	
    	var uri = $(this).attr("uri");
    	if(uri.indexOf("?") > -1){
    		uri = uri + "&tenantId=" + tenantId + "&userId=" + userId + "&systemCode=" + systemCode + "&token=" + token;
    	}else {
    		uri = uri + "?tenantId=" + tenantId + "&userId=" + userId + "&systemCode=" + systemCode + "&token=" + token;
    	}
    	
    	$("#contentFrame").attr("src", uri);
    	$(".no_child").removeClass("checkedMenu");
    	$(this).addClass("checkedMenu");
    });
	
	OneUI.init('uiNav');
    ContentLayout.setWelcomPage();
}

//修改密码界面
var changePwdPanel = {
	formId: 'form',
	form: null,
	init: function (){
		var t = this;
		$("span.error").html("");
	    $("#"+t.formId).validate({
	    	onkeyup:false,
	    	rules: {
	    		oldPassword: {
		    		required: true
		    	},
		    	newPassword: {
		    		required: true
		    	},
		    	confirm_newPassword: {
		    		required: true,
		    		equalTo: "#newPassword"
		    	}
	    	},
	    	messages: {
	    		oldPassword: {
	    			required: '不能为空'
				},
				newPassword: {
					required: '不能为空'
				},
				confirm_newPassword : {
					required: '不能为空',
					equalTo : "密码不一致"
				}
			},
			errorPlacement: function (error, element) {
				$(element).siblings("span").html(error[0].innerHTML);
				if(error[0].innerHTML == ""){
					$(element).removeClass("input-error");
				}else {
					$(element).addClass("input-error");
				}
			},
			success:function (element) {
			},
			submitHandler: function (){
				ContentLayout.loadData({
					url: test_url+"/cloud/management/util/changePassword.sa",
					type: 'post',
					params: {
						oldPassword: $("#oldPassword").val(),
						newPassword: $("#newPassword").val()
					},
					successCallBack: t.returnBack
				});
			}
	    });
	},
	formSubmit: function (){
		var t = this;
		// $("#"+t.formId).valid();
		$("#"+t.formId).submit();
	},
	returnBack: function (data){
		if(data.result == "0"){
			$(".resultMsg").css("color","#0f0");
			$(".resultMsg").html(data.err+" 跳转到登录界面...");
			ContentLayout.exitSystem();
		}else {
			$(".resultMsg").css("color","#f00");
			$(".resultMsg").html(data.err);
		}
	}
}

var logoutCallback=function(data){
	if(data.result==='success'){
		location.href=ctx + 'login.jsp';
	}	
}