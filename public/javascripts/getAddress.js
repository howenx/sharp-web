
//获取购物车数量
$(document).ready(function() {
    $.ajax({
          type :"GET",
          url : "/address/1",
          contentType: "application/json; charset=utf-8",
          error : function(request) {
          },
          success: function(data) {
              if (data!=""&&data!=null&&null!=data.message){ //成功
                    if(data.message.code==200){
                           if(null!=data.address&&data.address.length>0){

                          // data.address.each(function(){
                           var addressList = eval(data.address);
                            for(var o in addressList){
                                paintAddressLi(addressList[o]);
                             }
                        }

                    }
               }
          }
     });
});

//隐藏添加地址模块
$(document).on("click",".closeAddNew",function(){
    $('.xnew-add-shade').html("");
    $('.xnew-add-shade').hide();
    addNewViewClose();
});

//隐藏修改地址模块
$(document).on("click",".closeAddUpdate",function(){
    $('.xnew-add-shade').html("");
    $('.xnew-add-shade').hide();
    $('.add-shade').show();
});
//其他地址界面
$(document).on("click",".address-r",function(){
    $('.add-shade').show();
    $(".big").hide();
    $(".back").hide();
    $("li").removeClass("amputate-current");
    var addId=$("input[name=addressId]").val();
    $("#li"+addId).addClass("amputate-current");
 });

//关闭其他地址界面
$(document).on("click",".amputate span",function(){
    $('.add-shade').hide();
    $(".big").show();
    $(".back").show();
});


//新增地址界面保存或者关闭的时候
function addNewViewClose(){
    if($("#addressArea").find("ul").find("li").size()>2){//说明是从地址列表界面新增
        $('.add-shade').show();//打开其他地址界面
    }else{
         $(".big").show(); //结算界面
         $(".back").show();
    }

}

//点击选择地址
$(document).on("click",".addressAreaA",function(){
    var addId=$(this).parents("li").val();
    $(".address").find("input[name='addressId']").val(addId);

    $(".settleAddressDiv").html('<a href="javascript:;">'+$(this).html()+"</a>");

    $(".otherAddressDiv").show();

    $(".amputate span").trigger("click"); //关闭其他地址界面

});
//绘制地址
function paintAddressLi(address){
 var str='<li id="li'+address.addId+'" class="ul-box-li clearfix hiddenAddressLi" value="'+address.addId+'">'+
       '<div class="address address-padding clearfix">'+
           '<a href="javascript:;" class="sel_add clearfix addressAreaA">'+
                '<p><i class="add-icon"></i><span>姓名:</span><span class="nameSpan">'+address.name+'</span>';
                if(address.orDefault==1){
                    str+='<span class="default orDefaultSpan">默认</span>';
                }else{
                    str+='<span class="default orDefaultSpan" style="display:none">默认</span>';
                }
                str+='</p>'+
                '<p><span>联系电话:</span><span class="telSpan">'+address.tel+'</span></p>'+
//                '<p><span>身份证号:</span><span class="idCardNumSpan">'+address.idCardNum+'</span></p>'+
                '<p class="clearfix"><span class="add-l">收货地址:</span><span class="add-r deliverSpan">'+address.deliveryCity+' '+address.deliveryDetail+'</span></p>'+
            '</a>'+
            '<div class="alter-x clearfix">'+
//                '<a href="javascript:;" class="alter-btn color" onclick="delAddress('+address.addId+','+address.orDestroy+')">删除</a>'+
                '<a href="javascript:;" class="color addressUpdate">修改</a>'+
            '</div>'+
        '</div>'+
    '</li>';
  $("#addressArea").find("ul").append(str);
  if($(".settleAddressDiv").find('a').hasClass("addressnew")){
    $(".addressAreaA").trigger("click");
  }

};

//添加地址弹出层
$(document).on("click",".addressnew",function(){
     var str='<div class="amput clearfix closeAddNew">'+
                       '<span style="cursor:pointer;"> × </span>'+
                   '</div>'+
                   '<form id="cell_addressForm" style="padding: 0 10px">'+
                      '<input  id="addId" name="addId" value="0" type="hidden"/>'+
                      '<div class="add-s">'+'<span>收件人姓名:</span>'+
                          '<input  placeholder="请输入收货人" id="name" name="name">'+
                      '</div>'+

                      '<div class="add-s">'+'<span>联系电话:</span>'+
                          '<input placeholder="请输入联系电话" id="tel" name="tel">'+
                      '</div>'+
//                      '<div class="add-s">'+
//                          '<input placeholder="身份证" id="idCardNum" name="idCardNum">'+
//                      '</div>'+

                      '<div id="addAddress">'+
                          '<div class="address_input">'+'<span>省市区:</span>'+
                              '<input class="address_input1" type="text" placeholder="请选择省市" id="shengshi" onClick="getProvinceBuy()" readonly="readonly">'+
                          '</div>'+
                      '</div>'+

                      '<input  type="hidden" id="province" name="province"/>'+
                      '<input  type="hidden" id="city" name="city" />'+
                      '<input  type="hidden" id="area" name="area" />'+
                      '<input  type="hidden" id="area_code" name="area_code" />'+
                      '<input  type="hidden" id="city_code" name="city_code" />'+
                      '<input  type="hidden" id="province_code" name="province_code" />'+


                      '<div class="add-s">'+'<span>详细地址:</span>'+
                          '<input placeholder="输入详细地址" id="deliveryDetail" name="deliveryDetail">'+
                      '</div>'+

                      '<div class="add-m">'+
                          '<span><input type="checkbox" id="orDefault" name="orDefault"/>设为默认</span>'+
                      '</div>'+

                      '<div class="cue">'+
                          '<p class="color">提示:</p>'+
                          '<p>1.海关需要对海外购物查验身份信息，错误信息可能导致无法通关。</p>'+
                          '<p>2.身份信息将加密报关，韩秘美将保证您的信息安全。</p>'+
                      '</div>'+
                      '<input type="hidden" id="selId" name="selId" value="1">'+
                      '<input type="hidden" class="formSubmitTimes" value="0"/>'+
                  '</form>'+
             '<div class="plus-add-btn">'+
                  '<button class="plus-add bg addAddressBtn">添加</button>'+
             '</div>';
                  ;
                  $("#newAddressDiv").prepend(str);
                  $("#newAddressDiv").show();


                  $('.add-shade').hide(); //关闭其他地址界面
                  $('.big').hide(); //关闭结算界面


});

//更新地址弹出层
$(document).on("click",".addressUpdate",function(){
    var addId=$(this).parents("li").val();
    $.ajax({
              type :"GET",
              url : "/address/update/"+addId+"/1",
              contentType: "application/json; charset=utf-8",
              error : function(request) {
              },
              success: function(data) {
                 var str='<div class="amput clearfix closeAddUpdate">'+
                               '<span style="cursor:pointer;"> × </span>'+
                               '</div>'+
                               '<form id="cell_addressForm" style="padding: 0 10px">'+
                                 '<input  id="addId" name="addId" value="'+data.addId+'" type="hidden"/>'+
                                 '<div class="add-s">'+'<span>收件人姓名:</span>'+
                                     '<input  placeholder="请输入收货人" id="name" name="name" value="'+data.name+'">'+
                                 '</div>'+

                                  '<div class="add-s">'+'<span>联系电话:</span>'+
                                      '<input placeholder="请输入联系电话" id="tel" name="tel" value="'+data.tel+'">'+
                                  '</div>'+
//                                  '<div class="add-s">'+
//                                      '<input placeholder="身份证" id="idCardNum" name="idCardNum" value="'+data.idCardNum+'">'+
//                                  '</div>'+

                                  '<div id="addAddress">'+
                                      '<div class="address_input">'+'<span>省市区:</span>'+
                                          '<input class="address_input1" type="text" placeholder="请选择省市" id="shengshi" onClick="getProvinceBuy()" readonly="readonly" value="'+data.deliveryCity+'">'+
                                      '</div>'+
                                  '</div>'+

                                  '<input  type="hidden" id="province" name="province" value="0"/>'+
                                  '<input  type="hidden" id="city" name="city" />'+
                                  '<input  type="hidden" id="area" name="area" />'+
                                  '<input  type="hidden" id="area_code" name="area_code" />'+
                                  '<input  type="hidden" id="city_code" name="city_code" />'+
                                  '<input  type="hidden" id="province_code" name="province_code" />'+


                                  '<div class="add-s">'+'<span>详细地址:</span>'+
                                      '<input placeholder="输入详细地址" id="deliveryDetail" name="deliveryDetail" value="'+data.deliveryDetail+'">'+
                                  '</div>'+

                                  '<div class="add-m">'+
                                      '<span>';
                                      if(data.orDefault==true){
                                        str+='<input type="checkbox" name="orDefault" checked/>';
                                      }else{
                                       str+='<input type="checkbox" name="orDefault"/>';
                                      }
                                  str+='设为默认</span>'+
                                  '</div>'+

                                  '<div class="cue">'+
                                      '<p class="color">提示:</p>'+
                                      '<p>1.海关需要对海外购物查验身份信息，错误信息可能导致无法通关。</p>'+
                                      '<p>2.身份信息将加密报关，韩秘美将保证您的信息安全。</p>'+
                                  '</div>'+
                                  '<input type="hidden" id="selId" name="selId" value="2">'+
                                  '<input type="hidden" class="formSubmitTimes" value="0"/>'+
                              '</form>'+
                              '<div class="plus-add-btn">'+
                                  '<button class="plus-add bg addAddressBtn">保存修改</button>'+
                               '</div>';

                              $("#updateAddressDiv").prepend(str);
                              $("#updateAddressDiv").show();

                              $('.add-shade').hide(); //关闭其他地址界面

              }
         });
});


