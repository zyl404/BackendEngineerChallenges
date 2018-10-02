package com.uken.platform.interview.problem2;

import com.uken.platform.interview.problem1.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Timer;
import java.util.TimerTask;

@RestController
public class RedisController {
	
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedisService redisService;

	private final Timer timer = new Timer();
	
	@RequestMapping(value = "/pair/{key}/{value}", method = RequestMethod.GET)
    public void setInRedis(@PathVariable String key, @PathVariable String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@RequestMapping(value = "/fast/pair/{key}/{value}", method = RequestMethod.GET)
	public void fastSetInRedis(@PathVariable String key, @PathVariable String value) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				redisService.asyncSetInRedis(key, value);
			}
		}, 0);
	}
	
}