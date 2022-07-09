package edu.junnikym.springredisrankingboard.user.repository;

import edu.junnikym.springredisrankingboard.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID> {

	boolean existsByNickname(String nickname);

	Optional<User> findByNickname(String nickname);

}
