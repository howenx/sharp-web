//点击评论页签
function commentTab(skuType,skuTypeId,commentType){
     var index=commentType-1;
     $('.scroll-wrap .scroll-content section').eq(index).show().siblings().hide();
     $('.nav-tab-top li').find("a").removeClass('current');
     $('.nav-tab-top li').eq(index).find("a").addClass('current');
    // $(this).addClass('current').parent().siblings().children('a').removeClass('current');
    var curPage=$("#curPage"+commentType).val();
    if(curPage>0){ //说明请求过了,只切换当前状态
      return false;
    }
    $.ajax({
           type :"GET",
           url : "/comment/detail/"+skuType+"/"+skuTypeId+"/1"+"/"+commentType,
           contentType: "application/json; charset=utf-8",
           error : function(request) {
               tip("删除失败!");
           },
           success: function(data) {
                if(data.message.code==200){ //成功
                    $("#curPage"+commentType).val(1);
                    var commentList = eval(data.remarkList);
                    for(var o in commentList){
                        paintComment(commentList[o],commentType);
                     }

                } else{
                    tip(data.message.message);
                }
           }
      });
};

//绘制评论
function paintComment(comment,commentType){
    var html='<li><div class="hd clearfix">'+
            '<span class="hd-l"><img src="'+comment.userImg+'"/></span>'+
            '<span class="hd-m"><i>'+comment.userName+'</i></span>'+
            '<span class="hd-r"><i>'+comment.createAt+'</i></span></div>'+
            '<div class="md"><p></p><span>'+comment.content+'</span>'+
            '<div class="clearfix">';

            if(null!=comment.picture&&""!=comment.picture){
             var pictureList = eval(comment.picture);
                for(var o in pictureList){
                   html+='<img src="'+pictureList[o]+'">';
                }
            }
       html+='</div></div></li>';
       $("#commentUl"+commentType).append(html);
};

//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}


//$(document).ready(function() {
//
//
//});



