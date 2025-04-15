package ru.practicum.service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByIdIn(List<Long> ids);
}
