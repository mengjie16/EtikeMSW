CDT = {
	clock: null
}
$.validator.methods.captchaCode = function(value, element, param) {
	return /^\d{6}$/.test(value);
};
//注册零售商用户的表单验证
function ValidateRetAddOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'user.phone': {
				required: true,
				mobile: true
			},
			captcha: {
				required: true,
				captchaCode: true
			},
			'user.password': {
				required: true,
				password: true
			}
		},
		messages: {
			'user.phone': {
				required: Tr.error('请填写手机号码'),
				mobile: Tr.error('手机号码格式不正确')
			},
			captcha: {
				required: Tr.error('请填写验证码'),
				captchaCode: Tr.error('验证码格式不正确')
			},
			'user.password': {
				required: Tr.error('请填写密码'),
				password: Tr.error('密码格式不正确')
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
	$('.btnSendPhoneCaptcha').data('send', false);
	$('.btnSendPhoneCaptcha').click(function() {
		$('#txtImgCaptcha_ret').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error')
		$('div[for="txtImgCaptcha_ret"]').html('');
		var $me = $(this);
		var $phone = $me.siblings('input.registerPhone').eq(0);
		if (!$me.data('send')) {
			return;
		}
		var $pop = $('.sendSmsPop');
		$pop.data({
			'phone': $phone.val(),
			'sbtn': $me.attr('id')
		}).show().find('.imgCaptcha').attr('src', '/captcha?r=' + Math.random());
		$('.maskbg').show();
		$('.txtImgCaptcha_ret').val('');
	});
	//切换验证码图片
	$('.imgCaptcha').click(function() {
		$(this).attr('src', '/captcha?r=' + Math.random());
	});
	$('.sendSmsPop .popCloser').click(function() {
		$(this).parents('.sendSmsPop').hide();
		$('.maskbg').hide();
	});
	// 确认发送验证码
	$('.confirmSendSmsBtn a').click(function() {
		var $me = $(this);
		var validator1 = $('#captchaForm').validate(ValidateSimulateOpts());
		if (!validator1.form()) {
			return;
		}
		var $pop = $('.sendSmsPop');
		// 发送短信
		Tr.post('/regist/sendSms', {
			imgCaptcha: $('#txtImgCaptcha_ret').val(),
			phone: $('#txtLoginPhone_ret').val()
		}, function(data) {
			if (data.errors) {
				alert(data.errors[0].description);
			}
			if (data.code == 200) {
				// 发送短信
				sendSmsTimer($pop.data('sbtn'));
				$pop.hide();
				$('.maskbg').hide();
			} else if (data.code == 20002) {
				var timer = parseInt(data.msg);
				if (timer) {
					alert('1分钟内已发送验证码，请查看您的短信');
					sendSmsTimer($pop.data('sbtn'), timer);
				}
			} else {
				alert(data.msg);
			}
		});
	});
	//阻止回车自动提交表单
	$('input[type=text]').keypress(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
		}
	});
	//校验手机号码格式
	var oldValue = 0;
	CDT.clock = setInterval(function() {
		var reg = /^0{0,1}(13[0-9]|14[5-7]|15[0-3]|15[5-9]|18[0-9]|17[0-9])[0-9]{8}$/;
		if (reg.test($('#txtLoginPhone_ret').val())&&status) {
			$('.btnSendPhoneCaptcha').data('send', true);
			$('.btnSendPhoneCaptcha').addClass('active');
		} else {
			$('.btnSendPhoneCaptcha').data('send', false);
			$('.btnSendPhoneCaptcha').removeClass('active');
		}
		if ($('#txtPhoneCaptcha_ret').val() && ($('#txtLoginPhone_ret').val() != oldValue)) {
			$('#txtPhoneCaptcha_ret').val('');
		}
		if ($('#txtLoginPhone_ret').val() != oldValue) {
			oldValue = $('#txtLoginPhone_ret').val();
		}
	}, 500)
	// 发送验证码
	var status = true;
	function sendSmsTimer(targetBtn, setTimer) {
		$btn = $('#' + targetBtn);
		var timer = setTimer ? setTimer : 60;
		$btn.text('已发送(' + timer + 's)');
		$btn.removeClass('active');
		status = false;
		$btn.data('send', false);
		window.smsinter = setInterval(function() {
			timer--;
			if (timer < 0) {
				$btn.data('send', true);
				$btn.text('重新发送');
				status = true;
				$btn.addClass('active');
				clearInterval(window.smsinter);
				return;
			}
			$btn.text('已发送(' + timer + 's)');
		}, 1000);

	}
	// 注册验证(零售商)
	$('#btnRegisterRet').click(function() {
		var validator = $('#frmRegistRetailer').validate(ValidateRetAddOpts());
		if (!validator.form()) {
			return;
		}
		// 提交注册
		$('#frmRegistRetailer').submit();
	});

});