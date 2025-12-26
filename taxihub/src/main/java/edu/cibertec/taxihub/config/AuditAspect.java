package edu.cibertec.taxihub.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Around("execution(* edu.cibertec.taxihub.controller.PagoController.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping))")
    public Object auditFinancialOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUser();
        
        try {
            Object result = joinPoint.proceed();
            log.warn("FINANCIAL_AUDIT - Usuario: {} | Operación: {}", username, methodName);
            return result;
        } catch (Exception e) {
            log.error("FINANCIAL_ERROR - Usuario: {} | Operación fallida: {} | Error: {}", 
                     username, methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* edu.cibertec.taxihub.controller.UsuarioController.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping))")
    public Object auditSecurityOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUser();
        
        try {
            Object result = joinPoint.proceed();
            log.warn("SECURITY_AUDIT - Usuario: {} | Operación: {}", username, methodName);
            return result;
        } catch (Exception e) {
            log.error("SECURITY_ERROR - Usuario: {} | Operación fallida: {} | Error: {}", 
                     username, methodName, e.getMessage());
            throw e;
        }
    }

    @Before("execution(* edu.cibertec.taxihub.services.*UseCaseImpl.eliminar*(..))")
    public void auditDeletions(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName() + 
                          "." + joinPoint.getSignature().getName();
        String username = getCurrentUser();
        log.warn("ELIMINACION - Usuario: {} | Operación: {}", username, methodName);
    }

    @AfterThrowing(pointcut = "execution(* edu.cibertec.taxihub.services.*.*(..))", throwing = "ex")
    public void auditServiceErrors(JoinPoint joinPoint, Throwable ex) {
        String username = getCurrentUser();
        String serviceName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        log.error("SERVICE_ERROR - Usuario: {} | Servicio: {} | Error: {}", 
                 username, serviceName, ex.getMessage());
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return "ANONIMO";
    }
}