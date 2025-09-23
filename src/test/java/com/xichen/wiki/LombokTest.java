package com.xichen.wiki;

import com.xichen.wiki.entity.User;
import com.xichen.wiki.common.Result;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LombokTest {

    @Test
    public void testLombokAnnotations() {
        // 测试User实体类的Lombok注解
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@example.com");
        
        assertEquals(1L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        
        // 测试Result类的Lombok注解
        Result<String> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData("test data");
        
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
        
        System.out.println("Lombok注解工作正常！");
    }
}
