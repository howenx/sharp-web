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
            console.log("==hiddenDiscountFee=="+hiddenDiscountFee)
            if(typeof(denominationSpan)=="undefined"||null==denominationSpan){
                $("#totalPaySpan").html(tempTotal);
                $(".discountCss").html("￥"+hiddenDiscountFee);
            }else{
                $("#totalPaySpan").html(tempTotal-denominationSpan);
                var fee=parseFloat(denominationSpan)+parseFloat(hiddenDiscountFee);
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
                tip("提交订单失败");
                //   $("#loading").empty();
             },
            success: function(data) {
                console.log("data="+data);
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
    if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
        var loadDateTime = new Date();
        window.setTimeout(function() {
                var timeOutDateTime = new Date();
                console.log(timeOutDateTime - loadDateTime);
                if (timeOutDateTime - loadDateTime < 5000) {
                    // window.location = "https://www.baidu.com/";
                    kaituan();
                }
            },
            1000);
        window.location = "https://24114.com/";
    } else if (navigator.userAgent.match(/android/i)) {
        var state = null;
        try {
            var url= window.location.href;
            state = window.open("app://hanmimei/"+window.urlParam);
        } catch(e) {}
        if (state) {
            window.close();
        } else {
            kaituan();
        }
    }


});



//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}
function kaituan() {
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
            tip("立即开团失败");
        },
        success: function(data) {
            console.log("data="+data);
            if (data!=""&&data!=null){
                if(data.code==200) {
                    $("#isPinCheck").val(0);
                    $("#settleForm").submit();
                }else{
                    tip(data.message);
                }
            }else{
                tip("立即开团失败");
            }
        }
    });
}



