package com.example.distributed_lovable.account_service.repository;

import java.util.Optional;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.distributed_lovable.account_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);


	Optional<User> findByUsernameIgnoreCase(String email);
}
