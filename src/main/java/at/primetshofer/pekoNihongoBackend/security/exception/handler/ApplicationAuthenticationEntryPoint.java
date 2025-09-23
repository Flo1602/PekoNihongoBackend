package at.primetshofer.pekoNihongoBackend.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAuthenticationEntryPoint.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        final String requestUri = request.getRequestURI();
        final String message = authException.getMessage() != null
                ? authException.getMessage()
                : "Unauthorized";

        logger.warn("Unauthorized access to [{}]: {}", requestUri, message, authException);

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

}
