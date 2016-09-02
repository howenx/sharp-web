$(function(){

    $('.nav-tab-top ul li').click(function(){
        $(this).addClass('current').siblings().removeClass('current');
        $('.scroll-wrap .scroll-content section').eq($(this).index()).show().siblings().hide();
        $('.scroll-coupon .coupon-content section').eq($(this).index()).show().siblings().hide();


        $(".not").css("display","none");
        $(".orderNot").hide();
        if($('.scroll-wrap .scroll-content section').eq($(this).index()).find("li").length<=0){
           // $(".orderNot").eq($(this).index()).css("display","block");
           $(".orderNot").css("display","block");
           var html=$("#recommendUl").html().trim();
           if(null==html||""==html||html.length<=0){
                getRecommendSku(2);
           }
        }

        $(".couponNot").hide();
        if($('.scroll-coupon .coupon-content section').eq($(this).index()).find("li").length<=0){
           $(".couponNot").show();
           var html=$("#recommendUl").html().trim();
           if(null==html||""==html||html.length<=0){
               getRecommendSku(5);
           }
        }

    })

    $('.closeBtn').click(function(){
        $('.grayDiv').hide();
    })
    $('.spot').click(function(){
        $('.grayDiv').show()
    })

    $('.alter-btn').click(function(){
        alert('确定删除');
        $(this).parents('li').hide();
    })


    $(window).load(function(){
        $("#loading").hide();
        $(".fixed-box").css({"position":"static"});
    });



})