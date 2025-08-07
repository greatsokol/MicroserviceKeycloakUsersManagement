package org.gs.kcusers.controller.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.gs.kcusers.controller.CommonController;
import org.gs.kcusers.domain.User;
import org.gs.kcusers.repositories.UserRepository;
import org.gs.kcusers.service.KeycloakClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static org.gs.kcusers.utils.Utils.getAuthorizedUserName;

@RestController
@RequestMapping("/api/user")
public class UserApiController extends CommonController {
    protected UserRepository userRepository;
    KeycloakClient keycloakClient;

    public UserApiController(KeycloakClient keycloakClient, UserRepository userRepository) {
        this.keycloakClient = keycloakClient;
        this.userRepository = userRepository;
    }

    private UserApiResponse getUserPage(String realmName, String userName, boolean successResult) {
        saveLoginEvent();

        var response = new UserApiResponse(
                getPrincipal(),
                userRepository.findByUserNameAndRealmName(userName, realmName)
        );

        if (!successResult && response.payload != null) {
            response.payload.setComment("Ошибка операции");
        }
        return response;
    }

    @PreAuthorize("hasAnyAuthority(@getUserRoles)")
    @GetMapping(path = "/{realmName}/{userName}")
    public UserApiResponse userPage(@PathVariable String realmName, @PathVariable String userName) {
        return getUserPage(realmName, userName, true);
    }

    @PreAuthorize("hasAnyAuthority(@getAdminRoles)")
    @PostMapping(path = "/{realmName}/{userName}")
    public UserApiResponse putUser(@PathVariable String realmName,
                                   @PathVariable String userName,
                                   @RequestBody MultiValueMap<String, String> formData) {
        saveLoginEvent();
        User user = userRepository.findByUserNameAndRealmName(userName, realmName);
        User tmpUser = new User(user);


        String wantedEnabled = formData.getFirst("enabled");
        boolean enabled = wantedEnabled != null && wantedEnabled.equals("true");
        String adminName = getAuthorizedUserName();
        tmpUser.setUserStatusFromController(enabled, adminName);
        if (keycloakClient.updateUserFromController(tmpUser, adminName)) {
            user.setUserStatusFromController(enabled, adminName);
            return getUserPage(realmName, userName, true);
        }
        return getUserPage(realmName, userName, false);
    }

    @Data
    @AllArgsConstructor
    static public class UserApiResponse {
        Principal principal;
        User payload;
    }
}
