$.extend($.validator.messages, {
	captchaCode: Tr.error('验证码格式不正确'),
	password: Tr.error('密码格式不正确')
});
$.validator.methods.captchaCode = function(value, element, param) {
	return /^\d{6}$/.test(value);
};
//用户的表单验证
function userValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			captcha: {
				required: true,
				captchaCode: true
			},
			password: {
				required: true,
				password: true
			}
		},
		success: function(label, element) {}
	};
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
/*初始化*/
$(function(){
	//可见密码
	$(document).on('click','.visiblebtn',function(){
		var val = $('.changevisible input').val();
		if($('.changevisible input').attr('type')=='password'){
			$('.changevisible').empty();
			$(".changevisible").append('<input id="txtPass_ret" name="password" type="text" minlength="6" value="'+val+'" class="inputText middle" tabindex="2"> <i class="iconfont visiblebtn">&#xe634;</i>');
		}else {
			$('.changevisible').empty();
			$(".changevisible").append('<input id="txtPass_ret" name="password" type="password" class="inputText middle" value="'+val+'" tabindex="2" minlength="6"> <i class="iconfont visiblebtn">&#xe634;</i>');
		}
	});	
	// 点击发送验证码
	$('#epSendPhoneCaptcha').click(function() {
		var $me = $(this);
		var $phone = $me.prev().eq(0);
		if ($me.data('send') === false) {
			return;
		}
		var $pop = $(this).parents('.certificationTypeBox').find('.sendSmsPop');
		$pop.find('#txtImgCaptcha_ret').eq(0).val('');
		$pop.data({
			'phone': $phone.text()
		}).show().find('.imgCaptcha').attr('src', '/captcha?r=' + Math.random());
	});

	$('.imgCaptcha').click(function() {
		$(this).attr('src', '/captcha?r=' + Math.random());
	});

	$('.sendSmsPop .popCloser').click(function() {
		$(this).parents('.sendSmsPop').hide();
	});

	// 确认发送验证码
	$('.confirmSendSmsBtn').click(function() {
		var $me = $(this);
		var validator1 = $('#captchaForm').validate(ValidateSimulateOpts());
		if (!validator1.form()) {
			return;
		}
		var $pop = $me.parents('.certificationTypeBox').find('.sendSmsPop');
		// 发送短信
		Tr.post('/uservalid/sendSms', {
			phone: $pop.data('phone'),
			imgCaptcha: $('#txtImgCaptcha_ret').val()
		}, function(data) {
			if (data.code == 200) {
				// 发送短信
				sendSmsTimer(60);
				$pop.hide();
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

	// 密码保存
	$(document).on('click', '#btnUserSubmit', function() {
		var validator = $('#frmEditPass').validate(userValidateOpts());
		var param = {'pass':$('#txtPass_ret').val(),
				'captcha':$('#txtCaptcha_ret').val()
				};
		if (validator.form()) {
			Tr.post('/user/pass/modify',param, function(data) {
				if (data.code != 200) {
					alert(data.msg);
					return;
				}
				alert('保存成功');
				window.location.href = '/login';
			});
		}
	});


});
// 发送验证码
function sendSmsTimer(timer) {
		$btn = $('#epSendPhoneCaptcha');
		var timer = timer;
		$btn.text('已发送(' + timer + 's)');
		$btn.data('send', false);
		$btn.addClass('active');
		window.smsinter = setInterval(function() {
			timer--;
			if (timer < 0) {
				$btn.data('send', true);
				$btn.text('重新发送');
				$btn.removeClass('active');
				clearInterval(window.smsinter);
				return;
			}
			$btn.text('已发送(' + timer + 's)');
		}, 1000);
	}
