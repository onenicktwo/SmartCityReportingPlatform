package org.citywatcher.repository;

import org.citywatcher.model.Comment;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIssue(Issue issue);
}
