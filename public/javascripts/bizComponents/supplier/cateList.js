function initBase(argument) {
	//置顶
	$('.moveUpAll').on('click',function(){
		var index = $(this).parents('tr').index();
		if(index==0){
			$(this).css('color','#ccc');
		}else{
			$(this).parents('tr').prependTo('.container');
		}
	});
	//置底
	$('.moveDownAll').on('click',function(){
		var index = $(this).parents('tr').index();
		if(index==$('.container>tr').size() - 1){
			$(this).css('color','#ccc');
		}else{
			$(this).parents('tr').appendTo('.container');
		}
	});
	//向上移动
	$('.moveUp').on('click',function(){
		var index = $(this).parents('tr').index();
		if(index==0){
			$(this).css('color','#ccc');
		}else{
			$(this).parents('tr').prev().before($(this).parents('tr'));
		}
	});
	//向下移动
	$('.moveDown').on('click',function(){
		var index = $(this).parents('tr').index();
		if(index==$('.container>tr').size() - 1){
			$(this).css('color','#ccc');
		}else{
			$(this).parents('tr').next().after($(this).parents('tr'));
		}
	});
	$('.inputText').on('blur',function(){
		$('.alertChange').show();
	});
	$('.cansel').on('click',function(){
		$('.alertChange').hide();
	});
}
$(function() {
	initBase();
});