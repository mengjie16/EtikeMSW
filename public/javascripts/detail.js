CDT = {
    provinceCache: {}
};
var able = 0;
$(window).load(function() {
    $('.flexslider').flexslider({
        animation: "slide",
        controlNav: "thumbnails"
    });
});

function initBase() {
    var mid = $('#single .container').attr('mid');
    Tr.get('/user/favorite/hasSetFavorite', {
        "itemId": mid
    }, function(data) {
        if (data.code != 200) {
            return;
        }
        $('.item_collection').text('已收藏').css('background','#333');
    },{loadingMask: false});
    $(".memenu").memenu();
    $('.popup-with-zoom-anim').magnificPopup({
        type: 'inline',
        fixedContentPos: false,
        fixedBgPos: true,
        overflowY: 'auto',
        closeBtnInside: true,
        preloader: false,
        midClick: true,
        removalDelay: 300,
        mainClass: 'my-mfp-zoom-in'
    });
    $('.tags a').on('click', function() {
        $('.tags a').removeClass('active');
        $(this).addClass('active');
    })
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
        var storeNumber = parseInt($('.kucun').text());
        //var storeNumber = 100;
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
    //加入收藏
    $(document).on('click', '.item_collection', function() {
        if($(this).text() == '已收藏') return;
        $(this).text('已收藏').css('background','#333');
        var itemId = $('#single .container').attr('mid');
        var params = {
            "favorite.itemId": itemId,
            "favorite.retailPrice" : parseInt($('#single .container #detailRmb').text().substring(1)),
            "favorite.title" : $('#single .container #detailTitle').text(),
            "favorite.picUrl" : $('#single .container').find('div.picUrl img').eq(0).attr('src')
        };
        Tr.post('/user/favorite/save', params, function(data) {

            if (data.code != 200) {
                alert('加入失败');
                return;
            }
            alert('加入成功');

        },{loadingMask: false});
    });

    //加入购物车弹出弹框
    $(document).on('click', '.b-buyNow', function() {
        if (able == 0) {
            var num = 0;
            $('.qnty').each(function() {
                if ($(this).val() != '') {
                    num += parseInt($(this).val());
                }
            });
            if (!num) {
                alert('请选择商品！');
                return;
            }
            $('.alertCart').show();
        }
    });

    //添加购物车

    $(document).on('click', '.item_add', function() {
        var itemId = $('#single .container').attr('mid');
        var $getColor = $('#single .container').find('li.skuColor a.active').eq(0).text();
        var cnt =$('#single .container input[name="cartAccount"]').val();
        var totalPriceRMB = parseInt($('#single .container #detailRmb').text().substring(1))*cnt;
        var totalPriceEUR = parseInt($('#single .container #detailEur').text().substring(1))*cnt;
        var purl=$('#single .container').find('div.picUrl img').eq(0).attr('src');
        if(!purl){
            purl = 'http://img0.imgtn.bdimg.com/it/u=2517962916,2542239999&fm=214&gp=0.jpg';
        }
        if(!$getColor){
            alert('请选择分类');
            return;
        }
        var params = {
            "cart.itemId": itemId,
            "cart.skuColor":$getColor,
            "cart.picUrl" : purl,
            "cart.cartCount" : $('#single .container input[name="cartAccount"]').val(),
            "cart.retailPrice" : parseInt($('#single .container #detailRmb').text().substring(1)),
            "cart.cny2eur":parseInt($('#single .container #detailEur').text().substring(1)),
            "cart.cartPrice" : totalPriceRMB,
            "cart.title" : $('#single .container #detailTitle').text(),
            "cart.brandId":$('#single .container #brandId').val()
        };
        Tr.post('/retailer/cart/add', params, function(data) {

            if (data.code != 200) {
                alert('加入购物车失败');
                return;
            }
            alert('加入购物车成功');

        },{loadingMask: false});
    });


}

$(function() {
    initBase();
});
