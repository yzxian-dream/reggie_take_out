package com.yzx.reggie.config;


import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 手动创建一个redis配置类，其实自己不创建框架也会帮我们创建，但是框架自带的保存下来的key不是string,是其他格式的，不方便识别
 * 框架默认使用的是jdkSerializationRedisSerializer这个序列化器
 * 这里自己修改成StringRedisSerializer()这个序列化器
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer((new StringRedisSerializer()));
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
