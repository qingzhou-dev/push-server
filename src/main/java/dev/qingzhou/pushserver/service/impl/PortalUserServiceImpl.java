package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.mapper.portal.PortalUserMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.service.PortalUserService;
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
            throw new PortalException(PortalStatus.BAD_REQUEST, "账号和密码不能为空");
        }
        if (existsAccount(account)) {
            throw new PortalException(PortalStatus.CONFLICT, "账号已存在");
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
            throw new PortalException(PortalStatus.BAD_REQUEST, "账号和密码不能为空");
        }
        PortalUser user = findByAccount(account);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "凭证无效");
        }
        return user;
    }

    @Override
    public PortalUser findByAccount(String account) {
        if (!StringUtils.hasText(account)) {
            return null;
        }
        QueryWrapper<PortalUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account.trim());
        return getOne(wrapper);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "密码字段不能为空");
        }
        PortalUser user = getById(userId);
        if (user == null) {
            throw new PortalException(PortalStatus.NOT_FOUND, "用户未找到");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "旧密码不匹配");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(System.currentTimeMillis());
        updateById(user);
    }

    private boolean existsAccount(String account) {
        QueryWrapper<PortalUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account.trim());
        return count(wrapper) > 0;
    }
}
