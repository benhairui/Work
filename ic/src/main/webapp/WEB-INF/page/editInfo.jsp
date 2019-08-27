<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>注册用户</title>

    <style type="text/css">
        div.prop {
            float: left;
            margin-right: 10px;
            width: 100px;
            text-align: right;
            clear: both;
            /*top: 30px;*/
            vertical-align: text-bottom;
            min-height: 10px;
        }

        div.result {
            float: left;
            /*top: 30px;*/
            vertical-align: text-bottom;
            width: 500px;
        }

        .head {
            margin: 0 auto;
            width: 800px;
            height: 200px;
            border-radius: 10px;
            border: 1px solid #F00;
        }

        .main {
            margin: 0 auto;
            text-align: left;
            background-color: #fff;
            border-radius: 10px;
            border: 1px solid #FFF;;
            width: 800px;
            height: 500px;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
        }

        .textArea {
            width: 500px;
            float: left;
            height: 80px;
            resize: none;
        }

        .divTextArea {
            min-height: 100px;
            /*border: 1px solid #FF0;*/
        }

        .divSecond {
            min-height: 30px;
        }


    </style>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

    <script type="text/javascript">
        // $.cookie("b",121) /* 加入cookie*/

        $(document).ready(function () {


            /**
             * 在页面初始化时,加载一次数据
             **/
            $(function () {
                $("#next").click();
            })


            $("#submit").click(function () {
                        var url = "/hello/submit";
                $("#display").html($.cookie("userInfo"))
                        $.post(
                            url,
                            {
                                id: $("#sid").text(),
                                filterWord: $("#filter_word").val(),
                                author: $.cookie("userInfo")
                            },
                            function (data) {
                                if (data == "true") {
                                    $("#mydiv").html("<p>" + "提交成功" + "</p>");
                                } else {
                                    $("#mydiv").html("<p>" + "提交失败" + "</p>");
                                }
                            }
                        );
                }
            );


            $("#next").click(function () {
                    var url = "/hello/next";
                    $.post(
                        url,
                        {
                            id: $("#sid").text(),
                            userName: $.cookie("userInfo")
                        },
                        function (data) {

                            $("#author").html($.cookie("userInfo"))
                            $("#sid").html(data.id);
                            $("#category").html(data.category)
                            $("#judge").html("" + data.isFinStr)
                            $("#kw").html(data.kw)
                            $("#level").html(data.level)
                            $("#sen").html(data.midInfo)
                            $("#cand_word").html(data.cand_word_str)
                        }
                    );
                    isSubmit = false;
                    $.cookie("isSubmit", false, {secure: false})
            });
        })
        ;
    </script>


</head>
<body>

<div class="main">
    <div class="divSecond">
        <div>
            <div class="prop">筛选人:</div>
            <div id="author" class="result"> hell oworld</div>
        </div>
    </div>


    <div class="divSecond">
        <div>
            <div class="prop">id:</div>
            <div id="sid" class="result"> hell oworld</div>
        </div>
    </div>


    <div class="divSecond">
        <div>
            <div class="prop">类别:</div>
            <div id="category" class="result"> hell oworld</div>
        </div>
    </div>


    <div class="divSecond">
        <div>
            <div class="prop">是否已筛选:</div>
            <div id="judge" class="result"></div>
        </div>
    </div>

    <div class="divSecond">
        <div>
            <div class="prop">关键词:</div>
            <div id="kw" class="result"> hell oworld</div>
        </div>
    </div>

    <div class="divSecond">
        <div>
            <div class="prop">上/中/下游:</div>
            <div id="level" class="result">hell oworld</div>
        </div>
    </div>

    <div class="divSecond">
        <div class="prop">候选词:</div>
        <div id="cand_word" class="result" style="word-wrap: break-word">hell oworld</div>
    </div>


    <div class="divTextArea">
        <div class="prop">句子:</div>
        <textarea id="sen" class="textArea" readonly>hell oworld</textarea>
    </div>
    <br>


    <div class="divTextArea">
        <div class="prop">结果:</div>
        <textarea id="filter_word" class="textArea">减肥：</textarea>
    </div>


    <div class="divSecond">
        <div class="prop">&nbsp;</div>
        <div>
            <form id="myform2" name="myform" method="post" action="/hello/submit">
                <input type="button" value="提交" id="submit" style="float: left"/>
            </form>
        </div>
        <div>
            <form id="myform3" name="myform" method="post" action="/hello/pre">
                <input type="button" value="上一条" id="pre" style="float: right;margin-right: 20px;margin-left: 20px"/>
            </form>
        </div>

        <div>
            <form id="myform4" name="myform" method="post" action="/hello/next">
                <input type="button" value="下一条" id="next" style="float: right;margin-right: 20px"/>
            </form>

        </div>
    </div>

</div>

</body>
</html>
