package com.naz.PlexDownloader.util;

import com.naz.PlexDownloader.exceptions.rest.JwtInvalidException;
import com.naz.PlexDownloader.exceptions.rest.RecordNotFoundException;
import com.naz.PlexDownloader.models.plex.PlexUser;
import com.naz.PlexDownloader.repositories.PlexRepository;
import com.naz.PlexDownloader.services.plex.auth.PlexAuthService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private PlexAuthService plexAuthService;

    @Autowired
    private PlexRepository plexRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String[] allowedEndPoints = {"/basiclogin", "/oAuth", "/status"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)

            throws ServletException, IOException {

        //Needed since this filter comes before CORS filter. This handles pre flight requests.
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;

        String jwtToken = null;

        StringBuffer requestUrl = request.getRequestURL();

        if (checkRequestRequiresToken(requestUrl)) {
            chain.doFilter(request, response);
            return;
        }

        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

            jwtToken = requestTokenHeader.substring(7);

            try {

                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            } catch (IllegalArgumentException e) {

                System.out.println("Unable to get JWT Token");

            } catch (ExpiredJwtException e) {

                System.out.println("JWT Token has expired");

            }

        } else {
            logger.error("JWT Token does not begin with Bearer String");
            throw new JwtInvalidException("jwt.token.missing.bearer.token.header");
        }

        // Once we get the token validate it.

        if (username != null) {

            PlexUser plexUser = this.plexRepository.findPlexUserByUsername(username);

            if (plexUser == null) {
                throw new RecordNotFoundException("user.not.found.in.database", new Object[]{username});
            }

            // if token is valid configure Spring Security to manually set
            // authentication

            if (jwtTokenUtil.validateToken(jwtToken, plexUser)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        plexUser, null, null);

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // After setting the Authentication in the context, we specify

                // that the current user is authenticated. So it passes the

                // Spring Security Configurations successfully.

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);

                requestWrapper.addHeader("PLEX-TOKEN", plexUser.getLibraryAuthToken());

                chain.doFilter(requestWrapper, response);

                return;
            } else {

                if (jwtTokenUtil.isTokenExpired(jwtToken)) {
                    throw new JwtInvalidException("jwt.token.expired", new Object[]{
                            jwtTokenUtil.getExpirationDateFromToken(jwtToken)});
                } else {
                    throw new JwtInvalidException("jwt.token.invalid");
                }

            }

        }

        chain.doFilter(request, response);

    }

    private boolean checkRequestRequiresToken(StringBuffer requestUrl) {
        for (String endPoint :
                allowedEndPoints) {
            if (requestUrl.indexOf(endPoint) != -1)
                return true;
        }

        return false;
    }

    public PlexAuthService getPlexAuthService() {
        return plexAuthService;
    }

    public void setPlexAuthService(PlexAuthService plexAuthService) {
        this.plexAuthService = plexAuthService;
    }

    public PlexRepository getPlexRepository() {
        return plexRepository;
    }

    public void setPlexRepository(PlexRepository plexRepository) {
        this.plexRepository = plexRepository;
    }

    public JwtTokenUtil getJwtTokenUtil() {
        return jwtTokenUtil;
    }

    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
}
