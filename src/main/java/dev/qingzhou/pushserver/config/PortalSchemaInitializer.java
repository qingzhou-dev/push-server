package dev.qingzhou.pushserver.config;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class PortalSchemaInitializer {

    private final DataSource dataSource;

    public PortalSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initialize() {
        List<String> statements = new ArrayList<>();
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_system_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    config_key TEXT NOT NULL UNIQUE,
                    config_value TEXT,
                    updated_at INTEGER NOT NULL
                )
                """);
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
                    token TEXT,
                    encoding_aes_key TEXT,
                    name TEXT,
                    avatar_url TEXT,
                    description TEXT,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    UNIQUE(user_id, agent_id)
                )
                """);
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_app_api_key (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    app_id INTEGER NOT NULL UNIQUE,
                    api_key_hash TEXT NOT NULL,
                    api_key_plain TEXT NOT NULL,
                    rate_limit_per_minute INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """);
        statements.add("""
                CREATE TABLE IF NOT EXISTS v2_proxy_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL UNIQUE,
                    host TEXT NOT NULL,
                    port INTEGER NOT NULL,
                    username TEXT,
                    password TEXT,
                    type TEXT NOT NULL DEFAULT 'HTTP',
                    exit_ip TEXT,
                    active INTEGER NOT NULL DEFAULT 1,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """);
        statements.add("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_v2_app_api_key_hash
                ON v2_app_api_key(api_key_hash)
                """);
        List<String> alterStatements = new ArrayList<>();
        alterStatements.add("ALTER TABLE v2_app_api_key ADD COLUMN api_key_plain TEXT NOT NULL DEFAULT ''");
        alterStatements.add("ALTER TABLE v2_app_api_key ADD COLUMN rate_limit_per_minute INTEGER NOT NULL DEFAULT 0");
        alterStatements.add("ALTER TABLE v2_wecom_app ADD COLUMN token TEXT");
        alterStatements.add("ALTER TABLE v2_wecom_app ADD COLUMN encoding_aes_key TEXT");
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
                for (String sql : alterStatements) {
                    try {
                        statement.execute(sql);
                    } catch (Exception ignored) {
                        // Column may already exist; ignore migration errors to stay backward compatible.
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize portal schema", ex);
        }
    }
}
