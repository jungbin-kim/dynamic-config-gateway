package kim.jungbin.gwbackend.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class LogFilter {

    final Logger logger =
        LoggerFactory.getLogger(LogFilter.class);

    @Bean(name = "loggingResponseFilter")
    public GatewayFilter loggingResponseFilter() {
        return new ModifyResponseBodyGatewayFilterFactory(ServerCodecConfigurer.create())
            .apply(config -> config.setRewriteFunction(
                byte[].class, byte[].class,
                (exchange, body) -> {
                    String requestBody = Optional.ofNullable(exchange.getAttribute("requestBody")).map(o -> new String((byte[])o)).orElse(null);
                    String responseBody = Optional.ofNullable(body).map(bytes -> new String(bytes)).orElse(null);
                    logger.info(responseBody);
                    return Mono.just(body);
                }));
    }
}
