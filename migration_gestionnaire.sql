-- ============================================================
-- Migration : colonnes manquantes pour le gestionnaire de stock
-- À exécuter une seule fois sur la base MySQL HSP
-- ============================================================

-- 1. Ajout de la colonne statut dans demande (si absente)
ALTER TABLE `demande`
    ADD COLUMN IF NOT EXISTS `statut` VARCHAR(30) NOT NULL DEFAULT 'En attente';

-- 2. Ajout de la colonne motif_refus dans demande (si absente)
ALTER TABLE `demande`
    ADD COLUMN IF NOT EXISTS `motif_refus` TEXT NULL DEFAULT NULL;

-- 3. S'assurer que les demandes existantes ont un statut
UPDATE `demande` SET `statut` = 'En attente' WHERE `statut` IS NULL OR `statut` = '';

-- Vérification
SELECT 'Migration OK - colonnes statut et motif_refus ajoutées à la table demande' AS result;
