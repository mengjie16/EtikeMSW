CDT = {
	token: null,
	uptoken: null,
	ue: null,
	h5ue: null,
	uploadImgCache: new Array(),
	suggestSearchCache: {},
	placeSearchCache: {},
	provinceCache: {},
	cityCache: {},
	regionCache: {},
	uploadIDSCache: new Array(),
	itemId: '',
	sendLoactionTempCache: null
};

// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
//发布商品的表单验证
function ValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'item.title': {
				required: true,
				maxwords: 20,
				minwords: 5
			},
			'item.cate': {
				required: true
			},
			'item.picUrl': {
				required: true
			},
			'item.netWeight': {
				required: true,
				number:'净重量'
			},
			'item.grossWeight': {
				required: true,
				number:'毛重量'
			},
			'item.unit': {
				required: true,
				maxlength: 5,
				minlength: 1
			},
			'item.origin': {
				required: true
			},
			'item.outNo': {
				required: true
			},
			'item.d_retailPrice': {
				required: true,
				number: '价格'
			},
			'item.brandId': {
				required: true
			},
			'item.status': {
				required: true
			},
			'item.initialQuantity': {
				digits: true
			},
			'sku.color': {
				required: true
			},
			'sku.img': {
				required: true
			},
			'sku.quantity': {
				required: true,
				number: '库存',
				digits: true
			},
		},
		success: function(label, element) {}
	}
	return options;
}

function initBase(argument) {

	$('body').click(function(e) {
		if ($(e.srcElement).is('#regionControls *') || !e.srcElement) {
			return true;
		}
		$('.selectboxs').hide();
	});

	//选择品牌弹框
	$(document).on('click', '.chooseBrand', function() {
		$('#brandInput').val('');
		printBrandHTMLbyCache();
		Tr.popup('addBrand');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//选择品牌
	$(document).on('hover', '.brandList ul li', function() {
		$(this).find('.tip').toggle();
	});
	$(document).on('click', '.brandList ul li>span', function() {
		var brands = $('.brandList ul li>span');
		if (brands > 1) {
			$('.brandList ul li>span').removeClass('active');
		}
		$(this).toggleClass('active');
	});
	//显示品牌
	$('#btnConfirm').on('click', function() {
		$('.brandList ul li>span').each(function(index, obj) {
			if ($(obj).hasClass('active')) {
				var text = $(obj).text();
				var id = $(obj).parent().attr('data-id');
				$('#brandTxt').val(id).removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
				$('div[name="valbrand"]').children('span').remove();
				// $('#formComm').valid();
				$('#showBrand span').text(text);
				$('#showBrand').show().prev().hide();
				$(this).parents('.popWrapper').hide();
				$('html').removeClass('overflow-hidden');
				$('.brandList ul li>span').removeClass('active');

			}
		});
	});
	//删除品牌
	$('#delBrand').on('click', function() {
		$(this).parent().hide();
		$(this).prev().text('');
		$('#brandTxt').val('');
		$(this).parent().prev().show();
	});
	//查询品牌
	$('#searchBrandBtn').on('click', function() {
		printBrandHTMLbyCache();
	});
	$('#brandInput').on('keypress', function(e) {
		if (e.keyCode == 13) {
			printBrandHTMLbyCache();
		}
	});


	// 宝贝类目模糊查询
	//选择类目弹框
	$(document).on('click', '.chooseCate', function() {
		$('#cateInput').val('');
		$('#cateList ul').empty();
		// 先查缓存	
		if (CDT.suggestSearchCache && CDT.suggestSearchCache.length > 0) {
			$.each(CDT.suggestSearchCache, function(index, obj) {
				$('#cateList ul').append("<li class='cateitem' data-id='" + obj.id + "'>" + obj.name + "</li>");
			});
		} else {
			Tr.get('/dpl/cate/all', {}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.suggestSearchCache = data.results;
				$.each(data.results, function(index, obj) {
					$('#cateList ul').append("<li class='cateitem' data-id='" + obj.id + "'>" + obj.name + "</li>");
				});
			}, {
				loadingMask: false
			});
		}
		Tr.popup('addCate');
	});
	//选择类目
	$(document).on('click', '.cateList ul li', function() {
		$('.cateList ul li').removeClass('active');
		$(this).toggleClass('active');
	});
	//显示类目
	$('#btnCatefirm').on('click', function() {
		$('.cateList ul li').each(function(index, obj) {
			if ($(obj).hasClass('active')) {
				var text = $(obj).text();
				var id = $(obj).attr('data-id');
				$('#cateTxt').val(id).removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
				$('div[name="valcate"]').children('span').remove();
				// $('#formComm').valid();
				$('#showCate span').text(text);
				$('#showCate').show().prev().hide();
				$(this).parents('.popWrapper').hide();
				$('html').removeClass('overflow-hidden');
				$('.cateList ul li').removeClass('active');
			}
		});
	});
	//删除类目
	$('#delCate').on('click', function() {
		$(this).parent().hide();
		$(this).prev().text('');
		$('#cateTxt').val('');
		$(this).parent().prev().show();
	});
	//查询类目
	$('#cateInput').on('click', function() {
		$(this).val('');
		var $container = $('#cateList');
		if (!$container.length) return true;
		$items = $container.find('li'),
			$item = $(),
			itemsIndexed = [];
		$items.each(function() {
			itemsIndexed.push($(this).text().replace(/\s{2,}/g, ' ').toLowerCase());
		});
		$items.show();
		$(this).on('keyup', function(e) {
			var searchVal = $.trim($(this).val()).toLowerCase();
			if (searchVal.length) {
				for (var i in itemsIndexed) {
					$item = $items.eq(i);
					if (itemsIndexed[i].indexOf(searchVal) != -1)
						$item.show();
					else
						$item.hide();
				}
			} else $items.show();
		});
	});
	// 宝贝产地模糊查询
	$('#placePro').on('keyup', function() {
		var kw = $(this).val();
		if (!kw || kw.length < 1) {
			$('#placeList').hide();
			return;
		}
		// 先查缓存	
		if (CDT.placeSearchCache[kw] && CDT.placeSearchCache[kw].length > 0) {
			printPlaceHTMLbyCache(kw, true);
		} else {
			Tr.get('/dpl/origin/search', {
				'keyWord': kw
			}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.placeSearchCache[kw] = data.results;
				printPlaceHTMLbyCache(kw, true);
			}, {
				loadingMask: false
			});
		}
	});

	$(document).on('click', '.selectboxs li', function() {
		var text = $(this).text();
		var id = $(this).attr('data-id');
		$('#cateTxt').val(id);
		$(this).parent().parent().prev().val(text);
		$('.selectboxs').hide();
	});

	// 增加规格条件描述项
	$(document).on('click', '#addSpec', function(event) {
		var $plen = $(this).parent().find('.gg_container').length,
			$index = new Date().getTime();
		if ($plen >= 10) return;
		var $prev = $(this).prev();
		var $prevObj = $prev.clone();
		$prevObj.find('input').val('');
		if ($prevObj.find('.iconfont').length == 0) {
			$prevObj.append("<i class='iconfont ml10 ggdelbtn'>&#xe606;</i><i class='iconfont pl10 moveUp'>&#xe600;</i><i class='iconfont pl10 moveDown'>&#xe60d;</i>");
		}
		if ($plen <= 1) {
			$prev.append("<i class='iconfont ml10 ggdelbtn'>&#xe606;</i><i class='iconfont pl10 moveUp'>&#xe600;</i><i class='iconfont pl10 moveDown'>&#xe60d;</i>");
		}
		// 处理clone元素属性问题
		$prevObj.find('.mask').attr('id', 'T_adBanner_' + $index).show();
		$prevObj.find('.mask').attr('img-container', 'img_' + $index);
		$prevObj.find('.itemImg').attr('id', 'img_' + $index);
		$prevObj.find('img').attr('src', '');
		$prevObj.find('input[name="sku.img"]').attr('id', 'hidPicUrl_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('input[name="sku.color"]').attr('id', 'txtColor_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('input[name="sku.quantity"]').attr('id', 'txtQuantity_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('div[name="valImg"]').attr('for', 'hidPicUrl_' + $index).children('span').remove();
		$prevObj.find('div[name="valColor"]').attr('for', 'txtColor_' + $index).children('span').remove();
		$prevObj.find('div[name="valQuantity"]').attr('for', 'txtQuantity_' + $index).children('span').remove();
		$prev.after($prevObj);
		setBtns();
		if ($plen <= 1) {
			var $del = '<i class="iconfont ml10 ggdelbtn">&#xe606;</i><i class="iconfont pl10 moveUp">&#xe600;</i><i class="iconfont pl10 moveDown">&#xe60d;</i>';
			$prevObj.append($del);
		}
		if ($plen + 1 >= 10) $(this).hide();
	});

	//删除规格描述项
	$(document).on('click', '.ggdelbtn', function() {
		var ilen = $(this).parents('#imgcontainer').find('.gg_container').length;
		if (ilen <= 2) {
			$(this).parent().siblings().find('.ggdelbtn').remove();
			$(this).parent().siblings().find('.moveUp').remove();
			$(this).parent().siblings().find('.moveDown').remove();
		}
		$(this).parent().remove();
		if (ilen <= 10) $('#addSpec').show();
	});
	//上传图片后hover
	$(document).on('mouseover', '.uploadImg', function() {
		var img = $(this).find('img').attr('src');
		if (img) {
			$(this).find('.mask').show();
		}
	});
	$(document).on('mouseout', '.uploadImg', function() {
		var img = $(this).find('img').attr('src');
		if (img) {
			$(this).find('.mask').hide();
		}
	});
}
// 输出产地内容
function printPlaceHTMLbyCache(key, trimEmpty) {
	if (trimEmpty && trimEmpty == true) {
		$('#placeList ul').empty();
	}
	$.each(CDT.placeSearchCache[key], function(index, obj) {
		$('#placeList ul').append("<li data-id='" + obj.id + "'>" + obj.name + "</li>");
	});
	$('#placeList').show();
}
// 输出品牌内容
function printBrandHTMLbyCache() {
	var kw = $('#brandInput').val();
	$('#brandList ul').empty();
	if (!kw || kw.length < 1) return;
	Tr.get('/dpl/brand/search', {
		'keyWord': kw
	}, function(data) {
		if (data.code != 200 || !data.results || data.results.length <= 0) return;
		$.each(data.results, function(index, obj) {
			var secondaryName = '';
			if(obj.secondaryName){
				secondaryName = "<span>／" + obj.secondaryName + "</span>";
			}
			$('#brandList ul').append("<li data-id='" + obj.id + "'><span>" + obj.name + "</span><div class='tip'><span>" + obj.name + "</span>"+secondaryName+"</div></li>");
		});
	}, {
		loadingMask: false
	});
}

$(function() {
	$(window).bind('beforeunload',function(){return '确认离开当前页面？您的商品编辑还未保存';});
	// 初始化PC宝贝详情编辑器
	CDT.ue = UE.getEditor('contentEditor', {
		initialFrameHeight: 300,
		autoWidth: false
	});

	CDT.ue.addListener('ready', function(editor) {
		$('#edui346_state').unbind();
		$('#edui346_body').unbind().find('.edui-box').html('');
		loadUpToken_u('edui346_body');
	});
	// 初始化数据
	initBase();
	loadUpToken();

	/*验证*/
	$('#btnSubmit').click(function() {
		// PC详情设置
		$('#itemDetail').val(CDT.ue.getContent());
		var validator = $('#formComm').validate(ValidateOpts());
		if (!validator.form()) {
			var height = $('span.error').eq(0).offset().top;
			$(window).scrollTop(height-30);
			return;
		}
		// skus
		var $gg_container = $('.gg_container');
		var setPicUrl = false;
		$.each($gg_container, function(index, elobj) {
			var color = $(elobj).find("input[name='sku.color']").eq(0).val(),
				img = $(elobj).find("input[name='sku.img']").eq(0).val(),
				quantity = $(elobj).find("input[name='sku.quantity']").eq(0).val();
			if (quantity && color && img) { // 价格相等,件数却不一样
				if (!setPicUrl) { // 首个作为主图片
					$('#formComm').append("<input type='hide' name='item.picUrl' value='" + img + "'/>");
					setPicUrl = true;
				}
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].color' value='" + color + "'/>");
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].img' value='" + img + "'/>");
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].quantity' value='" + quantity + "'/>");
			}
		});

		$(window).unbind('beforeunload');
		$('#formComm').submit();
	});
});

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
	$('.mask').each(function(i) {
		var uId = this.id;
		if (uId && $.inArray(uId, CDT.uploadIDSCache) == -1) {
			initItemMainPicUploader(uId);
			CDT.uploadIDSCache.push(uId);
		}
	});
}

function initItemMainPicUploader(domId) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.tusibaby.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = domId;
	option.container = 'imgcontainer';
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
			$('#' + domId).hide();
			//$('#formComm').valid();
			$('#' + domId).find('input').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
			$('div[name="valImg"]').children('span').remove();
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

function loadUpToken_u(ueid) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.tusibaby.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = ueid;
	option.container = 'PCcontainer';
	option.multi_selection = true;
	option.filters.max_file_size = '5mb';
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
			CDT.ue.execCommand('inserthtml', '<br/><img src="' + sourceLink + '"/>');

		},
		'Error': function(up, err, errTip) {
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
