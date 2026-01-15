package dev.qingzhou.pushserver.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "dev.qingzhou.pushserver.mapper.portal", sqlSessionFactoryRef = "sqlSessionFactory")
public class PortalMybatisConfig {
}
