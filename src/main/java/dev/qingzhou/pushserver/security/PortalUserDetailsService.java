package dev.qingzhou.pushserver.security;

import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.service.PortalUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final PortalUserService portalUserService;

    public PortalUserDetailsService(PortalUserService portalUserService) {
        this.portalUserService = portalUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Account is required");
        }
        PortalUser user = portalUserService.findByAccount(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new PortalUserDetails(user.getId(), user.getAccount(), user.getPasswordHash());
    }
}
