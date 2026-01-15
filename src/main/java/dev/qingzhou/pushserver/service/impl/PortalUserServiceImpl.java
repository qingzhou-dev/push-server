package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.mapper.portal.PortalUserMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.service.PortalUserService;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalUserServiceImpl extends ServiceImpl<PortalUserMapper, PortalUser> implements PortalUserService {

    private final PasswordEncoder passwordEncoder;

    public PortalUserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PortalUser register(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "Account and password are required");
        }
        if (existsAccount(account)) {
            throw new PortalException(PortalStatus.CONFLICT, "Account already exists");
        }
        PortalUser user = new PortalUser();
        user.setAccount(account.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        long now = System.currentTimeMillis();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        save(user);
        return user;
    }

    @Override
    public PortalUser authenticate(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "Account and password are required");
        }
        PortalUser user = lambdaQuery().eq(PortalUser::getAccount, account.trim()).one();
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return user;
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "Password fields are required");
        }
        PortalUser user = getById(userId);
        if (user == null) {
            throw new PortalException(PortalStatus.NOT_FOUND, "User not found");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Old password mismatch");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(System.currentTimeMillis());
        updateById(user);
    }

    private boolean existsAccount(String account) {
        return Objects.nonNull(lambdaQuery()
                .select(PortalUser::getId)
                .eq(PortalUser::getAccount, account.trim())
                .one());
    }
}
