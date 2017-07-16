// 数据定义
CART = {
		cartCache: new Array(),
		loadCart:false
	};
	// 初始化内容
$(function() {
	// 加载购物车
	//loadCartData();
	$(".memenu").memenu();
    
    $("#J_SelectAllCbx1").click(function(){   
		    if(this.checked){   
		        $('input[name="select-goods"]:input').prop("checked", true);  
		    }else{   
						$('input[name="select-goods"]:input').prop("checked", false);
		    }   
		});
		$('input[name="select-goods"]').on('click',function() {
			if(!this.checked){
				$("#J_SelectAllCbx1").prop("checked", false);
			}
		})
    $('.check-goodsdelete').on('click',function(e){
    	$(this).parent().remove();
    });
    $('.J_Minus').on('click', function(e) {
        $(this).next().next().removeClass('no-plus');
        if ($(this).next()[0].value > 1) {
            $(this).next()[0].value--;
            if (parseInt($(this).next()[0].value) === 1) {
                $(this).addClass('no-minus');
            }
        } else {
            $(this).addClass('no-minus');
        }
    });
    $('.J_Plus').on('click', function(e) {
        var storeNumber = 12;
        $(this).prev().prev().removeClass('no-minus');
        if ($(this).prev()[0].value < storeNumber) {
            $(this).prev()[0].value++;
            console.log($('.text-amount').val())
            if (parseInt($(this).prev()[0].value) === storeNumber) {
                $(this).addClass('no-plus');
            }
        } else {
            $(this).addClass('no-plus');
        }
    });
	//删除购物车里的条目
	$(document).on('click','.delete',function(){
		var id =$(this).data('id');
		if(!id){
			return;
		}
		Tr.post('/retailer/cart/remove',{
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'itemId': id
		},function(data){
			if (data.code != 200) return;
			CART.cartCache = new Array();
			loadCartData();
		}, {
			loadingMask: false
		});
	});
});

// 加载购物车数据
function loadCartData(){
	if(CART.loadCart!=true) return;
	if (CART.cartCache.length <= 0) {
			Tr.get('/retailer/cart/query', {},
				function(data) {
					if (data.code == 200) {
						CART.cartCache = data.results;
					};
					printCartHtml();
				},{loadingMask:false});
		} else {
			printCartHtml();
		}
}
// 输出购物车内容
function printCartHtml() {
	if(CART.cartCache.length>0){
		$('#cartContainer').empty();
		var totalPrice = 0,
			  totalCount = 0;
		$.each(CART.cartCache, function(index, obj) {
			var $titledt = $("<td class='ring-in'><div class='cart-goods-checkbox'><input class='J_CheckBoxShop' type='checkbox' name='select-goods'  value='true'></div><div><a target='_blank' href='/item/" + obj.id + "' class='at-in'><img src=src='" + obj.sku.img + "' class='img-responsive' alt=''></a><div class='sed'><p>"+obj.title+"</p></div></div><div class='clearfix'></div></td>");
			var $basedd = $("<td class='per-price'><div class='check_price'>¥" + obj.cartPrice + "<span class='price-e'>20€</span></div></td>");
			var $skudd = $("<td class='check'><div class='amount-wrapper '><div class='item-amount '><a class='J_Minus minus no-minus'>-</a><input type='text' value='1' class='text text-amount J_ItemAmount' data-max='50' data-now='2' autocomplete='off'><a class='J_Plus plus'>+</a></div><div class='amount-msg J_AmountMsg'></div></div></td><td class='total-price'><div class='check_price allrmb'><span class='price-e allou'></span></div></td>");
			var $fundd = $("<td data-id='"+obj.id+"' class='check-goodsdelete delete'>删除</td>");	
			var $itemdl = $("<tr></tr>");
			$itemdl.append($titledt);
			$itemdl.append($basedd);
			$itemdl.append($skudd);
			$itemdl.append($fundd);
			totalPrice += parseInt(obj.cartPrice)*parseInt(obj.cartCount);
			totalCount += parseInt(obj.cartCount);
			if ($itemdl) {
				$('#cartContainer').append($itemdl);
			}
		});
		return;
	}
}