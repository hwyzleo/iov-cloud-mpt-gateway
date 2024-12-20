package net.hwyz.iov.cloud.mpt.gateway.filter;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import net.hwyz.iov.cloud.framework.common.constant.CacheConstants;
import net.hwyz.iov.cloud.framework.common.constant.HttpStatus;
import net.hwyz.iov.cloud.framework.common.constant.MptSecurityConstants;
import net.hwyz.iov.cloud.framework.common.constant.TokenConstants;
import net.hwyz.iov.cloud.framework.common.util.JwtUtil;
import net.hwyz.iov.cloud.framework.common.util.ServletUtil;
import net.hwyz.iov.cloud.framework.redis.service.RedisService;
import net.hwyz.iov.cloud.mpt.gateway.config.properties.IgnoreWhiteProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 网关鉴权
 *
 * @author hwyz_leo
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    // 排除过滤的 uri 地址，nacos自行添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Autowired
    private RedisService redisService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        if (matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        String userkey = JwtUtil.getUserKey(claims);
        boolean islogin = redisService.hasKey(getTokenKey(userkey));
        if (!islogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }
        String userid = JwtUtil.getUserId(claims);
        String username = JwtUtil.getUserName(claims);
        if (StrUtil.isEmpty(userid) || StrUtil.isEmpty(username)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 设置用户信息到请求
        addHeader(mutate, MptSecurityConstants.USER_KEY, userkey);
        addHeader(mutate, MptSecurityConstants.DETAILS_USER_ID, userid);
        addHeader(mutate, MptSecurityConstants.DETAILS_USERNAME, username);
        // 内部请求来源参数清除
        removeHeader(mutate, MptSecurityConstants.FROM_SOURCE);
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLEncoder.encode(valueStr, StandardCharsets.UTF_8);
        ;
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{},错误信息:{}", exchange.getRequest().getPath(), msg);
        return ServletUtil.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 获取缓存key
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(MptSecurityConstants.AUTHORIZATION_HEADER);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    @Override
    public int getOrder() {
        return -200;
    }

    public static boolean matches(String str, List<String> strs) {
        if (StrUtil.isEmpty(str) || strs.isEmpty()) {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : strs) {
            if (matcher.match(pattern, str)) {
                return true;
            }
        }
        return false;
    }
}