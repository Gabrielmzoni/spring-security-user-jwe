package br.com.gmsoft.userjwe.service;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {

    public String getAuthorizationToken(String username);

    public String getAuthUser(HttpServletRequest request);
}
