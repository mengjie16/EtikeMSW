$(function() {
	$(document).on('click','.s-cell',function(){
		$('.s-cell').removeClass('highlight').css({'border-bottom':'1px solid #e0e0e0','border-right':'1px solid #e0e0e0'});
		$('th').removeClass('highlight');
		$(this).parent().find('.s-cell').addClass('highlight').css('border-bottom','1px solid #0579C0');
		$(this).parent().find('.s-cell:last').css('border-right','1px solid #0579C0')
		$(this).parent().prev().find('.s-cell').css('border-bottom','1px solid #0579C0');
		$(this).parent().find('th').addClass('highlight');
	});
});
