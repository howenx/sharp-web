/**
 * 地区联动
 * */
function getProvinceBuy(){
$("body .dqld_div").remove();
	var province=eval(proStr);
	var newStr=new Array();
	newStr.push("<div class=\"dqld_div\" style=\"\"><ul>");
	for(var i=0,psize=province.length;i<psize;i++){
		province[i].NAME;
		newStr.push("<li onclick=\"getCityBuy("+i+")\">"+province[i].NAME+"</li>");
	}
	newStr.push("</ul></div>");
	$("body").append(newStr.join(""));
}

function getCityBuy(val){
	var province=eval(proStr);
	var city=eval(province[val].ITEMS);
	var newStr=new Array();
	newStr.push("<div class=\"dqld_div\"><ul>");
	newStr.push("<li onclick=\"getProvinceBuy()\" style=\"background-color:#CCCCCC;\">"+province[val].NAME+"</li>");
	for(var j=0,csize=city.length;j<csize;j++){
		newStr.push("<li onclick=\"getAreaBuy("+j+","+val+")\"  style=\"padding-left:20px;\">"+city[j].NAME+"</li>");
	}
	newStr.push("</ul></div>");
	$("body .dqld_div").remove();
	$("body").append(newStr.join(""));
}

function getAreaBuy(val,val1){
	var province=eval(proStr);
	var city=eval(province[val1].ITEMS);
	var area=eval(city[val].ITEMS);
	var newStr=new Array();
	newStr.push("<div class=\"dqld_div\"><ul>");
	newStr.push("<li onclick=\"getProvinceBuy()\" style=\"background-color:#CCCCCC;\">"+province[val1].NAME+"</li>");
	newStr.push("<li onclick=\"getCityBuy("+val1+")\" style=\"background-color:#e5e5e5;padding-left:10px;\">"+city[val].NAME+"</li>");
	for(var t=0,asize=area.length;t<asize;t++){
		area[t].NAME;
		newStr.push("<li  style=\"padding-left:25px;\" onclick=\"getallArea("+val1+","+val+","+t+")\">"+area[t].NAME+"</li>");
	}
	newStr.push("</ul></div>");
	if(asize==0){
		var allarea=province[val1].NAME+city[val].NAME;
		$("#shengshi").attr({"SS":province[val1].NAME,"SQ":city[val].NAME,"XS":""});
		$("#shengshi").val(allarea);
		$("#province").val(province[val1].NAME);
        $("#city").val(city[val].NAME);
        $("#area").val("");
		$("#province_code").val(province[val1].EN);

		$("body .dqld_div").remove();
	}
	else{
		$("body .dqld_div").remove();
		$("body").append(newStr.join(""));
	}
}

function getallArea(val,val1,val2){
	var province=eval(proStr);
	var city=eval(province[val].ITEMS);
	var area=eval(city[val1].ITEMS);
	var allarea=province[val].NAME+city[val1].NAME+area[val2].NAME;
	$("#shengshi").attr({"SS":province[val].NAME,"SQ":city[val1].NAME,"XS":area[val2].NAME});
	$("#shengshi").val(allarea);
    $("#province").val(province[val].NAME);
    $("#city").val(city[val1].NAME);
    $("#area").val(area[val2].NAME);
    $("#province_code").val(province[val].EN);
	$("body .dqld_div").remove();
}
/*地区联动*/

/*从百度地图中获得的地址*/
function getAddressFromBMap(province,city,area,street,streetNumber){
    var provinceList=eval(proStr);
    for(var p in provinceList){
        if(province==provinceList[p].NAME){ //省份
           var cityList=eval(provinceList[p].ITEMS);
           for(var c in cityList){
                if(city==cityList[c].NAME){ //市
                    var areaList=eval(cityList[c].ITEMS);
                    for(var a in areaList){
                        if(area==areaList[a].NAME){ //县

                            $("#shengshi").attr({"SS":province,"SQ":city,"XS":area});
                            $("#shengshi").val(province+city+area);
                            $("#province").val(province);
                            $("#city").val(city);
                            $("#area").val(area);
                            $("#province_code").val(provinceList[p].EN);
                            if(null!=street&&""!=street){
                                 $("#deliveryDetail").val(street+streetNumber);
                            }
                             return true;
                        }
                    }

                }
           }

        }

    }

}