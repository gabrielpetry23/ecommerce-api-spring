package io.github.gabrielpetry23.ecommerceapi.validators;

import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final SecurityService securityService;

    public void validateCurrentUserAccess(UUID userId) {
        if (!securityService.getCurrentUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    public void validateCurrentUserAccess(UUID userId, UUID currentUserId) {
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    public void validateCurrentUserAccessOrAdmin(UUID userId) {
        User currentUser = securityService.getCurrentUser();
        boolean isSameUser = currentUser.getId().equals(userId);

        System.out.println("currentUser ID: " + currentUser.getId());
        System.out.println("requested userId: " + userId);
        String role = currentUser.getRole();
        boolean isPrivileged = "ADMIN".equals(role) || "MANAGER".equals(role);

        if (!isSameUser && !isPrivileged) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }
}
