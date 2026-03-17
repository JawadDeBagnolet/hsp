-- ============================================================
-- MIGRATION LPRS : Hôpital -> Lycée de Santé Publique
-- À exécuter dans l'ordre dans votre BDD MySQL (hsp)
-- ============================================================

-- 1. Renommer la table fiche_patient -> fiche_eleve
RENAME TABLE fiche_patient TO fiche_eleve;

-- 2. Renommer la colonne id_patient -> id_eleve
ALTER TABLE fiche_eleve CHANGE id_patient id_eleve INT AUTO_INCREMENT;

-- 3. Remplacer num_secu (BIGINT) par num_etudiant (VARCHAR)
ALTER TABLE fiche_eleve CHANGE num_secu num_etudiant VARCHAR(50) NULL DEFAULT NULL;

-- 4. Ajouter la colonne candidature : NULL = en cours, 0 = refusé, 1 = validé
ALTER TABLE fiche_eleve ADD COLUMN candidature TINYINT(1) NULL DEFAULT NULL;

-- 5. Mettre à jour la table rendez_vous : id_patient -> id_eleve
ALTER TABLE rendez_vous CHANGE id_patient id_eleve INT NOT NULL;

-- 6. Mettre à jour la table rendez_vous : id_medecin -> id_prof
ALTER TABLE rendez_vous CHANGE id_medecin id_prof INT NOT NULL;

-- 7. Mettre à jour les rôles utilisateurs : MEDECIN -> PROF
UPDATE user SET role = 'PROF' WHERE role = 'MEDECIN';

-- 8. Créer la table visite_infirmerie
CREATE TABLE IF NOT EXISTS visite_infirmerie (
    id_visite   INT AUTO_INCREMENT PRIMARY KEY,
    id_eleve    INT NOT NULL,
    date_visite DATE NOT NULL,
    heure_visite TIME NOT NULL,
    motif       VARCHAR(500) NULL,
    id_infirmier INT NULL,
    FOREIGN KEY (id_eleve) REFERENCES fiche_eleve(id_eleve) ON DELETE CASCADE,
    FOREIGN KEY (id_infirmier) REFERENCES user(id_user) ON DELETE SET NULL
);

-- ============================================================
-- FIN DE MIGRATION
-- ============================================================
