package ua.pustovalov.redis.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Controller()
@RequestMapping("/user")
public class UserController {

    @ResponseBody
    @PostMapping(value = "/{message}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addMessageToRedis(@PathVariable String message) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("redis");
        SetParams setParams = new SetParams();
        setParams.ex(10);
        jedis.set("foo", message, setParams);
        return jedis.get("foo");
    }

    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> addMessageBodyToRedis(@RequestBody String message) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("redis");
        jedis.lpush("foo", message);
        jedis.expire("foo", 10);
        return jedis.lrange("foo", 0, -1);
    }

    @ResponseBody
    @GetMapping(value = "/list/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMessageListFromRedis(@PathVariable("message") String message) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("redis");
        if (jedis.exists(message)) {
            return jedis.lrange(message, 0, -1);
        }
        return Collections.EMPTY_LIST;
    }

    @ResponseBody
    @GetMapping(value = "/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMessageFromRedis(@PathVariable("message") String message) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("redis");
        if (jedis.exists(message)) {
            return jedis.get(message);
        }
        return "";
    }

}
