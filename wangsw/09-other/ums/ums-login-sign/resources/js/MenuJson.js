/*
*Modify by yzq
*Modify on 2016.06.22
*/
MenuJson = function (config){
	config = config || {};
	
	this.menuObj = config.menuObj;
	this.container = config.container;
	this.parentId = config.parentId;
	this.menuLevel = config.menuLevel;
	
	this.fileServerPath = config.fileServerPath;
	
	this.initMenu();
};

MenuJson.prototype.initMenu = function (){
	var t = this;
	t.showMenu(t.menuObj, t.container, t.parentId, t.menuLevel);
}

MenuJson.prototype.showMenu = function (obj , select, parentId, menuLevel){
	var t = this;
	menuLevel = Number(menuLevel)+1;
	   if(obj != null && obj.children != null && obj.children.length > 0){
		   for(var i=0;i<obj.children.length;i++){
			   if(obj.children[i].children != null && obj.children[i].children.length > 0){
				    if(obj.children[i].isWelcomeMenu == '1'){
				    	welcomeMenuId = obj.children[i].id;
				    }
				    
					if(1 == menuLevel){
						if(obj.children[i].photoIds != null  && obj.children[i].photoIds != "" && eval(obj.children[i].photoIds).length > 0){
							$("."+select).append("<li class='nav-main-heading' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><span class='sidebar-mini-hide'>"+obj.children[i].name+"</span></li>");
						}else {
							$("."+select).append("<li class='nav-main-heading' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><span class='sidebar-mini-hide'>"+obj.children[i].name+"</span></li>");
						}
						select = 'nav-main';
					}else if(2 == menuLevel){
	 					if(obj.children[i].photoIds != null  && obj.children[i].photoIds != "" && eval(obj.children[i].photoIds).length > 0){
	 						$("."+select).append("<li class='' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a class='nav-submenu' data-toggle='nav-submenu' href='javascript:void(0);'><i class='si si-wrench'></i><span class='sidebar-mini-hide'>"+obj.children[i].name+"</span></a><ul parentId="+obj.children[i].id+"></ul></li>");
	 					}else {
	 						$("."+select).append("<li class='' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a class='nav-submenu' data-toggle='nav-submenu' href='javascript:void(0);'><i class='si si-wrench'></i><span  class='sidebar-mini-hide'>"+obj.children[i].name+"</span></a><ul parentId="+obj.children[i].id+"></ul></li>");
	 					}
	 					if("secondMenuId" == select){
							select = 'thirdMenuId';
						}
					}else {
						if(obj.children[i].photoIds != null  && obj.children[i].photoIds != "" && eval(obj.children[i].photoIds).length > 0){
	 						$("ul[parentId='"+obj.children[i].parentId+"']").append("<li class='' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a class='nav-submenu' data-toggle='nav-submenu' href='javascript:void(0);'>"+obj.children[i].name+"</a><ul parentId="+obj.children[i].id+"></ul></li>");
	 					}else {
	 						$("ul[parentId='"+obj.children[i].parentId+"']").append("<li class='' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a class='nav-submenu' data-toggle='nav-submenu' href='javascript:void(0);'>"+obj.children[i].name+"</a><ul parentId="+obj.children[i].id+"></ul></li>");
	 					}
						if("thirdMenuId" == select){
							select = 'fourthMenuId';
						}
					}
					t.showMenu(obj.children[i],select,obj.children[i].id,menuLevel);
				}else {
					if(obj.children[i].isWelcomeMenu == '1'){
				    	welcomeMenuId = obj.children[i].id;
				    }
					if(defaultWelcomeId == ''){
						defaultWelcomeId = obj.children[i].id;
					}
					
					if(1 == menuLevel){
						if(obj.children[i].photoIds != null  && eval(obj.children[i].photoIds).length > 0){
							$("."+select).append("<li class='nav-main-heading' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><img src='"+t.filePath+"?id="+eval(obj.children[i].photoIds)[0].id+"&openmode=inline'><span>"+obj.children[i].name+"</span></li>");
						}else {
							$("."+select).append("<li class='nav-main-heading' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><span>"+obj.children[i].name+"</span></li>");
						}
					}else if(2 == menuLevel){
						if(obj.children[i].photoIds != null  && obj.children[i].photoIds != "" && eval(obj.children[i].photoIds).length > 0){
//							$("."+select).append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a href='javascript:void(0);'><img src='"+t.filePath+"?id="+eval(obj.children[i].photoIds)[0].id+"&openmode=inline'><span>"+obj.children[i].name+"</span></a></li>");
							$("."+select).append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a href='javascript:void(0);'><i class='si si-wrench'></i><span class='sidebar-mini-hide'>"+obj.children[i].name+"</span></a></li>");
	 					}else {
//	 						$("."+select).append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><a href='javascript:void(0);'><span>"+obj.children[i].name+"</span></a></li>");
	 						$("."+select).append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId = "+obj.children[i].id+"><a href='javascript:void(0);'><i class='si si-wrench'></i><span class='sidebar-mini-hide'>"+obj.children[i].name+"</span></a></li>");
	 					}
					}else {
						if(obj.children[i].photoIds != null  && obj.children[i].photoIds != "" &&  eval(obj.children[i].photoIds).length > 0){
							$("ul[parentId='"+obj.children[i].parentId+"']").append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a href='javascript:void(0);'><img src='"+t.filePath+"?id="+eval(obj.children[i].photoIds)[0].id+"&openmode=inline'><span>"+obj.children[i].name+"</span></a></li>");
	 					}else {
	 						$("ul[parentId='"+obj.children[i].parentId+"']").append("<li class='no_child' uri=\'"+obj.children[i].uri+"\' menuId ="+obj.children[i].id+"><a href='javascript:void(0);'><span>"+obj.children[i].name+"</span></a></li>");
	 					}
					}
				}
		   }
	   }
}