var timeoutId = 0;
$(document).ready(function () {
	timeoutId = setTimeout("configErr()", 6000);
	//注册权限
	wx.config({
	    debug: CDTWXConfig.debug,
	    appId: CDTWXConfig.appId,
	    timestamp: CDTWXConfig.timestamp,
	    nonceStr: CDTWXConfig.nonceStr,
	    signature: CDTWXConfig.signature,
	    jsApiList: CDTWXConfig.jsApiList
	});
});
//提示初始化错误
function configErr() {

}
// 微信ready
wx.ready(function(){
	clearTimeout(timeoutId);
	shareReady();

});
// 分享注册
function shareReady(){
	// 获取“分享到朋友圈”按钮点击状态及自定义分享内容接口
	wx.onMenuShareTimeline({
	    title:$('title').text(), // 分享标题
	    link: CDTWXConfig.url, 		// 分享链接
	   //imgUrl: '${articleMsg.picurl}', // 分享图标
	    success: function () { 
			// $('#marks').hide();
	  //   	// 用户确认分享后执行的回调函数
	  //       Tr.post('/share/confirm', {}, function(data){
	  //       	if(data.code==200){
		 //        	$('.ck-rescoin').text(50);
			// 		// 我金币刷新，也要做动画的
			// 		$('.user-coin strong').text(parseInt($('.user-coin strong').text()) + 50);
			// 		$('.animate').show().animate({
			// 			translate3d: '0,-30px,0'
			// 		}, 3000,function(){
			// 			$('.animate').hide().animate({
			// 				translate3d: '0,0,0'
			// 			});
			// 		});
	  //       	}
	  //       });
	    },
	    cancel: function () { 
	        // 用户取消分享后执行的回调函数
	    }
	});

	//获取“分享给朋友”按钮点击状态及自定义分享内容接口
	wx.onMenuShareAppMessage({
	     title:$('title').text(), // 分享标题
	    //desc: '${articleMsg.description}', // 分享描述
	    link: CDTWXConfig.url, // 分享链接
	   // imgUrl: '${articleMsg.picurl}', // 分享图标
	    //type: 'link', // 分享类型,music、video或link，不填默认为link
	    //dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
	    success: function () { 
	    // 	$('#marks').hide();
	    //     // 用户确认分享后执行的回调函数
	    //     Tr.post('/share/confirm', {}, function(data){
	    //     	if(data.code==200){
		   //      	$('.ck-rescoin').text(50);
					// // 我金币刷新，也要做动画的
					// $('.user-coin strong').text(parseInt($('.user-coin strong').text()) + 50);
					// $('.animate').show().animate({
					// 	translate3d: '0,-30px,0'
					// }, 3000,function(){
					// 	$('.animate').hide().animate({
					// 		translate3d: '0,0,0'
					// 	});
					// });
	    //     	}
	    //     });
	    },
	    cancel: function () { 
	        // 用户取消分享后执行的回调函数
	    }
	});

	// wx.hideMenuItems({
	// 	menuList: ['menuItem:copyUrl'] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
	// });
}