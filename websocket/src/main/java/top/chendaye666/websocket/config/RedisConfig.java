package top.chendaye666.websocket.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Springboot 整合 Redis 配类
 *
 * springboot最大化地简化了整合redis需要的配置，在用户只需要在配置文件（application.*
 * 中配置少量参数就可以使用官方默认提供的RedisTemplate和StringRedisTemplate来操作redis。
 * 由于官方提供的*RedisTemplate提供的功能有限，难以针对java的复杂数据类型进行序列化，且
 * 采用直连的方式以及没有对连接数进行限制等诸多因素在现代引用中制约较大，
 * 所以项目中一般需要提供一个RedisConfig类来针对redisTemplate做进一步配置。
 *
 * 编写redis配置类，内容如下，在该类中完成Jedis池、Redis连接和RedisTemplate序列化三个配置完成springboot整合redis的进一步配置。
 * 其中RedisTemplate对key和value的序列化类，各人结合自己项目情况进行选择即可
 */
@EnableCaching
@Configuration
public class RedisConfig{
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    /**
     * 构造器：使用fastjson的 autoType 功能，为防止漏洞要添加白名单
     * https://www.cnblogs.com/hollischuang/p/13253321.html
     */
    RedisConfig(){
        // 添加序列化 反序列化白名单
        ParserConfig.getGlobalInstance().addAccept("top.chendaye666.websocket.model.po.User");
    }

    @Bean
    public JedisPool redisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        if (StringUtils.isNotBlank(password))
            return new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return new JedisPool(jedisPoolConfig, host, port, timeout);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));

        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();
        builder.connectTimeout(Duration.ofMillis(timeout));
        builder.usePooling();
        return new JedisConnectionFactory(redisStandaloneConfiguration, builder.build());
    }

    /**
     * 操作对象的 template
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplate")
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // value 使用 fastjson 序列化
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        // key 使用 StringRedisSerializer 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 操作字符串的 template
     *  @ConditionalOnMissingBean作用：判断当前需要注入Spring容器中的bean的实现类是否已经含有，有的话不注入，没有就注入
     *  @ConditionalOnBean作用：判断当前需要注册的bean的实现类否被spring管理，如果被管理则注入，反之不注入
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 操作 String -> Object
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "stringObjectRedisTemplate")
    public RedisTemplate<String, Object> stringObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // value 使用fastJson的序列化方式
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        // key 使用 StringRedisSerializer 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 操作 List对象
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<Object, Object> listOperations(RedisTemplate<Object, Object> redisTemplate){
        return redisTemplate.opsForList();
    }

    /**
     * 操作字符串
     * @return
     */
    @Bean
    public ValueOperations<String, String> valueOperations(StringRedisTemplate stringRedisTemplate){
        return stringRedisTemplate.opsForValue();
    }

    /**
     * 操作 String -> Object
     * @param stringObjectRedisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueObjectOperations(RedisTemplate<String, Object> stringObjectRedisTemplate){
        return stringObjectRedisTemplate.opsForValue();
    }

    /**
     * 缓存管理器
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory);
        return builder.build();
    }

    /**
     * 生成 key
     * @return
     */
    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

}
