package com.payhere.pageonce.repository;

import com.payhere.pageonce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
