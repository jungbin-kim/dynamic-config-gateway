package kim.jungbin.gwbackend.filter;

import kim.jungbin.gwbackend.model.RoutingProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class RouteConfigFilter implements GatewayFilter, Ordered {

    public static final String URL_QUERY_SEPARATOR = "?";

    private final Logger logger =
        LoggerFactory.getLogger(RouteConfigFilter.class);
    private final ReactiveValueOperations<String, RoutingProperty> reactiveValueOps;

    public RouteConfigFilter(ReactiveValueOperations<String, RoutingProperty> reactiveValueOps) {
        this.reactiveValueOps = reactiveValueOps;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("First Pre Global Filter");
        ServerHttpRequest request = exchange.getRequest();
        String serviceId = request.getURI().getHost().split("\\.")[0];
        return this.reactiveValueOps.get(serviceId).flatMap(res -> {
            URI proxyUri = getProxyUri(res.getTargetUrl(), request.getURI());
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, proxyUri);
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("Last Post Global Filter");
            }));
        });
    }

    private URI getProxyUri(String realURI, URI callerURI) {
        StringBuilder uriBuilder = new StringBuilder(realURI);

        Optional.ofNullable(callerURI.getRawPath())
            .ifPresent(path -> uriBuilder.append(path));

        Optional.ofNullable(callerURI.getRawQuery())
            .ifPresent(query -> uriBuilder.append(URL_QUERY_SEPARATOR).append(query));

        try {
            return new URI(uriBuilder.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public int getOrder() {
        // MUST More Than 10000, Because of Applying after RouteToRequestUrlFilter.
        return 10001;
    }
}
