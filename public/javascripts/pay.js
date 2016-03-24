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

});

$(document).on("click",".submitOrder",function(){
    var addressId=$("input[name='addressId']").val() ;
    var orderId=$("#orderId").val();
    if(addressId<=0){
        alert("请填写地址");
    }else if(orderId>0){
        alert("请勿重复提交订单");
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
                alert("提交订单失败");
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
                         alert("提交订单失败code="+data.message.code+","+data.message.message);
                    }

                }else{
                 alert("提交订单失败");
                }
             //   $("#loading").empty();
            }
        });
    }


});




