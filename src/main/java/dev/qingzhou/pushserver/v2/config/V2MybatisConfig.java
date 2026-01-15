package dev.qingzhou.pushserver.v2.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "dev.qingzhou.pushserver.v2.mapper",sqlSessionFactoryRef = "sqlSessionFactory")
public class V2MybatisConfig {
}
