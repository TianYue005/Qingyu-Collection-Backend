package com.wang.tradingplatform.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // ===================== String 字符串操作 =====================
    /**
     * 设置值，永不过期
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值+过期时间
     */
    public void set(String key, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, time, unit);
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 自增
     */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // ===================== Hash 哈希 =====================
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void hDel(String key, String... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ===================== List 列表 =====================
    // 左插
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }
    // 右插
    public void rPush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }
    // 范围查询
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // ===================== Set 无序集合 =====================
    public void sAdd(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // ===================== ZSet 有序集合 =====================
    // 添加
    public void zAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }
    // 范围查询
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    // ===================== 通用key操作 =====================
    // 设置过期时间
    public Boolean expire(String key, long time, TimeUnit unit) {
        return redisTemplate.expire(key, time, unit);
    }
    // 删除key
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }
    // 批量删除
    public Long delBatch(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }
    // 判断key是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
