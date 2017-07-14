CDT = {
	token: null,
	uptoken: null,
	uploadImgCache:{}
};
$(function() {
	loadUpToken();
	$(document).on('hover','.my_photo',function(){
		$('.mask').toggle();
	});
});
function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		initItemMidPicUploader();
	});
}
//上传middlePoster
function initItemMidPicUploader() {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'uploadHead';
	option.container = 'img_container';
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
			CDT.uploadImgCache[file.name] = sourceLink;
			$.post('/user/upload/avatar', {
				'authenticityToken': $('input[name=authenticityToken]').val(),
				'avatar':sourceLink
			}, function(data) {
				if (data.code != 200) {
					alert('更新失败!');
					return;
				}
				alert('更新成功！');
				$('#img_content img').attr('src',sourceLink);
				
			});
		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
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