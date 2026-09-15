package saga.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.database.User;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    User findByUsername(String username);
}