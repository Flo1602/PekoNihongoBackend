package at.primetshofer.pekoNihongoBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public static final String AUDIO_PATH = "D:/Flo 1602/Projekte/PekoNihongo/pekoNihongoBackend/audio/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/audio/**")
                .addResourceLocations("file:" + AUDIO_PATH)
                .setCachePeriod(0)
                .resourceChain(true);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // apply CORS to the audio endpoint
        registry
                .addMapping("/audio/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*");
        // your other CORS rules...
        registry
                .addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

}
