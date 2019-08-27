<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>注册用户</title>
    <%--<script type="text/javascript" src="/home/benhairui/Documents/gitlab-workspace/Work/ic/src/main/webapp/jquery-3.3.1.min.js"></script>--%>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

    <script type="text/javascript">
        // $.cookie("a", 12, {secure: false})
        $(document).ready(function () {
            // $.cookie("a",12,{ expires: 7, path:'/', secure: false }) /* 加入cookie*/
            $("#login").click(function () {
                $("#mydiv").html("<p>" + "hello world" + "<p>");
                // $.cookie("a",12,{ expires: 7, path:'/', secure: false }) /* 加入cookie*/
                var url = "/hello/registerUser";
                var un = $("#uname").val();
                var ps = $("#pwd").val()
                $.post(
                    url,
                    {
                        username: $("#uname").val(),
                        password: $("#pwd").val()
                    },
                    function (data) {
                        $("#mydiv").html("<p>" + data + "_" + (data=="登录成功") + "<p>");
                        if(data == "success") {
                            // $("#mydiv").html("<p>" + data.username + "***" + data.password + "<p>");
                            $("#mydiv").html("<p>" + data + "<p>");
                            $.cookie("userInfo", un, {secure: false})
                            WindowDialog()
                        }else{
                            $("#mydiv").html("<p>" + "登录失败" + "<p>");
                        }
                    }
                );
            });
        });

        function WindowDialog() {
            window.location.href = "/hello/startTest"
            $.cookie("isSubmit",true,{secure:false})
        }

    </script>

    <style type="text/css">
        .main {
            text-align: center;
            background-color: #fff;
            border-radius: 20px;
            width: 800px;
            height: 400px;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
        }

        div.result {
            float: left;
        }

        div.prop {
            float: left;
            margin-right: 10px;
            width: 100px;
            text-align: right;
            clear: both;
        }
    </style>
</head>
<body>
<div class="main">


    <form id="myForm" action="/hello/registerUser" method="post">
        <div>
            <div class="prop">用户名：</div>
            <div class="result"><input type="text" id="uname" name="username"/></div>
        </div>

        <div>
            <div class="prop">密码：</div>
            <div class="result"><input type="password" id="pwd" name="password"/></div>
        </div>

        <div>
            <div class="prop">&nbsp;</div>
            <div class="result"><input type="button" value="登录" id="login"/></div>
        </div>
        <br>

        <div style="color: red;" id="mydiv"></div>

    </form>
</div>

<br/>


</body>
</html>