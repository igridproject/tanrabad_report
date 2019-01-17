/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nectec.tanrabad.controller;

import com.nectec.trbreport.user.UserManager;
import com.nectec.trbreport.user.UserProfile;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.xdi.oxd.common.response.GetClientTokenResponse;


/**
 *
 * @author Jukkrapong
 */
@WebServlet(name = "ReportController", urlPatterns = {"/report"})
public class ReportController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private OXDService oxd = null;
    final Logger logger = Logger.getLogger(getClass().getName());

    public ReportController() {
        this.oxd = new OXDService(logger);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);   
//        UserProfile userProfile = UserManager.getLoginedUser(request.getSession());
        String accessToken = UserManager.getAccessToken(request.getSession());
        UserProfile userProfile = null;
        if(accessToken != null){
            userProfile = this.oxd.getUserInfo(accessToken);
            if(userProfile == null){
                String rtk = UserManager.getRefreshToken(request.getSession());
                GetClientTokenResponse newToken = this.oxd.getTokenByRefreshToken(rtk);
                if(newToken != null) {
                    UserManager.storeAccessToken(request.getSession(), newToken.getAccessToken());
                    UserManager.storeRefreshToken(request.getSession(), newToken.getRefreshToken());
                    userProfile = this.oxd.getUserInfo(newToken.getAccessToken());
                    UserManager.storeLoginedUser(request.getSession(), userProfile);
                    request.setAttribute("profile", userProfile);
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }
                else{
                    UserManager.storeAccessToken(request.getSession(), null);
                    UserManager.storeRefreshToken(request.getSession(), null);
                    UserManager.storeLoginedUser(request.getSession(), null);
                    response.sendRedirect(OXDService.POST_LOGOUT_REDIRECT_URL);
                }
            }  
        }
        else {
            if(request.getParameter("code") == null){
                String authorizationUrl = this.oxd.getAuthorizationUrl();
                response.sendRedirect(authorizationUrl); 
            }
            else{
                String code = request.getParameter("code");
                String state = request.getParameter("state");
                OXDToken token = this.oxd.getToken(code, state);
                UserManager.storeAccessToken(request.getSession(), token.getAccessToken());
                UserManager.storeRefreshToken(request.getSession(), token.getRefreshToken());
                UserProfile userInfo = this.oxd.getUserInfo(token.getAccessToken());
                System.out.println("Hello: " + userInfo.getName());
                if(!userInfo.getEmailVerified().equals("")){
                    if(userInfo.getEmailVerified().equals("true") && userInfo.getUserState().equals("active")){
                        UserManager.storeLoginedUser(request.getSession(), userInfo);
                        request.setAttribute("profile", userInfo);
                        request.getRequestDispatcher("index.jsp").forward(request, response);
                    }
                    else {
                        response.sendRedirect(OXDService.IAAM_REG_URL + "/profile/");
                    }
                }
                else{
                    response.sendRedirect(OXDService.IAAM_REG_URL + "/edit-profile/");
                }
                
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
