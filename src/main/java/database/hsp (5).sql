-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mar. 10 fév. 2026 à 10:02
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `hsp`
--

-- --------------------------------------------------------

--
-- Structure de la table `chambre`
--

DROP TABLE IF EXISTS `chambre`;
CREATE TABLE IF NOT EXISTS `chambre` (
  `id_chambre` int NOT NULL AUTO_INCREMENT,
  `numero_chambre` int NOT NULL,
  `disponible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_chambre`),
  UNIQUE KEY `uq_chambre_numero` (`numero_chambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `commande`
--

DROP TABLE IF EXISTS `commande`;
CREATE TABLE IF NOT EXISTS `commande` (
  `id_commande` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `numCommande` int NOT NULL,
  `libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`id_commande`),
  UNIQUE KEY `uq_commande_num` (`numCommande`),
  KEY `idx_commande_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `commande_produit`
--

DROP TABLE IF EXISTS `commande_produit`;
CREATE TABLE IF NOT EXISTS `commande_produit` (
  `id_commande` int NOT NULL,
  `id_produit` int NOT NULL,
  `qte` int NOT NULL,
  PRIMARY KEY (`id_commande`,`id_produit`),
  KEY `fk_cp_produit` (`id_produit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `demande`
--

DROP TABLE IF EXISTS `demande`;
CREATE TABLE IF NOT EXISTS `demande` (
  `id_demande` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `date_demande` datetime NOT NULL,
  `quantite` int NOT NULL,
  PRIMARY KEY (`id_demande`),
  KEY `idx_demande_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `demande_produit`
--

DROP TABLE IF EXISTS `demande_produit`;
CREATE TABLE IF NOT EXISTS `demande_produit` (
  `id_demande` int NOT NULL,
  `id_produit` int NOT NULL,
  `qte` int NOT NULL,
  PRIMARY KEY (`id_demande`,`id_produit`),
  KEY `fk_dp_produit` (`id_produit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `dossier_charge`
--

DROP TABLE IF EXISTS `dossier_charge`;
CREATE TABLE IF NOT EXISTS `dossier_charge` (
  `id_dossier` int NOT NULL AUTO_INCREMENT,
  `id_patient` int NOT NULL,
  `id_user` int NOT NULL,
  `date_arrivee` datetime NOT NULL,
  `heure_arrivee` datetime NOT NULL,
  `symptomes` varchar(255) NOT NULL,
  `niveau_gravite` enum('1','2','3','4','5') NOT NULL,
  PRIMARY KEY (`id_dossier`),
  KEY `idx_dossier_patient` (`id_patient`),
  KEY `idx_dossier_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fiche_patient`
--

DROP TABLE IF EXISTS `fiche_patient`;
CREATE TABLE IF NOT EXISTS `fiche_patient` (
  `id_patient` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) NOT NULL,
  `prenom` varchar(150) NOT NULL,
  `num_secu` bigint NOT NULL,
  `email` varchar(255) NOT NULL,
  `tel` varchar(25) NOT NULL,
  `rue` varchar(255) NOT NULL,
  `cp` int NOT NULL,
  `ville` varchar(150) NOT NULL,
  PRIMARY KEY (`id_patient`),
  UNIQUE KEY `uq_patient_numsecu` (`num_secu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fiche_produit`
--

DROP TABLE IF EXISTS `fiche_produit`;
CREATE TABLE IF NOT EXISTS `fiche_produit` (
  `id_produit` int NOT NULL AUTO_INCREMENT,
  `libelle` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `niveau_dangerosite` int NOT NULL,
  PRIMARY KEY (`id_produit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fournisseur`
--

DROP TABLE IF EXISTS `fournisseur`;
CREATE TABLE IF NOT EXISTS `fournisseur` (
  `id_fournisseur` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) NOT NULL,
  PRIMARY KEY (`id_fournisseur`),
  UNIQUE KEY `uq_fournisseur_nom` (`nom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fournisseur_produit`
--

DROP TABLE IF EXISTS `fournisseur_produit`;
CREATE TABLE IF NOT EXISTS `fournisseur_produit` (
  `id_fournisseur` int NOT NULL,
  `id_produit` int NOT NULL,
  `prix` int NOT NULL,
  PRIMARY KEY (`id_fournisseur`,`id_produit`),
  KEY `fk_fp_produit` (`id_produit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `hospitalisation`
--

DROP TABLE IF EXISTS `hospitalisation`;
CREATE TABLE IF NOT EXISTS `hospitalisation` (
  `id_hospitalisation` int NOT NULL AUTO_INCREMENT,
  `id_dossier` int NOT NULL,
  `id_chambre` int NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime DEFAULT NULL,
  `desc_maladie` varchar(255) NOT NULL,
  PRIMARY KEY (`id_hospitalisation`),
  UNIQUE KEY `uq_hospit_dossier` (`id_dossier`),
  KEY `idx_hospit_chambre` (`id_chambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ordonnance`
--

DROP TABLE IF EXISTS `ordonnance`;
CREATE TABLE IF NOT EXISTS `ordonnance` (
  `id_ordonnance` int NOT NULL AUTO_INCREMENT,
  `id_dossier` int NOT NULL,
  `date_ordonnance` datetime NOT NULL,
  `contenu` varchar(255) NOT NULL,
  PRIMARY KEY (`id_ordonnance`),
  KEY `idx_ordonnance_dossier` (`id_dossier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) NOT NULL,
  `prenom` varchar(150) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` enum('medecin','secretaire','gestionnaire de stock') NOT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `uq_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `commande`
--
ALTER TABLE `commande`
  ADD CONSTRAINT `fk_commande_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `commande_produit`
--
ALTER TABLE `commande_produit`
  ADD CONSTRAINT `fk_cp_commande` FOREIGN KEY (`id_commande`) REFERENCES `commande` (`id_commande`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `demande`
--
ALTER TABLE `demande`
  ADD CONSTRAINT `fk_demande_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `demande_produit`
--
ALTER TABLE `demande_produit`
  ADD CONSTRAINT `fk_dp_demande` FOREIGN KEY (`id_demande`) REFERENCES `demande` (`id_demande`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_dp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `dossier_charge`
--
ALTER TABLE `dossier_charge`
  ADD CONSTRAINT `fk_dossier_patient` FOREIGN KEY (`id_patient`) REFERENCES `fiche_patient` (`id_patient`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_dossier_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `fournisseur_produit`
--
ALTER TABLE `fournisseur_produit`
  ADD CONSTRAINT `fk_fp_fournisseur` FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseur` (`id_fournisseur`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_fp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `hospitalisation`
--
ALTER TABLE `hospitalisation`
  ADD CONSTRAINT `fk_hospit_chambre` FOREIGN KEY (`id_chambre`) REFERENCES `chambre` (`id_chambre`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_hospit_dossier` FOREIGN KEY (`id_dossier`) REFERENCES `dossier_charge` (`id_dossier`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Contraintes pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD CONSTRAINT `fk_ordonnance_dossier` FOREIGN KEY (`id_dossier`) REFERENCES `dossier_charge` (`id_dossier`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
