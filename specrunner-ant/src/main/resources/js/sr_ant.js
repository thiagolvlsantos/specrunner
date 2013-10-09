$(document).ready(function(){
    $(".sr_antcall").each(function(){
        var log = $(this).attr("antcall");
        $(this).click(function(){
            $("[antlog='"+log+"']").toggle();
        });
    });
});