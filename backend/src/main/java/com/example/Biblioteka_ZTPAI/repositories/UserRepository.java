package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
