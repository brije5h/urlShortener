package com.bijju.url_shortener.security.jwt;

import com.bijju.url_shortener.services.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
            try {
                //Get JWT From Header
                //Validate Token
                //If Valid Get User details
                // -- get user name -> load User -> Set the auth context
                String jwt = jwtTokenProvider.getJwtFromHeader(request);

                if(jwt!=null && jwtTokenProvider.validateToken(jwt)){
                    String username = jwtTokenProvider.getUserNameFromJwtToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if(userDetails!=null){
                        UsernamePasswordAuthenticationToken authntication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authntication.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));
                        SecurityContextHolder.getContext().setAuthentication(authntication);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            filterChain.doFilter(request, response);
    }
}
