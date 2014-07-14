/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
$(document).ready(function() {
    $(".sr_resultset th[class*=sr_nop]").each(function(event) {
        var show = $("#sr_control").attr("show");
        if(show=="true"){
            var size = "20px";
            $(this).append('<input id="sr_plus" type=button value="+" style="width:'+size+'"/>');
            $(this).append(' <input id="sr_minus" type=button value="-" style="width:'+size+'"/>');
            $(this).append(' <input id="sr_previous" type=button value="&lt;" style="width:'+size+'"/>');
            $(this).append(' <span id="sr_counter">0</span>');
            $(this).append(' <input id="sr_next" type=button value="&gt;" style="width:'+size+'"/>');
        }
    });

    $("#sr_plus").click(function(event) {
        $(":button.sr_stackbutton").each(function(e) {
            var comando = $(this).attr("value");
            if (comando.indexOf("+")>=0) {
                $(this).click();
            } 
        });
    });
    $("#sr_minus").click(function(event) {
        $(":button.sr_stackbutton").each(function(e) {
            var comando = $(this).attr("value");
            if (comando.indexOf("-")>=0) {
                $(this).click();
            } 
        });
    });
    $("#sr_next").click(function(event) {
        var index = $("#sr_counter").data("index");
        if(index == undefined) {
            index = 0;
        }
        var type = $("#sr_control").attr("status");
        var max = $("#max_"+type).attr("max");
        index++;
        if(index <= max){
            $("#sr_counter").data("index",index);
            $("#sr_counter").html(index);
            var target = $("#"+type+index);
            if(target.attr("value").indexOf('+')>=0){
                target.click();
            }
            var pos = target.position();
            $(window).scrollTop( pos.top );
            $(window).scrollLeft( pos.left );
        }
    });
    $("#sr_previous").click(function(event) {
        var index = $("#sr_counter").data("index");
        if(index == undefined) {
            index = 0;
        }
        var type = $("#sr_control").attr("status");
        index--;
        if(index > 0){
            $("#sr_counter").data("index",index);
            $("#sr_counter").html(index);
            var target = $("#"+type+index);
            if(target.attr("value").indexOf('+')>=0){
                target.click();
            }
            var pos = target.position();
            $(window).scrollTop( pos.top );
            $(window).scrollLeft( pos.left );
        }
    });
    $(".sr_stacktrace").each(function(event) {
        $(this).hide();
    });
    $(":button.sr_stackbutton").click(function(event) {
        var name = $(this).attr("id");
        var trace = "#" + name + "_stack";
        var comando = $(this).attr("value");
        if (comando.indexOf("+")>=0) {
            comando = comando.replace("+", "-");
        } else {
            comando = comando.replace("-", "+");
        }
        $(this).attr("value", comando);
        $(trace).toggle(200);
    });
    $("#right_exp").click(function(event) {
        window.opener.document.$(":button.sr_stackbutton[value*='+']").each(function() {
            $(this).click();
        });
        event.preventDefault();
    });
    $("#right_col").click(function(event) {
        window.opener.document.$(":button.sr_stackbutton[value*='-']").each(function() {
            $(this).click();
        });
        event.preventDefault();
    });

    $("*[id*='_ref']").each(function(event) {
        $(this).hide();
    });
    $(":button.collapsable").click(function(event) {
        var name = $(this).attr("id");
        var ref = "#" + name + "_ref";
        var comando = $(this).attr("value");
        if (comando.indexOf("+")>=0) {
            comando = comando.replace("+", "-");
        } else {
            comando = comando.replace("-", "+");
        }
        $(this).attr("value", comando);
        $(ref).toggle("slow");
    });

    $(".sr_frame_link_span a").click(function(event) {
        event.preventDefault();
        var height = screen.height - 100;
        var width = screen.width * 0.75;
        if(!$(this).data("done")){
            window.moveTo(0,0);
            window.resizeTo(width,height);
            $(this).data("done",true);
        }
        var href = $(this).attr("href");
        var other = window.open(href, "sr_details", 'scrollbars=1,top=0,left='+(width-20)+',width='+(screen.width-width)+',height='+height);
    });
    
    $(".sr_status_item a").click(function(event) {
        event.preventDefault();
        var href = "../"+$(this).attr("href");
        window.opener.location.href = href;
    });

    $("*[id*='_inc']").each(function(event) {
        //$(this).hide();
    });
    $(".include").each(function() {
        $(this).click(function(event){
            var name = "#" + $(this).attr("id") + "_inc";
            $(name).slideToggle();
            event.preventDefault();
        });
    });
    $(".collapse").each(function() {
        var name = "#" + $(this).attr("id") + "_inc";
        $(name).hide();
    });
    $(".expanded").each(function() {
        var name = "#" + $(this).attr("id") + "_inc";
        $(name).show();
    });

    $(":button.top_exp, #right_exp, :button.top_col, #right_col").css("width","45%");
    $(":button.top_exp, #right_exp").attr("title","Expand all");
    $(":button.top_col, #right_col").attr("title","Collapse all");
});

$(function() {
});

function getFileName() {
    var url = window.location.href;
    url = url.substring(0, (url.indexOf("#") == -1) ? url.length : url.indexOf("#"));
    url = url.substring(0, (url.indexOf("?") == -1) ? url.length : url.indexOf("?"));
    url = url.substring(url.lastIndexOf("/") + 1, url.length);
    url = url.replace("_frame.html", ".html");
    url = url.replace("_top.html", ".html");
    url = url.replace("_right.html", ".html");
    return url;
}