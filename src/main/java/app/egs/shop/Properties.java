package app.egs.shop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "app.egs.shop")
@Data
@Configuration
public class Properties {

}