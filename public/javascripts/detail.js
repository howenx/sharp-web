$(function() {
//      var urlParam = window.urlParam;
//
//    var userAgent = navigator.userAgent || navigator.vendor || window.opera;
//    if( userAgent.match( /iPad/i ) || userAgent.match( /iPhone/i ) || userAgent.match( /iPod/i ) )
//        {
//          //    //ios APP
//          setTimeout(function () { window.location = "HmmApp://"; }, 1000);
//          alert("ios");
//
//        }
//        else if( userAgent.match( /Android/i ) )
//        {
//          //    //Android APP
//          setTimeout(function () { window.location = "HmmApp//"; }, 1000);
//          alert("android");
//        }


    $('.soldOut').click(function(){
        $('.hd-js').show();

        setInterval(function(){
            $('.hd-js').hide();
        },2000)
    });

    $('.finish-box').click(function(){
        $('.con').show();
    })

    $(".mabuy").click(function () {
        // if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
        //     var loadDateTime = new Date();
        //     window.setTimeout(function() {
        //             var timeOutDateTime = new Date();
        //         console.log(timeOutDateTime - loadDateTime);
        //             if (timeOutDateTime - loadDateTime < 5000) {
        //                 // window.location = "https://www.baidu.com/";
        //                 document.getElementById('settleForm').submit();
        //             }
        //         },
        //         1000);
        //     window.location = "https://24114.com/";
        // } else if (navigator.userAgent.match(/android/i)) {
        //     var state = null;
        //     var url= window.location.href;
        //     state = window.open("app://hanmimei/"+window.urlParam);
        //     window.close();
        //     document.getElementById('settleForm').submit()
        // }
            // 通过iframe的方式试图打开APP，如果能正常打开，会直接切换到APP，并自动阻止a标签的默认行为
            // 否则打开a标签的href链接
        if (navigator.userAgent.match(/MicroMessenger/i)) {
            return;
        }else{
            var ifr = document.createElement('iframe');
            ifr.src = 'hmmapp://data/'+window.urlParam;
            ifr.style.display = 'none';
            document.body.appendChild(ifr);
            window.setTimeout(function(){
                document.body.removeChild(ifr);
            },3000)
        }
    })

	$('.classify ul li').click(function(){
	    if($(this).attr("class") != "sel-none"){
	        $(this).addClass('current').siblings().removeClass('current');
                var index = $(".classify ul li").index($(this));
                $(".item").removeClass("now");
                $(".item").eq(index).addClass("now");

                $(".soul").removeClass("now");
                $(".soul").eq(index).addClass("now");

                $(".hiddenInput").find("input").attr("disabled",true);
                $(".hiddenInput").eq(index).find("input").attr("disabled",false);
                // $("#flyImgId").attr("src",$(".item.now").find("input[name='skuInvImg0-0']").val());
	    }
	})

	$('#slide').on('cycle-next', function(event, opts) {
		if (opts.slides[opts.currSlide].firstElementChild.src === '') {
			opts.slides[opts.currSlide].firstElementChild.src = "/images/sku-slide/" + opts.currSlide + ".jpg"
		}
	});
	$('#slide').on('cycle-pager-activated', function(event, opts) {
		if (opts.slides[opts.currSlide].firstElementChild.src === '') {
			opts.slides[opts.currSlide].firstElementChild.src = "/images/sku-slide/" + opts.currSlide + ".jpg"
		}
	})



	//切换
	var top = $('.pic-tex').offset().top;
	$(window).scroll(function(e) {
		if($(".pic-tex").length==0){
			$('.nav_ban_detail').hide();
		}
		if($(window).scrollTop() > top-51){
			$('.detail-tabpanel').addClass('detail-tabpanel-fixed');
			$('.nav_ban_detail').hide()
		}else{
			$('.detail-tabpanel').removeClass('detail-tabpanel-fixed');
			$('.nav_ban_detail').show()
		}
	});

	$('.tabpanel-tabs ul li').click(function(){
		$(this).addClass('current').siblings().removeClass('current');

		if ($(this).hasClass('pic-tex')) {
			$('body').scrollTop(top-50);
		}
		if($(this).hasClass('pin-pic')){
			$('body').scrollTop($('.sku-measure01').offset().top-60);
		}
		if ($(this).hasClass('goods-para')) {
			$('body').scrollTop($('.sku-measure').offset().top-61);
		}
		if ($(this).hasClass('hot-sale')) {
			$('body').scrollTop($('.hot').offset().top-61);
		}

	});


	$(window).scroll(function(e) {
		//console.log($(window).scrollTop());
		if($(window).scrollTop() > $(window).height()){
			$('.top').show();
		}else{
			$('.top').hide();
		}
	});

	$('.top').click(function(e) {
		$('html,body').stop().animate({'scrollTop':'0'},500);
	});

    $(function(){
        $(window).load(function(){
            $("#loading").hide();
        });
    });

//	/*收藏*/
//	var off = true;
//    var tim = null;
//	$('.soul').click(function(){
//
//		clearInterval( tim );
//		if(off){
//			$(this).addClass('min');
//			$('.like-s').show();
//
//			tim = setInterval(function () {
//				$('.like-s').hide();
//			},1000);
//
//			$('.like-x').hide();
//			off =false;
//
//		}else {
//			$(this).removeClass('min');
//			$('.like-s').hide();
//			$('.like-x').show();
//			tim = setInterval(function () {
//				$('.like-x').hide();
//			},1000);
//			off =true;
//		}
//
//
//
//	})
//
	//收藏
    $(document).on("click",".soul",function(){
            var isCollected = false;
            if($(".soul.now").parent().find(".collectId").val() != 0){
                isCollected = true;
            }
            //添加收藏
            if(!isCollected){
//                var skuId=$(".item.now").find("#skuId").val();
//                var skuType=$(".item.now").find("#skuType").val();
//                var skuTypeId=$(".item.now").find("#skuTypeId").val();
                  var skuId=$("input[name='skuId0-0']:not(:disabled)").val() ;
                  var skuType=$("input[name='skuType0-0']:not(:disabled)").val() ;
                  var skuTypeId=$("input[name='skuTypeId0-0']:not(:disabled)").val() ;
                var obj=new Object();
                obj.skuId=skuId; //sku id
                obj.skuType=skuType;//商品类型 1.vary,2.item,3.customize,4.pin
                obj.skuTypeId=skuTypeId;//商品类型所对应的ID
                obj.url=window.location.href;
                $.ajax({
                        type: 'POST',
                        url: "/collect/submit",
                        contentType: "application/json; charset=utf-8",
                        data : JSON.stringify(obj),
                        error:function(request) {
                        },
                        success: function(data) {
                            if (data!=""&&data!=null&&data.collectId>0) {
                                console.log(data.collectId);
                                $(".soul.now").parent().find(".collectId").val(data.collectId);
                                $(".soul.now").addClass('min');
                                $('.like-s').show();

                                tim = setInterval(function () {
                                	$('.like-s').hide();
                                },1000);
                                $('.like-x').hide();
                            }else if(null!=data.message&&null!=data.message.code&&data.message.code==5006) { //您还未登录,请先登录

                                 setTimeout("location.href='/login?state="+data.state+"'", 2000);

                            }else{

                            }
                        }
                });
            }
            //取消收藏
            else{
                var collectId = $(".soul.now").parent().find(".collectId").val();
                $.ajax({
                     type :"GET",
                     url : "/collect/del/"+collectId,
                     contentType: "application/json; charset=utf-8",
                     error : function(request) {
                     },
                     success: function(data) {
                        //console.log("data="+data);
                          if(data.code==200){ //取消收藏成功
                             $(".soul.now").removeClass('min');
                             $('.like-s').hide();
                             $('.like-x').show();
                             tim = setInterval(function () {
                             	$('.like-x').hide();
                             },1000);
                             $(".soul.now").parent().find(".collectId").val(0);
                          }
                          else{
                          }
                     }
                });
            }
    });
//
//    //加入购物车
//    $(document).on("click",".add_cart_large",function(){
//        var cartId = 0;
//        var amount = 1;
//        var state = $(".item.now").find("#skuId").val();
//        var skuId=$(".item.now").find("#skuId").val();
//        var skuType=$(".item.now").find("#skuType").val();
//        var skuTypeId=$(".item.now").find("#skuTypeId").val();
//        var obj=new Object();
//        obj.skuId=skuId; //sku id
//        obj.skuType=skuType;//商品类型 1.vary,2.item,3.customize,4.pin
//        obj.skuTypeId=skuTypeId;//商品类型所对应的ID
//
//        $.ajax({
//                type: 'POST',
//                url: "/cart/add",
//                contentType: "application/json; charset=utf-8",
//                data : JSON.stringify(obj),
//                error:function(request) {
//                },
//                success: function(data) {
//                    if (data!=""&&data!=null&&data.collectId>0) {
//                        console.log(data.collectId);
//                    }else{
//                    }
//                }
//        });
//
//    })









//    弹出
	$(".toup .con").animate({
		bottom:0,
	},1000);
	$(".bg").click(function(){
		$(".toup .con").animate({
			bottom:-295,
		},1000,function(){
			$(".toup").css("display","none");
		});
	});

    $(".finish-box").click(function () {
        $(".toup").css("display","block");
        $(".toup .con").animate({
            bottom:0,
        },1000);
    })

});
//添加购物车
$(document).on("click",".cartAdd",function(){
    var skuId=$("input[name='skuId0-0']:not(:disabled)").val() ;
    var skuType=$("input[name='skuType0-0']:not(:disabled)").val() ;
    var skuTypeId=$("input[name='skuTypeId0-0']:not(:disabled)").val() ;
    var state=$("input[name='state0-0']:not(:disabled)").val() ;

    var obj=new Object();
    obj.cartId=0;
    obj.skuId=skuId;
    obj.skuType=skuType;
    obj.skuTypeId=skuTypeId;
    obj.state="I";
    obj.amount=1;
    obj.url=window.location.href;
    $.ajax({
        type: 'POST',
        url: "/cart/add",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(obj),
        dataType: 'json',
        error : function(request) {
            console.log("request.responseText="+request.responseText);
            tip("加入购物车失败,请检测是否已登录");
         },
        success: function(data) {
            console.log("data="+data);
            if (data!=""&&data!=null){
                if(data.code==200) { //成功
                    addCartEffect(); //加入购物车特效
                }else if(null!=data.message&&null!=data.message.code&&data.message.code==5006) { //您还未登录,请先登录
                     setTimeout("location.href='/login?state="+data.state+"'", 2000);

                }else{
                     tip(data.message);
                }

            }else{
             tip("加入购物车失败");
            }
        }
    });

});
//加入购物车效果
function addCartEffect(){
// 元素以及其他一些变量
	var eleFlyElement = document.querySelector("#flyItem"), eleShopCart = document.querySelector("#shopCart");
	// 抛物线运动
	var myParabola = funParabola(eleFlyElement, eleShopCart, {
		speed:30, //抛物线速度
		curvature: 0.0090, //控制抛物线弧度
		complete: function() {
			eleFlyElement.style.visibility = "hidden";
			var numberItem = parseInt($("#cartAmountSpan").html());
			eleShopCart.querySelector("span").innerHTML = ++numberItem;
		}
	});

//	var src = $(this).parents(".cycle-slideshow").find('.slide-img').find("img").attr("src");
    var src=$(".item.now").find("input[name='skuInvImg0-0']").val();
    $("#flyItem").find("img").attr("src", src);
    // 滚动大小
    var scrollLeft = document.documentElement.scrollLeft || document.body.scrollLeft || 0,
            scrollTop = document.documentElement.scrollTop || document.body.scrollTop || 0;
    //eleFlyElement.style.left = event.clientX + scrollLeft + "px";
    //eleFlyElement.style.top = event.clientY + scrollTop + "px";
    eleFlyElement.style.visibility = "visible";
    // 需要重定位
    myParabola.position().move();
}

//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}

$(document).on("click",".pinSoldOutCss",function(){
    tip("该商品已结束去看看其他拼购吧");
});



