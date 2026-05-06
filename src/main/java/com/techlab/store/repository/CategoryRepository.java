package com.techlab.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techlab.store.entity.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.parentCategory = :parentCategory")
    public Category findByNameAndParentCategory(@Param("name") String name, 
                                         @Param("parentCategory") Category parentCategory);
  
}