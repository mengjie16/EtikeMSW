CDT = {
	clock: null
}
$.validator.methods.captchaCode = function(value, element, param) {
	return /^\d{6}$/.test(value);
};
function ValidateAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			phone: {
				required: true,
				mobile: true
			},
			smsCode: {
				required: true, 
				captchaCode: true
			}
		},
		messages: {
			phone: {
				required: Tr.error('请填写手机号码'),
				mobile: Tr.error('手机号码格式不正确')
			},
			smsCode: {
				required: Tr.error('请填写验证码'),
				captchaCode: Tr.error('验证码格式不正确')
			}
		},
		success: function(label, element) {}
	}
	return options;
}
function ValidatePswAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			password: {
				required: true,
				password: true
			},
			password_rep: {
				required: true,
				password: true,
				equalTo: '#txtPass_ret'
			}
		},
		messages: {
			password: {
				required: Tr.error('请输入密码'),
				password: Tr.error('密码格式不正确')
			},
			password_rep: {
				required: Tr.error('请再次输入密码'),
				password: Tr.error('密码格式不正确'),
				equalTo: Tr.error('两次密码不一致')
			}
		},
		success: function(label, element) {}
	}
	return options;
}
function ValidateSimulateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			captchaCode: {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
$(function() {
	// 点击发送验证码
	$('.btnSendPhoneCaptcha').data('send',false);
	$('.btnSendPhoneCaptcha').click(function() {
		$('#txtImgCaptcha_ret').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error')
		$('div[for="txtImgCaptcha_ret"]').html('');
		var $me = $(this);
		var $phone = $me.prev().eq(0);
		if ($me.data('send') === false) {
			return;
		}
		var $pop = $('.sendSmsPop');
		$pop.data({
			'phone': $phone.val()
		}).show().find('.imgCaptcha').attr('src', '/captcha?r=' + Math.random());
		$('.txtImgCaptcha_ret').val('');
	});
	$('.imgCaptcha').click(function() {
		$(this).attr('src', '/captcha?r=' + Math.random());
	});

	$('.sendSmsPop .popCloser').click(function() {
		$(this).parents('.sendSmsPop').hide();
	});

	// 确认发送验证码
	$('.confirmSendSmsBtn a').click(function() {
		var $me = $(this);
		var validator1 = $('#captchaForm').validate(ValidateSimulateOpts());
		if (!validator1.form()) {
			return;
		}
		var $pop = $me.parents('.certificationTypeBox').find('.sendSmsPop');	
		// 发送短信
		Tr.post('/uservalid/sendSms', {
			phone: $('#txtPhone_ret').val(),
			imgCaptcha: $('#txtImgCaptcha_ret').val()
		}, function(data) {
			if (data.code == 200) {
				// 发送短信
				sendSmsTimer(60);
				$pop.hide();
				$.cookie('the_uim', data.results.uim, {
					expires: 1
				});
				return;
			}else if(data.code == 20002){
				var timer  = parseInt(data.msg);
				if(timer>0){
					alert('1分钟内已发送验证码，请查看您的短信');
					sendSmsTimer(timer);
				}
			}else{
				alert(data.msg);
			}

		});
	});
	var status = true;
	// 发送验证码
	function sendSmsTimer(timer) {
		$btn = $('#rpSendPhoneCaptcha');
		var timer = timer;
		$btn.text('已发送(' + timer + 's)');
		$btn.data('send', false);
		$btn.removeClass('active');
		status = false;
		window.smsinter = setInterval(function() {
			timer--;
			if (timer < 0) {
				$btn.data('send', true);
				$btn.text('重新发送');
				$btn.addClass('active');
				status = true;
				clearInterval(window.smsinter);
				return;
			}
			$btn.text('已发送(' + timer + 's)');
		}, 1000);
	}
	//校验手机号码格式
	var oldValue = 0;
	CDT.clock = setInterval(function() {
		var reg = /^0{0,1}(13[0-9]|14[5-7]|15[0-3]|15[5-9]|18[0-9]|17[0-9])[0-9]{8}$/;
		if (reg.test($('#txtPhone_ret').val())&&status) {
			$('.btnSendPhoneCaptcha').data('send', true);
			$('.btnSendPhoneCaptcha').addClass('active');
		} else {
			$('.btnSendPhoneCaptcha').data('send', false);
			$('.btnSendPhoneCaptcha').removeClass('active');
		}
		if ($('#txtPhoneCaptcha_ret').val() && ($('#txtPhone_ret').val() != oldValue)) {
			$('#txtPhoneCaptcha_ret').val('');
		}
		if ($('#txtPhone_ret').val() != oldValue) {
			oldValue = $('#txtPhone_ret').val();
		}
	}, 500)
	// 确认验证用户身份
	var validator = $('#frValidUser').validate(ValidateAddOpts());
	$('#frValidUser').trForm({
		before: function() {
			return validator.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				$('.error_panel').html(data.msg);
				return;
			}
			$.cookie('smdCode', $('#txtCaptcha_ret').val(), {
				expires: 1
			});
			window.location.href = '/pwd/retrieve/action';
		}
	});
	$('input[name="hiduim"]').val($.cookie('the_uim'));
	$('input[name="hidsmdCode"]').val($.cookie('smdCode'));
	// 确认用户密码
	var validator2 = $('#frConfirmPass').validate(ValidatePswAddOpts());
	$('#frConfirmPass').trForm({

		before: function() {
			$('input[name="hiduim"]').val($.cookie('the_uim'));
			$('input[name="hidsmdCode"]').val($.cookie('smdCode'));
			return validator2.form();
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				$('.error_panel').html(data.msg);
				return;
			}
			$.cookie('the_uim', null); 
			$.cookie('smdCode', null); 
			window.location.href = '/pwd/retrieve/finished';
		}
	});
	// 立即登录
	$('#btnLoginNow').click(function() {
		window.location.href = '/login';
	});
})