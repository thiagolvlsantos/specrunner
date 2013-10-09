$(document).ready(function(){
	var c = 0;
    $(".sr_antcall").each(function(){
        var log = $(this).attr("antcall");
        $(this).click(function(){
        	console.log("log="+log);
            $("[antlog='"+log+"']").toggle();
        });
    });
});