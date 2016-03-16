$(function(){

    $("#addAddressBtn").click(function(e){
                var zszReg = new RegExp(/^[a-zA-Z0-9\u4e00-\u9fa5]/); //字母数字中文
                var telReg=new RegExp(/^[1][345678]\d{9}/);
                var card15Reg=new RegExp(/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/);
                var card18Reg=new RegExp(/^(\d{6})(18|19|20)?(\d{2})([01]\d)([0123]\d)(\d{3})(\d|X|x)?$/);
                var name=$("#name").val();
                var tel=$("#tel").val();
                var idCardNum=$("#idCardNum").val(); //TODO ...
                var deliveryDetail=$("#deliveryDetail").val();

                if (name.length>15||name.length<2 ||!zszReg.test(name)) {
                    $('#js-userinfo-error').text('姓名只能是中文/数字/字母').show();
                    setTimeout("$('#js-userinfo-error').hide()", 3000);
                }else if (tel.length!=11 ||!telReg.test(tel)) {
                     $('#js-userinfo-error').text('请填写正确的手机号码').show();
                     setTimeout("$('#js-userinfo-error').hide()", 3000);
                }else if (deliveryDetail.length>50||deliveryDetail.length<1 ||!zszReg.test(deliveryDetail)) {
                     $('#js-userinfo-error').text('详细地址只能是50字内的中文/数字/字母').show();
                     setTimeout("$('#js-userinfo-error').hide()", 3000);
                }else if(!((idCardNum.length==15&&card15Reg.test(idCardNum))||(idCardNum.length==18&&card18Reg.test(idCardNum)))){
                     $('#js-userinfo-error').text('请填写正确的身份证号码').show();
                     setTimeout("$('#js-userinfo-error').hide()", 3000);
                }
                else {
                    console.log($('form#cell_addressForm').serialize());
                    $.ajax({
                            type: 'POST',
                            url: "/address/save",
                            dataType: 'json',
                            data: $('form#cell_addressForm').serialize(),
                            success: function(data) {
                                console.log("data="+data);   //TODO...跳转
                                if (data!=""&&data!=null&&data.code==200) {
                                    setTimeout("location.href='/address'", 3000);
                                }

                            }
                    });
                }

        });


});


$(document).on("click",".cancelColl",function(e){
    e.preventDefault();
    var id=$(this).parents("li").val();
    var li=$(this).parents("li");
    console.log("id="+id);
    $.ajax({
          type :"GET",
          url : "/collect/del/"+id,
          contentType: "application/json; charset=utf-8",
          error : function(request) {
              alert("删除失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //取消收藏成功
                  li.remove();
               } else alert("删除失败!");

          }
     });
})

$(document).on("click",".cancelOrder",function(e){
    e.preventDefault();
    var id=$(this).parents("li").val();
    console.log("id="+id);
    $.ajax({
          type :"GET",
          url : "/order/cancel/"+id,
          contentType: "application/json; charset=utf-8",
          error : function(request) {
              alert("取消订单失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //取消订单成功
                 setTimeout("location.href='/all'", 3000);
               } else alert("取消订单失败!");

          }
     });
})

$(document).on("click",".delOrder",function(e){
    e.preventDefault();
    var id=$(this).parents("li").val();
    var li=$(this).parents("li");
    console.log("id="+id);
    $.ajax({
          type :"GET",
          url : "/order/del/"+id,
          contentType: "application/json; charset=utf-8",
          error : function(request) {
              alert("删除失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //删除订单成功
                  li.remove();
               } else alert("删除失败!");

          }
     });
});

function delOrder(id,position){
    if (window.confirm("确定删除吗?")) {
        console.log("id="+id);
        $.ajax({
              type :"GET",
              url : "/order/del/"+id,
              contentType: "application/json; charset=utf-8",
              error : function(request) {
                  alert("删除失败!");
              },
              success: function(data) {
                 console.log("data="+data);

                  if (data!=""&&data!=null&&data.code==200){ //删除成功
                      if(position==0){
                        $("#li"+id).remove();
                      }else{
                       setTimeout("location.href='/all'", 2000);
                      }

                   } else alert("删除失败!");

              }
         });
    }
};


function delAddress(addId,orDefault){
    if (window.confirm("确定删除该地址吗?")) {
        var obj=new Object();
        obj.addId=addId;
        obj.orDefault=orDefault==true?1:0;
         $.ajax({
                  type :"POST",
                  url : "/address/del",
                  contentType: "application/json; charset=utf-8",
                  dataType:"json",
                  data : JSON.stringify(obj),
                  error : function(request) {
                      alert("删除失败!");
                  },
                  success: function(data) {
                     console.log("data="+data);
                         if (data!=""&&data!=null&&data.code==200){ //删除地址成功
                          $("#li"+addId).remove();
                       } else alert("删除失败!");

                  }
             });
    }
}

$(document).ready(function(){
     //修改待支付待收货个数
     var waitPayNum=$("#waitPayUl li").length;
     if(waitPayNum>0){
         $("#waitPay").addClass("cart_num").html(waitPayNum);
     }
     var waitGoodsNum=$("#waitGoodsUl li").length;
     if(waitGoodsNum>0){
        $("#waitGoods").addClass("cart_num").html(waitGoodsNum);
     }
});





