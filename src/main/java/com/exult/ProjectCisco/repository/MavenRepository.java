package com.exult.ProjectCisco.repository;

import com.exult.ProjectCisco.model.Maven;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MavenRepository extends JpaRepository<Maven,Integer> {
}
