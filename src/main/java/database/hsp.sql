-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mar. 03 fév. 2026 à 10:43
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
  `numeroChambre` int DEFAULT NULL,
  `disponible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_chambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `commande`
--

DROP TABLE IF EXISTS `commande`;
CREATE TABLE IF NOT EXISTS `commande` (
  `id_commande` int NOT NULL AUTO_INCREMENT,
  `numeroCommande` varchar(50) NOT NULL,
  `libelle` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_commande`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `concernecommandeproduit`
--

DROP TABLE IF EXISTS `concernecommandeproduit`;
CREATE TABLE IF NOT EXISTS `concernecommandeproduit` (
  `ref_commande` int NOT NULL,
  `ref_ficheProduit` int NOT NULL,
  `qte` int NOT NULL,
  PRIMARY KEY (`ref_commande`,`ref_ficheProduit`),
  KEY `ref_ficheProduit` (`ref_ficheProduit`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `concernedemandeproduit`
--

DROP TABLE IF EXISTS `concernedemandeproduit`;
CREATE TABLE IF NOT EXISTS `concernedemandeproduit` (
  `ref_demande` int NOT NULL,
  `ref_ficheProduit` int NOT NULL,
  `qte` int NOT NULL,
  PRIMARY KEY (`ref_demande`,`ref_ficheProduit`),
  KEY `ref_ficheProduit` (`ref_ficheProduit`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `demande`
--

DROP TABLE IF EXISTS `demande`;
CREATE TABLE IF NOT EXISTS `demande` (
  `id_demande` int NOT NULL AUTO_INCREMENT,
  `date_demande` date DEFAULT NULL,
  `statut` varchar(30) NOT NULL,
  PRIMARY KEY (`id_demande`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `dossierpriseencharge`
--

DROP TABLE IF EXISTS `dossierpriseencharge`;
CREATE TABLE IF NOT EXISTS `dossierpriseencharge` (
  `id_dossier` int NOT NULL AUTO_INCREMENT,
  `date_arrivee` date DEFAULT NULL,
  `heure_arrivee` time DEFAULT NULL,
  `symptomes` text,
  `niveau_gravite` int DEFAULT NULL,
  `ref_fichePatient` int NOT NULL,
  `ref_user` int NOT NULL,
  PRIMARY KEY (`id_dossier`),
  KEY `ref_fichePatient` (`ref_fichePatient`),
  KEY `ref_user` (`ref_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fichepatient`
--

DROP TABLE IF EXISTS `fichepatient`;
CREATE TABLE IF NOT EXISTS `fichepatient` (
  `id_fichePatient` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  `num_secu` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `tel` varchar(15) DEFAULT NULL,
  `rue` varchar(100) DEFAULT NULL,
  `cp` varchar(10) DEFAULT NULL,
  `ville` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_fichePatient`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ficheproduit`
--

DROP TABLE IF EXISTS `ficheproduit`;
CREATE TABLE IF NOT EXISTS `ficheproduit` (
  `id_ficheProduit` int NOT NULL AUTO_INCREMENT,
  `libelle` varchar(100) DEFAULT NULL,
  `description` text,
  `niveau_dangerosite` int DEFAULT NULL,
  PRIMARY KEY (`id_ficheProduit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fournisseur`
--

DROP TABLE IF EXISTS `fournisseur`;
CREATE TABLE IF NOT EXISTS `fournisseur` (
  `id_fournisseur` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_fournisseur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `hospitalisation`
--

DROP TABLE IF EXISTS `hospitalisation`;
CREATE TABLE IF NOT EXISTS `hospitalisation` (
  `id_hospitalisation` int NOT NULL AUTO_INCREMENT,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `desc_maladie` text,
  `ref_dossier` int NOT NULL,
  PRIMARY KEY (`id_hospitalisation`),
  KEY `ref_dossier` (`ref_dossier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `occupe`
--

DROP TABLE IF EXISTS `occupe`;
CREATE TABLE IF NOT EXISTS `occupe` (
  `ref_chambre` int NOT NULL,
  `ref_hospitalisation` int NOT NULL,
  PRIMARY KEY (`ref_chambre`,`ref_hospitalisation`),
  KEY `ref_hospitalisation` (`ref_hospitalisation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ordonnance`
--

DROP TABLE IF EXISTS `ordonnance`;
CREATE TABLE IF NOT EXISTS `ordonnance` (
  `id_ordonnance` int NOT NULL AUTO_INCREMENT,
  `dateOrdonnance` date DEFAULT NULL,
  `contenuOrdonnance` text,
  `ref_dossier` int NOT NULL,
  `ref_user` int NOT NULL,
  PRIMARY KEY (`id_ordonnance`),
  KEY `ref_dossier` (`ref_dossier`),
  KEY `ref_user` (`ref_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `peut`
--

DROP TABLE IF EXISTS `peut`;
CREATE TABLE IF NOT EXISTS `peut` (
  `ref_fournisseur` int NOT NULL,
  `ref_ficheProduit` int NOT NULL,
  PRIMARY KEY (`ref_fournisseur`,`ref_ficheProduit`),
  KEY `ref_ficheProduit` (`ref_ficheProduit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
