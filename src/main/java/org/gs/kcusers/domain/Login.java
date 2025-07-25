package org.gs.kcusers.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EntityListeners(EntityListener.class)
@Data
@AllArgsConstructor
@IdClass(Login.LoginPK.class)
@Table(name = "logins", schema = "kcusers")
public class Login {
    @Id
    String userName;

    @Id
    Long authTime;

    @Id
    String session;

    String address;

    public Login() {
    }

    @EqualsAndHashCode
    public static class LoginPK {
        private String userName;
        private Long authTime;
        private String session;
    }

}
