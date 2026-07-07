package com.ecommerce.auth_service.config;

import com.ecommerce.auth_service.model.entity.AdminUser;
import com.ecommerce.auth_service.model.entity.Permission;
import com.ecommerce.auth_service.model.entity.Role;
import com.ecommerce.auth_service.repository.AdminUserRepository;
import com.ecommerce.auth_service.repository.PermissionRepository;
import com.ecommerce.auth_service.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final List<String> PERMISSION_CODES = List.of(
            "product:read",
            "product:write",
            "product:delete",
            "discount:read",
            "discount:write",
            "discount:delete",
            "category:write",
            "admin:user:manage"
    );

    @Bean
    CommandLineRunner seedData(PermissionRepository permissionRepository,
                               RoleRepository roleRepository,
                               AdminUserRepository adminUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Map<String, Permission> permissions = seedPermissions(permissionRepository);
            Map<String, Role> roles = seedRoles(roleRepository, permissions);
            seedAdminUsers(adminUserRepository, passwordEncoder, roles);
            log.info("Auth data initialized: {} permissions, {} roles", permissions.size(), roles.size());
        };
    }

    private Map<String, Permission> seedPermissions(PermissionRepository permissionRepository) {
        Map<String, Permission> permissions = new LinkedHashMap<>();
        for (String code : PERMISSION_CODES) {
            Permission permission = permissionRepository.findByCode(code)
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .code(code)
                            .description(code)
                            .build()));
            permissions.put(code, permission);
        }
        return permissions;
    }

    private Map<String, Role> seedRoles(RoleRepository roleRepository, Map<String, Permission> permissions) {
        Map<String, Role> roles = new LinkedHashMap<>();

        roles.put("SUPER_ADMIN", upsertRole(roleRepository, "SUPER_ADMIN", "Super Admin",
                "Full access to all admin capabilities", new LinkedHashSet<>(permissions.values())));

        roles.put("CATALOG_MANAGER", upsertRole(roleRepository, "CATALOG_MANAGER", "Catalog Manager",
                "Manage products and categories", permissionSet(permissions,
                        "product:read", "product:write", "category:write")));

        roles.put("CATALOG_VIEWER", upsertRole(roleRepository, "CATALOG_VIEWER", "Catalog Viewer",
                "Read-only catalog access", permissionSet(permissions, "product:read")));

        roles.put("PROMOTION_MANAGER", upsertRole(roleRepository, "PROMOTION_MANAGER", "Promotion Manager",
                "Manage discounts and view products", permissionSet(permissions,
                        "product:read", "discount:read", "discount:write")));

        return roles;
    }

    private Set<Permission> permissionSet(Map<String, Permission> permissions, String... codes) {
        Set<Permission> set = new LinkedHashSet<>();
        for (String code : codes) {
            set.add(permissions.get(code));
        }
        return set;
    }

    private Role upsertRole(RoleRepository roleRepository, String code, String name, String description,
                            Set<Permission> rolePermissions) {
        Role role = roleRepository.findByCode(code).orElseGet(() -> Role.builder()
                .code(code)
                .name(name)
                .description(description)
                .permissions(new LinkedHashSet<>())
                .build());
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(rolePermissions);
        return roleRepository.save(role);
    }

    private void seedAdminUsers(AdminUserRepository adminUserRepository,
                                PasswordEncoder passwordEncoder,
                                Map<String, Role> roles) {
        upsertAdmin(adminUserRepository, passwordEncoder, "super@mydomain.com", "Super Admin",
                Set.of(roles.get("SUPER_ADMIN")));
        upsertAdmin(adminUserRepository, passwordEncoder, "catalog@mydomain.com", "Catalog Manager",
                Set.of(roles.get("CATALOG_MANAGER")));
        upsertAdmin(adminUserRepository, passwordEncoder, "viewer@mydomain.com", "Catalog Viewer",
                Set.of(roles.get("CATALOG_VIEWER")));
    }

    private void upsertAdmin(AdminUserRepository adminUserRepository,
                             PasswordEncoder passwordEncoder,
                             String email,
                             String name,
                             Set<Role> adminRoles) {
        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(email).orElseGet(() -> AdminUser.builder()
                .email(email)
                .name(name)
                .active(true)
                .createdAt(Instant.now())
                .roles(new LinkedHashSet<>())
                .build());

        admin.setName(name);
        admin.setActive(true);
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setRoles(adminRoles);
        if (admin.getCreatedAt() == null) {
            admin.setCreatedAt(Instant.now());
        }
        adminUserRepository.save(admin);
    }
}
