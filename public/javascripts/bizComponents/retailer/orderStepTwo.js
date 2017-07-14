CDT = {
	lineColor : '#587584',
	circleColor : '#587584'
}
var enter = true;
function initBase() {
	//点击选择文件显示文件名
	$(".file").on("change", "input[type='file']", function() {
		var filePath = $(this).val();
		if (filePath.indexOf("xls") != -1 || filePath.indexOf("xlsx") != -1) {
			var arr = filePath.split('\\');
			var fileName = arr[arr.length - 1];
			$('.showFileName').html(fileName);
			$('#analysisBtn').animate({
			  left: 615, opacity: 'show'
			}, 2000);
		} else {
			$('.showFileName').html("");
			return false
		}
	});
	//上传显示用户excel表头列表
	$(document).on('click', '#analysisBtn', function() {
		if(!$('#uploadExcelFile').val()||$('.showFileName').text()=='未选择文件'){
			alert("未选择文件！");
			 return;
		}
		$('.excel_data_div ul').empty();
		$(".excel_data_div").empty();
		$('#line_canvas').empty();
		orderMapper = {};
		indexMark = [];
		arr = [];
		$('.showright .showitem').removeClass('matched');
		$.ajaxFileUpload({
			type:'POST',
			url: '/retailer/order/upload', //用于文件上传的服务器端请求地址
			//secureuri:false, //一般设置为false
			data:{'authenticityToken':$('#formUploadExcel input[name=authenticityToken]').val()},
			fileElementId: 'uploadExcelFile',  
			dataType: 'json', //返回值类型 一般设置为json
			complete: function() { //只要完成即执行，最后执行
			},
			success: function(data, status) //服务器成功响应处理函数
				{					
					if(data.code!=200){
						alert(data.msg);
						$('.uploadStatus').text('上传失败');
						$(".showFileName").html("");
						return;
					}
					$('.showFileName').text('重新选择文件');
					$('.uploadStatus').text('上传成功');
					//  这里就是做一些其他操作，比如把图片显示到某控件中去之类的
					$.each(data.results, function(index, obj) {
						setTimeout(function(){
							$('.excel_data_div').append('<div class="showitem" style="display:none" data-index="'+index+'">'+obj+'</div>');
							$('.showitem').eq(index).fadeIn();
						}, index * 300);
					});
					var len = $('.showright .showitem').length,plen = data.results.length;
					if(plen < len){
						setTimeout(function(){
							var n = len - plen;
							for(var i=0 ;i<n;i++){
								$('.excel_data_div').append('<div class="showitem emptyitem" data-index="'+(plen+i)+'"></div>');
							}
						}, plen * 310);
					}
					setTimeout('creatline()',data.results.length*350);
				}
		})
	});

	//跳至下一步
	$(document).on('click','#btnSubmit .btnStd',function(){
		if(indexMark.length==0){
			alert('请先进行连线再进行下一步操作');
			return;
		}
		if(!enter) return;
		$.each($('.showright .showitem'),function (index,obj) {
			if($(obj).data('must')){
				arr.push(index);
			}
		});
		var warnhtml = '';
		$.each(arr,function(m,n){
			if($.inArray(n,indexMark)==-1){
				var item = $('.showright .showitem').eq(n).text();
				warnhtml += '<p class="lineto">' + item + '未进行连线！</p>';
			}
		});
		if(warnhtml.length>0){
			$('.errorText').html(warnhtml);
			$('.popupError').show();
			enter = false;
			return;
		}
		// 先提交标头对应关系
		// 请求解析
		Tr.post('/retailer/ordervo/parse',orderMapper, function(data) {
				if (data.code == 200) {
					window.location.href = '/retailer/order/stepThree';
				}else if(data.code == 8001){
					var $html = '';
					$.each(data.results, function(index, obj) {
						$html += '<div class="errorItem">' + obj + '</div>';
					});
					$('.errorText').html($html);
					$('.popupError').show();
					var height = $('.popupError').height();
					var top = $(window).height();
					if(height<top-100){
						$('.popupError').css('margin-top',-height/2);
						$('html').css('overflow','hidden');
					}else{
						$('.popupError').css('margin-top',-(top-100)/2);
						$('.errorText').css({'height':top-250,'overflow-y':'auto'});
						$('html').css('overflow','hidden');
					}
				}else {
					alert(data.msg);

				}
		});
		
	});
	//确认警告
	$(document).on('click','.sureBtn',function () {
		$('.popupError').hide();
		arr = [];
		enter = true;
		$('html').css('overflow','auto');
	});					
}
var orderMapper = {};//提交参数
var indexMark = [];//已连接index
var arr = [];//必连项
$(function() {
	initBase();
});
//绘制连线
function creatline(){
	$('.uploadStatus').text('请将左侧“您的Excel表头”和右侧“艾泰客数据字段”进行连线,系统会在连线后自动进行匹配');
	var height = $(".lineTo").height();
	var canvas = document.getElementById('line_canvas');
	var paper = Raphael(canvas, 700, height);
	var cxt = paper.rect(0,0,700,height)
	cxt.attr({
		"stroke-width":"0",
		"fill": "#fff",
		"opacity":0,
		"filter":"alpha(opacity:0)"
	});
	var line,line2;//画线
	var imagedel;//删除图片
	var circle1,circle2;//两端圆圈
	var pressStatus = false,leftStatus = false,rightStatus = false;//记录按下的状态
	var index_down,index_up,index_click;//鼠标按下与抬起时记录的当前项index值
	var mid_startx,mid_starty,mid_endx,mid_endy;//存储虚拟连线起始坐标
	var mouseDownX = 0;
	var mouseDownY = 0;//鼠标进行操作的坐标
	cxt.mousedown(OnMouseDown);
	cxt.mousemove(OnMouseMove);
	cxt.mouseup(OnMouseUp);//连线中的事件
	var changed,this_one,matched;//用于左侧移位
	var matchList = ['','','','','','','','','','','','',''];//存储已连接对象
	var mouseStatus = false;
	$(document).on('mouseup','.line_canvas',function(){
		mouseStatus = true;
		if(pressStatus&&mouseStatus){
			leftStatus = false;
			rightStatus = false;
			pressStatus = false;
			mouseStatus = false;
			line.remove();
			return;
		}
	});
	//元素节点位置互换
	function exchangePos(elem1, elem2){
    if(elem1.length === 0 && elem2.length === 0){
        return;
    }
    if(elem1.index()==(elem2.index()+1)){
    	elem1.after(elem2);
    	return;
    }
    var next = elem2.next(),
        parent = elem2.parent();
    elem1.after(elem2);
    if(next.length === 0){
        parent.append(elem1);
    }else{
        next.before(elem1);
    }
	}
	//鼠标按下状态
	function OnMouseDown(event,x,y){
		mouseDownX = x-$('#line_canvas').offset().left;
		mouseDownY = y-$('#line_canvas').offset().top;
		// 处理左边INDEX
		index_down = -1;
		if(mouseDownX>=0&&mouseDownX<=240){
			if((mouseDownY%45)>=0&&(mouseDownY%45)<=30){
				index_down =  Math.floor(mouseDownY/45);
				var empty = $('.showleft .showitem').eq(index_down).hasClass('emptyitem');
				if(empty) return;
				mid_startx = 230;
				mid_starty = index_down*45+15;
				pressStatus = true;
				leftStatus = true;
			}
		}else if(mouseDownX>=460&&mouseDownX<=700&&mouseDownY>=0&&mouseDownY<=585){//处理右边INDEX
			if((mouseDownY%45)>=0&&(mouseDownY%45)<=30){
				index_down =  Math.floor(mouseDownY/45);
				mid_startx = 470;
				mid_starty = index_down*45+15;
				if($.inArray(8,indexMark)!=-1){
					if(index_down==4||index_down==5||index_down==6||index_down==7){
						return;
					}
				}else if($.inArray(4,indexMark)!=-1||$.inArray(5,indexMark)!=-1||$.inArray(6,indexMark)!=-1||$.inArray(7,indexMark)!=-1){
					if(index_down==8){
						return;
					}
				}
				pressStatus = true;
				rightStatus = true;
			}
		}
	}
	//鼠标按住移动
	function OnMouseMove(event,x,y){
		mouseDownX = x-$('#line_canvas').offset().left;
		mouseDownY = y-$('#line_canvas').offset().top;
		if(!pressStatus){
			return;
		}
		if(leftStatus){
			if($.inArray(index_down,indexMark)!=-1) {
				leftStatus = false;
				rightStatus = false;
				pressStatus = false;
				return;
			}
			if(line)
			line.remove();
			if(mouseDownX<=230){
				line = paper.path('M'+mid_startx+','+mid_starty+'L'+(mouseDownX+4)+','+mouseDownY);
				line.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
				if(mouseDownX<1){
					leftStatus = false;
					rightStatus = false;
					pressStatus = false;
					line.remove();
					return;
				}
			}else if(mouseDownX>230){
				line = paper.path('M'+mid_startx+','+mid_starty+'L'+(mouseDownX-2)+','+mouseDownY);
				line.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
			}
		}else if(rightStatus){
			if($.inArray(index_down,indexMark)!=-1) {
				leftStatus = false;
				rightStatus = false;
				pressStatus = false;
				return;
			}
			if($.inArray(8,indexMark)!=-1){
				if(index_down==4||index_down==5||index_down==6||index_down==7){
					leftStatus = false;
					rightStatus = false;
					pressStatus = false;
					return;
				}
			}else if($.inArray(4,indexMark)!=-1||$.inArray(5,indexMark)!=-1||$.inArray(6,indexMark)!=-1||$.inArray(7,indexMark)!=-1){
				if(index_down==8){
					leftStatus = false;
					rightStatus = false;
					pressStatus = false;
					return;
				}
			}
			if(line)
			line.remove();
			if(mouseDownX<=470){
				line = paper.path('M'+mid_startx+','+mid_starty+'L'+(mouseDownX+4)+','+mouseDownY);
				line.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
			}else if(mouseDownX>470){
				line = paper.path('M'+mid_startx+','+mid_starty+'L'+(mouseDownX-2)+','+mouseDownY);
				line.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
				if(mouseDownX>698){
					leftStatus = false;
					rightStatus = false;
					pressStatus = false;
					line.remove();
					return;
				}
			}
		}
	}
	function OnMouseUp(event,x,y){
		if(pressStatus){
			index_up = -1;
			mouseDownX = x-$('#line_canvas').offset().left;
			mouseDownY = y-$('#line_canvas').offset().top;
			if($.inArray(index_down,indexMark)!=-1) {
				leftStatus = false;
				rightStatus = false;
				pressStatus = false;
				return;
			}
			if(leftStatus){
				if(mouseDownX>=460&&mouseDownX<=700&&mouseDownY>=0&&mouseDownY<=585){
					mid_endx = 470;
					if((mouseDownY%45)>=0&&(mouseDownY%45)<=30){
						index_up = Math.floor(mouseDownY/45);
						if($.inArray(index_up,indexMark)!=-1){
							if(line)
							line.remove();
							pressStatus = false;
							leftStatus = false;
							return;
						}
						var active = $(".showright .showitem").hasClass('active');
						if(index_up==8&&!active){
							$(".showright .showitem").eq(4).addClass('active').data('must',false);
							$(".showright .showitem").eq(5).addClass('active').data('must',false);
							$(".showright .showitem").eq(6).addClass('active').data('must',false);
							$(".showright .showitem").eq(7).addClass('active').data('must',false);
						}else if((index_up==4||index_up==5||index_up==6||index_up==7)&&!active){
							$(".showright .showitem").eq(8).addClass('active').data('must',false);
						}
						if($.inArray(8,indexMark)!=-1){
							if(index_up==4||index_up==5||index_up==6||index_up==7){
								if(line)
								line.remove();
								pressStatus = false;
								leftStatus = false;
								return;
							}
						}else if($.inArray(4,indexMark)!=-1||$.inArray(5,indexMark)!=-1||$.inArray(6,indexMark)!=-1||$.inArray(7,indexMark)!=-1){
							if(index_up==8){
								if(line)
								line.remove();
								pressStatus = false;
								leftStatus = false;
								return;
							}
						}
						mid_endy = index_up*45+15;
						mid_startx = 230;
						mid_starty = index_up*45+15;
						if(line)
						line.remove();
						line2 = paper.path('M'+mid_startx+' '+mid_starty+'L'+mid_endx+' '+mid_endy);
						line2.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
						imagedel = paper.image('/public/images/connect_cutter.png', 340, mid_endy-10, 20, 20);
						circle1 = paper.circle(mid_startx-2,mid_starty,3);
						circle2 = paper.circle(mid_endx+2,mid_endy,3);
						circle1.attr({'stroke':CDT.circleColor,'stroke-width':'2','fill':CDT.circleColor});
						circle2.attr({'stroke':CDT.circleColor,'stroke-width':'2','fill':CDT.circleColor});
						changed = $('.showleft .showitem').eq(index_up);
						this_one = $('.showleft .showitem').eq(index_down).addClass('matched');
						this_data = $('.showleft .showitem').eq(index_down).data('index');
						matched = $('.showright .showitem').eq(index_up).addClass('matched');
						matched_name = $('.showright .showitem').eq(index_up).attr('name');
						exchangePos(changed, this_one);
						matchList[index_up] = {'left':this_data,'right':matched_name,'line':line2,'cutter':imagedel};
						indexMark.push(index_up);
						imagedel.click(delMatch);
					}else{
						if(line)
						line.remove();
					}
				}else{
					if(line)
					line.remove();
				}
				leftStatus = false;
			}else if(rightStatus){
				if(mouseDownX>=0&&mouseDownX<=240){
					if((mouseDownY%45)>=0&&(mouseDownY%45)<=30){
						index_up = Math.floor(mouseDownY/45);
						var empty = $('.showleft .showitem').eq(index_up).hasClass('emptyitem');
						if($.inArray(index_up,indexMark)!=-1||empty){
							if(line)
							line.remove();
							pressStatus = false;
							rightStatus = false;
							return;
						}
						var active = $('.showright .showitem').hasClass('active');
						if(index_down==8&&!active){
							$('.showright .showitem').eq(4).addClass('active').data('must',false);
							$('.showright .showitem').eq(5).addClass('active').data('must',false);
							$('.showright .showitem').eq(6).addClass('active').data('must',false);
							$('.showright .showitem').eq(7).addClass('active').data('must',false);
						}else if((index_down==4||index_down==5||index_down==6||index_down==7)&&!active){
							$('.showright .showitem').eq(8).addClass('active').data('must',false);
						}
						if($.inArray(8,indexMark)!=-1){
							if(index_down==4||index_down==5||index_down==6||index_down==7){
								return;
							}
						}else if($.inArray(4,indexMark)!=-1||$.inArray(5,indexMark)!=-1||$.inArray(6,indexMark)!=-1||$.inArray(7,indexMark)!=-1){
							if(index_down==8){
								return;
							}
						}
						mid_endx = 230;
						mid_endy = index_down*45+15;
						mid_startx = 470;
						mid_starty = index_down*45+15;
						if(line)
						line.remove();
						line2 = paper.path('M'+mid_startx+' '+mid_starty+'L'+mid_endx+' '+mid_endy);
						line2.attr({'stroke':CDT.lineColor,'stroke-width':'2'});
						imagedel = paper.image('/public/images/connect_cutter.png', 340, mid_endy-10, 20, 20);
						circle1 = paper.circle(mid_startx+2,mid_starty,3);
						circle2 = paper.circle(mid_endx-2,mid_endy,3);
						circle1.attr({'stroke':CDT.circleColor,'stroke-width':'2','fill':CDT.circleColor});
						circle2.attr({'stroke':CDT.circleColor,'stroke-width':'2','fill':CDT.circleColor});
						changed = $('.showleft .showitem').eq(index_down);
						this_one = $('.showleft .showitem').eq(index_up).addClass('matched');
						matched = $('.showright .showitem').eq(index_down).addClass('matched');
						this_data = $('.showleft .showitem').eq(index_up).data('index');
						matched_name = $('.showright .showitem').eq(index_down).attr('name');
						exchangePos(changed, this_one);
						matchList[index_down] = {'left':this_data,'right':matched_name,'line':line2,'cutter':imagedel};
						indexMark.push(index_down);
						imagedel.click(delMatch);
					}else{
						if(line)
						line.remove();
					}
				}else{
					if(line)
					line.remove();
				}
				rightStatus = false;
			}
		}
		pressStatus = false;
		$.each(matchList,function(index,obj){
			if(obj){
				var leftitem = obj.left;
				var rightitem = obj.right;
				orderMapper['orderMapper['+rightitem+']'] = leftitem;
			}
		});
	}
	function delMatch(event,x,y){
		this.prev.remove();
		this.next.next.remove();
		this.next.remove();
		this.remove();
		mouseDownX = x-$('#line_canvas').offset().left;
		mouseDownY = y-$('#line_canvas').offset().top;
		if((mouseDownY%45)>=0&&(mouseDownY%45)<=30){
			index_click =  Math.floor(mouseDownY/45);
			$('.showleft .showitem').eq(index_click).removeClass('matched');
			$('.showright .showitem').eq(index_click).removeClass('matched');
			$.each(indexMark,function(index,item){  
        if(item==index_click){
       	  indexMark.splice(index,1);
    	  }
     	});
     	matchList[index_click] = '';
     	orderMapper = {};
     	arr = [];
     	$.each(matchList,function(index,obj){
				if(obj){
					var leftitem = obj.left;
					var rightitem = obj.right;
					orderMapper['orderMapper['+rightitem+']'] = leftitem;
				}
			});
			if(index_click==8){
				$('.showright .showitem').eq(4).removeClass('active').data('must',true);
				$('.showright .showitem').eq(5).removeClass('active').data('must',true);
				$('.showright .showitem').eq(6).removeClass('active').data('must',true);
				$('.showright .showitem').eq(7).removeClass('active').data('must',true);
			}else if($.inArray(4,indexMark)==-1&&$.inArray(5,indexMark)==-1&&$.inArray(6,indexMark)==-1&&$.inArray(7,indexMark)==-1){
				$('.showright .showitem').eq(8).removeClass('active').data('must',true);
			}
		}
		
	}
}