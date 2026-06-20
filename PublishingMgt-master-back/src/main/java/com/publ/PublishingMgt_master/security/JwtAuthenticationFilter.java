package com.publ.PublishingMgt_master.security;

import com.publ.PublishingMgt_master.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromCookie(request);
        //System.out.println("JWT from cookie on F5: " + jwt);

        if (StringUtils.hasText(jwt)) {
            try {
                if (jwtProvider.validateToken(jwt)) {
                    String username = jwtProvider.getUsernameFromJwt(jwt);
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                   // System.out.println("✅ Authentication set in SecurityContextHolder");
                } else {
                   // System.out.println("❌ Invalid JWT");
                }
            } catch (Exception e) {
              //  System.out.println("❌ JWT validation error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("JWT".equals(cookie.getName())) {
                // Decode in case the cookie is URL-encoded
                return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}