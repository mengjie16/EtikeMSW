CDT = {
	token: null,
	uptoken: null,
	albumTemp: null,
	itemsTemp: null,
	authTemp: null,
	uploadImgCache: new Array(),
	loadedAlbumCache: {},
	loadedAlbumItemsCache: {},
	loadedAuthCache: {},
	uploadIDSCache: new Array(),
	currentAlbumID: 0
};

function initBase(argument) {
	
	//创建专辑
	$(document).on('click', '#albumCreateBtn', function() {
		var name = prompt("请输入专辑标题");
		if (name && name != "") {
			Tr.post('/sys/album/create', {
				'name': name
			}, function(data) {
				if (data.code != 200) {
					alert('创建失败');
					return;
				}
				alert('创建成功');
				loadAlbumList();
			});
		}
	});

	/* 删除专辑 */
	$(document).on('click', '#albumManageContainer .delbtn', function() {
		//var mid = $(this).parents('tr').attr('mid');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if (!confirm('确定删除该专辑吗？')) {
			return;
		}
		Tr.post('/sys/album/delete', {
			'id': id
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败!');
				return;
			}
			loadAlbumList();
		});
	});
	//点击专辑名称
	$(document).on('click', '.titleText', function() {
			var title = $(this).text();
			$(this).parent().html('<input type="text" name="title" class="inputText titleName" data-val="' + title + '" value="' + title + '">');
	})
	//保存修改专辑名称
	$(document).on('keypress', '.titleName', function(e) {
		var id = $(this).parents('tr').eq(0).attr('mid');
		var itemname = [];
		var $itemnames = $(this).parents('tr').siblings().find('.titleText');
		$itemnames.each(function(){
			itemname.push( $( this ).text());
		});
		var $me = $(this),
		    name = $me.val(),
		    oldName = !$me.data('val') ? '' : $me.data('val');
		var has = false;
		if (e.keyCode == 13) {
			$.each(itemname,function(index,obj){
				if(name==obj){
					alert('专辑名称重复');
					$me.val($me.data('val'));
					has = true;
				}
			});
			if(!has&&name!=''&&name!=oldName){
				Tr.post('/sys/album/modify', {
					'album.name': name,
					'album.id': id
				}, function(data) {
					if (data.code != 200) {
						alert('修改失败');
						name = oldName;
					}
					$me.parent().html('<span class="titleText">' + name + '</span>');
				},{
					loadingMask:false
				});
			}else{
				$me.parent().html('<span class="titleText">' + oldName + '</span>');
			}
		}
	});
	//弹出专辑列表框
	$(document).on('click', '#albumManageContainer .editbtn', function() {
		// 获取专辑ID
		
		var $id = $(this).parents('tr').eq(0).attr('mid');
		if (!$id) return;
		// 设置当前正在编辑ID
		CDT.currentAlbumID = $id;
		// 显示专辑的名称
		var albumTitle = $(this).parents('tr').eq(0).find('.titleText').text();
		$('#editAlbumWrapper #album_title').text(albumTitle);
		
		// 加载专辑下商品
		loadAlbumItemsList(false);
		var totalnum = $(this).parents('tr').find('.itemcount').text();
		$('.currentnum em').text(totalnum);

		Tr.popup('editAlbum');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});

	//添加商品
	$(document).on('click', '#itemCreateBtn', function() {
		var promptObj = prompt("请输入商品id");
		if(promptObj==null){
			return;
		}
		if(promptObj=='') {alert('请输入商品id');return;}
		// 存在的商品集
		var exsit = new Array(),
		repeat=[];
		var params = [];
		var $itemids = $('.itemId');
		$itemids.each(function(){
			exsit.push(parseInt($( this ).text())+'');
		});
		
		// 是1-n
		if(promptObj.indexOf("-")>=0){
			var arr = promptObj.split('-');
			var idstart = parseInt(arr[0]),idend = parseInt(arr[1]);
			for(var j = idstart; j <= idend;j++){
					if($.inArray(parseInt(j)+'', exsit)==-1){
						params.push(j);
					}else{
						repeat.push(j);
					}
			}
		}else{// 纯数字
			var reg = /^\d+$/;
			if(!reg.test(promptObj)){
				alert('非法输入，请输入数字');
				return;
			}
			var have = false;
			$.each(exsit,function (index,obj) {
				if(have = parseInt(promptObj)==obj){
					return false;
				}
			});
			if(have){
				alert('请勿输入重复id');
				return;
			}
			params.push(promptObj);
		}
		if(params.length == 0){
			if(repeat.length>0){
				alert('商品ID:'+repeat.join(',')+"重复");
			}
			return;
		}
		var pStr = params.join(',');
		Tr.get('/sys/item/batch?ids='+pStr, {
			
		}, function(data) {
		if (data.code != 200 || !data.results) {
			alert('商品添加失败!');
			return;
		}
		if(data.results.items&&data.results.items.length>0){
			var index = $('#albumItemsContainer tbody tr').length;
			var iprice = 0;
			$.each(data.results.items,function(n,i){
				// 价格层级1
				if (i.distPriceUse && i.distPrice > 0) {
					iprice = i.distPrice / 100;
				} else if (i.priceRangeUse && i.priceRanges && i.priceRanges.length > 0) {
					iprice = i.priceRanges[0].price/100;
				} else {
					iprice = i.retailPrice / 100;
				}
				var $tr = $('<tr mid="' + i.id + '"></tr>');
				index += 1;
				var $index = $('<td><div class="tdcell" style="text-align: center">' + index + '</div></td>');
				var $itemid = $('<td class="tditemid"><div class="thcellId"><div class="tdcell"><a href="/item/' + i.id + '" target="_blank" class="itemId">' + i.id + '</a></div></div></td>');
				var $picture = $('<td><div class="tdcell" style="position:relative"><div class="maskImg" id="al_item_img_' + index + '" img-container="al_img_' + index + '"><input type="hidden" value="' + i.picUrl + '" name=""></div><div class="albumImg" id="al_img_' + index + '"><img src="' + i.picUrl + '"><div class="tip"> <b></b><em>' + i.title + '</em></div></div></div></td>');
				var $price = $('<td class="tdprice"><div class="tdcell"><span class="price">' + iprice + '</span></div></td>');
				var $btn = $('<td class="thcelledit"><div class="tdcell"><a href="javascript:;" class="basebtns besure" style="display:none">确认</a><a href="javascript:;" class="evaluate delbtn">删除</a></div></td>');
				$tr.append($index);
				$tr.append($itemid);
				$tr.append($picture);
				$tr.append($price);
				$tr.append($btn);
				$('#albumItemsContainer tbody').append($tr);
				setBtn('al_item_img_' + index);
				$('.currentnum em').text((index));
			});
			var h = $('#albumItemsContainer table').height() - 390;
			$('#albumItemsContainer').scrollTop(h);
		}
		// 有重复项及不存在项提示
		var errorMessage = "";
		if(repeat.length>0){
			errorMessage +='商品ID:'+repeat.join(',')+"重复,";
		}
		if(data.results.unExsit&&data.results.unExsit.length>0){
			errorMessage +='商品ID:'+data.results.unExsit.join(',')+"不存在";
		}
		if(errorMessage!=''){
			alert(errorMessage);
		}
	}, {
		loadingMask: false
	});
		//loadProduct(cidArr);
	});
	//保存商品列表
	$(document).on('click','#btnAlbumfirm',function () {
		// 商品列表
		var $items = $('#albumItemsContainer table tbody tr');
		if($items.length<=0) return;
		var params = {
			'album.id': CDT.currentAlbumID
		};
		var idChecks = new Array();
		var index = 0;
		// 添加数据
		$.each($items,function(item_index,item){
			var itemId = $(item).find('.tditemid .itemId').text(),
				picUrl = $(item).find('.maskImg input').val(),
				price =  $(item).find('.tdprice .price').text();
			if(itemId&&picUrl&&price&&$.inArray(itemId,idChecks)==-1){ // 符合条件
				params['album.albumItems['+index+'].itemId'] = itemId;
				params['album.albumItems['+index+'].picUrl'] = picUrl;
				params['album.albumItems['+index+'].price'] = price;
				index++;
				idChecks.push(itemId);
			}

		});
		if(idChecks.length<=0){
			return;
		}
		Tr.post('/sys/album/modify',params, function(data) {
			if (data.code != 200) {
				alert('保存失败');
				return;
			}
			alert('保存成功');
			loadAlbumItemsList(true);
			loadAlbumList();
			$('#editAlbum').hide();
			$('html').removeClass('overflow-hidden');
		});
	})

	// 鼠标悬停显示商品标题
	$(document).on('hover', '.maskImg', function() {
		$(this).siblings().find('.tip').toggle();
	});
	// 点击id
	$(document).on('dblclick', '.itemId', function() {
			var id = $(this).text();
			$(this).parents('tr').find('.thcelledit .besure').show();
			$(this).parent().html('<input type="text" name="item_id" class="inputText idInput" data-val="' + id + '" value="' + id + '" style="width:120px">');
		})
	// 点击价格
	$(document).on('dblclick', '.price', function() {
		var title = $(this).text();
		$(this).parent().parent().next().find('.besure').show();
		$(this).parent().html('<input type="text" name="item_price" class="inputText priceInput" data-val="' + title + '" value="' + title + '" style="width:120px">');
	})

	// 价格和ID校验
	$(document).on('change', '#albumItemsContainer .idInput,#albumItemsContainer .priceInput', function() {
			var reg = /^[0-9]+$/;
			var data = $(this).data('val');
			// 当前的值
			var currentVal = $(this).val();
			// 判断当前的值是否符合逻辑(商品ID只能是数字,商品价格只能数字)
			if (!reg.test(currentVal)) {
				alert("只能是数字");
				$(this).val(data);
				return;
			}
	})
	//保存修改价格
	$(document).on('click', '#editAlbumWrapper .besure', function(e) {
		var $parent = $(this).parents('tr'),
			$me = $(this);
		var $priceObj = $parent.find('.priceInput'),
			$idObj = $parent.find('.idInput'),
			index = $parent.index();
		var price = !$priceObj.val() ? '' : $priceObj.val();
		var id = !$idObj ? 0 : $idObj.val();
		itemid = [];
		var $itemids = $(this).parents('tr').siblings().find('.itemId');
		$itemids.each(function(){
			itemid.push( $( this ).text());
		});
		var has = false;
		$.each(itemid,function (index,obj) {
			if(id==obj){
				alert('请勿输入重复id');
				$parent.find('.tditemid .tdcell').html('<a href="/item/'+$idObj.data('val')+'" target="_blank" class="itemId">' + $idObj.data('val') + '</a>');
				has = true;
			}
		});
		// 修改专辑商品第几行
		if (price) {
			$parent.find('.tdprice .tdcell').html('<span class="price">' + price + '</span>');
		}
		if(!has){
			if (id && id != 0) {
				var url = '/sys/item/' + id;
				Tr.get(url, {
					"id": id
				}, function(data) {
					if (data.code != 200 || !data.results) {
						alert('商品不存在!');
						$parent.find('.tditemid .tdcell').html('<a href="/item/'+$idObj.data('val')+'" target="_blank" class="itemId">' + $idObj.data('val') + '</a>');
						return;
					}
					var i = data.results;
					var index = $('#albumItemsContainer tbody tr').length;
					var iprice = 0;
					// 价格层级1
					if(i.distPriceUse&&i.distPrice>0){
						iprice = i.distPrice;
					}else if(i.priceRangeUse&&i.priceRanges&&i.priceRanges.length>0){
						iprice = i.priceRanges[0].price;
					}else{
						iprice = i.retailPrice;
					}
					index+=1;
					$parent.find('.albumImg img').attr('src',i.picUrl);
					$parent.find('.tip em').text(i.title);
					$parent.find('.price').text(iprice);
					setBtn('al_item_img_'+index);
					$parent.attr('mid',id);
					$parent.find('.tditemid .tdcell').html('<a href="/item/'+$idObj.data('val')+'" target="_blank" class="itemId">' + id + '</a>');
				});
				
			}
		}
		$me.hide();
	});

	/* 删除专辑商品 */
	$(document).on('click', '#albumItemsContainer .delbtn', function() {
		// var index = $(this).parents('tr').index();
		// if (!index || index < 0) return;
		if (!confirm('确定删除该商品吗？')) {
			return;
		}
		$(this).parents('tr').remove();
	});

	//弹出授权框
	$(document).on('click', '#albumManageContainer .authorizebtn', function() {
		// 获取专辑ID
		var $id = $(this).parents('tr').eq(0).attr('mid');
		if (!$id) return;
		// 设置当前正在编辑ID
		CDT.currentAlbumID = $id;
		// 显示专辑的名称
		var albumTitle = $(this).parents('tr').eq(0).find('.titleText').text();
		$('#editAuthorize #album_title').text(albumTitle);
		loadAuthList();
		Tr.popup('editAuthorize');
	});
	//添加现有用户
	$(document).on('click', '#addUserBtn', function() {
		var id = prompt("请输入微信用户id");
		itemid = [];
		var $itemids = $('.userid em');
		$itemids.each(function(){
			itemid.push( $( this ).text());
		});
		var has = false;
		$.each(itemid,function (index,obj) {
			if(id==obj){
				alert('请勿输入重复id');
				has = true;
			}
		});
		if(!has){
			if (id && id != "") {
				Tr.get('/sys/wechat/user/queryOne', {
					"id": id
				}, function(data) {
					if (data.code != 200 || !data.results) {
						alert('用户不存在!');
						return;
					}
					var i = data.results;
					var $tr = $('<li class="authuser" data-id="'+i.id+'"></li>');
					var $itemid = $('<p class="userid">ID:<em>'+i.id+'</em><i class="iconfont delete">&#xe606;</i></p>');
					var $picture = $('<div class="userimg"><img src="'+i.avatar+'"/></div>');
					var $nick = $('<p class="username">'+i.nick+'</p>');
					$tr.append($itemid);
					$tr.append($picture);
					$tr.append($nick);
					$('#authUsers').append($tr);
				},{loadingMask: false});
			}
		}
	});
	//点击预设用户
	$(document).on('click', '#preinstallBtn', function() {
		var nick = prompt("请输入昵称关键词");
		itemnick = [];
		var $itemnicks = $('.keyword');
		$itemnicks.each(function(){
			itemnick.push( $( this ).text());
		});
		var has = false;
		$.each(itemnick,function (index,obj) {
			if(nick==obj){
				alert('请勿输入重复关键词');
				has = true;
			}
		});
		if(!has){
			if (nick && nick != "") {
				$('#preset').append('<li class="keyword"><span class="currentkey">'+nick+'</span><i class="iconfont delete">&#xe606;</i></li>');
			}
		}
	});

	// 删除当前授权用户
	$(document).on('click','.authuser .delete',function(){
		var $me = $(this);
		$me.parent().parent().remove();

	});

	// 删除当前预设用户
	$(document).on('click','.keyword .delete',function(){
		var $me = $(this);
		$me.parent().remove();

	});

	//保存授权信息
	$(document).on('click','#btnPrefirm',function(){
		// 授权管理id
		var id = $('#editAuthorize').data("id");
		id = !id?'':id;
		// 授权用户信息
		var users = [];
		$.each($('#editAuthorize #authUsers li.authuser'),function(index,user){
			var userId = $(user).data('id');
			if(userId){
				users.push(userId);
			}
		});

		// 预设用户信息
		var presets = [];
		$.each($('#editAuthorize #preset li.keyword .currentkey'),function(index,preset){
			var keyword = $(preset).text();
			if(keyword){
				presets.push(keyword);
			}
		});
		var postParam = {
			'am._id': id,
			'am.type': 'album',
			'am.moudleId':CDT.currentAlbumID,
			'am.users':users.join(','),
			'am.preset':presets.join(',')
		};
		Tr.post('/sys/moudble/auth/save',postParam, function(data) {
			if (data.code != 200) {
				alert('保存失败');
				return;
			}
			$('#editAuthorize').hide();
			$('html').removeClass('overflow-hidden');
		});

	});
}
// 初始化数据
$(function() {
	// 专辑render模板
	CDT.albumTemp = $('#albumTemp').remove().val();
	Mustache.parse(CDT.albumTemp);
	// 专辑商品列表render模板
	CDT.itemsTemp = $('#itemsTemp').remove().val();
	Mustache.parse(CDT.itemsTemp);
	// 专辑微信用户列表render模板
	CDT.authTemp = $('#authTemp').remove().val();
	Mustache.parse(CDT.authTemp);
	initBase();
	loadUpToken();
	loadAlbumList();
});
//添加商品
function loadProduct(cid) {

}
// 加载专辑列表
function loadAlbumList() {
	Tr.get('/sys/album/query/list', {
		"type": 'album',
		'moudleId':CDT.currentAlbumID 
	}, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedAlbumCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.albumTemp, $.extend(data, {
			itemCount: function() {
				return this.albumItems ? this.albumItems.length : 0;
			},
			formatCreateTime: function() {
				if (this.createTime) {
					return new Date(this.createTime).Format('yyyy-MM-dd');
				}
				return '----';
			}
		}));
		$('#albumManageContainer tbody').html(output);
	});
}
// 加载微信用户列表
function loadAuthList() {
	Tr.get('/sys/moudble/auth/query', {
		'type': 'album',
		'moudleId':CDT.currentAlbumID
	}, function(data) {
		if (data.code != 200 || !data.results) {
			$('#editAuthorize #authUsers').empty();
			$('#editAuthorize #preset').empty();
			$('#editAuthorize').data("id",'');
			return;
		};
		// 保存到缓存
		CDT.loadedAlbumCache = data.results;
		// 当前用户信息
		$('#editAuthorize').data("id",data.results._id);
		// 输出授权用户
		var output = Mustache.render(CDT.authTemp, $.extend(data.results, {}));
		$('#editAuthorize #authUsers').empty().html(output);
		// 输出预设用户
		var presetStr = "";
		if(data.results.preset){
			$.each(data.results.preset,function(index,obj){
				presetStr += "<li class='keyword'><span class='currentkey'>"+obj+"</span><i class='iconfont delete'>&#xe606;</i></li>";
			}
			);
		}
		$('#editAuthorize #preset').empty().append(presetStr);
	});
}
// 加载专辑商品列表
function loadAlbumItemsList(force) {
	if (CDT.loadedAlbumItemsCache[CDT.currentAlbumID] && force == false) {
		printAlbumItemsHTML(CDT.currentAlbumID);
	} else {
		Tr.get('/sys/album/item/query', {
			id: CDT.currentAlbumID
		}, function(data) {
			if (data.code != 200) return;
			// 保存到缓存
			CDT.loadedAlbumItemsCache[CDT.currentAlbumID] = {
				items: data.results
			};
			printAlbumItemsHTML(CDT.currentAlbumID)
		});
	}
}

function printAlbumItemsHTML(albumId) {
	$('#albumItemsContainer tbody').empty();
	if(!CDT.loadedAlbumItemsCache[albumId].items) return;
	var data = CDT.loadedAlbumItemsCache[albumId];
	// 追加index
	$.each(data.items,function(index,obj){
		obj.index = index+1;
	});
	// 输出到html
	var output = Mustache.render(CDT.itemsTemp, $.extend(data, {
		formatPrice: function() {
				return this.price/100;
			}
	}));
	$('#albumItemsContainer tbody').html(output);
	setBtns();
}

function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		setBtns();
	}, {
		loadingMask: false
	});
}

function setBtns() {
	$('.maskImg').each(function(i) {
		var uId = this.id;
		if (uId && $.inArray(uId, CDT.uploadIDSCache) == -1) {
			initItemMainPicUploader(uId);
			CDT.uploadIDSCache.push(uId);
		}
	});
}

function setBtn(uId){
	if(!uId) return;
	initItemMainPicUploader(uId);
	CDT.uploadIDSCache.push(uId);
}

function initItemMainPicUploader(domId) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = domId;
	option.container = 'albumItemsContainer';
	option.init = {
		'BeforeUpload': function(up, file) {
			// 检查文件类型

		},
		'UploadProgress': function(up, file) {
			// 每个文件上传时,处理相关的事情，转菊花、显示进度等
		},
		'FileUploaded': function(up, file, info) {
			var domain = up.getOption('domain');
			var res = $.parseJSON(info);
			var sourceLink = domain + res.key;
			var imgId = $('#' + domId).attr('img-container');

			/*添加图片*/
			$('#' + imgId).find('img').attr('src', sourceLink);
			CDT.uploadImgCache[file.name] = sourceLink;
			$('#' + domId).find('input').val(sourceLink);
			//$('#' + domId).hide();
		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// // alert('请不要上传重复的图片');
				var imgId = $('#' + domId).attr('img-container');
				$('#' + imgId).find('img').attr('src', sourceLink);
				$('#' + domId).find('input').val(sourceLink);
				return;
			}
			//上传出错时,处理相关的事情
			alert('上传失败！');
		},
		'Key': function(up, file) {
			// 若想在前端对每个文件的key进行个性化处理，可以配置该函数
			// 该配置必须要在 unique_names: false , save_key: false 时才生效
			var key = "";
			// do something with key here
			return key
		}
	}
	Qiniu.uploader(option);
}