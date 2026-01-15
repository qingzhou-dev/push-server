package dev.qingzhou.pushserver.v2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.v2.mapper.V2UserMapper;
import dev.qingzhou.pushserver.v2.model.V2User;
import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2Status;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class V2UserService extends ServiceImpl<V2UserMapper, V2User> {

    private final PasswordEncoder passwordEncoder;

    public V2UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public V2User register(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Account and password are required");
        }
        if (existsAccount(account)) {
            throw new V2Exception(V2Status.CONFLICT, "Account already exists");
        }
        V2User user = new V2User();
        user.setAccount(account.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        long now = System.currentTimeMillis();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        save(user);
        return user;
    }

    public V2User authenticate(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Account and password are required");
        }
        V2User user = lambdaQuery().eq(V2User::getAccount, account.trim()).one();
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new V2Exception(V2Status.UNAUTHORIZED, "Invalid credentials");
        }
        return user;
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Password fields are required");
        }
        V2User user = getById(userId);
        if (user == null) {
            throw new V2Exception(V2Status.NOT_FOUND, "User not found");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new V2Exception(V2Status.UNAUTHORIZED, "Old password mismatch");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(System.currentTimeMillis());
        updateById(user);
    }

    private boolean existsAccount(String account) {
        return Objects.nonNull(lambdaQuery()
                .select(V2User::getId)
                .eq(V2User::getAccount, account.trim())
                .one());
    }
}
