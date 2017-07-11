jQuery(document).ready(function () {
    /* header_banner */
    var jQueryheader_banner = jQuery("#header_banner_box");
    jQueryheader_banner.mouseleave(function () {
        setTimeout(hideFx, 3000);
    });
    function hideFx() {
        jQueryheader_banner.slideUp("fast")
    };
    jQueryheader_banner.find(".close").click(function () {
        jQueryheader_banner.slideUp("fast")
        return false;
    });
    /* category */
    var jQuerygnb1 = jQuery("#gnb1");
    if (jQuerygnb1.is(".off")) {
        jQuerygnb1.hover(function () {
            jQuery(this).removeClass("off");
        }, function () {
            jQuery(this).addClass("off");
        });
    };
    jQuerygnb1.find(".depth2_li").mouseover(function () {
        jQuerygnb1.find(".depth2_li").children("div").hide().siblings("a").removeClass("hover");
        jQuery(this).children("div").show().siblings("a").addClass("hover");
    });
    jQuerygnb1.find(".depth2").mouseleave(function () {
        jQuerygnb1.find(".depth2_li").children("div").hide().siblings("a").removeClass("hover");
    });

    jQuery(".category_close").click(function () {
        jQuery(this).parent("span").parent("div").parent("div").hide();
        return false;
    });

    /* item */
    jQuery("#gnb2").hover(function () {
        jQuery(this).children("div").show();
        jQuery(this).find(".gnb2_bg").addClass("on");
    }, function () {
        jQuery(this).children("div").hide();
        jQuery(this).find(".gnb2_bg").removeClass("on");
    });
    var thisZindex;
    jQuery("#item_cate").find("dd").hover(function () {
        thisZindex = jQuery(this).css("z-index");
        jQuery(this).addClass("on").css("z-index", "150");
        if (jQuery(this).attr("class").indexOf != -1) {
            jQuery(this).siblings("dd").css("z-index", "1");
        };
    }, function () {
        jQuery(this).removeClass("on").css("z-index", thisZindex);
        if (jQuery(this).attr("class").indexOf != -1) {
            jQuery(this).siblings("dd").css("z-index", thisZindex);
        };
    });
    jQuery("#item_cate_close").find("a").click(function () {
        jQuery("#item_cate").hide();
        return false;
    });
    /* baro_on */
    jQuery("#util_left2").hover(function () {
        jQuery(this).children("div").show();
    }, function () {
        jQuery(this).children("div").hide();
    });
    /* custom select */
    var jQueryselect = jQuery("#selectbox").find("ul li");
    var selectSta = true;
    jQueryselect.append("<span></span>");
    jQueryselect.click(function () {
        if (selectSta) {
            jQueryselect.addClass("on").parent().css("height", "auto");
            jQueryselect.find("span").remove()
            selectSta = false;
        } else {
            jQueryselect.not(this).removeClass("on").parent().css("height", "20");
            jQuery(this).append("<span></span>");
            jQuery("#select_hidden").val(jQuery(this).val());
            selectSta = true;
        };
    });
    $('#leftBest100RightArrow').click(function(){
        bannerPagingNext();
    });
    $('#leftBest100LeftArrow').click(function(){
        bannerPagingPrev();
    });
    var unslider = $('.banner_zoon_wrap').unslider({
        speed: 500,               //  滚动速度
        delay: 5000,              //  动画延迟
        complete: function() {},  //  动画完成的回调函数
        keys: false,               //  启动键盘导航
        dots: false,               //  显示点导航
        fluid: false,              //  支持响应式设计
        arrows: true,
        loop:true,
        prev: '←', // 上一页按钮的文字 (string)
        next: '→', // 下一页按钮的文字
    });
});

$(window).load(function () {
    var jQuerygnb1 = jQuery("#gnb1");
    if (jQuerygnb1.is(".off")) {
        jQuerygnb1.hover(function () {
            jQuery(this).removeClass("off");
            jQuery(".depth2_li").eq(0).children("a").addClass("hover").next("div").show();
            
        }, function () {
            jQuery(this).addClass("off");
        });
    };
})

function autoTapFx() {
    var autoTapCnt;
    var autoTapSet;
    var jQuerytapTarget;
    var jQueryimgTap;
    this.autoT = function autoTap(q, img) {
        autoTapCnt = 0;
        jQuerytapTarget = q;
        jQueryimgTap = img;
        autoTapSet = setInterval(autoRun, 3000);
        function autoRun() {
            autoTapCnt++;
            if (autoTapCnt == jQuery(jQuerytapTarget).children("li").length) autoTapCnt = 0;
            jQuery(q).find("li").eq(autoTapCnt).addClass("on").siblings("li").removeClass("on");
            if (jQueryimgTap != undefined && jQueryimgTap == "img") {
                jQuery(q).children("li").not(".on").each(function () {
                    jQuery(this).children("a").children("img").attr("src", jQuery(this).children("a").children("img").attr("src").attr("offimg"));
                });
                jQuery(q).children("li").eq(autoTapCnt).children("a").children("img").attr("src", jQuery(q).children("li").eq(autoTapCnt).children("a").children("img").attr("onimg"));
            };
            jQuery(q + autoTapCnt).show().siblings("div").hide();
        };
        jQuery(q).children("li").each(function (index, item) {
            jQuery(item).mouseover(function () {
                clearInterval(autoTapSet);
                autoTapCnt = index;
                jQuery(this).find('img.lazy').each(function () {
                    if ($(this).attr('data-src') != undefined) {
                        var dImage = $(this).attr('data-src');
                        $(this).attr('src', dImage).removeAttr('data-src');
                    }
                });   
                jQuery(this).addClass("on").siblings("li").removeClass("on");
                if (jQueryimgTap != undefined && jQueryimgTap == "img") {
                    jQuery(item).children("li").not(".on").each(function () {
                        jQuery(this).children("a").children("img").attr("src", jQuery(this).children("a").children("img").attr("offimg"));
                    });
                    jQuery(item).children("li").eq(index).children("a").children("img").attr("src", jQuery(item).children("li").eq(index).children("a").children("img").attr("onimg"));
                };
                jQuery(q + index).show().siblings("div").hide();
            });
            jQuery(item).on("mouseout", function () {
                autoTapSet = setInterval(autoRun, 3000);
            });
        });
        for (var i = 0; i < jQuery(jQuerytapTarget).children("li").length; i++) {
            jQuery(jQuerytapTarget + i).on("mouseover", function () {
                clearInterval(autoTapSet);
            });
            jQuery(jQuerytapTarget + i).on("mouseout", function () {
                autoTapSet = setInterval(autoRun, 3000);
            });
        };
    };
};

var banner_zoon = new autoTapFx();
banner_zoon.autoT("#banner_zoon");

var bannerPagingVal = null;
// $(function () {
//     bannerPagingVal = setTimeout(function () { bannerPagingTimer() }, 5000);

//     $(".header_rightBanner").hover(function () { clearTimeout(bannerPagingVal); }, function () { bannerPagingVal = setTimeout(function () { bannerPagingTimer() }, 3000); });
// });
// function bannerPagingTimer() {
//     clearTimeout(bannerPagingVal);

//     $("#leftBest100RightArrow").trigger("click");

//     bannerPagingVal = setTimeout(function () { bannerPagingTimer() }, 5000);
// }
// function bannerPagingNext() {
//     var nextObj = null;

//     if ($(".banner_zoon_wrap ul .selected").next().length != 0) {
//         nextObj = $(".banner_zoon_wrap ul .selected").next();
//     }
//     else {
//         nextObj = $(".banner_zoon_wrap ul li").eq(0)
//     }
//     $(".banner_zoon_wrap ul li").removeClass("selected");
//     nextObj.addClass("selected")
// }
function bannerPagingPrev() {
    var nextObj = null;

    if ($(".banner_zoon_wrap ul .selected").prev().length != 0) {
        nextObj = $(".banner_zoon_wrap ul .selected").prev();
    }
    else {
        nextObj = $(".banner_zoon_wrap ul li").eq($(".banner_zoon_wrap ul li").length - 1)
    }
    $(".banner_zoon_wrap ul li").removeClass("selected");
    nextObj.addClass("selected")
}
/*上传组件默认配置*/
Tr.uploadOption = function() {
    return {
        runtimes: 'html5,flash,html4', //上传模式,依次退化
        browse_button: 'btnPickfiles', //上传选择的点选按钮ID，**必需**
        unique_names: true, // 默认 false，key为文件名。若开启该选项，SDK为自动生成上传成功后的key（文件名）。
        // save_key: true,   // 默认 false。若在服务端生成uptoken的上传策略中指定了 `sava_key`，则开启，SDK会忽略对key的处理
        domain: '', //bucket 域名，下载资源时用到，**必需**
        container: 'container', //上传区域DOM ID，默认是browser_button的父元素，
        max_file_size: '5mb', //最大文件体积限制
        flash_swf_url: '/public/javascripts/plupload/Moxie.swf', //引入flash,相对路径
        max_retries: 3, //上传失败最大重试次数
        dragdrop: true, //开启可拖曳上传
        drop_element: 'container', //拖曳上传区域元素的ID，拖曳文件或文件夹后可触发上传
        chunk_size: '4mb', //分块上传时，每片的体积
        auto_start: true, //选择文件后自动上传，若关闭需要自己绑定事件触发上传
        multi_selection: false,
        filters: {
            mime_types: [{
                title: 'Image files',
                extensions: 'jpg,gif,png'
            }],
            max_file_size: "1mb",
            prevent_duplicates: true
        },
        init: {
            'FilesAdded': function(up, files) {
                plupload.each(files, function(file) {
                    var progress = new FileProgress(file, 'fsUploadProgress');
                    progress.setStatus("等待...");
                });
                // plupload.each(files, function(file) {
                //  // 文件添加进队列后,处理相关的事情
                // });
            },
            'BeforeUpload': function(up, file) {
                // 每个文件上传前,处理相关的事情
                // return confim('确定要上传图片');
            },
            'UploadProgress': function(up, file) {
                // 每个文件上传时,处理相关的事情，转菊花、显示进度等
            },
            'FileUploaded': function(up, file, info) {},
            'Error': function(up, err, errTip) {
                //上传出错时,处理相关的事情
                alert('上传失败！');
            },
            'UploadComplete': function() {
                //队列文件处理完毕后,处理相关的事情
            },
            'Key': function(up, file) {
                // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
                // 该配置必须要在 unique_names: false , save_key: false 时才生效

                var key = "";
                // do something with key here
                return key;
            }
        }
    };

};
