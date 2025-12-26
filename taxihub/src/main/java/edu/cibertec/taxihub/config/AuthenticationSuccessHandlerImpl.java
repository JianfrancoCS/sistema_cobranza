package edu.cibertec.taxihub.config;

import edu.cibertec.taxihub.constantes.CargoEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = determineTargetUrl(authorities);
        
        log.info("Usuario {} logueado exitosamente. Redirigiendo a: {}", 
                authentication.getName(), redirectUrl);
        
        response.sendRedirect(redirectUrl);
    }

    private String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
        
        for (GrantedAuthority authority : authorities) {
            String authName = authority.getAuthority();
            
            if (authName.startsWith(ROLE_PREFIX)) {
                String cleanRole = authName.substring(ROLE_PREFIX.length()).toUpperCase();
                
                return switch (cleanRole) {
                    case "ADMINISTRADOR" -> "/dashboard";
                    case "SUPERVISOR" -> "/deudas";
                    case "CONDUCTOR" -> "/deudas/conductor";
                    default -> "/dashboard";
                };
            }
        }
        
        return "/dashboard";
    }
}