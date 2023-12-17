package site.courseregistrationsystem.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import site.courseregistrationsystem.util.encryption.Aes256Properties;

@Configuration
@EnableConfigurationProperties(value = {
	Aes256Properties.class
})
public class PropertiesConfig {

}
