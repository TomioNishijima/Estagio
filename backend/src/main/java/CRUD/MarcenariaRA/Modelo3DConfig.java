
package CRUD.MarcenariaRA.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class Modelo3DConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path pastaModelos = Path.of(
                System.getProperty("user.home"),
                "MarcenariaNishijima",
                "modelos"
        ).toAbsolutePath();

        String local = pastaModelos.toUri().toString();

        if (!local.endsWith("/")) {
            local += "/";
        }

        registry.addResourceHandler("/modelos/uploads/**")
                .addResourceLocations(local);
    }
}
