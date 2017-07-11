CDT = {
	token: null,
	uptoken: null,
	ue: null,
	h5ue: null,
	uploadImgCache: new Array(),
	suggestSearchCache: {},
	placeSearchCache: {},
	provinceCache: {},
	cityCache: {},
	regionCache: {},
	uploadIDSCache: new Array(),
	itemId: '',
	sendLoactionTempCache: null
};

// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
$.extend($.validator.messages, {
	checkRange: Tr.error('请填写低于零售价的价格')
});
// 参考链接
$.validator.methods.checkUrl = function(value, element, param) {
	var key = $(element).parent().find('select').val().split('|');
	var inputvalue = $(element).val();
	if (!inputvalue) return true;
	var content = false;
	$.each(key,function(index,obj){
		if (inputvalue.indexOf(obj) >= 0) {
			content = true;
		}
	})
	if(content){
		return true;
	}else{
		return false;
	}
};
// 参考链接
function checkRefUrl() {
	var $referUrlList = $('#referUrlList .rederurl_item');
	var check = true;
	$.each($referUrlList,function(index,item){
		// 取URL
		var url = $(item).find('.txtUrl').eq(0).val();
		var check = /^https?:\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(url);
		if(!check){
			$(item).find('.validateLine').eq(0).val("格式不正确");
			return true;
		}
		// 取key
		// referurl_key
	});
	return check;
};
$.extend($.validator.messages, {
	checkUrl: Tr.error('链接格式不正确')
})

// 商品价格区间检查(针对price)
$.validator.methods.checkPriceItem = function(value, element, param) {
	var eValue = $(element).parent().find('.priceTxt').val(),
		$eparent = $(element).parent();
	if (!eValue) return true;
	// 获取所有
	var $priceItems = $('.priceItem'),
		tprice = parseInt(eValue),
		cond = $eparent.find('.conditionTxt').eq(0).val();
	$.each($priceItems, function(index, elobj) {
		if ($(elobj) != $eparent) {
			var condition = $(elobj).find('.conditionTxt').eq(0).val(),
				price = $(elobj).find('.priceTxt').eq(0).val();
			if (price && tprice == parseInt(price) && condition && cond &&
				condition != cond) { // 价格相等,件数却不一样
				$eparent.find('.conditionTxt').addClass('warn');
				$('<span class="errortxt">请填写正确的件数</span>').appendTo($eparent);
				return false;
			} else {
				$eparent.find('.conditionTxt').removeClass('warn');
				$('span.errortxt').remove();
			}
		}
	});
	return true;
};
// 商品价格区间检查(针对condition)
$.validator.methods.checkConditItem = function(value, element, param) {
	var eValue = $(element).parent().find('.conditionTxt').val(),
		$eparent = $(element).parent();
	if (!eValue) return true;
	// 获取所有
	var $priceItems = $('.priceItem'),
		cond = parseInt(eValue),
		tprice = $eparent.find('.priceTxt').eq(0).val();
	$.each($priceItems, function(index, elobj) {
		if ($(elobj) != $eparent) {
			var condition = $(elobj).find('.conditionTxt').eq(0).val(),
				price = $(elobj).find('.priceTxt').eq(0).val();
			if (condition && cond == parseInt(condition) && price && tprice &&
				price != tprice) { // 件数相等,价格却不一样
				$eparent.find('.priceTxt').addClass('warn');
				$('<span class="errortxt">请填写正确的价格</span>').appendTo($eparent);
				return false;
			} else {
				$eparent.find('.priceTxt').removeClass('warn');
				$('span.errortxt').remove();
			}
		}
	});
	return true;
};
//代发价与批发价低于零售价
$.validator.methods.checkRange = function(value, element, param) {
	var eValue = $(element).val(),
		$eparent = $(element).parent(),
		$ecompare = $('#txtRetailPrice').val();
	if(eValue>$ecompare) {
		
		return false;
	}
	return true;
};
//发布商品的表单验证
function ValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'item.title': {
				required: true,
				maxwords: 20,
				minwords: 5
			},
			'item.cate': {
				required: true
			},
			'item.picUrl': {
				required: true
			},
			'item.d_supplyPrice': {
				required: true,
				number:'价格'
			},
			'item.netWeight': {
				required: true,
				number:'净重量'
			},
			'item.grossWeight': {
				required: true,
				number:'毛重量'
			},
			'item.quality': {
				required: true,
				range:[1000,10000]
			},
			'item.num': {
				required: true,
				digits: true
			},
			'item.unit': {
				required: true,
				maxlength: 5,
				minlength: 1
			},
			'item.origin': {
				required: true
			},
			'item.outNo': {
				required: true
			},
			'item.deliverType': {
				required: true
			},
			'item.d_retailPrice': {
				required: true,
				number: '价格'
			},
			'item.d_distPrice': {
				//required: true,
				number: '价格'
			},
			'item.brandId': {
				required: true
			},
			'item.unit': {
				required: true
			},
			'item.status': {
				required: true
			},
			'item.initialQuantity': {
				digits: true
			},
			'sku.color': {
				required: true
			},
			'sku.img': {
				required: true
			},
			'sku.quantity': {
				required: true,
				number: '库存',
				digits: true
			},
			// referurl_value: {
			// 	url: true,
			// 	checkUrl: true
			// },
			'item.note': {},
			condition_item: {
				//required: true,
				checkPriceItem: true,
				number: '起订量'
			},
			price_item: {
				//required: true,
				checkConditItem: true,
				number: '价格'
			},
			name_attribute: {
				required: true
			},
			value_attribute: {
				required: true
			},
			'item.cateId': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}

function initBase(argument) {

	$('body').click(function(e) {
		if ($(e.srcElement).is('#regionControls *') || !e.srcElement) {
			return true;
		}
		$('.selectboxs').hide();
	});

	//选择品牌弹框
	$(document).on('click', '.chooseBrand', function() {
		$('#brandInput').val('');
		printBrandHTMLbyCache();
		Tr.popup('addBrand');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//选择品牌
	$(document).on('hover', '.brandList ul li', function() {
		$(this).find('.tip').toggle();
	});
	$(document).on('click', '.brandList ul li>span', function() {
		var brands = $('.brandList ul li>span');
		if (brands > 1) {
			$('.brandList ul li>span').removeClass('active');
		}
		$(this).toggleClass('active');
	});
	//显示品牌
	$('#btnConfirm').on('click', function() {
		$('.brandList ul li>span').each(function(index, obj) {
			if ($(obj).hasClass('active')) {
				var text = $(obj).text();
				var id = $(obj).parent().attr('data-id');
				$('#brandTxt').val(id).removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
				$('div[name="valbrand"]').children('span').remove();
				// $('#formComm').valid();
				$('#showBrand span').text(text);
				$('#showBrand').show().prev().hide();
				$(this).parents('.popWrapper').hide();
				$('html').removeClass('overflow-hidden');
				$('.brandList ul li>span').removeClass('active');

			}
		});
	});
	//删除品牌
	$('#delBrand').on('click', function() {
		$(this).parent().hide();
		$(this).prev().text('');
		$('#brandTxt').val('');
		$(this).parent().prev().show();
	});
	//查询品牌
	$('#searchBrandBtn').on('click', function() {
		printBrandHTMLbyCache();
	});
	$('#brandInput').on('keypress', function(e) {
		if (e.keyCode == 13) {
			printBrandHTMLbyCache();
		}
	});


	// 宝贝类目模糊查询
	//选择类目弹框
	$(document).on('click', '.chooseCate', function() {
		$('#cateInput').val('');
		$('#cateList ul').empty();
		// 先查缓存	
		if (CDT.suggestSearchCache && CDT.suggestSearchCache.length > 0) {
			$.each(CDT.suggestSearchCache, function(index, obj) {
				$('#cateList ul').append("<li class='cateitem' data-id='" + obj.id + "'>" + obj.name + "</li>");
			});
		} else {
			Tr.get('/dpl/cate/all', {}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.suggestSearchCache = data.results;
				$.each(data.results, function(index, obj) {
					$('#cateList ul').append("<li class='cateitem' data-id='" + obj.id + "'>" + obj.name + "</li>");
				});
			}, {
				loadingMask: false
			});
		}
		Tr.popup('addCate');
	});
	//选择类目
	$(document).on('click', '.cateList ul li', function() {
		$('.cateList ul li').removeClass('active');
		$(this).toggleClass('active');
	});
	//显示类目
	$('#btnCatefirm').on('click', function() {
		$('.cateList ul li').each(function(index, obj) {
			if ($(obj).hasClass('active')) {
				var text = $(obj).text();
				var id = $(obj).attr('data-id');
				$('#cateTxt').val(id).removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
				$('div[name="valcate"]').children('span').remove();
				// $('#formComm').valid();
				$('#showCate span').text(text);
				$('#showCate').show().prev().hide();
				$(this).parents('.popWrapper').hide();
				$('html').removeClass('overflow-hidden');
				$('.cateList ul li').removeClass('active');
			}
		});
	});
	//删除类目
	$('#delCate').on('click', function() {
		$(this).parent().hide();
		$(this).prev().text('');
		$('#cateTxt').val('');
		$(this).parent().prev().show();
	});
	//查询类目
	$('#cateInput').on('click', function() {
		$(this).val('');
		var $container = $('#cateList');
		if (!$container.length) return true;
		$items = $container.find('li'),
			$item = $(),
			itemsIndexed = [];
		$items.each(function() {
			itemsIndexed.push($(this).text().replace(/\s{2,}/g, ' ').toLowerCase());
		});
		$items.show();
		$(this).on('keyup', function(e) {
			var searchVal = $.trim($(this).val()).toLowerCase();
			if (searchVal.length) {
				for (var i in itemsIndexed) {
					$item = $items.eq(i);
					if (itemsIndexed[i].indexOf(searchVal) != -1)
						$item.show();
					else
						$item.hide();
				}
			} else $items.show();
		});
	});
	// 宝贝产地模糊查询
	$('#placePro').on('keyup', function() {
		var kw = $(this).val();
		if (!kw || kw.length < 1) {
			$('#placeList').hide();
			return;
		}
		// 先查缓存	
		if (CDT.placeSearchCache[kw] && CDT.placeSearchCache[kw].length > 0) {
			printPlaceHTMLbyCache(kw, true);
		} else {
			Tr.get('/dpl/origin/search', {
				'keyWord': kw
			}, function(data) {
				if (data.code != 200 || !data.results || data.results.length <= 0) return;
				CDT.placeSearchCache[kw] = data.results;
				printPlaceHTMLbyCache(kw, true);
			}, {
				loadingMask: false
			});
		}
	});

	$(document).on('click', '.selectboxs li', function() {
		var text = $(this).text();
		var id = $(this).attr('data-id');
		$('#cateTxt').val(id);
		$(this).parent().parent().prev().val(text);
		$('.selectboxs').hide();
	});

	// 增加价格条件描述项
	$(document).on('click', '#addPriceItem', function() {
		var $plen = $(this).parent().find('.priceItem').length,
			$index = new Date().getTime();
		if ($plen >= 5) return;
		var $prev = $(this).prev();
		var $prevObj = $prev.clone();
		$prevObj.find('input').val('');
		if ($prevObj.find('.iconfont').length == 0) {
			$prevObj.append("<i class='iconfont ml10 pdelbtn'>&#xe606;</i>");
		}
		if ($plen <= 1) {
			$prev.append("<i class='iconfont ml10 pdelbtn'>&#xe606;</i>");
		}
		// 处理clone元素属性问题
		$prevObj.find('.conditionTxt').attr('id', 'txtPi_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('.priceTxt').attr('id', 'txtNum_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('div[name="valPrice"]').attr('for', 'txtPi_' + $index).children('span').remove();
		$prevObj.find('div[name="valNum"]').attr('for', 'txtNum_' + $index).children('span').remove();
		$prev.after($prevObj);
		if ($plen + 1 >= 5) $(this).hide();
	});
	//删除价格条件描述项
	$(document).on('click', '.pdelbtn', function() {
		var ilen = $(this).parents('#priceItems').find('.priceItem').length;
		if (ilen <= 2) {
			$(this).parent().siblings().find('.pdelbtn').remove();
		}
		$(this).parent().remove();
		if (ilen <= 5) $('#addPriceItem').show();
	});
	// 增加收货地址弹框	
	$(document).on('click', '#addAddress', function() {
		// 加载历史记录
		if (!CDT.sendLoactionTempCache) {
			Tr.get('/sys/item/sendLocationlist', {
				'supplierId': $("input[name='item.supplierId']").val()
			}, function(data) {
				if (data.code != 200 || !data.results) return;
				CDT.sendLoactionTempCache = data.results;
				printSendLocationTempHtml();
			}, {
				loadingMask: false
			});
		} else {
			printSendLocationTempHtml();
		};
		$('#addressSelects').val('others');
		var length = $('#selProvince').children().length;
		if (length > 1) {
			$('#selProvince').children().eq(0).attr('selected', true);
			$('#selCity').children().eq(0).attr('selected', true);
			$('#selRegion').children().eq(0).attr('selected', true);
		}
		$('#txtAddress').val('');
		$('.saveMoudel input').attr('checked', false);
		$('.address').show();
		Tr.popup('addAddressBlock');
	});

	// 收货地址选择
	$(document).on('change', '#addressSelects', function() {
		var opt = $(this).val();
		if (opt == 'others') {
			$('.address').show();
		} else { // 选择了已有的地址
			$('.address').hide();
		}
	});

	// 确认收货地址增加
	$(document).on('click', '#addAddressBlock #btnAddfirm', function() {
		// 是否保存到模板地址
		var checked = $('#addAddressBlock input[name=item_location_save]').prop('checked');
		var saveTemp = false;
		var selectedLocation = $('#addressSelects').val();
		var provinceId = "",
			province = "",
			city = "",
			region = "",
			address = "",
			locatintxt = "";
		if (selectedLocation == 'others') {
			// 商品地址确认到模板
			provinceId = $('#selProvince').find('option:selected').val();
			province = $('#selProvince').find('option:selected').text();
			city = $('#selCity').val();
			region = $('#selRegion').val();
			address = $('#txtAddress').val();
			saveTemp = true;
		} else { // 选择了已有的地址
			$.each(CDT.sendLoactionTempCache, function(index, location) {
				if (selectedLocation == location.id) {
					provinceId = location.provinceId;
					province = location.province;
					city = location.city;
					region = location.region;
					address = location.address;
				}
			});
		}
		// 当前地址
		var locatintxt = province + city + region + address;
		if (!locatintxt || locatintxt === '') {
			alert('添加失败!');
			return;
		}

		var checkRule = true;
		// 当前宝贝发货地址列表检查
		var $addressList = $('#addressList .addressContainer .hLocation');
		$.each($addressList, function(index, elobj) {
			var provinceN = $(elobj).data('province'),
				cityN = $(elobj).data('city'),
				regionN = $(elobj).data('region'),
				addressN = $(elobj).data('address');
			var oldLcationtxt = provinceN + cityN + regionN + addressN;
			if (oldLcationtxt == locatintxt) {
				checkRule = false;
				return true;
			}
		});
		if (!checkRule) {
			alert("发货地址填写重复,请重新填写!");
			return;
		}
		var loactionTemp = {
			'locationTemp.supplierId': $("input[name='item.supplierId']").val(),
			'locationTemp.province': province,
			'locationTemp.provinceId': provinceId,
			'locationTemp.city': city,
			'locationTemp.region': region,
			'locationTemp.address': address,
			'authenticityToken': $('#frmAddAddress').find("input[name=authenticityToken]").val()
		};
		var locationCount = $('#addressList addressContainer').length;
		var $addressContainer = $("<div class='addressContainer'></div>");
		var $addressInput = $("<input class='hLocation' type='hidden'/>");
		var obj = {
			'province': province,
			'pid': provinceId,
			'city': city,
			'region': region,
			'address': address
		};
		$addressInput.data(obj);
		var $addressP = "<p><span>" + province + "</span><span>" + city + "</span><span>" + region + "</span></p><p>" + address + "</p><i class='iconfont mr10 addressDelbtn'>&#xe631;</i>";
		$addressContainer.append($addressInput);
		$addressContainer.append($addressP);
		if (checked && saveTemp) {
			Tr.post('/sys/item/sendlocationConfirm', loactionTemp, function(data) {
				if (data.code != 200) {
					var msg = '添加失败!';
					if (data.code == 8001) {
						msg = '该地址已在地址模板中存在!';
					}
					alert(msg);
					return;
				}
				alert('添加成功！');
				// 清空历史记录,下次重新加载
				CDT.sendLoactionTempCache = null;
				$('#addressList').append($addressContainer);
				$('#addAddressBlock').hide();
			});
		} else {
			$('#addressList').append($addressContainer);
			$('#addAddressBlock').hide();
		}
		$('html').removeClass('overflow-hidden');
	});

	//删除地址
	$(document).on('click', '.addressDelbtn', function() {
			/*if(!confirm('确认删除该地址吗？')){
				return;
			}*/
			$(this).parent().remove();
		})
		// 增加属性条件描述项
	$(document).on('click', '#addAttribute', function() {
		var $plen = $(this).parent().find('.attribute').length,
			$index = new Date().getTime();
		if ($plen >= 20) return;
		var $prev = $(this).prev();
		var $prevObj = $prev.clone();
		$prevObj.find('input').val('');
		if ($prevObj.find('.iconfont').length == 0) {
			$prevObj.append("<i class='iconfont ml10 adelbtn'>&#xe606;</i>");
		}
		if ($plen <= 1) {
			$prev.append("<i class='iconfont ml10 adelbtn'>&#xe606;</i>");
		}
		// 处理clone元素属性问题
		$prevObj.find('.nameTxt').attr('id', 'txtName_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('.valueTxt').attr('id', 'txtValue_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('div[name="valName"]').attr('for', 'txtName_' + $index).children('span').remove();
		$prevObj.find('div[name="valValue"]').attr('for', 'txtValue_' + $index).children('span').remove();
		$prev.after($prevObj);
		if ($plen + 1 >= 20) $(this).hide();
	});
	//调整规格顺序
	//向上移动
	$(document).on('click','.moveUp',function(){
		var $me = $(this);
		var $tr = $me.parents('.gg_container');
		var $prev = $tr.prev();
		var index = $tr.index();
		// 上一个元素(存在)
		if($prev){
			$prev.before($tr);
		}
	});
	//向下移动
	$(document).on('click','.moveDown',function(){
		var $me = $(this);
		var $tr = $me.parents('.gg_container');
		var $next = $tr.next('.gg_container');
		var index = $tr.index();
		if($next){ // 下一个元素(存在)
			$next.after($tr);
		}
	});
	//删除属性条件描述项
	$(document).on('click', '.adelbtn', function() {
		var ilen = $(this).parents('#attributes').find('.attribute').length;
		if (ilen <= 2) {
			$(this).parent().siblings().find('.adelbtn').remove();
		}
		$(this).parent().remove();
		if (ilen <= 20) $('#addAttribute').show();
	});
	// 增加规格条件描述项
	$(document).on('click', '#addSpec', function(event) {
		var $plen = $(this).parent().find('.gg_container').length,
			$index = new Date().getTime();
		if ($plen >= 10) return;
		var $prev = $(this).prev();
		var $prevObj = $prev.clone();
		$prevObj.find('input').val('');
		if ($prevObj.find('.iconfont').length == 0) {
			$prevObj.append("<i class='iconfont ml10 ggdelbtn'>&#xe606;</i><i class='iconfont pl10 moveUp'>&#xe600;</i><i class='iconfont pl10 moveDown'>&#xe60d;</i>");
		}
		if ($plen <= 1) {
			$prev.append("<i class='iconfont ml10 ggdelbtn'>&#xe606;</i><i class='iconfont pl10 moveUp'>&#xe600;</i><i class='iconfont pl10 moveDown'>&#xe60d;</i>");
		}
		// 处理clone元素属性问题
		$prevObj.find('.mask').attr('id', 'T_adBanner_' + $index).show();
		$prevObj.find('.mask').attr('img-container', 'img_' + $index);
		$prevObj.find('.itemImg').attr('id', 'img_' + $index);
		$prevObj.find('img').attr('src', '');
		$prevObj.find('input[name="sku.img"]').attr('id', 'hidPicUrl_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('input[name="sku.color"]').attr('id', 'txtColor_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('input[name="sku.quantity"]').attr('id', 'txtQuantity_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('div[name="valImg"]').attr('for', 'hidPicUrl_' + $index).children('span').remove();
		$prevObj.find('div[name="valColor"]').attr('for', 'txtColor_' + $index).children('span').remove();
		$prevObj.find('div[name="valQuantity"]').attr('for', 'txtQuantity_' + $index).children('span').remove();
		$prev.after($prevObj);
		setBtns();
		if ($plen <= 1) {
			var $del = '<i class="iconfont ml10 ggdelbtn">&#xe606;</i><i class="iconfont pl10 moveUp">&#xe600;</i><i class="iconfont pl10 moveDown">&#xe60d;</i>';
			$prevObj.append($del);
		}
		if ($plen + 1 >= 10) $(this).hide();
	});

	//删除规格描述项
	$(document).on('click', '.ggdelbtn', function() {
		var ilen = $(this).parents('#imgcontainer').find('.gg_container').length;
		if (ilen <= 2) {
			$(this).parent().siblings().find('.ggdelbtn').remove();
			$(this).parent().siblings().find('.moveUp').remove();
			$(this).parent().siblings().find('.moveDown').remove();
		}
		$(this).parent().remove();
		if (ilen <= 10) $('#addSpec').show();
	});
	//上传图片后hover
	$(document).on('mouseover', '.uploadImg', function() {
		var img = $(this).find('img').attr('src');
		if (img) {
			$(this).find('.mask').show();
		}
	});
	$(document).on('mouseout', '.uploadImg', function() {
		var img = $(this).find('img').attr('src');
		if (img) {
			$(this).find('.mask').hide();
		}
	});

	// --------详情编辑切换
	function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); 
    return null;
	}
	CDT.itemId = GetQueryString("id");
	$(document).on('click', '.h5tab', function() {
		$(this).addClass('active').prev().removeClass('active');
		CDT.ue.setHide();
		CDT.h5ue.setShow();
		$('#contentEditor').next().hide();
		$('#h5contentEditor').next().show();
		if(CDT.itemId){
			$.cookie('the_cookie_' + CDT.itemId, 'h5', {
				expires: 7
			});
		}
	});

	$(document).on('click', '.PCtab', function() {
		$(this).addClass('active').next().removeClass('active');
		CDT.h5ue.setHide();
		CDT.ue.setShow();
		$('#contentEditor').next().show();
		$('#h5contentEditor').next().hide();
		if(CDT.itemId){
			$.cookie('the_cookie_' + CDT.itemId, 'PC', {
				expires: 7
			});
		}
	});

	// 代销价和批发价checkbox选中及取消效果
	$('input[type="checkbox"]').click(function() {
		$(this).prop("checked");
		if ($(this).attr('checked') == 'checked') {
			$(this).parent().next().find('.checkboxPrice').show();
		} else {
			$(this).parent().next().find('.checkboxPrice').hide();
		}
	});

	// 增加参考链接描述项
	$(document).on('click', '#addUrl', function() {
		var $plen = $(this).parents('#referUrlList').find('.rederurl_item').length,
			$index = new Date().getTime(),
			$parents = $(this).parents('#referUrlList').find('.rederurl_item');
		if ($plen >= 5) return;
		var $prev = $parents.eq(0);
		var num = $parents.eq(0).find('option').length;
		var $last = $parents.eq(-1);
		var $prevObj = $prev.clone();
		$prevObj.find('input').val('');
		$prevObj.find('select').val('');
		//var $sibl = [];
		$.each($parents.find('select'),function(index,obj){
			//$sibl.push($(obj).val());
			var same = $prevObj.find('option');
			$.each(same,function(i,n){
				if($(n).val() == $(obj).val()){
				$prevObj.find('option').eq(i).remove();
			}
			});
			

		});
		if ($prevObj.find('.iconfont').length == 0) {
			$prevObj.append("<i class='iconfont ml10 delbtns'>&#xe606;</i>");
		}
		if ($plen <= 1) {
			$prev.append("<i class='iconfont ml10 delbtns'>&#xe606;</i>");
		}
		// 处理clone元素属性问题
		$prevObj.find('.selUrl').attr('id', 'selUrl_' + $index).removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		$prevObj.find('.txtUrl').attr('id', 'txtUrl_' + $index).css('border-color', '#bebebe').removeAttr('aria-required')
			.removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
		//$prevObj.find('.txtUrl').next().hide();
		$prevObj.find('div[name="valUrl"]').attr('for', 'txtUrl_' + $index).children('span').remove();
		$last.after($prevObj);
		if ($plen + 1 >= num) $(this).hide();
	});
	//删除参考链接描述项
	$(document).on('click', '.delbtns', function() {
		var ilen = $('#referUrlList .rederurl_item').length-1;
		// 删除当前
		$(this).parent().remove();
		if (ilen== 1) {
			$("#referUrlList .rederurl_item").find('.delbtns').remove();
		}
		if (ilen<= 5) $('#addUrl').show();
	});
}
// 输出产地内容
function printPlaceHTMLbyCache(key, trimEmpty) {
	if (trimEmpty && trimEmpty == true) {
		$('#placeList ul').empty();
	}
	$.each(CDT.placeSearchCache[key], function(index, obj) {
		$('#placeList ul').append("<li data-id='" + obj.id + "'>" + obj.name + "</li>");
	});
	$('#placeList').show();
}
// 输出品牌内容
function printBrandHTMLbyCache() {
	var kw = $('#brandInput').val();
	$('#brandList ul').empty();
	if (!kw || kw.length < 1) return;
	Tr.get('/dpl/brand/search', {
		'keyWord': kw
	}, function(data) {
		if (data.code != 200 || !data.results || data.results.length <= 0) return;
		$.each(data.results, function(index, obj) {
			var secondaryName = '';
			if(obj.secondaryName){
				secondaryName = "<span>／" + obj.secondaryName + "</span>";
			}
			$('#brandList ul').append("<li data-id='" + obj.id + "'><span>" + obj.name + "</span><div class='tip'><span>" + obj.name + "</span>"+secondaryName+"</div></li>");
		});
	}, {
		loadingMask: false
	});
}

$(function() {
	$(window).bind('beforeunload',function(){return '确认离开当前页面？您的商品编辑还未保存';});
	// 初始化PC宝贝详情编辑器
	CDT.ue = UE.getEditor('contentEditor', {
		initialFrameHeight: 300,
		autoWidth: false
	});

	CDT.ue.addListener('ready', function(editor) {
		$('#edui346_state').unbind();
		$('#edui346_body').unbind().find('.edui-box').html('');
		loadUpToken_u('edui346_body');
	});

	// 初始化H5宝贝详情编辑器
	CDT.h5ue = UE.getEditor('h5contentEditor', {
		initialFrameHeight: 300,
		autoWidth: false
	});

	CDT.h5ue.addListener('ready', function(editor) {
		$('#edui139_state').unbind();
		$('#edui139_body').unbind().find('.edui-box').html('');
		loadUpToken_h('edui139_body');
		CDT.h5ue.setHide();
	});
	// 初始化数据
	initBase();
	loadUpToken();
	loadProviceHTML();

	/*验证*/
	$('#btnSubmit').click(function() {
		// PC详情设置
		$('#itemDetail').val(CDT.ue.getContent());
		// 手机详情设置
		$('#h5itemDetail').val(CDT.h5ue.getContent());
		var validator = $('#formComm').validate(ValidateOpts());
		// 提交
		$("[name=referurl_value]").each(function() {
			$(this).rules("add", {
				url: true,
				checkUrl: true
			});
		});
		if (!validator.form()) {
			var height = $('span.error').eq(0).offset().top;
			$(window).scrollTop(height-30);
			return;
		}
		// 批发价格和代销价格必须填写一种
		var $checkDistPrice = $("input[name='item.distPriceUse']").prop('checked');
		var $checkRangePrice = $("input[name='item.priceRangeUse']").prop('checked');
		if (!$checkDistPrice && !$checkRangePrice) {
			alert('代销价与批发价至少填写一种！');
			return;
		}
		var distprice = $('#txtDistPrice').val(),
			conditionTxt = $('.conditionTxt').eq(0).val(),
			priceTxt = $('.priceTxt').eq(0).val();
		// 检查批发价格
		if ($checkDistPrice && !distprice) {
			alert("代销价格未填写！");
			return;
		}
		if ($checkRangePrice && (!conditionTxt || !priceTxt)) {
			alert("批发价格必须填写一项！");
			return;
		}
		// skus
		var $gg_container = $('.gg_container');
		var setPicUrl = false;
		$.each($gg_container, function(index, elobj) {
			var color = $(elobj).find("input[name='sku.color']").eq(0).val(),
				img = $(elobj).find("input[name='sku.img']").eq(0).val(),
				quantity = $(elobj).find("input[name='sku.quantity']").eq(0).val();
			if (quantity && color && img) { // 价格相等,件数却不一样
				if (!setPicUrl) { // 首个作为主图片
					$('#formComm').append("<input type='hide' name='item.picUrl' value='" + img + "'/>");
					setPicUrl = true;
				}
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].color' value='" + color + "'/>");
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].img' value='" + img + "'/>");
				$('#formComm').append("<input type='hide' name='item.skus[" + index + "].quantity' value='" + quantity + "'/>");
			}
		});
		// 设置价格
		var $priceItems = $('#priceItems .priceItem');
		$.each($priceItems, function(index, elobj) {
			var condition = $(elobj).find('input[name=condition_item]').eq(0).val(),
				price = $(elobj).find('input[name=price_item]').eq(0).val();
			if (condition && price) { // 价格相等,件数却不一样
				$('#formComm').append("<input type='hide' name='item.priceRanges[" + index + "].range' value='" + condition + "'/>");
				$('#formComm').append("<input type='hide' name='item.priceRanges[" + index + "].d_price' value='" + price + "'/>");
			}
		});

		// 设置属性
		var $attributes = $('.attribute');
		$.each($attributes, function(index, elobj) {
			var key = $(elobj).find('.nameTxt').eq(0).val(),
				value = $(elobj).find('.valueTxt').eq(0).val();
			if (key && value) { // 价格相等,件数却不一样
				$('#formComm').append("<input type='hide' name='item.properties[" + index + "].key' value='" + key + "'/>");
				$('#formComm').append("<input type='hide' name='item.properties[" + index + "].value' value='" + value + "'/>");
			}
		});

		// 设置收货地址
		var $addressList = $('#addressList .addressContainer .hLocation');
		var addressIndex = 0;
		$.each($addressList, function(index, elobj) {
			var province = $(elobj).data('province'),
				provinceId = $(elobj).data('pid'),
				city = $(elobj).data('city'),
				region = $(elobj).data('region'),
				address = $(elobj).data('address');
			if (province != '' && provinceId && city && region) {
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].index' value='" + index + "'/>");
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].province' value='" + province + "'/>");
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].provinceId' value='" + provinceId + "'/>");
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].city' value='" + city + "'/>");
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].region' value='" + region + "'/>");
				$('#formComm').append("<input type='hide' name='item.sendGoodLocations[" + addressIndex + "].address' value='" + address + "'/>");
				addressIndex++;
			}
		});
		// 保存参考链接
		var $referUrlList = $('#referUrlList .rederurl_item');
		$.each($referUrlList, function(index, elobj) {
			var referurlKey = $(elobj).find('select[name=referurl_key]').val(),
				referurlValue = $(elobj).find('input[name=referurl_value]').val();
			if (referurlKey && referurlValue) {
				$('#formComm').append("<input type='hide' name='item.referUrls[" + index + "].key' value='" + referurlKey + "'/>");
				$('#formComm').append("<input type='hide' name='item.referUrls[" + index + "].value' value='" + referurlValue + "'/>");
			}
		});
		$(window).unbind('beforeunload');
		$('#formComm').submit();
	});
	// 选择省份
	$(document).on('change', '#selProvince', function() {
		var id = $(this).find('option:selected').attr('rid');
		if (!id) {
			return;
		}
		// 省份赋值
		$(this).siblings('.itemProvince').eq(0).val($(this).find('option:selected').text());
		var $me = $(this),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
		$city.find('option').slice(1).remove();
		$region.find('option').slice(1).remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		// 先读缓存
		if (CDT.cityCache[id] && CDT.cityCache[id].length > 0) {
			$.each(CDT.cityCache[id], function(index, obj) {
				$city.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
			});
			$city.children().eq(1).attr('selected', true);
			$city.change();
		} else {
			Tr.get('/dpl/region', {
				id: id
			}, function(data) {
				if (data.code != 200) return;
				CDT.cityCache[id] = data.results;
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
		}
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
		if (CDT.regionCache[id] && CDT.regionCache[id].length > 0) {
			$.each(CDT.regionCache[id], function(index, obj) {
				$region.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
			});
			$region.children().eq(1).attr('selected', true);
			$region.change();
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			CDT.regionCache[id] = data.results;
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
	//读取cookie
	var name = $.cookie('the_cookie_' + CDT.itemId);
	if (name == 'h5') {
		var test = setTimeout(function() {
			$('.h5tab').addClass('active').prev().removeClass('active');
			CDT.ue.setHide();
			CDT.h5ue.setShow();
			$('#contentEditor').next().hide();
			$('#h5contentEditor').next().show();
		}, 1500);
	} else if (name == 'PC') {
		return
	}
});

// 输出历史记述
function printSendLocationTempHtml() {
	var $loactions = $('#addressSelects');
	$loactions.find('option').slice(1).remove();
	$.each(CDT.sendLoactionTempCache, function(index, location) {
		var locatintxt = location.country +
			location.province +
			location.city +
			location.region +
			location.address;
		$loactions.append("<option value='" + location.id + "'>" + locatintxt + "</option>");
	});
}

// 加载省份信息
function loadProviceHTML() {
	var $provice = $('.province');
	//先读取缓存
	if (CDT.provinceCache && CDT.provinceCache.length > 0) {
		$.each(CDT.provinceCache, function(index, obj) {
			$provice.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
		});
		var loadV = $provice.data('val');
		if (loadV && loadV.length > 0) {
			$.each($provice.children(), function(index, obj) {
				if ($(obj).text().trim() == loadV.trim()) {
					$(obj).attr('selected', true);
				}
			});
		} else {
			$provice.children().eq(0).attr('selected', true);
		}
	} else {
		Tr.get('/dpl/region', {
			id: 1
		}, function(data) {
			if (data.code != 200) return;
			CDT.provinceCache = data.results;
			if (data.results && data.results.length > 0) {
				$.each(data.results, function(index, obj) {
					$provice.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
				});
				var loadV = $provice.data('val');
				if (loadV && loadV.length > 0) {
					$.each($provice.children(), function(index, obj) {
						if ($(obj).text().trim() == loadV.trim()) {
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
}

function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		setBtns();
	}, {
		loadingMask: false
	});
}

function setBtns() {
	$('.mask').each(function(i) {
		var uId = this.id;
		if (uId && $.inArray(uId, CDT.uploadIDSCache) == -1) {
			initItemMainPicUploader(uId);
			CDT.uploadIDSCache.push(uId);
		}
	});
}

function initItemMainPicUploader(domId) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.tusibaby.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = domId;
	option.container = 'imgcontainer';
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
			var imgId = $('#' + domId).attr('img-container');

			/*添加图片*/
			$('#' + imgId).find('img').attr('src', sourceLink);
			CDT.uploadImgCache[file.name] = sourceLink;
			$('#' + domId).find('input').val(sourceLink);
			$('#' + domId).hide();
			//$('#formComm').valid();
			$('#' + domId).find('input').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
			$('div[name="valImg"]').children('span').remove();
		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// // alert('请不要上传重复的图片');
				var imgId = $('#' + domId).attr('img-container');
				$('#' + imgId).find('img').attr('src', sourceLink);
				$('#' + domId).find('input').val(sourceLink);
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

function loadUpToken_u(ueid) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.tusibaby.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = ueid;
	option.container = 'PCcontainer';
	option.multi_selection = true;
	option.filters.max_file_size = '5mb';
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
			CDT.ue.execCommand('inserthtml', '<br/><img src="' + sourceLink + '"/>');

		},
		'Error': function(up, err, errTip) {
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

function loadUpToken_h(h5ueid) {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.tusibaby.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = h5ueid;
	option.container = 'h5container';
	option.multi_selection = true;
	option.filters.max_file_size = '5mb';
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
			CDT.h5ue.execCommand('inserthtml', '<br/><img src="' + sourceLink + '"/>');
		},
		'Error': function(up, err, errTip) {
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