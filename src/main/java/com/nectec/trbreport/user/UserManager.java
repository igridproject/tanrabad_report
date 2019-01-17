package com.nectec.trbreport.user;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jukkrapong
 */
public class UserManager {
    
    private static final String ATT_NAME_USER_NAME = "trbreport";
    
    // Store accessToken in Session.
    public static void storeAccessToken(HttpSession session, String accessToken) {
        // On the JSP can access via ${loginedUser}
        session.setAttribute("accessToken", accessToken);
    }
    
    // Store refreshToken in Session.
    public static void storeRefreshToken(HttpSession session, String refreshToken) {
        // On the JSP can access via ${loginedUser}
        session.setAttribute("refreshToken", refreshToken);
    }
    
    // Store accessToken in Session.
    public static String getAccessToken(HttpSession session) {
        // On the JSP can access via ${loginedUser}
        
        if(session.getAttribute("accessToken") != null){
            return session.getAttribute("accessToken").toString();
        }
        else {
            return null;
        }
        
    }
    
    // Store refreshToken in Session.
    public static String getRefreshToken(HttpSession session) {
        // On the JSP can access via ${loginedUser}
        if(session.getAttribute("refreshToken") != null){
            return session.getAttribute("refreshToken").toString();
        }
        else return null;
        
    }
    
    // Store user info in Session.
    public static void storeLoginedUser(HttpSession session, UserProfile loginedUser) {
        // On the JSP can access via ${loginedUser}
        session.setAttribute("loginedUser", loginedUser);
    }
 
    // Get the user information stored in the session.
    public static UserProfile getLoginedUser(HttpSession session) {
        UserProfile loginedUser = (UserProfile) session.getAttribute("loginedUser");
        return loginedUser;
    }
 
    // Store info in Cookie
    public static void storeUserCookie(HttpServletResponse response, UserProfile user) {
        System.out.println("Store user cookie");
        Cookie cookieUserName = new Cookie(ATT_NAME_USER_NAME, user.getName());
        // 1 day (Converted to seconds)
        cookieUserName.setMaxAge(6 * 60 * 60);
        response.addCookie(cookieUserName);
    }
 
    public static String getUserNameInCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ATT_NAME_USER_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
 
    // Delete cookie.
    public static void deleteUserCookie(HttpServletResponse response) {
        Cookie cookieUserName = new Cookie(ATT_NAME_USER_NAME, null);
        // 0 seconds (This cookie will expire immediately)
        cookieUserName.setMaxAge(0);
        response.addCookie(cookieUserName);
    }
}
