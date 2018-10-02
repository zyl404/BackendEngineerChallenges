package com.uken.platform.interview.problem1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BulkController {

    @Autowired
    private RedisService redisService;

    @RequestMapping(value="/bulk-slow", method=RequestMethod.GET)
    public String setInRedis() {
        return "Collapse Time: " + redisService.getSlowMethodTime();
    }

    @RequestMapping(value="/bulk-fast", method=RequestMethod.GET)
    public String pipeSetInRedis(){
        return "Collapse Time: " + redisService.getfastMethodTime();
    }
}
