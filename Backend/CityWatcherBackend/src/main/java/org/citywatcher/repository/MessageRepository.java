package org.citywatcher.repository;

import org.citywatcher.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTimestampGreaterThanOrderByTimestampDesc(LocalDateTime since, Pageable pageable);
    List<Message> findAllByOrderByTimestampDesc(Pageable pageable);
}