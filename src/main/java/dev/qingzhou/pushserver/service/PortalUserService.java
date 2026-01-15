package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalUser;

public interface PortalUserService extends IService<PortalUser> {

    PortalUser register(String account, String password);

    PortalUser authenticate(String account, String password);

    void updatePassword(Long userId, String oldPassword, String newPassword);
}
