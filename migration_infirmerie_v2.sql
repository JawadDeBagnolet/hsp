-- Migration infirmerie v2 : traitement + statut
-- À exécuter une seule fois

ALTER TABLE `visite_infirmerie`
    ADD COLUMN `traitement` VARCHAR(500) DEFAULT NULL,
    ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'Terminée';
