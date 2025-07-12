package at.primetshofer.pekoNihongoBackend.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        //log.error("Authentication exception occurred for request: {}", request, authException);
        System.out.println("Authentication exception occurred for request: " + request);

        //ApiErrorResponse apiErrorResponse = new ApiErrorResponse(authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //response.setContentType("application/json");
        //objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);
    }

}
