package at.primetshofer.pekoNihongoBackend.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        //log.error("Access denied for request: {}", request, accessDeniedException);
        System.out.println("Access denied for request: " + request);

        //ApiErrorResponse apiErrorResponse = new ApiErrorResponse(accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        //response.setContentType("application/json");
        //objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);
    }
}
