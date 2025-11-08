package com.example.geco.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.UserDetail;

@Repository
public interface UserDetailRepository extends CrudRepository<UserDetail, Integer>{

}
