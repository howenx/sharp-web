
//当前url
$(document).ready(function() {

    $("#curUrl").val(window.location.href);

});

//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}

//增加表单提交次数
function addFormSubmitTimes(curForm){
    var formSubmitTimes=curForm.find(".formSubmitTimes").val();
    if(typeof(formSubmitTimes)=="undefined"||null==formSubmitTimes){
         return ;
    }
    curForm.find(".formSubmitTimes").val(parseInt(formSubmitTimes)+1);
}
//重置表单提交次数
function resetFormSubmitTimes(curForm){
    var formSubmitTimes=curForm.find(".formSubmitTimes").val();
    if(typeof(formSubmitTimes)=="undefined"||null==formSubmitTimes){
         return ;
    }
    curForm.find(".formSubmitTimes").val(0);
}

//检测表单提交次数
function checkFormRepeatSubmit(curForm){
    var formSubmitTimes=curForm.find(".formSubmitTimes").val();
    if(typeof(formSubmitTimes)=="undefined"||null==formSubmitTimes){
        return true;
    }
    if(formSubmitTimes>=1){  //提交过了
        tip("亲,数据正在提交中,请勿重复操作");
        return false;
    }
    return true;
}


