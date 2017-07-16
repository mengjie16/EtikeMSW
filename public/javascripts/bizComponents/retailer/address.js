// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
//供应商的表单验证
function supValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'retailer.name': {
				required: true,
				username: true
			},
			'retailer.phone': {
				required: true,
				mobile: true
			},
			'retailer.country': {
				required: true
			},
			'retailer.province': {
				required: true
			},
			'retailer.address': {
				required: true
			}
		},
		success: function(label, element) {}
	};
	return options;
}
$(function(){
	loadCountryHTML();
	loadProviceHTML();

	// 地址信息保存
	$(document).on('click', '#btnSubmitSup .btnSave', function() {
		var validator = $('#frmEditSup').validate(supValidateOpts());
		if (validator.form()) {
			// var param = {
			// 	'retailer.name': $('input[name="retailer.name"]').val(),
			// 	'retailer.country': $('#selCountry').val(),
			// 	'retailer.countryId': $('#selCountry_id').val(),
			// 	'retailer.province': $('#selProvince').val(),
			// 	'retailer.provinceId': $('#selProvince_id').val(),
			// 	'retailer.city': $('#selCity').val(),
			// 	'retailer.region': $('#selRegion').val(),
			// 	'retailer.address': $('#txtAddress').val()
			// };
			// Tr.post('/supplier/modify',param, function(data) {
			// 	if (data.code != 200) {
			// 		alert(data.msg);
			// 		return;
			// 	}
			// 	alert('保存成功');
			// 	window.location.reload();
			// });
			alert(1)
		}
	});

	// 选择国家
	$(document).on('change', '#selCountry', function() {
		var $option = $(this).find('option:selected');
		var id = $option.attr('value');
		if(!id) {
			$(this).prev().val('');
			return;
		}else{
			$(this).siblings('#selCountry_id').val($option.text());
			$('div[for="selCountry_id"]').html('');
		}
		// 选国外，不显示省市区内容
		if(id!=1){
			$('.pro_city_region').hide();
		}else{
			$('.pro_city_region').show();
		}
	});
	
	// 选择省份
	$(document).on('change', '#selProvince_id', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			$(this).prev().val('');
			return;
		}else{
			$(this).prev().val($(this).find('option:selected').text());
			$('div[for="selProvince"]').html('');

		}
		// 省份赋值
		
		var $me = $(this),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
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
				//$city.selectedindex= 1;
				$city.change();
			}
		}, {
			loadingMask: false
		});
	});
	// 选择城市
	$(document).on('change', '.city', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			return;
		}
		$(this).siblings('.itemCity').eq(0).val($(this).find('option:selected').text());
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
				$provice.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
			});
			var loadV= $provice.data('val');
			if(loadV){
				$.each($provice.children(), function(index, obj) {
					if($(obj).val()==loadV){
						$(obj).attr('selected', true);
					}
				});
			}else{
				$provice.children().eq(0).attr('selected', true);
			}
			
		}
	}, {
		loadingMask: false
	});
}

function loadCountryHTML() {
	var $country = $('.country');
	Tr.get('/dpl/region', {
		id: 0
	}, function(data) {
		if (data.code != 200) return;
		if (data.results && data.results.length > 0) {
			$.each(data.results, function(index, obj) {
				$country.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
			});
			var loadV= $country.data('val');
			if(loadV){
				$.each($country.children(), function(index, obj) {
					if($(obj).val()==loadV){
						$(obj).attr('selected', true);
					}
				});
			}else{
				$country.children().eq(0).attr('selected', true);
			}
			
		}
	}, {
		loadingMask: false
	});
}


