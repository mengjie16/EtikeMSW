App = {};

$(function() {
	$('.navLink[module="'+ App.module +'"]').addClass('active');
	loadNav();
});
function loadNav(){
	var txt = $('.container_left').find('.active').text(),
			tp = $('.container_left').find('.active').parents('ul').prev().find('span').text();
	$('#mdetail').text('> '+txt);
	$('#mblock').text('> '+tp);
	var tt = $('.boriboriCSMenusContent').find('.active').text();
	$('#detail').text('> '+tt);
}