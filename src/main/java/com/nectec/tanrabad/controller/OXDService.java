/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nectec.tanrabad.controller;

import com.nectec.trbreport.user.UserProfile;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.xdi.oxd.client.CommandClient;
import org.xdi.oxd.common.Command;
import org.xdi.oxd.common.CommandType;
import org.xdi.oxd.common.params.GetAccessTokenByRefreshTokenParams;
import org.xdi.oxd.common.params.GetAuthorizationUrlParams;
import org.xdi.oxd.common.params.GetLogoutUrlParams;
import org.xdi.oxd.common.params.GetTokensByCodeParams;
import org.xdi.oxd.common.params.GetUserInfoParams;
import org.xdi.oxd.common.response.GetAuthorizationUrlResponse;
import org.xdi.oxd.common.response.GetClientTokenResponse;
import org.xdi.oxd.common.response.GetTokensByCodeResponse;
import org.xdi.oxd.common.response.GetUserInfoResponse;
import org.xdi.oxd.common.response.LogoutResponse;

/**
 *
 * @author Jukkrapong
 */
public class OXDService {
    
    private final String OXD_ID = "de5474c5-0cf2-4d7a-8ba0-cf64037a54cb";
    private final String HOST = "localhost";
    private final int PORT = 8099;
    public static String POST_LOGOUT_REDIRECT_URL = "https://reporttest.lsr.nectec.or.th/trbreport/report";
    public static String IAAM_REG_URL = "https://iaamtest.lsr.nectec.or.th/iaamreg";
    
    private CommandClient client = null;
    private Logger logger = null;

    public OXDService(Logger logger) {
        this.logger = logger;
    }
    
    
    public String getAuthorizationUrl() throws IOException{
        String authorizationUrl = null;
        try {
            client = new CommandClient(this.HOST, this.PORT);
            final GetAuthorizationUrlParams commandParams = new GetAuthorizationUrlParams();
            commandParams.setOxdId(this.OXD_ID);
            commandParams.setScope(Arrays.asList("openid", "profile"));

            final Command command = new Command(CommandType.GET_AUTHORIZATION_URL);
            command.setParamsObject(commandParams);

            final GetAuthorizationUrlResponse resp = client.send(command).dataAsResponse(GetAuthorizationUrlResponse.class);
            authorizationUrl = resp.getAuthorizationUrl();
            this.logger.info("authorizationUrl: " + authorizationUrl);
            return authorizationUrl;
        } finally {
            this.logger.info("CommandClient closed.");
            CommandClient.closeQuietly(client);
        }
    }
    
    public OXDToken getToken(String code, String state) throws IOException{
        try {
            client = new CommandClient(this.HOST, this.PORT);
            final GetTokensByCodeParams commandParams = new GetTokensByCodeParams();
            commandParams.setOxdId(this.OXD_ID);
            commandParams.setCode(code);
            commandParams.setState(state);

            final Command command = new Command(CommandType.GET_TOKENS_BY_CODE).setParamsObject(commandParams);

            final GetTokensByCodeResponse resp = client.send(command).dataAsResponse(GetTokensByCodeResponse.class);
            String accessToken = resp.getAccessToken();
            String refreshToken = resp.getRefreshToken();
            return new OXDToken(accessToken, refreshToken);
        } finally {
            this.logger.info("CommandClient closed.");
            CommandClient.closeQuietly(client);
        }
    }
    
    public GetClientTokenResponse getTokenByRefreshToken(String rtk) throws IOException{
        try {
            client = new CommandClient(this.HOST, this.PORT);
            GetAccessTokenByRefreshTokenParams params = new GetAccessTokenByRefreshTokenParams();
            params.setOxdId(this.OXD_ID);
            params.setRefreshToken(rtk);
            GetClientTokenResponse dataAsResponse = client.send(new Command(CommandType.GET_ACCESS_TOKEN_BY_REFRESH_TOKEN).setParamsObject(params)).dataAsResponse(GetClientTokenResponse.class);
            return dataAsResponse;
        }finally {
            this.logger.info("CommandClient closed.");
            CommandClient.closeQuietly(client);
        }
    }
    
    public UserProfile getUserInfo(String accessToken) throws IOException{
        UserProfile profile = null;
        try {
            client = new CommandClient(this.HOST, this.PORT);
//            final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrl);
  
            GetUserInfoParams params = new GetUserInfoParams();
            params.setOxdId(this.OXD_ID);
            params.setAccessToken(accessToken);
            GetUserInfoResponse userinfo = client.send(new Command(CommandType.GET_USER_INFO).setParamsObject(params)).dataAsResponse(GetUserInfoResponse.class);  
            if(userinfo != null && !userinfo.getClaims().isEmpty()){
                System.out.println(userinfo);
                profile = new UserProfile();
                profile.setEmail(userinfo.getClaims().get("email").get(0));
                profile.setFamily_name(userinfo.getClaims().get("family_name").get(0));
                profile.setUserRole(userinfo.getClaims().get("userRole").get(0));
                profile.setName(userinfo.getClaims().get("name").get(0));
                profile.setDob(userinfo.getClaims().get("dob").get(0));
                profile.setOrgProvinceName(userinfo.getClaims().get("orgProvinceName").get(0));
                profile.setUserState(userinfo.getClaims().get("userState").get(0));
                profile.setParam(userinfo.getClaims().get("param").get(0));
                if(userinfo.getClaims().get("emailVerified").get(0) != null){
                    profile.setEmailVerified(userinfo.getClaims().get("emailVerified").get(0));
                }
                else{
                    profile.setEmailVerified("");
                }
                profile.setUpdateTimestamp(userinfo.getClaims().get("updateTimestamp").get(0));
                profile.setOrgId(userinfo.getClaims().get("orgId").get(0));
                profile.setInum(userinfo.getClaims().get("inum").get(0));
                profile.setGiven_name(userinfo.getClaims().get("given_name").get(0));
                profile.setTelephone(userinfo.getClaims().get("telephone").get(0));
                profile.setUser_name(userinfo.getClaims().get("user_name").get(0));
                profile.setRegisterTimestamp(userinfo.getClaims().get("registerTimestamp").get(0));
                profile.setOrgTambonName(userinfo.getClaims().get("orgTambonName").get(0));
                profile.setOrgAmphurName(userinfo.getClaims().get("orgAmphurName").get(0));
                profile.setOrgProvinceCode(userinfo.getClaims().get("orgProvinceCode").get(0));
                profile.setGender(userinfo.getClaims().get("gender").get(0));
                profile.setOrgTambonCode(userinfo.getClaims().get("orgTambonCode").get(0));
                profile.setOrgAmphurCode(userinfo.getClaims().get("orgAmphurCode").get(0));
                profile.setOrgName(userinfo.getClaims().get("orgName").get(0));
                profile.setSub(userinfo.getClaims().get("sub").get(0));
            }
            return profile;
        } finally {
            this.logger.info("CommandClient closed.");
            CommandClient.closeQuietly(client);
        }
    }
    
    public String getLogoutURL(String accessToken) throws IOException{
        try {
            client = new CommandClient(this.HOST, this.PORT);
            final GetLogoutUrlParams commandParams = new GetLogoutUrlParams();
            commandParams.setOxdId(this.OXD_ID);
//            commandParams.setIdTokenHint("dummy_token");
            commandParams.setPostLogoutRedirectUri(this.POST_LOGOUT_REDIRECT_URL);
//            commandParams.setState(UUID.randomUUID().toString());
//            commandParams.setSessionState(UUID.randomUUID().toString()); // here must be real session instead of dummy UUID

            final Command command = new Command(CommandType.GET_LOGOUT_URI).setParamsObject(commandParams);

            final LogoutResponse resp = client.send(command).dataAsResponse(LogoutResponse.class);
            System.out.println("Logout: " + resp.getUri());
            return resp.getUri();
        } finally {
            this.logger.info("CommandClient closed.");
            CommandClient.closeQuietly(client);
        }
    }
}
