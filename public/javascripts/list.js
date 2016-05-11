$(function(){


        //获取window的高度
        //滚动条滚动的垂直距离大于窗口高度 才是小火箭现身的最佳时机

    $(window).scroll(function(e) {
        if($(window).scrollTop() > $(window).height()){
            $('.top').show();
        }else{
            $('.top').hide();
        }
    });
        //点击火箭返回顶部
        $('.top').click(function(e) {
            $('html,body').stop().animate({'scrollTop':'0'},500);
        });

    $(document).on("click",".redirect-app",function(){
        var path = $(this).attr("href");
        appRedirect(path);
    })


    $(window).load(function(){
        $("#loading").hide();
    });


    echo.init();

})