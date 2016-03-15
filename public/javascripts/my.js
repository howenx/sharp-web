$(function(){
   $("#addAddressBtn").click(function(){
        var isPost = true;
        var addId=$("#addId").val();
        var name=$("#name").val();
        var tel=$("#tel").val();
        var idCardNum=$("#idCardNum").val();
        var deliveryDetail=$("#deliveryDetail").val();
        var orDefault=$("#orDefault").is(':checked');

        var province=$("#province").val();
        var city=$("#city").val();
        var area=$("#area").val();
        var area_code=$("#area_code").val();
        var city_code=$("#city_code").val();
        var province_code=$("#province_code").val();


        var deliveryCity=new Object();
        deliveryCity.province=province;
        deliveryCity.city=city;
        deliveryCity.area=area;
        deliveryCity.area_code=area_code;
        deliveryCity.city_code=city_code;
        deliveryCity.province_code=province_code;

        var address=new Object();
        address.addId=addId;
        address.name=name;
        address.tel=tel;
        address.idCardNum=idCardNum;
        address.deliveryDetail=deliveryDetail;
        address.orDefault=orDefault?1:0;
        address.deliveryCity=deliveryCity;

        if (isPost) {
                $.ajax({
                    type :  "POST",
                    url : "/address/save",
                    contentType: "application/json; charset=utf-8",
                    data : JSON.stringify(address),
                    error : function(request) {
                        if (window.lang = 'cn') {
                            $('#js-userinfo-error').text('保存失败');
                        } else {
                            $('#js-userinfo-error').text('Save error');
                        }
                        setTimeout("$('#js-userinfo-error').text('')", 2000);
                    },
                    success: function(data) {
                    console.log("data="+data);
                        if (data!=""&&data!=null&&data.code==200) {
                            setTimeout("location.href='/address'", 3000);
                        }
                     }
                });

        }



    });

})
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
})


