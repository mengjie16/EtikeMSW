$(function () {
	$(document).on('change','.weight',function(){
		var reg = /^[0-9]+(.[0-9]{2})?|(.[0-9]{1})?$/;
		var $me = $(this),
				price = $me.val();
		if(!reg.test(price)){
			$me.val(0);
		}else{
			$me.val(price);
		}	
	});
	$(document).on('click','.countBtn',function () {
		var weightVal = $('.weight').val();
		if(weightVal=="undefined"||weightVal==''||weightVal==null){
			alert('请填写重量');
			return;
		}
		var weight = parseFloat(weightVal);
		var $tr = $('.tbHoverable tbody tr');
		$.each($tr,function(index,obj) {
			var firstWeight = parseInt($(obj).find('.firstWeight').data('num'));
			var fwPrice = parseInt($(obj).find('.firstPrice').data('num'));
			var addedWeight = parseInt($(obj).find('.nextWeight').data('num'));
			var awPrice = parseInt($(obj).find('.nextPrice').data('num'));

			// 首重价格,或无法计算价格
            var price = 0; // 单位分
            // 未超出首重
            if (weight <= firstWeight) {
                price = fwPrice;
            } else if (weight > firstWeight) { // 超出首重
                // 计算续重每千克费用，如果续重价格为0，续重费用为0，则续重为0
                var addedAvg = awPrice == 0 || addedWeight == 0 ? 0 : awPrice / addedWeight;
                // 单件总邮费为，首重价格＋续重千克＊续重价格
                price = (weight - firstWeight) * addedAvg + fwPrice;
            }
            // 邮费(金额元向上取整［520分＝6元］，邮费不可能很大)
            var lastPrice = Math.ceil(price/100);
			var totalPrice = $(obj).find('.totalPrice').text(lastPrice);
			$('.showcontent').hide();
			$('.result').show();
			$('.backBtn').css('display','inline-block');
		})
	});
	$(document).on('click','.backBtn',function() {
		$('.weight').val('');
		$('.totalPrice').text('');
		$('.showcontent').show();
		$('.result').hide();
		$(this).hide();
	});
	//导出运费模板excell
	//$(document).on('click','#exportExcelbtn',function(){
		//$(this).attr('href','');
	//});
})