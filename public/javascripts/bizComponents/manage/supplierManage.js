CDT = {
	token: null,
	uptoken: null,
	regUserTemp: null,
	loadedRegCache: null,
	currPageNo: 1,
	pageSize: 20
};
// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
//编辑供应商的表单验证
function ValidateOpts_ret() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'edit.name': {
				required: true,
				username: true
			},
			'edit.product': {
				required: true
			},
			'edit.countryId': {
				required: true
			},
			'edit.provinceId': {
				required: true
			},
			'edit.address': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//添加供应商的表单验证
function ValidateNewAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'add.name': {
				required: true,
				username: true
			},
			'add.product': {
				required: true
			},
			'add.countryId': {
				required: true
			},
			'add.provinceId': {
				required: true
			},
			'add.address': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
function initBase(argument) {
	var validator1, validator2, validator3, validator4, validator5, validator6;
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//修改注册用户
	$(document).on('click', '.editbtn', function() {
		var id = $(this).data('id');
		$('#hidRetFormId').val(id);
		$.each(CDT.loadedRegCache, function(i, n) {
			if (n.id == id) {
				$('#editName').val(n.name);
				$('#editProduct').val(n.product);
				$('#editCountry_id').val(n.countryId);
				$('#editCountry').val(n.country);
				$('#editProvince_id').val(n.provinceId);
				$('#editProvince').val(n.province);
				$('#editCity').append('<option class="addArs" selected="true">'+n.city+'</option>');
				$('#editRegion').append('<option class="addArs" selected="true">'+n.region+'</option>');
				$('#editAddress').val(n.address);
				$('#editType').val(n.type);
				$('#editDomain').val(n.domain);
				$('#editNote').val(n.note);
			}
		});
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
		if (!confirm('确定删除该供应商吗？')) {
			return;
		}
		Tr.post('/sys/supplier/remove', {
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
	// 添加注册用户弹窗
	$(document).on('click', '#registNew', function() {
		clearFrom($('.trControl'));
		Tr.popup('registeUser');
	});
	//阻止回车自动提交表单
	$('input[type=text]').keypress(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
		}
	});
	// 保存添加供应商用户信息
	var validator1 = $('#frmRegistSupplier').validate(ValidateNewAddOpts());
	$('#addSupWnd').trForm({
		before: function() {
			return validator1.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				return;
			}
			alert('注册成功');
			$('.popWrapper').hide();
			clearFrom($('.trControl'));
			$('html').removeClass('overflow-hidden');
			loadRegList(1);
		}
	});
	// 保存编辑供应商用户信息
	var validator2 = $('#frmEdit_sup').validate(ValidateOpts_ret());
	$('#editSupWnd').trForm({
		before: function() {
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
	loadProviceHTML();
	// 选择国家
	$(document).on('change', '.country', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			$(this).prev().val('');
			return;
		}
		if ($(this).val() != '中国') {
			$('.pro_city_region').hide();
			$('.pro_city_region select').hide().eq(0).attr('selected', true);
		} else {
			$('.pro_city_region').show();
			$('.pro_city_region select').show();
		}
		var $me = $(this);
		$me.prev().val(id);
		var $province = $me.parents('.form_line').next().find('.province');
		$province.find('option').slice(1).remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results && data.results.length > 0) {
				$.each(data.results, function(index, obj) {
					$province.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
				});
				$province.children().eq(1).attr('selected', true);
				$province.change();
			}
		}, {
			loadingMask: false
		});
		$('div[name="selCountry"]').children('span').remove();
		$('#selCountry_id').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	});
	// 选择省份
	$(document).on('change', '.province', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			$(this).prev().val('');
			return;
		}
		var $me = $(this),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
		$me.prev().val(id);
		$city.find('option').slice(1).remove();
		$region.find('option').slice(1).remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results && data.results.length > 0) {
				$.each(data.results, function(index, obj) {
					$city.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
				});
				$city.children().eq(1).attr('selected', true);
				$city.change();
			}
		}, {
			loadingMask: false
		});
		$('div[name="selProvince"]').children('span').remove();
		$('#selProvince_id').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	});
	// 选择城市
	$(document).on('change', '.city', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			return;
		}
		var $me = $(this),
			$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$region.find('option').slice(1).remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results && data.results.length > 0) {
				$.each(data.results, function(index, obj) {
					$region.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
				});
				$region.children().eq(1).attr('selected', true);
				$region.change();
			}
		}, {
			loadingMask: false
		});
	});
});
function loadProviceHTML() {
	var $provice = $('.province');
	Tr.get('/dpl/region', {
		id: 1
	}, function(data) {
		if (data.code != 200) return;
		if (data.results && data.results.length > 0) {
			$.each(data.results, function(index, obj) {
				$provice.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
			});
			var loadV = $provice.data('val');
			if (loadV) {
				$.each($provice.children(), function(index, obj) {
					if ($(obj).val() == loadV) {
						$(obj).attr('selected', true);
					}
				});
			} else {
				$provice.children().eq(0).attr('selected', true);
			}

		}
	}, {
		loadingMask: false
	});
}
//load零售商列表
function loadRegList(pageNo) {
	var paramObj = {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.name': $('#user_name').val()
	};
	Tr.get('/sys/supplier/query', paramObj, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedRegCache = data.results;
		var output = Mustache.render(CDT.regUserTemp, $.extend(data, {
			supType: function(){
				if (this.type == 'FACTORY') {
					return '工厂';
				} else if (this.type == 'BRAND') {
					return '品牌方';
				} else if (this.type == 'RESELLER') {
					return '经销商';
				}
				return '----';
			}
		}));
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
	obj.filter('[tr-param]').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	$('.validateLine').children('span').remove();
}