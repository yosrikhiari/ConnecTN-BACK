package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByKeycloakId(String keycloakId);
    public List<User> findByUsername(String username);
    public List<User> findByEmailAddress(String emailAddress);

}
