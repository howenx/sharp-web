//添加修改地址
$(document).on("click",".addAddressBtn",function(){
        var zszReg = new RegExp(/^[a-zA-Z0-9\u4e00-\u9fa5]/); //字母数字中文
        var telReg=new RegExp(/^[1][345678]\d{9}/);
        var card15Reg=new RegExp(/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/);
        var card18Reg=new RegExp(/^(\d{6})(18|19|20)?(\d{2})([01]\d)([0123]\d)(\d{3})(\d|X|x)?$/);
        var addId=$("#addId").val();
        var name=$("#name").val();
        var tel=$("#tel").val();
        var idCardNum=$("#idCardNum").val(); //
        var deliveryDetail=$("#deliveryDetail").val();
        var province=$("#province").val();
        var selId = $("#selId").val();
        var orDefault=$("input[name='orDefault']").is(':checked');
        var shengshi=$("#shengshi").val();


        if (name.length>15||name.length<2 ||!zszReg.test(name)) {
            tip('姓名只能是中文/数字/字母');
        }else if (tel.length!=11 ||!telReg.test(tel)) {
             tip('请填写正确的手机号码');
        }else if (deliveryDetail.length<5||deliveryDetail.length>50||!zszReg.test(deliveryDetail)) {
             tip('详细地址只能是5~50字内的中文/数字/字母');
        }else if(!((idCardNum.length==15&&card15Reg.test(idCardNum))||(idCardNum.length==18&&card18Reg.test(idCardNum)))){
             tip('请填写正确的身份证号码');
        }else if(null==province||""==province){
             tip('请填写正确的地址');
        }
        else {

            $.ajax({
                    type: 'POST',
                    url: "/address/save",
                    dataType: 'json',
                    data: $('form#cell_addressForm').serialize(),
                    success: function(data) {
                        console.log("data="+data);
                        if (data!=""&&data!=null){
                            if(selId!=0){ //0-普通添加更新跳全部地址界面  1-结算结算添加 2-结算界面更新
                                if(data.message.code==200) {
                                    if(orDefault==true){ // 先全部去掉默认
                                      $(".orDefaultSpan").hide();
                                    }
                                    if(selId==1){
                                        //结算界面添加地址
                                        paintAddressLi(data.address);
                                        addNewViewClose();
                                    }
                                    if(selId==2){
                                        //结算界面更新地址
                                        var li=$("#li"+addId);
                                        li.find(".nameSpan").html(name);
                                        li.find(".telSpan").html(tel);
                                        li.find(".idCardNumSpan").html(idCardNum);
                                        li.find(".deliverSpan").html(shengshi+" "+deliveryDetail);
                                        if(orDefault==true){
                                            li.find(".orDefaultSpan").show();
                                        }

                                        $('.add-shade').show(); //打开其他地址界面

                                    }

                                    $('.xnew-add-shade').html("");
                                    $('.xnew-add-shade').hide();

                                }else{
                                    tip(data.message.message);
                                }
                            }else{
                                if(data.code==200) {
                                     setTimeout("location.href='/address/"+Number(selId)+"'", 1000);
                                }else{
                                    tip(data.message);
                                }
                            }
                        }

                    }
            });
        }
 });




//取消收藏
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
              tip("删除失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //取消收藏成功
                  li.remove();
               } else tip("删除失败!");

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
              tip("取消订单失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //取消订单成功
                 setTimeout("location.href='/all'", 3000);
               } else tip("取消订单失败!");

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
              tip("删除失败!");
          },
          success: function(data) {
             console.log("data="+data);

               if(data.code==200){ //删除订单成功
                  li.remove();
               } else tip("删除失败!");

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
                  tip("删除失败!");
              },
              success: function(data) {
                 console.log("data="+data);

                  if (data!=""&&data!=null&&data.code==200){ //删除成功
                      if(position==0){
                        $("#li"+id).remove();
                      }else{
                       setTimeout("location.href='/all'", 2000);
                      }

                   } else tip("删除失败!");

              }
         });
    }
};

//删除地址
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
                      tip("删除失败!");
                  },
                  success: function(data) {
                     console.log(data);
                         if (data!=""&&data!=null&&data.code==200){ //删除地址成功
                          $("#li"+addId).remove();
                       } else tip("删除失败!");
                  }
             });
    }
}
//我的订单待付款,待收款
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
//意见反馈
$(document).on("click",".feedbackBtn",function(){
    var content=$("#feedback").val();

    if(null==content||""==content||content.length<5||content.length>140){
       tip("请输入5~140字的反馈意见");
    }
    else {
        var obj=new Object();
        obj.content=content;
        $.ajax({
                type: 'POST',
                url: "/feedback",
                contentType: "application/json; charset=utf-8",
                data : JSON.stringify(obj),
                error:function(request) {
                    tip("意见反馈失败");
                },
                success: function(data) {
                    console.log("data="+data+"==="+data.code);
                    if (data!=""&&data!=null&&data.code==200) {
                        setTimeout("location.href='/myView'", 3000);
                    }else{
                        tip("意见反馈失败");
                    }

                }
        });
    }
});
//收藏
$(document).on("click",".like-s",function(){
        var skuId=$("#skuId").val();
        var skuType=$("#skuType").val();
        var skuTypeId=$("#skuTypeId").val();
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
                    tip("收藏失败");
                },
                success: function(data) {
                    if (data!=""&&data!=null&&data.collectId>0) {
                        $("#collectId").val(data.collectId);
                    }else{
                        tip("收藏失败");
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

//申请售后
$(document).on("click", ".apply", function() {
    var orderId = $("#orderId").text();
    var img = $(this).parent().children().children().children();
    var invImg = img.attr("src");
    var skuTitle = img.parent().next().html();
    var price = img.parent().next().next().find(".price").html();
    var amount = img.parent().next().next().find(".number").html();
    var skuId = $(this).next().val();
    var url = '/service';
    var form = $('<form action="' + url + '" method="post">' +
    '<input type="hidden" name="orderId" value="' + orderId + '" />' +
    '<input type="hidden" name="invImg" value="' + invImg + '" />' +
    '<input type="hidden" name="skuTitle" value="' + skuTitle + '" />' +
    '<input type="hidden" name="price" value="' + price + '" />' +
    '<input type="hidden" name="amount" value="' + amount + '" />' +
    '<input type="hidden" name="skuId" value="' + skuId + '" />' +
    '</form>');
    $('body').append(form);
    form.submit();
});

//申请数量加减
$(document).on("click", "#cut", function() {
    var num = Number($(".num").html());
    if (num>1) {
        $(".num").html(num-1);
    }
});
$(document).on("click", "#add", function() {
    var num = Number($(".num").html());
    var amount = Number($(".number").html());
    if (num<amount) {
        $(".num").html(num+1);
    }
});

//上传图片
$(document).on('change','.face',function() {
    var file = $(this);
    file.after(file.clone().val(""));
    file.remove();
    var files = this.files;
    previewImage(this.files[0]);
});

function previewImage(file) {
    var imageType = /image.*/;
    if (!file.type.match(imageType)) {
        throw "File Type must be an image";
    }
    var gallery = document.getElementById("gallery");
    var div = document.createElement("div");
    var img = document.createElement("img");
    img.classList.add('pre-img');
    div.appendChild(img);
    gallery.appendChild(div);
    gallery.appendChild(div);

    //upload(file);
    // Using FileReader to display the image content
    var reader = new FileReader();
    reader.onload = (function(aImg) {
        return function(e) {
            aImg.src = e.target.result;
        }
    })(img);
    reader.readAsDataURL(file);
}

//申请售后下一步
$(document).on("click", ".next", function() {
    var num = Number($(".num").html());
    var amount = Number($(".number").html());
    if (num<1 || num>amount) {
        tip("申请数量不正确");
    } else if ($("#reason").val()=="") {
        tip("请填写问题描述");
    } else {
        $('.shade').show();
    }
});

//申请退款提交
$(document).on("click", ".box-btn", function() {
    var phoneReg = new RegExp(/^1[345678]\d{9}$/);
    if ($("#contactName").val()=="") {
         $("#warn").html("请填写联系人姓名").show()
         setTimeout(function(){$("#warn").hide();},2000);
    } else if ($("#contactTel").val()=="") {
        $("#warn").html("请填写联系方式").show()
        etTimeout(function(){$("#warn").hide();},2000);
    } else if (!phoneReg.test($("#contactTel").val())) {
        $("#warn").html("请填写正确的联系方式").show()
        setTimeout(function(){$("#warn").hide();},2000);
    } else {
//        alert("yes");
        $.ajax({
            type: "POST",
            url: "/order/apply/refund",
            dataType: 'json',
            data: $('form#cell_refForm').serialize(),
            success: function(data) {
                console.log(data);
            }

        });
//     $("#cell_refForm").submit();

    }
});

function pay(url,orderId,token,securityCode){
    $.ajax({
          type :"GET",
          url : "/order/verify/"+orderId,
          contentType: "application/json; charset=utf-8",
          error : function(request) {
              tip("请求失败!");
          },
          success: function(data) {
             console.log("data="+data);
              if (data!=""&&data!=null){

                  if(data.code==200){ //校验订单成功
                        //去支付
                        var form = $('<form action="' + url + '" method="post">' +
                                    '<input type="hidden" name="orderId" value="'+orderId+'"/>' +
                                    '<input type="hidden" name="token" value="'+token+'"/>' +
                                    '<input type="hidden" name="securityCode" value="'+securityCode+'"/>' +
                                    '</form>');
                         form.submit();

                   }else {
                       tip(data.message);
                        setTimeout("location.href='/all'", 3000);
                   }
              }
              else tip("请求失败!");

          }
     });
}


