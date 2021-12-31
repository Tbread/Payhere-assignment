package com.payhere.pageonce.repository;

import com.payhere.pageonce.model.Pageonce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageonceRepository extends JpaRepository<Pageonce,Long> {
    List<Pageonce> findByUserIdAndDeleted(Long userid,boolean isDeleted);
    Optional<Pageonce> findById(Long id);
    Optional<Pageonce> findByIdAndDeleted(Long id,boolean isDeleted);
}
