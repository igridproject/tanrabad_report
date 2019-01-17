<%-- 
    Document   : index
    Created on : Jan 15, 2019, 10:20:23 AM
    Author     : Jukkrapong
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TanRabad Report</title>
    </head>
    <body>
        <h1>Hello ${profile.getName()}</h1>
        <button type="button" onclick="logout()">Logout</button>
    </body>
    
    <script>
        var logout = function(){
            window.location.href = "/trbreport/logout";
        }
    </script>
</html>
