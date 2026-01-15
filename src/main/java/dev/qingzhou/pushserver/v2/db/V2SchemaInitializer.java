package dev.qingzhou.pushserver.v2.db;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class  V2SchemaInitializer {

    private final DataSource dataSource;

    public V2SchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initialize() {
        List<String> statements = new ArrayList<>();
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_user (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """);
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_corp_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL UNIQUE,
                    corp_id TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """);
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_wecom_app (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    agent_id TEXT NOT NULL,
                    secret TEXT NOT NULL,
                    name TEXT,
                    avatar_url TEXT,
                    description TEXT,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    UNIQUE(user_id, agent_id)
                )
                """);
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_message_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    app_id INTEGER NOT NULL,
                    agent_id TEXT NOT NULL,
                    msg_type TEXT NOT NULL,
                    to_user TEXT,
                    to_party TEXT,
                    to_all INTEGER NOT NULL DEFAULT 0,
                    title TEXT,
                    description TEXT,
                    url TEXT,
                    content TEXT,
                    request_json TEXT,
                    response_json TEXT,
                    success INTEGER NOT NULL,
                    error_message TEXT,
                    created_at INTEGER NOT NULL
                )
                """);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (String sql : statements) {
                    statement.execute(sql);
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize v2 schema", ex);
        }
    }
}
