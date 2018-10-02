package com.uken.platform.interview.problem1;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;


@Component
public class RedisService implements InitializingBean{
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private static final int MAX_RECORDS = 100000;
	
	private void insertRecords(){
		for (int i = 0; i < MAX_RECORDS; i++) {
			redisTemplate.opsForValue().set("slow-" + i, Integer.toString(i));
		}
	}

	private void pipeInsertRecord() {
		redisTemplate.executePipelined(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
				for (int i = 0; i < MAX_RECORDS; i++ ) {
					stringRedisConn.set("fast-" + i, Integer.toString(i));
				}
				return null;
			}
		});
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Instant start = Instant.now();
		insertRecords();
		Instant end = Instant.now();
		System.out.println("Standard method took: " + Duration.between(start, end).getSeconds() + " seconds");
	}

	public String getSlowMethodTime() {
		Instant start = Instant.now();
		insertRecords();
		Instant end = Instant.now();
		return String.valueOf(Duration.between(start, end).getSeconds());
	}

	public String getfastMethodTime() {
		Instant start = Instant.now();
		pipeInsertRecord();
		Instant end = Instant.now();
		return String.valueOf(Duration.between(start, end).getSeconds());
	}
	
	public void asyncSetInRedis(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}
}
