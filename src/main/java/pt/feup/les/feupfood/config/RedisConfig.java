package pt.feup.les.feupfood.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Bean
    JedisPoolConfig jedisPoolConfig() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(10);

        poolConfig.setMaxIdle(5);

        poolConfig.setMinIdle(1);

        poolConfig.setTestOnBorrow(true);

        poolConfig.setTestOnReturn(true);

        poolConfig.setTestWhileIdle(true);

        return poolConfig;

    }



    @Bean

    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) throws URISyntaxException {
        String envRedisUrl = System.getenv("REDIS_URL");

        URI redisUri = new URI(envRedisUrl);



        RedisStandaloneConfiguration hostConfig = new RedisStandaloneConfiguration();

        hostConfig.setPort(redisUri.getPort());

        hostConfig.setHostName(redisUri.getHost());

        hostConfig.setPassword(redisUri.getUserInfo().split(":", 2)[1]);



        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();

        JedisClientConfiguration clientConfig = builder

                .usePooling()

                .poolConfig(jedisPoolConfig)

                .build();



        JedisConnectionFactory factory = new JedisConnectionFactory(hostConfig, clientConfig);

        return factory;

    }



    @Bean

    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());

        redisTemplate.setHashValueSerializer(redisTemplate.getValueSerializer());

        return redisTemplate;

    }
    
}
