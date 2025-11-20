package com.example.geco.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.UserDetail;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Integer>{
}
