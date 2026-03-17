-- Migration : workflow demande infirmier → commande fournisseur
-- À exécuter une seule fois

-- Ajout du statut sur la table demande
ALTER TABLE `demande`
    ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'En attente';

-- Ajout des champs manquants sur la table commande
ALTER TABLE `commande`
    ADD COLUMN `id_fournisseur` INT NOT NULL DEFAULT 0,
    ADD COLUMN `date_commande` DATETIME NOT NULL DEFAULT NOW(),
    ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'En attente';
