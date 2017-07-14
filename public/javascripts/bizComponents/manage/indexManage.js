CDT = {
	token: null,
	uptoken: null,
	uploadImgCache: new Array(),
	homeSetId: null,
	bannerTemp: null,
	loadHomeSettingCache: null, // 首页配置信息缓存
	loadHomeSettingListCache: {}, // 首页配置信息列表缓存，
	loadedBannerCache: {},
	righttopTemp: null,
	loadedRighttoprCache: {},
	brandCache: {},
	brandTemp: null,
	activityTemp: null,
	uploadIDSCache: new Array(),
	loadedBrandCache: {}
};
//banner验证
function bannValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'homePageSet.bigBannerSetting.title': {
				required: true,
				maxlength: 30,
				minlength: 2
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//小banner验证
function topValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'homePageSet.right_SmallBannerSettig.title': {
				required: true,
				maxlength: 30,
				minlength: 2
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//block验证
function blockValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'comm.id': {
				required: true,
				digits: true
			},
			'comm.price': {
				required: true,
				number: true,
				min: 0
			}
		},
		success: function(label, element) {}
	}
	return options;
}

// 初始化内容
function initBase(argument) {
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	// 添加banner弹框
	$(document).on('click', '.addBanner', function() {
		var index = new Date().getTime();
		// 设置操作类型
		$("#editBanner input[name='homePageSet._id']").val(CDT.homeSetId);
		$('#editBanner input[name=modifyType]').val('add');
		$('#editBanner #frmEditBanner .title').text('添加Banner');
		$('#txtBannerTitle').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('#txtBannerUrl').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('.validateLine').children('span').remove();
		$('#frmEditBanner').find("input[type!='hidden']").val('');
		//$('#frmEditBanner').find('.loadbigImg').html('');
		var $cloneObj = $('#frmEditBanner .maskadd').remove().clone();
		$cloneObj.attr('id', 'T_adBanner_' + index).show();
		$cloneObj.attr('img-container', 'T_img_' + index);
		$cloneObj.find('input').attr('id', 'hidPicUrl_' + index).val('');
		$('#frmEditBanner .uploadImg .loadbigImg').attr('id', 'T_img_' + index).html('');
		$('#frmEditBanner .uploadImg').next().attr('for', 'hidPicUrl_' + index);
		$('#frmEditBanner .uploadImg').append($cloneObj);
		setBtns();
		$('#btnBannerdel').hide();
		Tr.popup('editBanner');
	});
	//修改banner弹框
	$(document).on('click', '.editbanner', function() {
		var index = $(this).parent().index(),
			title = $(this).data('title'),
			url = $(this).data('url'),
			imgSrc = $(this).find('img').attr('src'),
			domId = new Date().getTime();
		// 设置操作类型
		$("#editBanner input[name='homePageSet._id']").val(CDT.homeSetId);
		$('#editBanner input[name=modifyType]').val('update');
		$('#editBanner input[name=index]').val(index);
		$('#editBanner #txtBannerTitle').val(title);
		$('#editBanner #txtBannerUrl').val(url);
		var $cloneObj = $('#frmEditBanner .maskadd').remove().clone();
		$cloneObj.attr('id', 'T_adBanner_' + domId);
		$cloneObj.attr('img-container', 'T_img_' + domId);
		$cloneObj.find('input').attr('id', 'hidPicUrl_' + domId).val(imgSrc);
		$('#frmEditBanner .uploadImg .loadbigImg').attr({
			'id': 'T_img_' + domId
		}).empty().append("<img src='" + imgSrc + "'/>");
		$('#frmEditBanner .uploadImg').next().attr('for', 'hidPicUrl_' + domId);
		$('#frmEditBanner .uploadImg').append($cloneObj);
		setBtns();
		$('#editBanner #frmEditBanner .title').text('替换序号为' + index + '的Banner');
		$('#btnBannerdel').show();
		Tr.popup('editBanner');
	});
	// 确认大Banner保存
	$(document).on('click', '#btnBannerfirm', function() {
		var validator = $('#frmEditBanner').validate(bannValidateOpts());
		var $me = $(this);
		if (!validator.form()) {
			return;
		}
		var imgsrc = $('input[name="homePageSet.bigBannerSetting.imgUrl"]');
		if (imgsrc.val() == '' || !imgsrc.val()) {
			alert('请上传图片');
			return;
		}
		var $form = $('#frmEditBanner');
		var params = {
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'moudle': $form.find("input[name=moudle]").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.bigBannerSetting.title': $form.find("input[name='homePageSet.bigBannerSetting.title']").val(),
			'homePageSet.bigBannerSetting.url': $form.find("input[name='homePageSet.bigBannerSetting.url']").val(),
			'homePageSet.bigBannerSetting.imgUrl': $form.find("input[name='homePageSet.bigBannerSetting.imgUrl']").val()
		};
		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('更新成功！');
			loadHomeSetting('loadBannerList');
			$me.parents('.popWrapper').hide();
			loadBannerList();
		});
	});
	// 删除大Banner
	$(document).on('click', '#btnBannerdel', function() {
		var $form = $('#frmEditBanner');
		var $me = $(this);
		if (!confirm('确定删除该图片吗？')) {
			return;
		}
		Tr.post('/sys/homePageSetting/delete', {
			'moudle': $form.find("input[name=moudle]").val(),
			'id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'authenticityToken': $form.find("input[name=authenticityToken]").val(),
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功！');
			loadHomeSetting('loadBannerList');
			$me.parents('.popWrapper').hide();
		});
	});

	//添加右上角小banner弹框
	$(document).on('click', '.addRighttop', function() {
		var index = new Date().getTime();
		$("#editRighttop input[name='homePageSet._id']").val(CDT.homeSetId);
		$('#editRighttop input[name=modifyType]').val('add');
		$('#editRighttop #frmEditRighttop .title').text('添加右上角小banner');
		$('#txtRighttopTitle').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('#txtRighttopUrl').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('.validateLine').children('span').remove();
		var $cloneObj = $('#frmEditRighttop .maskadd').remove().clone();
		$cloneObj.attr('id', 'T_adBanner_a_' + index).show();
		$cloneObj.attr('img-container', 'T_img_a_' + index);
		$cloneObj.find('input').attr('id', 'hidPicUrl_' + index).val('');
		$('#frmEditRighttop .uploadImg .loadbigImg').attr('id', 'T_img_a_' + index).html('');
		$('#frmEditRighttop .uploadImg').next().attr('for', 'hidPicUrl_a_' + index);
		$('#frmEditRighttop .uploadImg').append($cloneObj);
		setBtns();
		$('#btnRighttopdel').hide();
		Tr.popup('editRighttop');
	});
	//修改右上角小banner弹框
	$(document).on('click', '.editrighttop', function() {
		var index = $(this).parent().index(),
			title = $(this).data('title'),
			url = $(this).data('url'),
			imgSrc = $(this).find('img').attr('src'),
			domId = new Date().getTime();
		$('#editRighttop #txtRighttopTitle').val(title);
		$('#editRighttop #txtRighttopUrl').val(url);
		$('#editRighttop input[name=index]').val(index);
		$("#editRighttop input[name='homePageSet._id']").val(CDT.homeSetId);
		$('#editRighttop input[name=modifyType]').val('update');
		$('#editRighttop #frmEditRighttop .title').text('替换序号为' + index + '的小banner');

		var $cloneObj = $('#frmEditRighttop .maskadd').remove().clone();
		$cloneObj.attr('id', 'T_adBanner_' + domId);
		$cloneObj.attr('img-container', 'T_img_' + domId);
		$cloneObj.find('input').attr('id', 'hidPicUrl_' + domId).val(imgSrc);
		$('#frmEditRighttop .uploadImg .loadbigImg').attr({
			'id': 'T_img_' + domId
		}).empty().append("<img src='" + imgSrc + "'/>");
		$('#frmEditRighttop .uploadImg').next().attr('for', 'hidPicUrl_' + domId);
		$('#frmEditRighttop .uploadImg').append($cloneObj);
		setBtns();
		$('#btnRighttopdel').show();
		Tr.popup('editRighttop');
	});
	// 确认小banner保存
	$(document).on('click', '#btnRighttopfirm', function() {
		var validator = $('#frmEditRighttop').validate(topValidateOpts());
		var $me = $(this);
		if (!validator.form()) {
			return;
		}
		var imgsrc = $('input[name="homePageSet.right_SmallBannerSettig.imgUrl"]');
		if (imgsrc.val() == '' || !imgsrc.val()) {
			alert('请上传图片');
			return;
		}
		var $form = $('#frmEditRighttop');
		var params = {
			'moudle': $form.find("input[name=moudle]").val(),
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.right_SmallBannerSettig.title': $form.find("input[name='homePageSet.right_SmallBannerSettig.title']").val(),
			'homePageSet.right_SmallBannerSettig.url': $form.find("input[name='homePageSet.right_SmallBannerSettig.url']").val(),
			'homePageSet.right_SmallBannerSettig.imgUrl': $form.find("input[name='homePageSet.right_SmallBannerSettig.imgUrl']").val()
		};
		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('更新成功！');
			loadHomeSetting('loadRighttopList');
			$me.parents('.popWrapper').hide();
			loadBannerList();
		});
	});
	// 删除小banner
	$(document).on('click', '#btnRighttopdel', function() {
		var $form = $('#frmEditRighttop');
		var $me = $(this);
		if (!confirm('确定删除该图片吗？')) {
			return;
		}
		Tr.post('/sys/homePageSetting/delete', {
			'moudle': $form.find("input[name=moudle]").val(),
			'id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'authenticityToken': $form.find("input[name=authenticityToken]").val(),
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功！');
			loadHomeSetting('loadRighttopList');
			$me.parents('.popWrapper').hide();
		});
	});

	// 添加品牌弹框
	$(document).on('click', '.addBrand', function() {
		$('.brandList ul').empty();
		$("#frmEditBrand input[name='homePageSet._id']").val(CDT.homeSetId);
		$("#frmEditBrand input[name='modifyType']").val('add');
		$('.branditem').removeClass('active');
		if (CDT.brandCache && CDT.brandCache.length > 0) {
			$.each(CDT.brandCache, function(index, obj) {
				$('.brandList ul').append("<li class='branditem' bimg='" + obj.picUrl + "' data-bid='" + obj.id + "'>" + obj.name + "</li>");
			});
		} else {
			Tr.get('/dpl/brand/all', {}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.brandCache = data.results;
				$.each(data.results, function(index, obj) {
					$('.brandList ul').append("<li class='branditem' bimg='" + obj.picUrl + "' data-bid='" + obj.id + "'>" + obj.name + "</li>");
				});
			}, {
				loadingMask: false
			});
		}
		$('#btnBranddel').hide();
		Tr.popup('editBrand');
	});
	// 修改品牌弹窗
	$(document).on('click', '.editBrand', function() {
		$('.branditem').removeClass('active');
		$('.brandList ul').empty();
		var index = $(this).parent().index();
		$('#frmEditBrand input[name=index]').val(index);
		$("#frmEditBrand input[name='homePageSet._id']").val(CDT.homeSetId);
		$('#frmEditBrand input[name=modifyType]').val('update');
		if (CDT.brandCache && CDT.brandCache.length > 0) {
			$.each(CDT.brandCache, function(index, obj) {
				$('.brandList ul').append("<li class='branditem' bimg='" + obj.picUrl + "' data-bid='" + obj.id + "'>" + obj.name + "</li>");
			});
		} else {
			Tr.get('/dpl/brand/all', {}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.brandCache = data.results;
				$.each(data.results, function(index, obj) {
					$('.brandList ul').append("<li class='branditem' bimg='" + obj.picUrl + "' data-bid='" + obj.id + "'>" + obj.name + "</li>");
				});
			}, {
				loadingMask: false
			});
		}
		$('#btnBranddel').show();
		$('#btnBrandfirm').text('修改');
		Tr.popup('editBrand');
	});
	// 品牌配置确认保存
	$(document).on('click', '#btnBrandfirm', function() {
		var bid = $('.brandList').find('.active').data('bid');
		$('#brandSelects').val(bid);
		var $me = $(this);
		var $form = $('#frmEditBrand');
		var params = {
			'moudle': $form.find("input[name=moudle]").val(),
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.brandSetting.bid': $form.find("input[name='homePageSet.brandSetting.bid']").val(),
			'homePageSet.brandSetting.imgUrl': $form.find("input[name='homePageSet.brandSetting.imgUrl']").val()
		};
		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('操作成功！');
			loadHomeSetting('loadBrandList');
			$me.parents('.popWrapper').hide();
		});
	});
	// 删除品牌配置
	$(document).on('click', '#btnBranddel', function() {
		var $form = $('#frmEditBrand');
		var $me = $(this);
		if (!confirm('确定删除该品牌吗？')) {
			return;
		}
		Tr.post('/sys/homePageSetting/delete', {
			'moudle': $form.find("input[name=moudle]").val(),
			'id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'authenticityToken': $form.find("input[name=authenticityToken]").val(),
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功！');
			loadHomeSetting('loadBrandList');
			$me.parents('.popWrapper').hide();
		});
	});

	// 品牌选择
	$(document).on('click', '.branditem', function() {
		var img = $(this).attr('bimg');
		$('#txtBrandImg').val(img);
		$(this).addClass('active');
		$(this).siblings().removeClass('active');
	});


	// 修改活动配置图片
	$(document).on('click', '.editleftimg', function() {
		var index = $(this).parent().index(),
			type = $(this).parent().data('type'),
			url = $(this).data('url') == 'undefined' ? '' : $(this).data('url'),
			imgSrc = $(this).find('img').attr('src');
		if (type && type == 'add') {
			url = '';
			imgSrc = '';
			$('.loadbigImg').find('img').attr('src', '');
		}
		$('#editLeftimg #txtLeftimgUrl').val(url).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('.validateLine').children('span').remove();
		var $maskadd = $('#frmEditLeftimg .maskadd').remove().clone();
		$maskadd.attr('id', 'T_adBanner_b_' + index).show();
		$maskadd.attr('img-container', 'T_img_b_' + index);
		$maskadd.find('input').attr('id', 'hidPicUrl_b_' + index).val(imgSrc);
		$('#frmEditLeftimg .uploadImg').append($maskadd);
		$('#editLeftimg').find('.loadbigImg').attr('id', 'T_img_b_' + index);
		$('#editLeftimg').find('.uploadImg').next().attr('for', 'hidPicUrl_b_' + index);
		$('#editLeftimg .loadbigImg').empty().html('<img src="' + imgSrc + '" />');
		$('#editLeftimg').data('index', index);
		$('#editLeftimg').data('type', type);
		setBtns();
		Tr.popup('editLeftimg');
	});
	// 修改活动商品弹框
	$(document).on('click', '.itemSet', function() {
		var $me = $(this);
		var $parent = $(this).parents('.activityContainer').eq(0),
			id = $me.data('id') == 0 ? '' : $me.data('id'),
			price = $me.data('price') == 0 ? '' : $me.data('price');
		var index = $parent.index(),
			itemIndex = $(this).index(),
			type = $parent.data('type');
		if (type && type == 'add') {
			id = '';
			price = '';
		}
		$('#editBlock #txtCommId').val(id).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('#editBlock #txtBeforePrice').val(price).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$('.validateLine').children('span').remove();
		$('#editBlock').data('index', index);
		$('#editBlock').data('itemIndex', itemIndex);
		$('#editBlock').data('type', type);
		Tr.popup('editBlock');
	});
	// 确认活动配置
	$(document).on('click', '#btnLeftimgfirm', function() {
		//var validator = $('#frmEditLeftimg').validate(leftValidateOpts());
		var $me = $(this);
		var index = $('#editLeftimg').data('index');
		var type = $('#editLeftimg').data('type');
		if ($('input[name="leftImg"]').val() == '' || !$('input[name="leftImg"]').val()) {
			alert('请上传图片')
			return;
		}
		if (isNaN(index)) return;
		// 清空原有的非公共数据
		var $dataForm = $('#frmEditActivity .data-no-pub');
		$dataForm.empty();
		var $actSet = $('#activityList .activityContainer').eq(index);
		var leftimg = $('#addLeftimgWrapper input[name=leftImg]').val();
		var leftimgUrl = $('#addLeftimgWrapper input[name=leftimgUrl]').val();
		var ispublic = $actSet.find('input[name=is_public]').prop('checked');
		// 处理index
		$("#frmEditActivity input[name='index']").val(index);
		$("#frmEditActivity input[name='homePageSet._id']").val(CDT.homeSetId);
		$("#frmEditActivity input[name='homePageSet.activitySetting.isPublic']").val(ispublic);
		if (type && type == 'add') {
			$("#frmEditActivity input[name='modifyType']").val('add');
		} else {
			$("#frmEditActivity input[name='modifyType']").val('update');
		}
		// 处理商品列表配置项
		var $form = $('#frmEditActivity');
		var params = {
			'moudle': $form.find("input[name=moudle]").val(),
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.activitySetting.isPublic': ispublic,
			'homePageSet.activitySetting.imgUrl': leftimg,
			'homePageSet.activitySetting.url': leftimgUrl
		};
		var itemSets = $actSet.find('.itemSet');
		if (itemSets && itemSets.length > 0) {
			$.each(itemSets, function(index, object) {
				var id = $(object).data('id');
				var price = $(object).data('price');
				if (id && price) {
					params['homePageSet.activitySetting.itemSettings['+index+'].id']=id;
					params['homePageSet.activitySetting.itemSettings['+index+'].price']=price;
				}
			});
		}
		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('保存成功！');
			loadHomeSetting('loadActivityList');
			$me.parents('.popWrapper').hide();
		});
	});
	// 确认item右侧block保存
	$(document).on('click', '#btnBlockfirm', function() {
		var validator = $('#frmEditBlock').validate(blockValidateOpts());
		var index = $('#editBlock').data('index');
		var itemIndex = $('#editBlock').data('itemIndex');
		var type = $('#editBlock').data('type');
		var $me = $(this);
		if (!validator.form() || isNaN(index) || isNaN(itemIndex)) {
			return;
		}
		// 清空原有的非公共数据
		var $dataForm = $('#frmEditActivity .data-no-pub');
		$dataForm.empty();
		var $actSet = $('#activityList .activityContainer').eq(index);
		var leftimg = $actSet.find('.editleftimg img').attr('src');
		var leftimgUrl = $actSet.find('.editleftimg').data('url');
		var ispublic = $actSet.find('input[name=is_public]').prop('checked');

		// 处理index
		$("#frmEditActivity input[name='index']").val(index);
		$("#frmEditActivity input[name='homePageSet.activitySetting.imgUrl']").val(leftimg);
		$("#frmEditActivity input[name='homePageSet.activitySetting.url']").val(leftimgUrl);
		$("#frmEditActivity input[name='homePageSet._id']").val(CDT.homeSetId);
		$("#frmEditActivity input[name='homePageSet.activitySetting.isPublic']").val(ispublic);
		if (type && type == 'add') {
			$("#frmEditActivity input[name='modifyType']").val('add');
		} else {
			$("#frmEditActivity input[name='modifyType']").val('update');
		}
		// 处理当前活动的配置项
		var $form = $('#frmEditActivity');
		var params = {
			'moudle': $form.find("input[name=moudle]").val(),
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.activitySetting.isPublic': ispublic,
			'homePageSet.activitySetting.imgUrl': leftimg,
			'homePageSet.activitySetting.url': leftimgUrl
		};
		// 处理商品列表配置项
		// 处理当前
		var id = $('#editBlock #txtCommId').val();
		var price = $('#editBlock #txtBeforePrice').val();
		if (id && price) {
			$dataForm.append("<input type='hidden' name='homePageSet.activitySetting.itemSettings[" + itemIndex + "].id' value='" + id + "' />");
			$dataForm.append("<input type='hidden' name='homePageSet.activitySetting.itemSettings[" + itemIndex + "].price' value='" + price + "' />");
			params['homePageSet.activitySetting.itemSettings['+itemIndex+'].id']=id;
			params['homePageSet.activitySetting.itemSettings['+itemIndex+'].price']=price;
		}
		var itemSets = $('#activityList .activityContainer').eq(index).find('.itemSet');
		if (itemSets && itemSets.length > 0) {
			$.each(itemSets, function(index, object) {
				var id = $(object).data('id') ? $(object).data('id') : '';
				var price = $(object).data('price') ? $(object).data('price') : '';
				if (index != itemIndex) {
					params['homePageSet.activitySetting.itemSettings['+index+'].id']=id;
					params['homePageSet.activitySetting.itemSettings['+index+'].price']=price;
				}
			});
		}

		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}

			loadHomeSetting('loadActivityList');
			alert('保存成功！');
			$me.parents('.popWrapper').hide();
		});
	});
	// 活动是否可见设置
	$(document).on('change', '.activityIsPublic', function() {
		var index = $(this).parents('.activityContainer').eq(0).index();
		var type = $(this).parents('.activityContainer').eq(0).data('type');
		var $me = $(this);
		if (isNaN(index)) {
			return;
		}
		// 清空原有的非公共数据
		var $dataForm = $('#frmEditActivity .data-no-pub');
		$dataForm.empty();
		var $actSet = $('#activityList .activityContainer').eq(index);
		var leftimg = $actSet.find('.editleftimg img').attr('src');
		var leftimgUrl = $actSet.find('.editleftimg').data('url');
		var ispublic = $actSet.find('input[name=is_public]').prop('checked');

		// 处理index
		$("#frmEditActivity input[name='index']").val(index);
		$("#frmEditActivity input[name='homePageSet._id']").val(CDT.homeSetId);
		$("#frmEditActivity input[name='homePageSet.activitySetting.isPublic']").val(ispublic);
		if (type && type == 'add') {
			$("#frmEditActivity input[name='modifyType']").val('add');
		} else {
			$("#frmEditActivity input[name='modifyType']").val('update');
		}
		// 处理商品列表配置项
		var itemSets = $('#activityList .activityContainer').eq(index).find('.itemSet');
		var $form = $('#frmEditActivity');
		var params = {
			'moudle': $form.find("input[name=moudle]").val(),
			'homePageSet._id': $form.find("input[name='homePageSet._id']").val(),
			'index': $form.find("input[name=index]").val(),
			'modifyType': $form.find("input[name=modifyType]").val(),
			'homePageSet.activitySetting.isPublic': ispublic,
			'homePageSet.activitySetting.imgUrl': leftimg,
			'homePageSet.activitySetting.url': leftimgUrl
		};
		if (itemSets && itemSets.length > 0) {
			$.each(itemSets, function(index, object) {
				var id = $(object).data('id') ? $(object).data('id') : '';
				var price = $(object).data('price') ? $(object).data('price') : '';
				params['homePageSet.activitySetting.itemSettings['+index+'].id']=id;
				params['homePageSet.activitySetting.itemSettings['+index+'].price']=price;
			});
		}

		Tr.post('/sys/homePageSetting/modify', params, function(data) {
			if (data.code != 200) {
				$me.prop('checked', false);
			}
		});
	});
	// 添加活动配置
	$(document).on('click', '.addactivity', function() {
		var $list = $('.activityContainer').first().clone();
		$list.data('type', 'add');
		$list.find('.leftimg img').attr('src', '');
		$list.find('.block img').attr('src', '');
		$list.find('.activityIsPublic').prop('checked', false);
		$list.find('.editleftimg').removeAttr('data-url');
		$list.find('.itemSet').removeAttr('data-id');
		$list.find('.itemSet').removeAttr('data-price');
		$("#activityList").append($list);
	});

	// 模板鼠标悬停
	$(document).on('mouseenter', '#homePageSettings li .styleType', function() {
			$me = $(this);
			$next = $me.next();
			$next.show();
		})
		// 模板鼠标移出
	$(document).on('mouseleave', '#homePageSettings li', function() {
			$(this).find('.mask').hide();
		})
		// 编辑或查看模板
	$(document).on('click', '.homePageSetting', function() {
		var isActive = $(this).parent().parent().siblings('.styleType').hasClass('active');
		var id = $(this).parent().parent().data('id');
		if (!id) {
			return;
		}
		CDT.loadHomeSettingCache = CDT.loadHomeSettingListCache[id];
		CDT.homeSetId = id;
		loadHomeSettingByCache();
		if (!isActive) {
			$(this).parent().parent().prev().addClass('addcolor');
		}
		$(this).parents('li').siblings().find('.styleType').removeClass('addcolor');
	});
	// 删除模板
	$(document).on('click', '.delBtn', function() {
		var id = $(this).parent().parent().data('id');
		if (!confirm('确认删除该配置信息吗？') || !id) {
			return;
		}
		Tr.post('/sys/homePageSetting/deleteWId', {
			'id': id,
			'authenticityToken': $('.styleTypelist input[name=authenticityToken]').val()
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功！');
			// 加载主页配置信息列表
			loadHomeSettingList();
		});
	});
	// 添加模板
	$(document).on('click', '.addStyleType', function() {
		var title = prompt("请输入模板名称");
		if (title && title != "") {
			Tr.post('/sys/homePageSetting/create', {
				'name': title,
				'authenticityToken': $('.styleTypelist input[name=authenticityToken]').val()
			}, function(data) {
				if (data.code != 200) {
					var msg = "添加失败";
					if (data.code == 8001) {
						msg = "模板名称相同，添加失败";
					}
					alert(msg);
					return;
				}
				alert('添加成功！');
				// 加载主页配置信息列表
				loadHomeSettingList();
			});
		}
	});
	// 应用当前选中配置
	$(document).on('click', '.useBtn', function() {
		var active = $(this).parent().prev().hasClass('active');
		var $me = $(this);
		var id = $(this).parent().data('id');
		if (!confirm('确认应用当前选中配置？') || !id) {
			return;
		}
		Tr.post('/sys/homePageSetting/useWId', {
			'id': id,
			'authenticityToken': $('.styleTypelist input[name=authenticityToken]').val()
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('应用成功！');
			// 加载当前最新应用信息
			loadBuildSetting();
			// 加载主页配置信息列表
			loadHomeSettingList();
			$me.parent().prev().addClass('active');
		});
	});
}
$(function() {

	CDT.bannerTemp = $('#bannerTemp').remove().val();
	Mustache.parse(CDT.bannerTemp);
	CDT.righttopTemp = $('#righttopTemp').remove().val();
	Mustache.parse(CDT.righttopTemp);
	CDT.brandTemp = $('#brandTemp').remove().val();
	Mustache.parse(CDT.brandTemp);
	CDT.activityTemp = $('#activityTemp').remove().val();
	Mustache.parse(CDT.activityTemp);
	initBase();
	// 加载主页配置信息列表
	loadHomeSettingList();
	// 加载可选品牌列表
	loadBrandListForSelect();
	// 加载上传凭证
	loadUpToken();
});

// 设置当前为查看模式 // role = true 只能查看
function setLookRole(role) {
	// 是否可编辑为false
	CDT.editCurrent = !role;
	// 活动是否可见 只读
	if (role) {
		$('#homecontent').addClass('homecontent_look');
	} else {
		$('#homecontent').removeClass('homecontent_look');
	}
}

// 加载当前主页配置信息列表
function loadHomeSettingList() {
	Tr.get('/sys/homePageSetting/list', {}, function(data) {
		if (data.code == 200) {
			$('#homePageSettings').empty();
			// print 模板列表
			$.each(data.results, function(index, jsonObj) {
				CDT.loadHomeSettingListCache[jsonObj._id] = jsonObj;
				var $block = $('<div class="styleType">' + jsonObj.name + '</div>');
				var $divItem = $('<div class="mask"><p class="useBtn">应用</p></div>');
				$divItem.data('id', jsonObj._id);
				// 状态
				if (jsonObj.isUse) {
					CDT.homeSetId = jsonObj._id;
					CDT.loadHomeSettingCache = jsonObj;
					loadHomeSettingByCache();
					$block.addClass('active');
					$divItem.append('<p class="controlbtns"><i class="iconfont homePageSetting" style="font-size:20px">&#xe62f;</i></p>');
				} else {
					$divItem.append('<p class="controlbtns"><i class="iconfont homePageSetting" style="font-size:20px">&#xe62f;</i><i class="iconfont delBtn">&#xe631;</i></p>');
				}
				var $liItem = $('<li></li>').append($block);
				$liItem.append($divItem);
				$('#homePageSettings').append($liItem);
			});
		}
	});
}

// 加载当前缓存中的配置信息
function loadHomeSettingByCache() {
	// 显示标题
	var tempName = !CDT.loadHomeSettingCache || !CDT.loadHomeSettingCache.name ? 'xxx' : CDT.loadHomeSettingCache.name;
	$('#editTempName').text(tempName);
	loadSettingStatus();
	loadBannerList();
	loadRighttopList();
	loadBrandList();
	loadActivityList();
}

// 加载当前模板和已应用模板相较于的状态
function loadSettingStatus() {
	if (CDT.loadHomeSettingCache && CDT.loadHomeSettingCache.isUse) { // 已被应用
		loadBuildSetting();
	} else {
		$('#editTempStatus').text("未应用");
	}
}

// 加载最新应用的主页设置信息
function loadBuildSetting() {
	Tr.get('/sys/homePageSetting/build', {}, function(data) {
		if (data.code == 200) {
			var setting = data.results;
			if (!setting) return;
			// 显示状态
			var tempStauts = '已应用模板设置于当前编辑模板设置保持一致';
			var updateTime = CDT.loadHomeSettingCache.updateTime;
			var newSetUseTime = setting.createTime;
			if (updateTime && newSetUseTime) {
				var dt1 = Date.parse(updateTime);
				var dt2 = Date.parse(newSetUseTime);
				if (dt1 > dt2) {
					tempStauts = "已应用模板设置落后于当前编辑模板设置";
				}
			}
			$('#editTempStatus').text(tempStauts);
		}
	});
}
// 加载当前的配置信息
function loadHomeSetting(moubleName) {
	Tr.get('/sys/homePageSetting/query', {
		'sid': CDT.homeSetId
	}, function(data) {
		if (data.code == 200) {
			// 更新当前主页配置缓存信息
			CDT.loadHomeSettingCache = data.results;
			CDT.loadHomeSettingListCache[CDT.homeSetId] = CDT.loadHomeSettingCache;
			loadSettingStatus();
			if (moubleName == '') {
				loadBannerList();
				loadRighttopList();
				loadBrandList();
				loadActivityList();
			} else if (moubleName == 'loadBannerList') {
				loadBannerList();
			} else if (moubleName == 'loadRighttopList') {
				loadRighttopList();
			} else if (moubleName == 'loadBrandList') {
				loadBrandList();
			} else if (moubleName == 'loadActivityList') {
				loadActivityList();
			}
		}
	});
}

// 加载所有品牌内容
function loadBrandListForSelect() {
	Tr.get('/dpl/brand/all', {}, function(data) {
		if (data.code == 200) {
			var $obj = $('#brandSelects');
			$.each(data.results, function(index, brand) {
				$obj.append("<option value='" + brand.id + "'>" + brand.name + "</option>");
			});
			$obj.children().eq(0).attr('selected', true);
		}
	}, {
		loadingMask: false
	});
}

// 加载big_banner列表
function loadBannerList() {
	// 输出到html
	var output = Mustache.render(CDT.bannerTemp, $.extend(CDT.loadHomeSettingCache, {}));
	$('#bannerContainer').html(output);
}

// 加载小banner列表
function loadRighttopList() {
	// 输出到html
	var output = Mustache.render(CDT.righttopTemp, $.extend(CDT.loadHomeSettingCache, {}));
	$('#righttopContainer').html(output);
}

// 加载品牌列表
function loadBrandList() {
	var output = Mustache.render(CDT.brandTemp, $.extend(CDT.loadHomeSettingCache, {}));
	$('#brandContainer').html(output);
}

// 加载活动列表
function loadActivityList() {
	var activitySettings = {
		'activitySettings': !CDT.loadHomeSettingCache.activitySettings ? new Array() : CDT.loadHomeSettingCache.activitySettings
	};
	if (activitySettings.activitySettings.length <= 0) {
		activitySettings.activitySettings.push({
			'url': '',
			'imgUrl': '',
			'itemSettings': new Array()
		});
	}
	var output = Mustache.render(CDT.activityTemp, $.extend(activitySettings, {
		formatItemSettings: function() {

				if (!this.itemSettings) {
					this.itemSettings = new Array();
				}
				if (this.itemSettings.length < 3) {
					var needCount = 3 - this.itemSettings.length;
					for (var i = 0; i < needCount; i++) {
						this.itemSettings.push({
							id: '',
							img: '',
							price: ''
						});
					}
				}
				var nitemSettings = new Array();
				$.each(this.itemSettings, function(index, obj) {
					var np = obj.price ? parseInt(obj.price) / 100 : '';
					nitemSettings.push({
						id: obj.id,
						img: obj.img,
						price: np
					});
				});

				return nitemSettings;
			}
			// unedit: function() {
			// 	return !CDT.editCurrent
			// }
	}));
	$('#activityList').html(output);
	// 活动商品配置内容补充
}


function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		setBtns();
	});
}

function setBtns() {
	$('.maskadd').each(function(i) {
		var uId = this.id;
		if (uId && uId != '') initItemMainPicUploader(uId);
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
			var $dom = $('#' + domId);
			var imgId = $dom.attr('img-container');
			/*添加图片*/
			$dom.find('input').eq(0).val(sourceLink);
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
				// // alert('请不要上传重复的图片');
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