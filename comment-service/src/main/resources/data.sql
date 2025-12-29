-- Données de démonstration pour les commentaires d'audit
INSERT INTO audit_comments (id, content, audit_id, requirement_id, proof_id, is_resolved, comment_type, created_by, created_at, updated_at) 
VALUES 
(
    'cmt-001',
    'La preuve ne couvre pas complètement l''exigence 7.1 de la norme ISO 27001',
    'audit-2024-001',
    'req-iso-27001-7.1',
    'proof-001',
    false,
    'PROOF_COMMENT',
    'auditor1',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
),
(
    'cmt-002',
    'Documentation de la politique de sécurité manquante',
    'audit-2024-001',
    'req-iso-27001-5.1',
    NULL,
    true,
    'REQUIREMENT_COMMENT',
    'auditor2',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
),
(
    'cmt-003',
    'La preuve a été validée après correction',
    'audit-2024-001',
    'req-iso-27001-8.2',
    'proof-003',
    true,
    'VALIDATION_COMMENT',
    'reviewer1',
    NOW() - INTERVAL '3 hours',
    NOW() - INTERVAL '3 hours'
),
(
    'cmt-004',
    'Audit global : bonne conformité mais amélioration possible sur la formation',
    'audit-2024-001',
    'general',
    NULL,
    false,
    'GENERAL_COMMENT',
    'auditor1',
    NOW() - INTERVAL '1 hour',
    NOW() - INTERVAL '1 hour'
);

-- Historique de démonstration
INSERT INTO audit_comment_history (id, comment_id, old_content, new_content, old_resolved, new_resolved, modified_by, action_type, modified_at)
VALUES
(
    'hist-001',
    'cmt-002',
    'Documentation manquante',
    'Documentation de la politique de sécurité manquante',
    NULL,
    NULL,
    'auditor2',
    'CREATED',
    NOW() - INTERVAL '1 day'
),
(
    'hist-002',
    'cmt-002',
    'Documentation de la politique de sécurité manquante',
    'Documentation de la politique de sécurité manquante',
    false,
    true,
    'reviewer1',
    'RESOLVED',
    NOW() - INTERVAL '12 hours'
);