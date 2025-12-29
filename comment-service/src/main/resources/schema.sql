-- Table des commentaires d'audit
CREATE TABLE IF NOT EXISTS audit_comments (
    id VARCHAR(36) PRIMARY KEY,
    content TEXT NOT NULL,
    audit_id VARCHAR(50) NOT NULL,
    requirement_id VARCHAR(50) NOT NULL,
    proof_id VARCHAR(50),
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    resolved_by VARCHAR(100),
    comment_type VARCHAR(20) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

-- Table d'historique des commentaires
CREATE TABLE IF NOT EXISTS audit_comment_history (
    id VARCHAR(36) PRIMARY KEY,
    comment_id VARCHAR(36) NOT NULL,
    old_content TEXT,
    new_content TEXT,
    old_resolved BOOLEAN,
    new_resolved BOOLEAN,
    modified_by VARCHAR(100) NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index pour optimiser les requêtes
CREATE INDEX idx_audit_comments_audit_id ON audit_comments(audit_id);
CREATE INDEX idx_audit_comments_requirement_id ON audit_comments(requirement_id);
CREATE INDEX idx_audit_comments_proof_id ON audit_comments(proof_id);
CREATE INDEX idx_audit_comments_created_by ON audit_comments(created_by);
CREATE INDEX idx_audit_comments_resolved ON audit_comments(is_resolved);
CREATE INDEX idx_audit_comment_history_comment_id ON audit_comment_history(comment_id);
CREATE INDEX idx_audit_comment_history_modified_by ON audit_comment_history(modified_by);

-- Contraintes de clé étrangère
ALTER TABLE audit_comment_history 
ADD CONSTRAINT fk_comment_history_comment 
FOREIGN KEY (comment_id) REFERENCES audit_comments(id) ON DELETE CASCADE;