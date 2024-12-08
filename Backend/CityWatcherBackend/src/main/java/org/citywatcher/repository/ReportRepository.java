package org.citywatcher.repository;

import org.citywatcher.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCommentId(Long commentId);
    List<Report> findByReporterId(Long reporterId);
}