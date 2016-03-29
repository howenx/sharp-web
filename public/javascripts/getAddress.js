
//获取购物车数量
$(document).ready(function() {
    $.ajax({
          type :"GET",
          url : "/address/1",
          contentType: "application/json; charset=utf-8",
          error : function(request) {
          },
          success: function(data) {
             console.log("data="+data);
              if (data!=""&&data!=null&&null!=data.message){ //成功
                    if(data.message.code==200){
                           if(data.address.length>0){

                          // data.address.each(function(){
                           var addressList = eval(data.address);
                            for(var o in addressList){
                               var str='<li id="li'+addressList[o].addId+'" class="ul-box-li clearfix" onclick="liAddressOnclick('+addressList[o].addId+')">'+
                                       '<div class="address clearfix">'+
                                           '<a href="javascript:;" class="sel_add clearfix">'+
                                                '<p><i class="add-icon"></i><span>姓名:</span><span>'+addressList[o].name+'</span>';
                                                if(addressList[o].orDestroy==true){
                                                    str+='<span class="color">默认</span>';
                                                }
                                                str+='</p>'+
                                                '<p><span>联系电话:</span><span>'+addressList[o].tel+'</span></p>'+
                                                '<p><span>身份证号:</span><span>'+addressList[o].idCardNum+'</span></p>'+
                                                '<p class="clearfix"><span class="add-l">收货地址:</span><span class="add-r">'+addressList[o].deliveryCity+' '+addressList[o].deliveryDetail+'</span></p>'+
                                            '</a>'+
                                            '<div class="alter clearfix">'+
                                                '<a href="javascript:;" class="alter-btn color" onclick="delAddress('+addressList[o].addId+','+addressList[o].orDestroy+')">删除</a>'+
                                                '<a id="700351" href="javascript:;" class="color addup">修改</a>'+
                                            '</div>'+
                                        '</div>'+
                                    '</li>';
                                $("#addressArea").find("ul").append(str);
                             }
                        }

                    }
               }
          }
     });
});

function liAddressOnclick(addId){
$(".address").find("input[name='addressId']").val(addId);

}


