package com.audit.comments.repository;

import com.audit.comments.entity.CommentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentHistoryRepository extends JpaRepository<CommentHistoryEntity, String> {

    List<CommentHistoryEntity> findByCommentIdOrderByModifiedAtDesc(String commentId);

    Page<CommentHistoryEntity> findByCommentId(String commentId, Pageable pageable);

    @Query("SELECT h FROM CommentHistoryEntity h WHERE h.commentId IN :commentIds ORDER BY h.modifiedAt DESC")
    List<CommentHistoryEntity> findByCommentIds(@Param("commentIds") List<String> commentIds);

    @Query("SELECT h FROM CommentHistoryEntity h WHERE h.modifiedBy = :userId ORDER BY h.modifiedAt DESC")
    Page<CommentHistoryEntity> findByUser(@Param("userId") String userId, Pageable pageable);
}