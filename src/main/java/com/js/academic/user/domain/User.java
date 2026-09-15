package com.js.academic.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 PK

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId; // 로그인 아이디

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호

    @Column(nullable = false, length = 50)
    private String name; // 이름

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId; // 학번 (유니크 키)

    private Integer age; // 나이
    private String phone; // 전화번호
    private String email; // 이메일
    private Integer grade; // 학년
    private String major; // 전공

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 일시

    // 기본 생성자
    public User() {}

    // 생성자 (회원가입용)
    public User(String loginId, String password, String name, String studentId, 
                Integer age, String phone, String email, Integer grade, String major) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.studentId = studentId;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.grade = grade;
        this.major = major;
    }

    // Getter 및 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}