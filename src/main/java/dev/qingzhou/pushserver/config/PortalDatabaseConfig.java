package dev.qingzhou.pushserver.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

@Configuration
@EnableTransactionManagement
public class PortalDatabaseConfig {

    @Bean
    public DataSource dataSource(PortalDataSourceProperties properties) {
        String jdbcUrl = properties.getUrl();
        if (!StringUtils.hasText(jdbcUrl)) {
            jdbcUrl = buildSqliteUrl(properties.getFilePath());
        }
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setMaximumPoolSize(properties.getMaxPoolSize());
        return dataSource;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }

    private String buildSqliteUrl(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath();
        Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to create sqlite directory: " + parent, ex);
            }
        }
        return "jdbc:sqlite:" + path;
    }
}
