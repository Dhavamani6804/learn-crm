package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByLeadId(Long leadId);

}