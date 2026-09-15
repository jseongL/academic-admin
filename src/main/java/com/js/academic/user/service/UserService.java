package com.js.academic.user.service;

import com.js.academic.user.domain.User;
import com.js.academic.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 (등록)
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByLoginId(user.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
        }
        if (userRepository.existsByStudentId(user.getStudentId())) {
            throw new IllegalArgumentException("이미 등록된 학번입니다.");
        }
        return userRepository.save(user);
    }

    // 전체 유저 조회
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // PK 기준 조회
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 로그인 아이디 기준 조회
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    // 유저 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    
 // 로그인 검증
    public User login(String loginId, String password) {
        return userRepository.findByLoginId(loginId)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }
    
    
    
    
    
}