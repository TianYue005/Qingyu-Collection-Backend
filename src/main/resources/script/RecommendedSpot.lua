-- 原子操作：删除该商品旧条目 + 检查空位 + 写入最新商品详情JSON
-- KEYS[1]：推荐位 ZSet 的 key（goods:recommend）
-- ARGV[1]：商品ID（字符串）
-- ARGV[2]：商品详情JSON字符串
-- ARGV[3]：排序分数（数字字符串）
-- 返回值：
--   -1  推荐位已满（前 5 个位置已占满且当前商品不在其中）
--   n   写入成功，n 为写入后剩余的空位数（0~4）
local key = KEYS[1]
local goodsId = ARGV[1]
local goodsJson = ARGV[2]
local score = tonumber(ARGV[3]) or 99

-- 删除该商品已有的旧条目（旧快照），保证推荐位中同一商品只保留最新一条
local members = redis.call('ZRANGE', key, 0, -1)
for _, member in ipairs(members) do
    if string.find(member, goodsId, 1, true) then
        redis.call('ZREM', key, member)
    end
end

-- 检查前 5 个位置是否已满
local top = redis.call('ZRANGE', key, 0, 4)
if #top >= 5 then
    return -1
end

-- 写入最新商品详情
redis.call('ZADD', key, score, goodsJson)

return 5 - redis.call('ZCARD', key)
