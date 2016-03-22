$(function(){
    $('.account-j h2').click(function(){
        $(this).parents(".account-j").find(".way").toggle();
    })

    $('li.liOther').click(function(){
        $(this).parents("ul").prev().find(".quick").html($(this).find(".quick").html())
        $(this).parents("ul").prev().find("input").val($(this).find("input").val())
    });
    //点击优惠券
    $('li.liCoupon').click(function(){
            $(this).parents("ul").prev().find(".quick").html($(this).find(".quick").html())
            $(this).parents("ul").prev().find("input").val($(this).find("input").val())
            var tempTotal=$("#tempTotal").val();
            var denominationSpan=$(this).find(".quick").find(".denominationSpan").html();
            if(typeof(denominationSpan)=="undefined"||null==denominationSpan){
                $("#totalPaySpan").html(tempTotal);
            }else{
                $("#totalPaySpan").html(tempTotal-denominationSpan);
            }

        });

    $('.back').click(function(){
        $('.shade').show();
    })

    $('.first').click(function(){
        $('.shade').hide();
    })

})

