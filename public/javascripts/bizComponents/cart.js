// 数据定义
CART = {
    cartCache: new Array(),
    loadCart: false,


};

// 初始化内容
$(function() {
    // 加载购物车
    var dtd = $.Deferred();

    $(".memenu").memenu();
    $.when(loadCartData()).done(function() {
        var hasCheckedBoxes = [];
        $("#J_SelectAllCbx1").click(function(e) {
            if (this.checked) {
                $('input[name="select-goods"]:input').prop("checked", true);
                var number = 0;
                $('.price-rmbs').each(function() {
                    if ($(this).text() != '') {
                        number += parseInt($(this).text());
                    }
                });
                $('.check-trans-rmb').text(number);
                $('.check-count').text($('.price-rmbs').length);
                var arrCheckBox = $('input[name="select-goods"]:input');
                hasCheckedBoxes = $.map(arrCheckBox, function(ele, index) {
                    return $(ele).data('cart');
                });
            } else {
                $('input[name="select-goods"]:input').prop("checked", false);
                /*$('input[name="select-goods"]:input').prop("checked", false);*/
                $('.check-trans-rmb').text('0');
                $('.check-count').text('0');
                hasCheckedBoxes = [];
            }
        });
        $('input[name="select-goods"]').on('click', function(e) {
                var num = parseInt($('.check-trans-rmb').text());
                var number = parseInt($('.check-count').text());
                var price = $(this).parents('tr').find('.price-rmbs').text();
                var cartId = $(this).data('cart');
                if (!this.checked) {
                    $("#J_SelectAllCbx1").prop("checked", false);
                    num -= parseInt(price);
                    number -= 1;
                    var index = hasCheckedBoxes.indexOf(cartId);
                    if (index > -1) {
                        hasCheckedBoxes.splice(index, 1);
                    }
                } else {
                    num += parseInt(price);
                    number += 1;
                    hasCheckedBoxes.push(cartId);
                }
                $('.check-trans-rmb').text(num);
                $('.check-count').text(number);
            })
            // $('.check-goodsdelete').on('click', function(e) {
            //     $(this).parent().remove();
            // });

        function J_Minus() {
            $('.J_Minus').bind('click', function(e) {
                $(this).next().next().removeClass('no-plus');
                if ($(this).next()[0].value > 1) {
                    $(this).next()[0].value--;
                    var num = $('.text-amount').val();
                    var price = $(this).parents('.check').prev('.per-price').find('.price-rmb').text();
                    $(this).parents('.check').next('.cart-price').find('.price-rmbs').text(num * price);
                    if (parseInt($(this).next()[0].value) === 1) {
                        $(this).addClass('no-minus');
                    }
                } else {
                    $(this).addClass('no-minus');
                }
            });
        };

        function J_Plus() {
            $('.J_Plus').bind('click', function(e) {
                var storeNumber = $($(this).prev()[0]).data('max');
                //var storeNumber = 100;
                $(this).prev().prev().removeClass('no-minus');
                if ($(this).prev()[0].value < storeNumber) {
                    $(this).prev()[0].value++;
                    var num = $('.text-amount').val();
                    var price = $(this).parents('.check').prev('.per-price').find('.price-rmb').text();
                    $(this).parents('.check').next('.cart-price').find('.price-rmbs').text(num * price);
                    if (parseInt($(this).prev()[0].value) === storeNumber) {
                        $(this).addClass('no-plus');
                    }
                } else {
                    $(this).addClass('no-plus');
                }
            });
        };
        $('#editCount').on('click', function() {
            $(this).toggleClass('editCount'); //逻辑
            if ($(this).hasClass('editCount')) {
                $('#cartContainer .cartCount').each(function(index, ele) {
                    ele.readOnly = false;
                    $('#editCount').text('完成编辑');
                    $('#editCount').addClass('doneEdit'); //样式
                });
                $('.J_Plus').bind('click', J_Plus());
                $('.J_Minus').bind('click', J_Minus());
            } else {
                $('#cartContainer .cartCount').each(function(index, ele) {
                    ele.readOnly = true;
                });
                $('.J_Plus').unbind('click');
                $('.J_Minus').unbind('click');
                $.when(doneEdit()).done(function() {
                    $.when(updateCart()).done(function() {

                        alert('更新购物车');

                    });



                });

            }
        });

        function doneEdit() {
            $('#editCount').removeClass('doneEdit');
            $('#editCount').text('编辑数量');
        }

        function updateCart(tag) {
            var dtd = $.Deferred();
            var carts = [];
            var tag;
            $('#cartContainer .lineCart').each(function(index, ele) {
                var cart = {};
                var $id = $(ele)[0].id;
                var $cartCount = $(ele).find('.cartCount').val();
                var $cartPrice = $(ele).find('.price-rmbs').text();
                cart["cartCount"] = $cartCount;
                cart["id"] = $id;
                cart["cartPrice"] = $cartPrice;

                carts.push(cart);

            });

            var params = {
                "authenticityToken": $('input[name=authenticityToken]').val(),
                carts: JSON.stringify(carts)
            };

            $.ajax({
                type: "post",
                url: '/retailer/cart/updateBatchCount',
                data: params,
                success: function(data) {

                    if (data.code != 200) {
                        return;
                    } else if (data.code === 200) {
                        tag = true;
                        dtd.resolve(tag);
                    }

                }

            });
            return dtd.promise(tag);
        }

        $('.to-buy').on('click', function() {
            var that = this;
            if (!validEditDone()) {
                alert('请完成商品数量编辑');
                $(this).attr('href', 'javascript:void(0);');
                return;
            } else {
                if (hasCheckedBoxes.length > 0) {
                    $.when(checkedToPay(hasCheckedBoxes)).done(function() {
                        /*$(that).attr('href', '/user/cart/stepTwo');*/
                    }).fail(function() {
                        alert("跳转失败");
                        $(that).attr('href', 'javascript:void(0);');
                        return;
                    });
                } else {
                    alert('未选择商品');
                    $(that).attr('href', 'javascript:void(0);');
                    return;
                }
            }

        });

        //删除购物车里的条目
        $(document).on('click', '.delete', function() {
            var id = $(this).data('id');
            if (!id) {
                return;
            }
            Tr.post('/retailer/cart/remove', {
                'authenticityToken': $('input[name=authenticityToken]').val(),
                'id': id
            }, function(data) {
                if (data.code != 200) return;
                CART.cartCache = new Array();
                loadCartData();
            }, {
                loadingMask: false
            });
        });



    }).fail(function() {
        aler("购物车跑了……");
    });
});
// 加载购物车数据
function loadCartData() {
        var dtd = $.Deferred();
        //if(CART.loadCart!=true) return;
        if (CART.cartCache.length <= 0) {
            Tr.get('/retailer/cart/query', {},
                function(data) {
                    if (data.code == 200) {
                        CART.cartCache = data.results;
                    };
                    $.when(printCartHtml()).done(function() {
                        dtd.resolve();
                    });
                }, {
                    loadingMask: false
                });
        } else {
            printCartHtml();
        }
        return dtd.promise();
    }
    // 输出购物车内容
function printCartHtml() {
    var dtd = $.Deferred();
    if (CART.cartCache.length > 0) {
        $('#cartContainer').empty();
        $.each(CART.cartCache, function(index, obj) {
            var id = obj.id;
            var itemId = obj.itemId;
            var brandName = obj.brand.name;
            var cartCount = obj.cartCount;
            var picUrl = obj.picUrl;
            var retailPrice = obj.retailPrice;
            var title = obj.title;
            var color = obj.skuColor;
            var quantity = obj.skuQuantity;
            var cartPrice = cartCount * retailPrice;
            var $titledt = $("<td class='ring-in'><div class='cart-goods-checkbox'><input data-cart='" + id + "' class='J_CheckBoxShop' type='checkbox' name='select-goods'  value='true'></div><div><a target='_blank' href='/item/" + itemId + "' class='at-in'><img src='" + picUrl + "' class='img-responsive' alt=''></a><div class='sed'><p class='detailCart><span class='brand'>" + brandName + "</span><span class='title'>" + title + "</span><span class='color'>" + color + "</span></p></div></div><div class='clearfix'></div></td>");
            var $basedd = $("<td class='per-price'><div class='check_price'>¥<span class='price-rmb'>" + retailPrice + "</span></div></td>");
            var $skudd = $("<td class='check'><div class='amount-wrapper'><div class='item-amount '><a class='J_Minus minus  updateVal'>-</a><input type='text' data-id='" + itemId + "' value='" + cartCount + "' class='cartCount text text-amount J_ItemAmount' data-max='" + quantity + "' data-now='2' autocomplete='off'><a class='updateVal J_Plus plus'>+</a></div><div class='amount-msg J_AmountMsg'></div></div></td>");
            var $totaldd = $("<td class='cart-price'><div class='check_price allrmb'>¥<span class='price-rmbs'>" + cartPrice + "</span></div></td>");
            var $fundd = $("<td data-id='" + id + "' class='check-goodsdelete delete'>删除</td>");
            var $itemdl = $("<tr class='lineCart' id='" + id + "'></tr>");
            $itemdl.append($titledt);
            $itemdl.append($basedd);
            $itemdl.append($skudd);
            $itemdl.append($totaldd);
            $itemdl.append($fundd);
            /*                    totalPrice += parseInt(obj.cartPrice) * parseInt(obj.cartCount);
                                totalCount += parseInt(obj.cartCount);*/
            if ($itemdl) {
                $('#cartContainer').append($itemdl);
            }
            $('.cartCount')[0].readOnly = true;
        });
        return dtd.resolve();
    }
};


function checkedToPay(cartIds) {
    var dtd = $.Deferred();
    var cartIdArr = cartIds;
    var params = {
        "confirmOrder": cartIdArr.join(',')
    };
    Tr.post('/retailer/order/generate', params, function(data) {
        if (data.code != 200) {
            dtd.reject();
        } else {
            var tradeId = data.results;
            if(tradeId){
               window.location.href="/user/cart/stepTwo?tradeId="+tradeId;  
            }
            
            dtd.resolve();
        }


    });
    return dtd.promise();
}

function validEditDone() {
    return !$('#editCount').hasClass('editCount');
};
