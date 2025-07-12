package at.primetshofer.pekoNihongoBackend.config;

import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(
        name = AuthConstants.SECURITY_SCHEME_NAME,
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = AuthConstants.AUTHORIZATION_HEADER)
public class OpenApiConfig {

}
