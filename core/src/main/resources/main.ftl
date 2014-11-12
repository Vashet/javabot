<!DOCTYPE html>

<html>
    <head>
        <title>javabot</title>

        <#--<link href="assets/images/favicon.png" rel="shortcut icon" type="image/png">-->

        <script src="/webjars/bootstrap/2.2.1/js/bootstrap.min.js"></script>

        <script type="text/javascript" src='assets/js/javabot.js'></script>

        <link rel="stylesheet/less" type="text/css" media="screen" href='assets/style/main.less'>
        <script type='text/javascript' src='/webjars/less/1.3.1/less.min.js'></script>
    </head>
    <body >
    <div id="page_wrapper">
        <div id="header_wrapper">
            <div id="header">
                <h1>Javabot</h1>
            </div>
        </div>

        <div id="left_side">
            <div>
                <h3>Info</h3>

                <div id="boxWrapper">
                    <ul class="plain">
                        <li><a href="//index">Home Page</a></li>
                        <li><a href="//factoids">Factoids</a>: ${factoidCount} </li>
                        <!--<li><a wicket:id="activity_link"><span wicket:id="stats">[stats]</span></a></li>-->
                        <li><a href="//karma">Karma Ranking</a></li>
                        <li><a href="//changes">Changelog</a></li>
                    </ul>
                </div>
            </div>
            @subjectNotPresent(handler) {
            <div>
                <h3><a href="//admin/login">Login</a></h3>
            </div>
            }
            @restrict(List(as("botAdmin"))) {
            <div>
                <h3>Admin</h3>
                <ul>
                    <li><a href="/admin">Admins</a></li>
                    <li><a href="/admin/config">Configuration</a></li>
                    <li><a href="/admin/javadoc">Javadoc</a></li>
                </ul>
            </div>
            }
            <h3>
                <table>
                    <tr>
                        <td>Channels</td>
                        <td>
                            @restrict(List(as("botAdmin"))) {
                            <a href="//admin/newChannel)}">+</a>
                            }
                        </td>
                    </tr>
                </table>
            </h3>

            <div class="boxWrapper">
                <table class="plain">
                    @context.channels.map { logged =>
                    <tr>
                        <td>
                            <a id="@{logged.getName()}"
                            @if(logged.getName().equals(context.channel)) { class="current" }
                            href="@{routes.Application.logs(URLEncoder.encode(logged.getName(), "UTF-8"), "today")}">@{logged.getName()}</a>
                        </td>
                        @subjectNotPresent(handler) {
                            <td></td>
                            <td></td>
                        }
                    </tr>
                    }
                </table>
            </div>
            <div>
                <h3>Credits</h3>
                <ul>
                    <li>cheeser</li>
                    <li>ernimril</li>
                    <li>joed</li>
                    <li>kinabalu</li>
                    <li>lunk</li>
                    <li>ojacobson</li>
                    <li>r0bby</li>
                    <li>ThaDon</li>
                    <li>ricky_clarkson</li>
                    <li>topriddy</li>
                </ul>
            </div>
        </div>
        <div id="content">
            <div class='featurebox_center'>
            <#include getChildView() >
            </div>
        </div>

        <br style="clear:both;border:none"/>
    </div>
    </body>
</html>
