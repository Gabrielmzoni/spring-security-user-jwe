package br.com.gmsoft.userjwe.repository;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.security.LoggedUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    @Query("SELECT new br.com.gmsoft.userjwe.security.LoggedUserDetails(u.name, u.email, u.phone) FROM User u WHERE u.email = ?1")
    Optional<LoggedUserDetails> findUserDetailsByEmail(String email);

    Optional<User> findByPhone(String phone);
}