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
    $(".stacktrace").each(function(event) {
        $(this).hide();
    });
    $(":button.stackbutton").click(function(event) {
        var name = $(this).attr("id");
        var trace = "#" + name + "_stack";
        var comando = $(this).attr("value");
        if (comando.indexOf("+")>=0) {
            comando = comando.replace("+", "-");
        } else {
            comando = comando.replace("-", "+");
        }
        $(this).attr("value", comando);
        var button = $(this);
        //button.addClass("sr_border");
        $(trace).toggle(250, function(){
                //button.removeClass("sr_border");
            }
        );
    });
    $("#right_exp").click(function(event) {
        window.opener.document.$(":button.stackbutton[value*='+']").each(function() {
            $(this).click();
        });
        event.preventDefault();
    });
    $("#right_col").click(function(event) {
        window.opener.document.$(":button.stackbutton[value*='-']").each(function() {
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
            $(".sr_frame_link_span").hide();
            $(".sr_resultset").hide();
            $(".sr_frame_link_span").show();
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