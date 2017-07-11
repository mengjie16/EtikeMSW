CDT = {
	token: null,
	uptoken: null,
	regUserTemp: null,
	loadedRegCache: null,
	currPageNo: 1,
	pageSize: 10
};
// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
//编辑用户的表单验证
function ValidateOpts_ret() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'edit.email':{
				required: true,
				email: true
			},
			'edit.name': {
				required: true,
				username: true
			},
			'edit.userId': {
				required: true,
				digits: true
			},
			'edit.qq': {
				digits: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}

//编辑的验证码表单验证
function ValidateSmsCodeOpts() {
	// 校验规则
	var options = {
		onkeyup: true,
		focusCleanup: true,
		rules: {
			user_mobile: {
				mobile: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//添加注册用户的表单验证
function ValidateNewAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'add.phone': {
				required: true,
				checkContact: true
			},
			'add.email':{
				required: true,
				email: true
			},
			'add.name': {
				required: true,
				username: true
			},
			'add.userId': {
				required: true,
				digits: true
			},
			'add.password': {
				required: true,
				password: true
			},
			'add.qq': {
				digits: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
//模拟用户登录的验证码表单验证
function ValidateSimulateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			simulate_mobile: {
				required: true,
				checkContact: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}

function initBase(argument) {
	var validator1, validator2;
	//弹出模拟用户登录弹框
	$(document).on('click', '#simulate', function() {
		if (validator1) {
			validator1.resetForm();
		}
		$('#simulateMobile').val('');
		Tr.popup('simulateLogin');
	});
	var currentTime = new Date().Format("yyyy-MM-dd");
	$('.inputDate').datePicker({
		startDate: '2015-01-01',
		endDate: currentTime,
		clickInput: true,
		verticalOffset: 30,
		horizontalOffset: -26
	});
	//模拟用户登录提交
	$(document).on('click', '#btnSimulateLogin', function() {
		validator1 = $('#frmSimulateLogin').validate(ValidateSimulateOpts());
		if (!validator1.form()) {
			return;
		}
		$('#simulateLogin').hide();
		$('html').removeClass('overflow-hidden');
		$('#frmSimulateLogin').submit();
	});
	// 重新发送验证码弹窗
	$(document).on('click', '#smsCodeHelper', function() {
		if (validator2) {
			validator2.resetForm();
		}
		Tr.popup('repeatSend');
	});

	// 手机号码输入查询验证码
	$(document).on('change', '#reSendWrapper #mobile', function() {
		validator2 = $('#frmReSend').validate(ValidateSmsCodeOpts());
		if (!validator2.form()) {
			return;
		}
		// 查询验证码
		Tr.get('/sys/user/smsCode/query', {
			'phone': $('#reSendWrapper #mobile').val()
		}, function(data) {
			if (data.code == 200) {
				$('#frmReSend #captcha').val(data.results);
				return;
			}
			$('#frmReSend #captcha').val('');
		});
	});

	// 重新发送验证码
	$(document).on('click', '#btnReSend', function() {
		validator2 = $('#frmReSend').validate(ValidateSmsCodeOpts());
		if (!validator2.form()) {
			return;
		}
		Tr.post('/regist/sendSms', {
				phone: $('#reSendWrapper #mobile').val(),
				imgCaptcha:'6666',
				garen:1
			}, function(data) {
				if (data.code != 200) {
					alert('发送失败！');
					return;
				}
				alert('发送成功!');
				$('#reSendWrapper #captcha').val(data.results);
			});
	});

	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//修改注册用户
	$(document).on('click', '.editbtn', function() {

		var id = $(this).data('id');
		$('#hidReAddFormId').val(id);
		$.each(CDT.loadedRegCache, function(i, n) {
			if (n.id == id) {
				$('#ed_phone').text(n.phone);
				$('#ed_email').val(n.email);
				$('#ed_username').val(n.name);
				$('#ed_userId').val(n.userId);
				$('#ed_qq').val(n.qq);
				$('#ed_weixin').val(n.weixin);
				$.each($('input[type="radio"]'),function(index,obj){
					if($(obj).val()==n.role){
						$(obj).attr('checked','checked');
						$(obj).parent().find('input[type=hidden]').val(n.role);
					}
				})
			}
		});
		$('.validateLine').children('span').remove();
		Tr.popup('editRegist');
	});
	// 重置密码
	$(document).on('click', '#btnrePass', function() {
		if (!confirm('确定重置该用户密码？')) {
			return;
		}
		Tr.post('/sys/user/resetpass', {
			'userId': $('#hidReAddFormId').val()
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('重置密码成功');
		}, {
			loadingMask: false
		});
	});
	//选择角色
	$(document).on('change','input[type="radio"]',function(){
		var val = $(this).val();
		$(this).parent().find('input[type="hidden"]').val(val);
	});
	// 保存添加注册用户信息
	var validator3 = $('#frmRegistSupplier').validate(ValidateNewAddOpts());
	$('#addUserWnd').trForm({
		before: function() {
			return validator3.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('注册成功');
			$('.popWrapper').hide();
			clearFrom($('.trControl'));
			$('html').removeClass('overflow-hidden');
			loadRegList(1);
		}
	});
	// 保存编辑注册用户信息
	var validator6 = $('#frmEdit_sup').validate(ValidateOpts_ret());
	$('#editUserWnd').trForm({
		before: function() {
			return validator6.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('修改成功');
			$('.popWrapper').hide();
			$('html').removeClass('overflow-hidden');
			loadRegList(CDT.currPageNo);
		}
	});
	// 关键字查询商品
	$(document).on('click', '#searchBtn', function() {
		loadRegList(1);
	});
	// 删除选中
	$(document).on('click', '.delbtn', function() {
		var id = $(this).data('id');
		if (!confirm('确定删除该用户吗？')) {
			return;
		}
		Tr.post('/sys/user/delone', {
			id: id
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败');
				return;
			}
			alert('删除成功');
			loadRegList(CDT.currPageNo);
		}, {
			loadingMask: false
		});
	});
	// 授权认证
	$(document).on('click', '.tbHoverable .changeAuth', function() {
		var auth = $(this).data('auth');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if (typeof(auth) == 'undefined' || !id) {
			return;
		}
		var title = '确定取消对该用户授权认证?';
		if (auth == false) {
			title = '确定对该用户授权认证?';
		}
		if (!confirm(title)) {
			return;
		}
		$.post('/sys/user/authone', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'userId': id
		}, function(data) {
			if (data.code != 200) {
				alert('授权认证失败!');
				return;
			}
			$me.data('auth', !auth);
			if (auth == false) {
				$me.html('&#xe620;');
				$me.css('color', '#FECF10');
			} else {
				$me.html('&#xe61f;');
				$me.css('color', '#888');
			}
		});
	});
	// 添加注册用户弹窗
	$(document).on('click', '#registNew', function() {
		clearFrom($('.trControl'));
		$('input[tr-param="user.role"]').val('RETAILER');
		$('input[type=radio]').removeAttr('checked');
		$('input[value="RETAILER"]').attr('checked','checked');
		Tr.popup('registeUser');
	});
	//阻止回车自动提交表单
	$('input[type=text]').keypress(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
		}
	});
	//比较查询开始时间与结束时间大小
	$(document).on('change', '.inputDate', function() {
		var startTime = $("#startTime").val();
		var start = new Date(startTime.replace("-", "/").replace("-", "/"));
		var endTime = $("#endTime").val();
		var end = new Date(endTime.replace("-", "/").replace("-", "/"));
		if (end < start) {
			$(this).val('');
			$('.errorDate').text('结束时间不得小于开始时间');
		} else {
			$('.errorDate').text('');
			return true;
		}
	});
}
//load用户列表
function loadRegList(pageNo) {
	var paramObj = {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.userType': $('#kindSel').val(),
		'vo.name': $('#user_name').val(),
		'vo.phone': $('#user_mobile').val(),
		'vo.createTimeStart': $('#startTime').val(),
		'vo.createTimeEnd': $('#endTime').val()
	};
	Tr.get('/sys/user/query', paramObj, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedRegCache = data.results;
		var output = Mustache.render(CDT.regUserTemp, $.extend(data, {
			userType: function() {
				if (this.role == 'RETAILER') {
					return '零售商';
				} else if (this.role == 'SUPPLIER') {
					return '供应商';
				}
				return '----';
			},
			authShow: function() {
				if (this.role == 'RETAILER') {
					return true;
				} else if (this.role == 'SUPPLIER') {
					return true;
				}
				return false;
			},
			userInfomation: function() {
				if (this.role == 'RETAILER') {
					return this.email;
				} else if (this.role == 'SUPPLIER') {
					if (this.company) {
						return this.company;
					}
					return '----';
				}
				return '----';
			},
			createTimeStr: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd');
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

$(function() {
	CDT.regUserTemp = $('#regUserTemp').remove().val();
	Mustache.parse(CDT.regUserTemp);
	initBase();
	loadRegList(1);
});
function clearFrom(obj){
	obj.filter('[tr-param]').val('').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	$('.validateLine').children('span').remove();
}