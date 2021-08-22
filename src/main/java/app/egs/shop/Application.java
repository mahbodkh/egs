package app.egs.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Ebrahim Kh.
 */

@EnableCaching
@SpringBootApplication
@EntityScan("app.egs.shop.domain")
@EnableJpaRepositories("app.egs.shop.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
