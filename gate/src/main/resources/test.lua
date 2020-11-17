local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = ARGV[1]
local capacity = ARGV[2]
local now = ARGV[3]
local requested = ARGV[4]