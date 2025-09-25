package com.xichen.wiki.service;

import com.xichen.wiki.config.VerificationCodeProperties;
import com.xichen.wiki.constant.VerificationCodeConstants;
import com.xichen.wiki.exception.VerificationCodeException;
import com.xichen.wiki.mapper.VerificationCodeMapper;
import com.xichen.wiki.service.IEmailService;
import com.xichen.wiki.service.IVerificationCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 验证码服务测试类
 * 
 * @author xichen
 * @since 2024-09-25
 */
@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {
    
    @Mock
    private VerificationCodeMapper verificationCodeMapper;
    
    @Mock
    private IEmailService emailService;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ValueOperations<String, Object> valueOperations;
    
    @Mock
    private VerificationCodeProperties properties;
    
    @InjectMocks
    private IVerificationCodeService verificationCodeService;
    
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TYPE = VerificationCodeConstants.TYPE_REGISTER;
    private static final String TEST_CODE = "123456";
    
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(properties.getLength()).thenReturn(6);
        when(properties.getExpireMinutes()).thenReturn(5);
        when(properties.getRateLimitMinutes()).thenReturn(1);
    }
    
    @Test
    void testGenerateVerificationCode() {
        // 测试验证码生成
        String code = verificationCodeService.generateVerificationCode();
        
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }
    
    @Test
    void testSendVerificationCode_Success() {
        // 模拟Redis操作
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        
        // 执行测试
        boolean result = verificationCodeService.sendVerificationCode(TEST_EMAIL, TEST_TYPE);
        
        // 验证结果
        assertTrue(result);
        verify(redisTemplate).opsForValue();
        verify(emailService).sendVerificationCode(eq(TEST_EMAIL), anyString(), eq(TEST_TYPE));
    }
    
    @Test
    void testSendVerificationCode_RateLimited() {
        // 模拟频率限制
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        
        // 执行测试并验证异常
        assertThrows(VerificationCodeException.class, () -> {
            verificationCodeService.sendVerificationCode(TEST_EMAIL, TEST_TYPE);
        });
    }
    
    @Test
    void testVerifyCode_Success() {
        // 模拟Redis验证成功
        when(valueOperations.get(anyString())).thenReturn(TEST_CODE);
        
        // 执行测试
        boolean result = verificationCodeService.verifyCode(TEST_EMAIL, TEST_CODE, TEST_TYPE);
        
        // 验证结果
        assertTrue(result);
        verify(redisTemplate).delete(anyString());
    }
    
    @Test
    void testVerifyCode_InvalidCode() {
        // 模拟Redis验证失败
        when(valueOperations.get(anyString())).thenReturn("654321");
        
        // 执行测试
        boolean result = verificationCodeService.verifyCode(TEST_EMAIL, TEST_CODE, TEST_TYPE);
        
        // 验证结果
        assertFalse(result);
    }
    
    @Test
    void testVerifyCode_CodeNotFound() {
        // 模拟Redis中没有验证码
        when(valueOperations.get(anyString())).thenReturn(null);
        
        // 执行测试
        boolean result = verificationCodeService.verifyCode(TEST_EMAIL, TEST_CODE, TEST_TYPE);
        
        // 验证结果
        assertFalse(result);
    }
    
    @Test
    void testCleanExpiredCodes() {
        // 执行测试
        verificationCodeService.cleanExpiredCodes();
        
        // 验证调用
        verify(verificationCodeMapper).cleanExpiredCodes();
    }
}
