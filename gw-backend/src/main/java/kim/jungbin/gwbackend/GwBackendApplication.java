package kim.jungbin.gwbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableAutoConfiguration
@RefreshScope
public class GwBackendApplication {

	private static final String URI_NO_OP = "no://op";

	@Autowired private GatewayFilter routeConfigFilter;
	@Autowired private GatewayFilter loggingResponseFilter;

	public static void main(String[] args) {
		SpringApplication.run(GwBackendApplication.class, args);
	}

	@Bean
	public RouteLocator proxyRoute(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(p -> p.alwaysTrue()
				.filters(f -> {
					f.filter(routeConfigFilter);
					f.filter(loggingResponseFilter);
					return f;
				})
				.uri(URI_NO_OP)
			)
			.build();
	}
}
