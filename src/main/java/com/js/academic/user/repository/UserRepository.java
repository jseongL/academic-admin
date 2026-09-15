package com.js.academic.user.repository;

import com.js.academic.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 로그인 아이디 중복 확인 및 조회
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    // 학번 중복 확인 및 조회
    Optional<User> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
}