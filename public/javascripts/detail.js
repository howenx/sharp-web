$(function() {
	$('.classify ul li').click(function(){
		$(this).addClass('current').siblings().removeClass('current');
		var index = $(".classify ul li").index($(this));
		$(".item").removeClass("now");
		$(".item").eq(index).addClass("now");
		$(".soul").removeClass("now");
        $(".soul").eq(index).addClass("now");

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
                var skuId=$(".item.now").find("#skuId").val();
                var skuType=$(".item.now").find("#skuType").val();
                var skuTypeId=$(".item.now").find("#skuTypeId").val();
                var obj=new Object();
                obj.skuId=skuId; //sku id
                obj.skuType=skuType;//商品类型 1.vary,2.item,3.customize,4.pin
                obj.skuTypeId=skuTypeId;//商品类型所对应的ID
                $.ajax({
                        type: 'POST',
                        url: "/collect/submit",
                        contentType: "application/json; charset=utf-8",
                        data : JSON.stringify(obj),
                        error:function(request) {
                            alert("收藏失败");
                        },
                        success: function(data) {
                            if (data!=""&&data!=null&&data.collectId>0) {
                                $("#collectId").val(data.collectId);
                                $(this).addClass('min');
                                $('.like-s').show();
                                tim = setInterval(function () {
                                	$('.like-s').hide();
                                },1000);
                                $('.like-x').hide();
                                alert("收藏成功");
                            }else{
                                alert("收藏失败");
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
                         alert("删除失败!");
                     },
                     success: function(data) {
                        //console.log("data="+data);
                          if(data.code==200){ //取消收藏成功
                             $(this).removeClass('min');
                             $('.like-s').hide();
                             $('.like-x').show();
                             tim = setInterval(function () {
                             	$('.like-x').hide();
                             },1000);
                             off =true;
                             alert("取消成功");
                          }
                          else{
                             alert("取消失败!");
                          }
                     }
                });
            }
    });









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
	})


})
