package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    //내가 작성
    //Optional<Object> findByEmail(String email);


    //이메일 중복 체크
    //@Query("SELECT COUNT(*) FROM User u WHERE u.email =: email") //JPQL 방식

    //exists 만 붙이면 되는 : 쿼리메서드 문법* 간단한거 @@
    boolean existsByEmail(String email); //리턴타입 boolean


    Optional<User> findByEmail(String email); //메서드 직접 선언






}
