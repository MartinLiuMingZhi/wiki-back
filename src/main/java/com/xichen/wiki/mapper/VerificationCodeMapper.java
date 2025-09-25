package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 验证码Mapper接口
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    
    /**
     * 根据邮箱和类型查找有效的验证码
     */
    @Select("SELECT * FROM verification_codes WHERE email = #{email} AND type = #{type} AND used = false AND expire_time > NOW() ORDER BY create_time DESC LIMIT 1")
    VerificationCode findValidCodeByEmailAndType(@Param("email") String email, @Param("type") String type);
    
    /**
     * 清理过期的验证码
     */
    @Select("DELETE FROM verification_codes WHERE expire_time < NOW()")
    void cleanExpiredCodes();
}
