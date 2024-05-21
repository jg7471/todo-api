package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    //특정 회원의 할 일 목록 리턴
    //native : SELECT * FROM tbl_todo WHERE user_id = ?

    @Query("SELECT t FROM Todo t WHERE t.user = :user")
    List<Todo> findAllByUser(@Param("user") User user); //JPA entity 기반 sql

    //종류
    //쿼리메서드
    //네이티브 쿼리
    //JPQL
    //@@@
    //Page<Student> students = studentPageRepository.findAll(pageable); 있고 없고 차이
    //디스패쳐란?
}
