package org.citywatcher.repository;

import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByReporter(User reporter);
    Optional<Issue> findByIdAndReporter(Long id, User reporter);
    Optional<Issue> findById(Long id);
    List<Issue> findByVolunteersContaining(User volunteer);
    List<Issue> findByReportedDateBetween(Date startDate, Date endDate);

    @Query("SELECT i FROM Issue i WHERE " +
            "acos(sin(:lat) * sin(radians(i.latitude)) + " +
            "cos(:lat) * cos(radians(i.latitude)) * " +
            "cos(radians(i.longitude) - :lng)) * 6371 <= :radius")
    List<Issue> findByLocationWithinRadius(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusInKm);
}