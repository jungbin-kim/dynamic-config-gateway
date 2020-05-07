package kim.jungbin.gwbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableAutoConfiguration
@RefreshScope
public class GwBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwBackendApplication.class, args);
	}

}
