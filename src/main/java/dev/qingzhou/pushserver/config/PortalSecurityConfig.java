package dev.qingzhou.pushserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qingzhou.pushserver.common.PortalSessionKeys;
import dev.qingzhou.pushserver.security.CaptchaService;
import dev.qingzhou.pushserver.security.PortalJsonLoginAuthenticationFilter;
import dev.qingzhou.pushserver.security.PortalUserDetails;
import dev.qingzhou.pushserver.security.PortalUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.PrintWriter;
import java.util.Map;

@Configuration
public class PortalSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PortalUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }


    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
    }

    @Bean
    public PortalJsonLoginAuthenticationFilter portalJsonLoginAuthenticationFilter(
            ObjectMapper objectMapper,
            CaptchaService captchaService, // 确保 CaptchaService 加了 @Service 注解
            AuthenticationManager authenticationManager,
            SessionAuthenticationStrategy sessionAuthenticationStrategy
    ) {
        // 创建 Filter
        PortalJsonLoginAuthenticationFilter filter =
                new PortalJsonLoginAuthenticationFilter(objectMapper, captchaService);

        // 注入必要的组件
        filter.setAuthenticationManager(authenticationManager);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);

        SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
        filter.setSecurityContextRepository(contextRepository);

        // 设置 JSON 成功处理器 (解决 302 问题)
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            HttpSession session = request.getSession(true);
            Object principal = authentication.getPrincipal();
            if (principal instanceof PortalUserDetails userDetails) {
                session.setAttribute(PortalSessionKeys.USER_ID, userDetails.getUserId());
            }
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write(objectMapper.writeValueAsString(Map.of(
                    "code", 200,
                    "msg", "登录成功",
                    "username", authentication.getName()
            )));
        });

        filter.setAuthenticationFailureHandler((request, response, exception) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write(objectMapper.writeValueAsString(Map.of(
                    "code", 401,
                    "msg", "登录失败: " + exception.getMessage()
            )));
        });

        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            PortalJsonLoginAuthenticationFilter portalJsonLoginAuthenticationFilter
    ) throws Exception { // 注意这里要抛出异常
        http.authorizeHttpRequests(authorize -> authorize
                        // 静态资源和登录接口放行
                        .requestMatchers("/","/api/login", "/login", "/index.html", "/assets/**", "/logo.png","/favicon.ico", "/api/captcha").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v2/openapi/**").permitAll()
                        .requestMatchers("/api/v2/auth/register", "/api/v2/auth/csrf").permitAll()
                        .requestMatchers("/api/v1/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .spa()
                        .ignoringRequestMatchers("/api/v1/**", "/api/login", "/api/captcha", "/api/v2/openapi/**")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                )

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            String requestURI = request.getRequestURI();
                            // 判断：如果是 API 请求，或者是 AJAX 请求
                            if (requestURI.startsWith("/api/") || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                // 方案 A：对接口返回 401 JSON
                                response.setContentType("application/json;charset=UTF-8");
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                                response.getWriter().write("{\"code\": 401, \"msg\": \"未登录或会话已过期\"}");
                            } else {
                                // 方案 B：对普通页面访问，重定向到登录页
                                // 注意：这里不要跳首页 /，应该跳 /login，否则可能死循环
                                response.sendRedirect("/login");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\": 200, \"msg\": \"退出成功\"}");
                        })
                )
                // 你的自定义 Filter
                .addFilterAt(portalJsonLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
