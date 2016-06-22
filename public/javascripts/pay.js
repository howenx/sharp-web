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
            var hiddenDiscountFee=$("input[name='hiddenDiscountFee']").val();
            $("#discTipSpan").hide();
            if(typeof(denominationSpan)=="undefined"||null==denominationSpan){
                $("#totalPaySpan").html(tempTotal);
                $(".discountCss").html("￥"+hiddenDiscountFee);
            }else{
                var totalFinal=tempTotal-denominationSpan;
                var fee=parseFloat(denominationSpan)+parseFloat(hiddenDiscountFee);
                if(totalFinal<1){
                    totalFinal=1;
                    fee=tempTotal-totalFinal;
                    $("#discTipSpan").show();
                }
                $("#totalPaySpan").html(totalFinal);
                $(".discountCss").html("￥"+fee);
            }

        });

    $('.back').click(function(){
        $('.shade').show();
    })

    $('.first').click(function(){
        $('.shade').hide();
    })

});

//提交订单
$(document).on("click",".submitOrder",function(){
    var addressId=$("input[name='addressId']").val() ;
    var orderId=$("#orderId").val();
    if(addressId<=0){
        tip("请填写地址");
    }else if(orderId>0){
        tip("请勿重复提交订单");
    }else{
        $.ajax({
            type: 'POST',
            url: "/order/submit",
            dataType: 'json',
            data: $('form#orderForm').serialize(),
//            beforeSend:function(XMLHttpRequest){
//                $("#loading").html("加载中...");
//             },
            error : function(request) {
                tip("提交订单失败,请检测是否已登录");
                //   $("#loading").empty();
             },
            success: function(data) {
                if (data!=""&&data!=null){
                    if(data.message.code==200) {
                        $("#orderId").val(data.orderId);
                        $("#token").val(data.token);
                        $("#securityCode").val(data.securityCode);
                        // var url='/pay/order/get/'+data.orderId;
                        // setTimeout("location.href=\'"+url+"\'", 3000);
                        $("#payForm").submit();

                    }else{
                         tip(data.message.message);
                    }

                }else{
                 tip("提交订单失败");
                }
             //   $("#loading").empty();
            }
        });
    }


});


//立即开团
$(document).on("click",".pinSubmitBtn",function(){
    var tipContent=$("#tip").html().trim();
    if(""!=tipContent&&tipContent.length>1){
        tip(tipContent);
        return ;
    }
    $.ajax({
        type: 'POST',
        url: "/settle",
        dataType: 'json',
        data: $('form#settleForm').serialize(),
        error : function(request) {
            tip("立即开团失败,请检测是否已登录");
        },
        success: function(data) {
            if (data!=""&&data!=null){
                if(data.code==200) {
                    $("#isPinCheck").val(0);
                    $("#settleForm").submit();
                }else if(null!=data.message&&null!=data.message.code&&data.message.code==5006) { //您还未登录,请先登录
                    setTimeout("location.href='/login?state="+data.state+"'", 2000);
                }else{
                    tip(data.message);
                }
            }else{
                tip("立即开团失败");
            }
        }
    });
});



//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}
//当前url
$(document).ready(function() {

    $("#curUrl").val(window.location.href);

});


$(function(){
    $(window).load(function(){
        $("#loading").hide();
    });
})



