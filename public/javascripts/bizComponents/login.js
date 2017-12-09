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
				required: true,
				password: true
			}
		},
		messages: {
			name: {
				required: Tr.error('用户名需填写')
			},
			password: {
				required: Tr.error('密码需填写'),
				password: Tr.error('密码格式错误')
			}
		},
		success: function(label, element) {}
	};
	return options;
}

$(function() {

	// 登录
	// $(document).on('click', '#btnLogin', function() {
	// 	var validator = $('#frmLogin').validate(validateOpts());
	// 	if (!validator.fcartorm()) {
	// 		return;
	// 	}
		
	// });
	var validator = $('#frmLogin').validate(validateOpts());
	/*$('#frmLogin').trForm({
		before: function() {
			$('.error_panel').html('');
			return validator.form();
		},
		exParams: {
			savePass: function(){
				return $('#secure_connection1').is(':checked');
			},
			rUrl:$('input[name="rUrl"]').val()
		},
		callback: function(data) {
			if (data.code != 200) {
				$('.error_panel').html(data.msg);
				return;
			}
			if($('input[name="rUrl"]').val()){
				window.location.href = $('input[name="rUrl"]').val();
			}else{
				window.location.href = '/user/home';
			}
			
			//window.location.href = '/'+$('input[name="rUrl"]').val()?$('input[name="rUrl"]').val():'user/home';
		}
	});*/
	$("#frmLogin").on('click', function(){		
		var params ={
			savePass: function(){
				return $('#secure_connection1').is(':checked');
			},
			rUrl:$('input[name="rUrl"]').val(),
			'authenticityToken': $('input[name=authenticityToken]').val(),
			name:$('input[name="name"]').val(),
			password:$('input[name="password"]').val()
		}
		var tr_url;
		if (validator.form()) {
			$('.error_panel').html('');
			if(!/^admin/i.test(params.name)){
				tr_url = "/doLogin";
			}else{
				tr_url ="/sys/doLogin";
			}
			$.ajax({
		        type: "post",
		        url: tr_url,
		        data: params,
		        success: function(data) {
		        	if(tr_url === "/doLogin"){
			        	if (data.code != 200) {
			                return;
			            } else if (data.code === 200){		            	
			            		window.location.href = '/user/home';
			            }
		        	}else{
		        		window.location.href = '/sys/user/manage';
		        	}


		        }

		    });
		}
	});
	
	 //回车提交事件
	$("body").keydown(function(event) {
	    if (event.keyCode == "13") {//keyCode=13是回车键
	      $('#btnLogin').click();
	    }
	});    
	
	// 快速登录
	$('#frmQuickLogin').trForm({
		before: function() {
			return true;
		},
		exParams: {},
		callback: function(data) {
			if (data.code != 200) {
				window.location.href = '/login?switchUser=true';
				return;
			}
			if($('input[name="rUrl"]').val()){
				window.location.href = $('input[name="rUrl"]').val();
			}else{
				window.location.href = '/user/home';
			}
		}
	});
	$('.loginInfo .inputText').focusout(function(){
		$(this).removeClass('txtOn');
	}).focusin(function(){
		$(this).addClass('txtOn');
	});
});

$(window).resize(function() {

});