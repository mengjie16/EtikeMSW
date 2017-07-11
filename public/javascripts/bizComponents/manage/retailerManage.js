CDT = {
	token: null,
	uptoken: null,
	regUserTemp: null,
	loadedRegCache: {},
	currPageNo: 1,
	pageSize: 10
};
//编辑零售商的表单验证
function ValidateOpts_ret() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'edit.name': {
				required: true,
				username: true
			},
			'edit.shopUrl': {
				// required: true,
				 url: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//添加零售商的表单验证
function ValidateNewAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'add.name': {
				required: true,
				username: true
			},
			'add.shopUrl': {
				// required: true,
				 url: true
			},
			'add.channel': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
function initBase(argument) {
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//零售商复选框点击文字选中
	$('.channel').each(function() {
		var index = $(this).index();
		$(this).find('input').attr('id', index);
		$(this).find('label').attr('for', index);
	});
	$.each($('.channels'),function(index,obj) {
		$(obj).find('input').attr('id', index+'edit');
		$(obj).find('label').attr('for', index+'edit');
	});
	//修改零售商
	$(document).on('click', '.editbtn', function() {
		var id = $(this).data('id');
		$('#hidRetFormId').val(id);
		$('input[type="checkbox"]').removeAttr('checked');
		$.each(CDT.loadedRegCache, function(i, n) {
			if (n.id == id) {
				$('#editName').val(n.name);
				$('#editShopUrl').val(n.shopUrl);
				$('#editNote').val(n.note);
				$.each($('input[type="checkbox"]'),function(index,obj){
					$.each(n.channel.split(','),function(a,b){
						if($(obj).val()==b){
							$(obj).attr('checked','checked');
						}
					});
				})
			}
		});
		var list1 = [];
		$.each($('input[name="edit.channels"]:checked'), function(index, obj) {
			list1.push($(obj).val());
		});
		$('input[name="edit.channel"]').val(list1.join(','));
		$('.validateLine').children('span').remove();
		Tr.popup('editRegist');
	});
	// 关键字查询商品
	$(document).on('click', '#searchBtn', function() {
		loadRegList(1);
	});
	// 删除选中
	$(document).on('click', '.delbtn', function() {
		var id = $(this).data('id');
		if (!confirm('确定删除该零售商吗？')) {
			return;
		}
		Tr.post('/sys/retailer/remove', {
			id: id
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功');
			loadRegList(CDT.currPageNo);
		}, {
			loadingMask: false
		});
	});
	//选择渠道
	$(document).on('change','input[type="checkbox"]',function(){
		var list1 = [];
		$.each($('input[name="edit.channels"]:checked'), function(index, obj) {
			list1.push($(obj).val());
		});
		$('input[name="edit.channel"]').val(list1.join(','));
		var list2 = [];
		$.each($('input[name="add.channels"]:checked'), function(index, obj) {
			list2.push($(obj).val());
		});
		$('input[name="add.channel"]').val(list2.join(','));
	});
	// 添加零售商弹窗
	$(document).on('click', '#registNew', function() {
		clearFrom($('.trControl'));
		$('input[name="add.channels"]').removeAttr('checked');
		Tr.popup('registeUser');
	});
	//阻止回车自动提交表单
	$('input[type=text]').keypress(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
		}
	});
	// 保存添加零售商信息
	var validator1 = $('#frmRegistRetailer').validate(ValidateNewAddOpts());
	$('#retAddWnd').trForm({
		before: function() {
			var boxes = $('input[name="add.channels"]:checked').length;
			if (boxes <= 0) {
				$('.checkboxval').html('<span class="errortxt">请至少选择一种渠道</span>');
				return false;
			} else {
				$('span.errortxt').remove();
			}
			return validator1.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				return;
			}
			alert('添加成功');
			$('.popWrapper').hide();
			clearFrom($('.trControl'));
			$('html').removeClass('overflow-hidden');
			loadRegList(1);
		}
	});
	// 保存编辑零售商信息
	var validator2 = $('#frmEdit_ret').validate(ValidateOpts_ret());
	$('#editRetWnd').trForm({
		before: function() {
			var boxes = $('input[name="edit.channels"]:checked').length;
			if (boxes <= 0) {
				$('.checkboxval').html('<span class="errortxt">请至少选择一种渠道</span>');
				return false;
			} else {
				$('span.errortxt').remove();
			}
			return validator2.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				return;
			}
			alert('修改成功');
			$('.popWrapper').hide();
			$('html').removeClass('overflow-hidden');
			loadRegList(CDT.currPageNo);
		}
	});
}
$(function() {
	CDT.regUserTemp = $('#regUserTemp').remove().val();
	Mustache.parse(CDT.regUserTemp);
	initBase();
	loadRegList(1);
});
//load零售商列表
function loadRegList(pageNo) {
	var paramObj = {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.name': $('#user_name').val()
	};
	Tr.get('/sys/retailer/query', paramObj, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedRegCache = data.results;
		var output = Mustache.render(CDT.regUserTemp, $.extend(data, {}));
		$('#regContainer').html(output);
		if (data.totalCount <= 0) {
			$('.pagination').hide();
			return;
		}
		$('.pagination').show();
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadRegistCallBack,
			callback_run: false,
			prev_text: ' '
		});
		CDT.currPageNo = pageNo;
	}, {
		loadingMask: false
	});
}

function loadRegistCallBack(index, jq) {
	loadRegList(index + 1);
}
function clearFrom(obj){
	obj.filter('[tr-param]').val('').removeAttr('checked').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	$('.validateLine').children('span').remove();

}