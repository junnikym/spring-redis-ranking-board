package edu.junnikym.springredisrankingboard.user.repository;

import edu.junnikym.springredisrankingboard.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRedisRepository extends CrudRepository<User, UUID> { }
