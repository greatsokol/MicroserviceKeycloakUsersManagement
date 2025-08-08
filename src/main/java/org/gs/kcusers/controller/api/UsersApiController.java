/*
 * Created by Eugene Sokolov 09.07.2024, 11:02.
 */

package org.gs.kcusers.controller.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.gs.kcusers.controller.CommonController;
import org.gs.kcusers.domain.User;
import org.gs.kcusers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class UsersApiController extends CommonController {
    protected UserRepository userRepository;

    @Value("${service.keycloakclient.realms}")
    private String KEYCLOAK_REALMS;

    private ArrayList<String> keycloakRealms;


    UsersApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyAuthority(@getUserRoles)")
    @GetMapping("users")
    public UsersApiResponse getUsers(@PageableDefault Pageable pagable) {
        saveLoginEvent();
        return new UsersApiResponse(
                getPrincipal(),
                userRepository.findByRealmNameInOrderByRealmNameAscUserNameAsc(getKeycloakRealms(), pagable),
                null
        );
    }

    @PreAuthorize("hasAnyAuthority(@getUserRoles)")
    @GetMapping(path = "users", params = "filter")
    public UsersApiResponse getUsers(@RequestParam String filter, @PageableDefault Pageable pagable) {
        saveLoginEvent();

        if (filter.isEmpty()) {
            return getUsers(pagable);
        }

        if (filter.length() > 20) {
            filter = filter.substring(0, 20);
        }

        return new UsersApiResponse(
                getPrincipal(),
                userRepository.findByRealmNameInAndUserNameContainingOrderByRealmNameAscUserNameAsc(
                        getKeycloakRealms(), filter, pagable),
                filter
        );
    }

    private ArrayList<String> getKeycloakRealms() {
        if (keycloakRealms == null) {
            keycloakRealms = new ArrayList<>(Arrays.asList(KEYCLOAK_REALMS.split(",")));
        }
        return keycloakRealms;
    }

    @Data
    @AllArgsConstructor
    static public class UsersApiResponse {
        Principal principal;
        Page<User> payload;
        String filter;
    }
}
