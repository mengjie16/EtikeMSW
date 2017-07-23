// 数据定义
CART = {
    cartCache: new Array(),
    sendLoactionTempCache: null,
    totalPrice: 0,
    totalCount: 0
};
$(function() {
    // Tr.get('', {},
    // 	function(data) {
    // 		if (data.code == 200) {
    // 			CART.cartCache = data.results;s
    // 		};
    // 		printCartHtml();
    // },{loadingMask:false});

    $('.otherAdd').on('click', function() {

        Tr.get('/retailer/address/addressList', {}, function(data) {
            if (data.code != 200 || !data.results) return;
            CART.sendLoactionTempCache = data.results;
            printSendLocationTempHtml(CART.sendLoactionTempCache);
        }, {
            loadingMask: false
        });
    });
    $('.goBack').on('click', function() {
    	var param ={
    		'tradeId':$('#tradeId').text();
    	};
    	Tr.post('/retailer/order/delete', param, function(data) {
            if (data.code != 200) {
                alert('返回购物车失败!');
                return;
            } else {
                 window.location.href='/user/cart';
            }

        });        
    });
    $(document).on('click', '#btnSubmitAdd .btnSave', function() {
        var $option = $('#addressSelects').find('option:selected');
        var id = $option.attr('value');
        if (id === 'others') {
            alert('未选择地址');
            return;
        }
        $.ajax({
            url: "/retailer/address/get",
            data: {
                id: id
            },
            success: function(result) {
                var obj = result.results;
                $('.order-addresslist').attr('id', id);
                $('#user-name').text(obj.name);
                $('#user-phone').text(obj.phone);
                $('.province').text(obj.province);
                $('.city').text(obj.city);
                $('.dist').text(obj.region);
                $('.street').text(obj.address);
                $("#btnSubmitAdd .closePopUp").click();



            },
            error: function(ex) {
                console.log('can not get the address');
            }
        });
    });
    // 提交支付
    $(document).on('click', '#btnSubmit', function() {
        var boxes = $('input[name="express_id"]:checked').length;
        if (boxes <= 0) {
            alert("请至少选择一种物流方式");
            return false;
        }
        var expressid = $('input[name="express_id"]:checked').attr('id');
        /*var itemVos = [];
        $('#stepContainer tr').each(function(index, obj) {
            var itemVo = {
                "sku": {}
            };
            var $id = $(obj)[0].id;
            var $cartCount = $(obj).find('.cartCount').val();
            var $color = $($(obj).find('.cartColor')[0]).text();
            itemVo["sku"]["color"] = $color;
            itemVo["cartCount"] = $cartCount;
            itemVo["id"] = $id;
            itemVos.push(itemVo);

        });*/
        var express = $('input.expressText:checked').next().text(); //物流方式
        var param = {
            "authenticityToken": $('input[name=authenticityToken]').val(),
            "orderVo.provinceId": $('.provinceId').text(),
            "orderVo.buyerName": $('#user-name').text(),
            "orderVo.contact": $('#user-phone').text(),
            "orderVo.province": $('.province').text(),
            "orderVo.city": $('.city').text(),
            "orderVo.region": $('.dist').text(),
            "orderVo.address": $('.street').text(),
            "orderVo.expNo": expressid,
            "orderVo.express": express,
            "orderVo.statusCode": 'TRADE_UNPAIED',
            "orderVo.statusText": '待付款',
            "orderVo.shippingFee": $('#express_fee').text(),
            "orderVo.totalFee": $('#order_amount').text(),
            "orderVo.note": $('#comment').val(),
            "orderVo.tradeId": $('#tradeId').text()
        };


        Tr.post('/retailer/order/update', param, function(data) {
            if (data.code != 200) {
                alert('提交失败!');
                return;
            } else {
                $('#payForm #txtFee').val(param["orderVo.totalFee"]);
                $('#payForm').submit();
            }

        });

    });
});
// function printCartHtml() {
// 	if(CART.cartCache.length>0){
// 		var totalPrice = 0,
// 			  totalCount = 0;
// 		$.each(CART.cartCache, function(index, obj) {
// 			var $titledt = $("<td><a><img src='" + obj.img + "' class='img'></a></td><td><a>"+obj.title+"</a></td><td align='center'><input name='goods_color' type='hidden' value='"+obj.color+"'></td><td align='center'>+ "+obj.color+"</td>");
// 			var $basedd = $("<td align='center'>￥" + obj.cartPrice + "<input name='goods_price' type='hidden' value='" + obj.cartPrice + "'></td>");
// 			var $skudd = $("<td align='center'>"+obj.cartCount+"</td>");
// 			var $fundd = $("<td align='center'><font color='#FF0000' size='2'>￥<label name='real_amount'></label></font></td><td><input id='car_id' name='car_id' type='hidden' value=''></td>");	
// 			var $itemdl = $("<tr></tr>");
// 			$itemdl.append($titledt);
// 			$itemdl.append($basedd);
// 			$itemdl.append($skudd);
// 			$itemdl.append($fundd);
// 			totalPrice += parseInt(obj.cartPrice)*parseInt(obj.cartCount);
// 			totalCount += parseInt(obj.cartCount);
// 			if ($itemdl) {
// 				$('#stepContainer').append($itemdl);
// 			}
// 		});
// 		$('#goods_amount').text(totalPrice);
// 		$('#order_amount').text(totalPrice);
// 		$('#cartNum').text(totalCount);
// 		return;
// 	}
// }
// 输出其他地址
function printSendLocationTempHtml() {
    var $loactions = $('#addressSelects');
    $loactions.find('option').slice(1).remove();
    $.each(CART.sendLoactionTempCache, function(index, location) {
        var locatintxt = location.name + ' ' + location.phone + ' ' + location.country +
            location.province +
            location.city +
            location.region +
            location.address;
        $loactions.append("<option value='" + location.id + "'>" + locatintxt + "</option>");
    });
}

function FreightAmountTotal(obj) {
    var price = parseInt($(obj).siblings('.Validform_checktip').text());
    $('#express_fee').text(price);
    $('#order_amount').text(price + parseInt($('#goods_amount').text()));
    $('#txtFee').val(price + parseInt($('#goods_amount').text()));
}

function openModal() {
    $('#doc-modal-1').modal({
        /*        onConfirm: function() {  
                    var username=document.getElementById("username").value;  
                    var password=document.getElementById("password").value;  
                    alert("用户点击了提交，输入了用户名："+username+"，密码："+password+"，接下去一般是ajax提交表单");  
                },  */
        onCancel: function() {
            alert("用户点击了关闭按钮");
        }
    });
}
