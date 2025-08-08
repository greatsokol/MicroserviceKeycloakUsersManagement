package org.gs.kcusers.repositories;

import org.gs.kcusers.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserNameAndRealmName(String userName, String realmName);

    Page<User> findByRealmNameInAndUserNameContainingOrderByRealmNameAscUserNameAsc(
            Collection<String> realmName,
            String userName,
            Pageable pagable
    );

    Page<User> findByRealmNameInOrderByRealmNameAscUserNameAsc(Collection<String> realmName, Pageable pagable);

//    @Query(value = "SELECT DISTINCT realmName FROM User")
//    List<String> finaAllRealmNames();
//
//    @Query(value = "SELECT DISTINCT realmName FROM User WHERE userName LIKE %:userName%")
//    List<String> finaAllRealmNamesContaining(@Param("userName") String userName);

    List<User> findAllByRealmNameAndEnabled(String realmName, Boolean enabled);

    long countByEnabled(boolean enabled);
}
