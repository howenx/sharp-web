$(function(){

    $('.nav-tab-top ul li').click(function(){
        $(this).addClass('current').siblings().removeClass('current');
        $('.scroll-wrap .scroll-content section').eq($(this).index()).show().siblings().hide();
        $('.scroll-coupon .coupon-content section').eq($(this).index()).show().siblings().hide();

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





})