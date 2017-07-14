CDT = {
	token: null,
	uptoken: null,
	activityId: null,
	uploadImgCache: new Array(),
	activityCache: null,
	itemsTemp: null,
	loadeditemsCache: {},
	bottomItemsTemp: null,
	loadedbottomItemsCache: {},
	uploadIDSCache: new Array(),
	currPageNo: 1,
	pageSize: 10
};

function initBase() {

	//读取id
	$(document).on('click', '.readBtn', function() {
		var itemId = $(this).prev().val();
		if (!itemId && itemId == '') return
		var item = $(this).parents('.activityItem');
		var url = '/sys/item/' + itemId;
		Tr.get(url, {
			"id": itemId
		}, function(data) {
			if (data.code != 200 || !data.results) return;
			var i = data.results;
			item.find('input[name="items.itemId"]').val(itemId);
			item.find('input[name="items.subTitle"]').val(i.title);
			item.find('input[name="items.price"]').val(i.retailPrice);
			item.find('input[name="items.distPrice"]').val(i.distPrice);
			item.find('input[name="items.itemImg"]').val(i.picUrl);
			item.find('.loadbigImg').html('<img src="' + i.picUrl + '" />');
		});
	});
	//添加主活动商品items
	$(document).on('click', '.addactivity', function() {
		var $index = $('.activityItem').length;
		var $prevObj = $('.activityItem').first().clone();
		$prevObj.find('.maskbigImg').attr('id', 'T_adBanner_' + $index).show();
		$prevObj.find('.maskbigImg').attr('img-container', 'T_img_' + $index);
		$prevObj.find('.loadbigImg').attr('id', 'T_img_' + $index);
		$prevObj.find('.loadbigImg').html('');
		$prevObj.find('input[name="items.itemId"]').val('');
		$prevObj.find('input[name="items.mainTitle"]').val('');
		$prevObj.find('input[name="items.subTitle"]').val('');
		$prevObj.find('input[name="items.price"]').val('');
		$prevObj.find('input[name="items.distPrice"]').val('');
		$prevObj.find('input[name="items.itemImg"]').val('');
		$prevObj.find('textarea[name="items.content"]').val('');
		$prevObj.find('input[name="modifyType"]').val('add');
		$(".activityItems").append($prevObj);
		setBtns();
	});

	// 确认主活动商品items保存
	$(document).on('click', '.btnItemForm', function() {
		var $me = $(this);
		var validator = $me.parents('.frmItems').validate(itemValidateOpts());
		var type = $me.parents('.frmItems').find('input[name="modifyType"]').val();
		var param = {};
		var index = $me.parents('.activityItem').eq(0).index();
		var item = $('.activityItem').eq(index);
		param['moudle'] = 'activityItems';
		param['index'] = index;
		param['modifyType'] = type;
		param['authenticityToken'] = $('input[name=authenticityToken]').val(),
			param['activityPageSet._id'] = CDT.activityId;
		param['activityPageSet.activityItem.itemId'] = item.find('input[name="items.itemId"]').val();
		param['activityPageSet.activityItem.mainTitle'] = item.find('input[name="items.mainTitle"]').val();
		param['activityPageSet.activityItem.subTitle'] = item.find('input[name="items.subTitle"]').val();
		param['activityPageSet.activityItem.price'] = item.find('input[name="items.price"]').val();
		param['activityPageSet.activityItem.distPrice'] = item.find('input[name="items.distPrice"]').val();
		param['activityPageSet.activityItem.itemImg'] = item.find('input[name="items.itemImg"]').val();
		param['activityPageSet.activityItem.content'] = item.find('textarea[name="items.content"]').val();
		if (!validator.form()) {
			return;
		}
		Tr.post('/sys/activity/mouble/modify', param, function(data) {
			if (data.code != 200) {
				alert('更新失败!');
				return;
			}
			alert('更新成功！');
			loadActivityContent('loadActivityItems');
		}, {
			loadingMask: false
		});
	});

	/*删除主活动商品行*/
	$(document).on('click', '.delbtn', function() {
		var index = $(this).parents('.activityItem').index();
		if (!confirm('确定删除该主活动吗？')) {
			return;
		}
		$.post('/sys/activity/delmodle', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'moudle': 'activityItems',
			'id': CDT.activityId,
			'index': index
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败');
				return;
			}
			alert('删除成功');
			loadActivityContent('loadActivityItems');
		});
	});

	// 添加商品列表弹框
	$(document).on('click', '.addList', function() {
		$('#txtItemId').val('');
		Tr.popup('editbottomItems');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//保存活动商品列表
	$(document).on('click', '#btnbottomItemsfirm', function() {
		$me = $(this);
		var validator = $('#frmEditbottomItems').validate(bottomValidateOpts());
		if (!validator.form()) {
			return;
		}
		var itemId = $('#txtItemId').val();
		$.post('/sys/activity/mouble/modify', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'moudle': 'bottomItemIds',
			'activityPageSet._id': CDT.activityId,
			'activityPageSet.itemId': itemId,
			'modifyType': 'add'
		}, function(data) {
			if (data.code != 200) {
				alert('更新失败!');
				return;
			}
			alert('更新成功！');
			loadActivityContent('loadBottomItemsList');
			$me.parents('.popWrapper').eq(0).hide();
		})
	});
	/*删除活动商品列表行*/
	$(document).on('click', '.delbtns', function() {
		var index = $(this).parents('.prt_list').eq(0).index();
		if (!confirm('确定删除该活动商品吗？')) {
			return;
		}
		$.post('/sys/activity/delmodle', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'moudle': 'bottomItemIds',
			'id': CDT.activityId,
			'index': index
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败');
				return;
			}
			alert('删除成功');
			loadActivityContent('loadBottomItemsList');
		});
	});
};
//活动商品列表弹框验证
function bottomValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: true,
		rules: {
			'bottomItems.id': {
				required: true,
				digits: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//主活动商品验证
function itemValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: true,
		rules: {
			'items.mainTitle': {
				required: true
			},
			'items.itemImg': {
				required: true
			},
			'items.itemId': {
				required: true,
				digits: true
			},
			'items.subTitle': {
				required: true
			},
			'items.price': {
				required: true,
				number: true
			},
			'items.distPrice': {
				required: true,
				number: true
			},
			'items.content': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}

$(function() {
	loadUpToken();
	CDT.itemsTemp = $('#itemsTemp').remove().html();
	Mustache.parse(CDT.itemsTemp);
	CDT.bottomItemsTemp = $('#bottomItemsTemp').remove().val();
	Mustache.parse(CDT.bottomItemsTemp);
	initBase();
	loadActivityContent('');
});

function loadActivityTitle() {
	$('#activityTitle').text(CDT.activityCache.title);
}
//加载banner
function loadBigBannerImg() {
	var bigImgUrl = !CDT.activityCache.bannerImg || CDT.activityCache.bannerImg == 'undefined' ? '' : CDT.activityCache.bannerImg;
	$('#bigbanner').html('<img src="' + bigImgUrl + '" />');
}
//加载middlePoster
function loadMiddlePosterImg() {
	var smaImgUrl = !CDT.activityCache.middlePoster || CDT.activityCache.middlePoster == 'undefined' ? '' : CDT.activityCache.middlePoster;
	$('#smallbanner').html('<img src="' + smaImgUrl + '" />');
}
// 加载主活动商品列表
function loadActivityItems() {
	var items = {
		'activityItems': !CDT.activityCache.activityItems ? new Array() : CDT.activityCache.activityItems
	};
	if (items.activityItems.length <= 0) {
		items.activityItems.push({
			'mainTitle': '',
			'itemImg': '',
			'itemId': '',
			'subTitle': '',
			'price': '',
			'distPrice': '',
			'content': '',
			'modifyType': 'add'
		});
	}
	var output = Mustache.render(CDT.itemsTemp, $.extend(items, {}));
	$('.activityItems').html(output);
	setBtns();
}

// 加载活动商品列表
function loadBottomItemsList() {
	var bottomItems = {
		'bottomItems': null
	};
	if (CDT.activityCache && CDT.activityCache.bottomItems) {
		bottomItems = {
			'bottomItems': CDT.activityCache.bottomItems
		};
	}
	var output = Mustache.render(CDT.bottomItemsTemp, $.extend(bottomItems, {}));
	$('#thumImageObjectList').html(output);
}
// 加载活动信息
function loadActivityContent(functionName) {
	Tr.get('/sys/activity/query', {
		'id': CDT.activityId
	}, function(data) {
		if (data.code != 200 || !data.results || data.results.length <= 0) return;
		// 保存到缓存
		CDT.activityCache = data.results;
		if (!functionName || functionName == '') {
			// 活动标题设置
			loadActivityTitle();
			// banner图片
			loadBigBannerImg();
			//加载middlePoster
			loadMiddlePosterImg();
			//加载bottomItems
			loadBottomItemsList();
			//加载items
			loadActivityItems();
		} else if (functionName == 'loadBottomItemsList') {
			loadBottomItemsList();
		} else if (functionName == 'loadActivityItems')
			loadActivityItems();
	});
}

function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		setBtns();
		initItemBigPicUploader();
		initItemMidPicUploader();
	});
}

function setBtns() {
	$('.upload').each(function(i) {
		var uId = $(this).attr('id');
		if (uId && $.inArray(uId, CDT.uploadIDSCache) == -1) {
			initItemMainPicUploader(uId);
			CDT.uploadIDSCache.push(uId);
		}
	});
}

function initItemMainPicUploader(domId) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = domId;
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
			$('#' + domId).find('input').eq(0).val(sourceLink);
			$('#' + imgId).empty().html('<img src="' + sourceLink + '" />');
			CDT.uploadImgCache[file.name] = sourceLink;

		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// alert('请不要上传重复的图片');
				var imgId = $('#' + domId).attr('img-container');
				$('#' + imgId).html('<img src="' + sourceLink + '" />');
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
//上传大banner
function initItemBigPicUploader() {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'activityBanner';
	option.container = 'bigcontainer';
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
			var type = $('.bigBanner').data('type');
			if (type && type == 'add') {
				$("#bannerImg input[name='modifyType']").val('add');
			} else {
				$("#bannerImg input[name='modifyType']").val('update');
			}
			$.post('/sys/activity/mouble/modify', {
				'authenticityToken': $('input[name=authenticityToken]').val(),
				'moudle': 'bannerImg',
				'activityPageSet._id': CDT.activityId,
				'activityPageSet.bannerImg': sourceLink,
				'modifyType': 'update'
			}, function(data) {
				if (data.code != 200) {
					alert('更新失败!');
					return;
				}
				alert('更新成功！');
				/*添加图片*/
				$('#activityBanner').find('input').eq(0).val(sourceLink);
				$('#bigbanner').empty().html('<img src="' + sourceLink + '" />');
				CDT.uploadImgCache[file.name] = sourceLink;
			});
		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// alert('请不要上传重复的图片');
				$('#activityBanner').find('input').eq(0).val(sourceLink);
				$('#bigbanner').empty().html('<img src="' + sourceLink + '" />');
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

//上传middlePoster
function initItemMidPicUploader() {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'T_smBanner';
	option.container = 'midcontainer';
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
			var type = $('.smallBanner').data('type');
			if (type && type == 'add') {
				$("#frmMiddlePoster input[name='modifyType']").val('add');
			} else {
				$("#frmMiddlePoster input[name='modifyType']").val('update');
			}
			$.post('/sys/activity/mouble/modify', {
				'authenticityToken': $('input[name=authenticityToken]').val(),
				'moudle': 'middlePoster',
				'activityPageSet._id': CDT.activityId,
				'activityPageSet.middlePoster': sourceLink,
				'modifyType': 'update'
			}, function(data) {
				if (data.code != 200) {
					alert('更新失败!');
					return;
				}
				alert('更新成功！');
				$('#T_smBanner').find('input').eq(0).val(sourceLink);
				$('#smallbanner').empty().html('<img src="' + sourceLink + '" />');
				CDT.uploadImgCache[file.name] = sourceLink;
			});
		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// alert('请不要上传重复的图片');
				$('#activityBanner').find('input').eq(0).val(sourceLink);
				$('#smallbanner').empty().html('<img src="' + sourceLink + '" />');
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