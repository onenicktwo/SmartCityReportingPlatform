package org.citywatcher.repository;

import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByReporter(User reporter);
    Optional<Issue> findByIdAndReporter(Long id, User reporter);
    Optional<Issue> findById(Long id);
    List<Issue> findByVolunteersContaining(User volunteer);
}