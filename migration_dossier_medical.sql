-- Migration: Transformation DossierEnCharge → Dossier Médical Permanent
-- Date: 2026-03-18
-- Description: Un dossier médical permanent par élève (antécédents, allergies, traitements chroniques)
--              L'historique des visites est lu depuis la table visite_infirmerie existante

CREATE TABLE IF NOT EXISTS dossier_medical (
    id_dossier INT AUTO_INCREMENT PRIMARY KEY,
    id_eleve INT NOT NULL UNIQUE,
    antecedents TEXT,
    allergies TEXT,
    traitements_chroniques TEXT,
    date_creation DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
