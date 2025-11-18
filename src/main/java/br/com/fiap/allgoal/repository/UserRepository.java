package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.enums.Roles;
import br.com.fiap.allgoal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.xpTotal > :xp AND u.role = 'FUNCIONARIO'")
    long getRanking(@Param("xp") Long xpTotal);

    List<User> findTop5ByRoleOrderByXpTotalDesc(Roles role);
}
