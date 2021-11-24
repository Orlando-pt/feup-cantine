package pt.feup.les.feupfood.repository;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisSessionRepository {


	private final long JWT_TOKEN_VALIDITY = 60L * 60L * 1000L;  // 1 hour timeout

    // @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> valueOperations;

    public RedisSessionRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public ValueOperations<String, String> getValueOperations() {
        return this.valueOperations;
    }

    public Boolean addUser(String email) {
        return this.valueOperations.setIfAbsent(email, "active", JWT_TOKEN_VALIDITY, TimeUnit.MILLISECONDS);
    }

    public Boolean removeUser(String email) {
        return this.redisTemplate.delete(email);
    }

    public String userIsActive(String email) {
        return this.valueOperations.get(email);
    }
    
}
