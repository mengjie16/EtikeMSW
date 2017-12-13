$.extend($.validator.messages, {
	captchaCode: Tr.error('验证码不正确')
});

//表单验证
function validateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			name: {
				required: true
			},
			password: {
				password: true
			}
		},
		messages: {
			name: Tr.error('账号必填'),
			password: Tr.error('密码必填')
		},
		success: function(label, element) {}
	};
	return options;
}

$(function() {

	// 登录
	$(document).on('click', '#btnLogin', function() {
		var validator = $('#frmLogin').validate(validateOpts());
		if (!validator.form()) {
			return;
		}
		$('#frmLogin').submit();
	});

	 //回车提交事件
	$("body").keydown(function(event) {
	    if (event.keyCode == "13") {//keyCode=13是回车键
	      $('#btnLogin').click();
	    }
	});    
	
});

$(window).resize(function() {

});