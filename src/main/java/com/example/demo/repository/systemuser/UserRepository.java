package com.example.demo.repository.systemuser;

import com.example.demo.entity.systemuser.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByAuthorizationServiceUserId(String authorizationServiceUserId);

    Page<User> findByUserNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(String userName, String emailAddress, Pageable pageable);

    Optional<User> findByEmailAddress(String emailAddress);
}
