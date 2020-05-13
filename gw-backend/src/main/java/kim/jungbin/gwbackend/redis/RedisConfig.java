package kim.jungbin.gwbackend.redis;

import kim.jungbin.gwbackend.model.RoutingProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    @Bean
    public ReactiveValueOperations<String, RoutingProperty> redisOperations(ReactiveRedisConnectionFactory factory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<RoutingProperty> valueSerializer =
            new Jackson2JsonRedisSerializer<>(RoutingProperty.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, RoutingProperty> builder =
            RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, RoutingProperty> context =
            builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(factory, context).opsForValue();
    }
}
