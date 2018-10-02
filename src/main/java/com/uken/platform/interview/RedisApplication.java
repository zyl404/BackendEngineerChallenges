package com.uken.platform.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import com.uken.platform.interview.problem2.RedisController;
import com.uken.platform.interview.problem1.BulkController;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackageClasses=RedisController.class)
@ComponentScan(basePackageClasses=BulkController.class)
public class RedisApplication {
	@Autowired
	private Environment env;

	@Bean
	public JedisConnectionFactory jedisConnectionFactory(){
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName(env.getProperty("REDIS_HOST"));
		connectionFactory.setPort(6379);
		return connectionFactory;
	}
	
	@Bean
	public StringRedisTemplate redisTempalte(){
		return new StringRedisTemplate(jedisConnectionFactory());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
		
	}
}
