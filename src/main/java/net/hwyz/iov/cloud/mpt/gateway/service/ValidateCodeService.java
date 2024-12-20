package net.hwyz.iov.cloud.mpt.gateway.service;


import net.hwyz.iov.cloud.framework.common.web.domain.AjaxResult;
import net.hwyz.iov.cloud.mpt.gateway.exception.CaptchaException;

import java.io.IOException;

/**
 * 验证码处理
 *
 * @author hwyz_leo
 */
public interface ValidateCodeService {
    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
