package org.gs.kcusers.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Entity
@EntityListeners(EntityListener.class)
@Data
@AllArgsConstructor
@IdClass(Event.EventPK.class)
@Table(name = "events", schema = "kcusers")
public class Event {
    @Id
    String userName;
    @Id
    String realmName;
    @Id
    Long created;
    @NonNull
    String admLogin;

    String comment;

    Boolean enabled;

    public Event() {
    }

    @EqualsAndHashCode
    public static class EventPK {
        private String userName;
        private String realmName;
        private Long created;
    }


}
