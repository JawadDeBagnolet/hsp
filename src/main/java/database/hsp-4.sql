-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Mar 17, 2026 at 11:37 AM
-- Server version: 8.0.40
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hsp`
--

-- --------------------------------------------------------

--
-- Table structure for table `commande`
--

CREATE TABLE `commande` (
  `id_commande` int NOT NULL,
  `id_user` int NOT NULL,
  `numCommande` int NOT NULL,
  `libelle` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commande_produit`
--

CREATE TABLE `commande_produit` (
  `id_commande` int NOT NULL,
  `id_produit` int NOT NULL,
  `qte` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `demande`
--

CREATE TABLE `demande` (
  `id_demande` int NOT NULL,
  `id_user` int NOT NULL,
  `date_demande` datetime NOT NULL,
  `quantite` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `demande`
--

INSERT INTO `demande` (`id_demande`, `id_user`, `date_demande`, `quantite`) VALUES
(1, 1, '2026-03-17 09:15:40', 10),
(3, 1, '2026-03-17 11:51:52', 1),
(4, 1, '2026-03-17 12:21:29', 1);

-- --------------------------------------------------------

--
-- Table structure for table `demande_produit`
--

CREATE TABLE `demande_produit` (
  `id_demande` int NOT NULL,
  `id_produit` int NOT NULL,
  `qte` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `demande_produit`
--

INSERT INTO `demande_produit` (`id_demande`, `id_produit`, `qte`) VALUES
(1, 1, 10),
(3, 1, 1),
(4, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `fiche_eleve`
--

CREATE TABLE `fiche_eleve` (
  `id_eleve` int NOT NULL,
  `nom` varchar(150) NOT NULL,
  `prenom` varchar(150) NOT NULL,
  `num_etudiant` varchar(50) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `tel` varchar(25) NOT NULL,
  `rue` varchar(255) NOT NULL,
  `cp` int NOT NULL,
  `ville` varchar(150) NOT NULL,
  `candidature` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `fiche_eleve`
--

INSERT INTO `fiche_eleve` (`id_eleve`, `nom`, `prenom`, `num_etudiant`, `email`, `tel`, `rue`, `cp`, `ville`, `candidature`) VALUES
(1, 'lak', 'reda', '1234567891234', 'r@r.com', '763456080', '7 rue de afg', 94000, 'creteil', NULL),
(3, 'Lemoine', 'Sébastien', '123', 'test@test.te', 'aze', '', 99, '', NULL),
(4, 'lemoine', 'seb', '0009', '@l.com', '0909090909', '', 99999, 'lol', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `fiche_produit`
--

CREATE TABLE `fiche_produit` (
  `id_produit` int NOT NULL,
  `libelle` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `niveau_dangerosite` int NOT NULL,
  `stock` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `fiche_produit`
--

INSERT INTO `fiche_produit` (`id_produit`, `libelle`, `description`, `niveau_dangerosite`, `stock`) VALUES
(1, 'dolipranes', '500 mg', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `fournisseur`
--

CREATE TABLE `fournisseur` (
  `id_fournisseur` int NOT NULL,
  `nom` varchar(150) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `tel` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `fournisseur`
--

INSERT INTO `fournisseur` (`id_fournisseur`, `nom`, `email`, `tel`) VALUES
(1, 'pharma', 'pharma@gmail.com', 75643790);

-- --------------------------------------------------------

--
-- Table structure for table `fournisseur_produit`
--

CREATE TABLE `fournisseur_produit` (
  `id_fournisseur_produit` int NOT NULL,
  `id_fournisseur` int NOT NULL,
  `id_produit` int NOT NULL,
  `prix` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `fournisseur_produit`
--

INSERT INTO `fournisseur_produit` (`id_fournisseur_produit`, `id_fournisseur`, `id_produit`, `prix`) VALUES
(2, 1, 1, 10.00);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int NOT NULL,
  `nom` varchar(150) NOT NULL,
  `prenom` varchar(150) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` enum('ADMIN','PROF','INFIRMIER','SECRETAIRE','GESTIONNAIRE_DE_STOCK') NOT NULL,
  `mdp` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `nom`, `prenom`, `email`, `role`, `mdp`) VALUES
(1, 'Lakhledj', 'Reda', 'red@gmail.com', 'ADMIN', '9999');

-- --------------------------------------------------------

--
-- Table structure for table `visite_infirmerie`
--

CREATE TABLE `visite_infirmerie` (
  `id_visite` int NOT NULL,
  `id_eleve` int NOT NULL,
  `date_visite` date NOT NULL,
  `heure_visite` time NOT NULL,
  `motif` varchar(500) DEFAULT NULL,
  `id_infirmier` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `visite_infirmerie`
--

INSERT INTO `visite_infirmerie` (`id_visite`, `id_eleve`, `date_visite`, `heure_visite`, `motif`, `id_infirmier`) VALUES
(1, 1, '2026-03-17', '22:00:00', 'sida', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `commande`
--
ALTER TABLE `commande`
  ADD PRIMARY KEY (`id_commande`),
  ADD UNIQUE KEY `uq_commande_num` (`numCommande`),
  ADD KEY `idx_commande_user` (`id_user`);

--
-- Indexes for table `commande_produit`
--
ALTER TABLE `commande_produit`
  ADD PRIMARY KEY (`id_commande`,`id_produit`),
  ADD KEY `fk_cp_produit` (`id_produit`);

--
-- Indexes for table `demande`
--
ALTER TABLE `demande`
  ADD PRIMARY KEY (`id_demande`),
  ADD KEY `idx_demande_user` (`id_user`);

--
-- Indexes for table `demande_produit`
--
ALTER TABLE `demande_produit`
  ADD PRIMARY KEY (`id_demande`,`id_produit`),
  ADD KEY `fk_dp_produit` (`id_produit`);

--
-- Indexes for table `fiche_eleve`
--
ALTER TABLE `fiche_eleve`
  ADD PRIMARY KEY (`id_eleve`),
  ADD UNIQUE KEY `uq_patient_numsecu` (`num_etudiant`);

--
-- Indexes for table `fiche_produit`
--
ALTER TABLE `fiche_produit`
  ADD PRIMARY KEY (`id_produit`);

--
-- Indexes for table `fournisseur`
--
ALTER TABLE `fournisseur`
  ADD PRIMARY KEY (`id_fournisseur`),
  ADD UNIQUE KEY `uq_fournisseur_nom` (`nom`);

--
-- Indexes for table `fournisseur_produit`
--
ALTER TABLE `fournisseur_produit`
  ADD PRIMARY KEY (`id_fournisseur`,`id_produit`),
  ADD UNIQUE KEY `id_fournisseur_produit` (`id_fournisseur_produit`),
  ADD KEY `fk_fp_produit` (`id_produit`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `uq_user_email` (`email`);

--
-- Indexes for table `visite_infirmerie`
--
ALTER TABLE `visite_infirmerie`
  ADD PRIMARY KEY (`id_visite`),
  ADD KEY `id_eleve` (`id_eleve`),
  ADD KEY `id_infirmier` (`id_infirmier`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `commande`
--
ALTER TABLE `commande`
  MODIFY `id_commande` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `demande`
--
ALTER TABLE `demande`
  MODIFY `id_demande` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `fiche_eleve`
--
ALTER TABLE `fiche_eleve`
  MODIFY `id_eleve` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `fiche_produit`
--
ALTER TABLE `fiche_produit`
  MODIFY `id_produit` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `fournisseur`
--
ALTER TABLE `fournisseur`
  MODIFY `id_fournisseur` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `fournisseur_produit`
--
ALTER TABLE `fournisseur_produit`
  MODIFY `id_fournisseur_produit` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `visite_infirmerie`
--
ALTER TABLE `visite_infirmerie`
  MODIFY `id_visite` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `commande`
--
ALTER TABLE `commande`
  ADD CONSTRAINT `fk_commande_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `commande_produit`
--
ALTER TABLE `commande_produit`
  ADD CONSTRAINT `fk_cp_commande` FOREIGN KEY (`id_commande`) REFERENCES `commande` (`id_commande`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `demande`
--
ALTER TABLE `demande`
  ADD CONSTRAINT `fk_demande_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `demande_produit`
--
ALTER TABLE `demande_produit`
  ADD CONSTRAINT `fk_dp_demande` FOREIGN KEY (`id_demande`) REFERENCES `demande` (`id_demande`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_dp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `fournisseur_produit`
--
ALTER TABLE `fournisseur_produit`
  ADD CONSTRAINT `fk_fp_fournisseur` FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseur` (`id_fournisseur`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_fp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `visite_infirmerie`
--
ALTER TABLE `visite_infirmerie`
  ADD CONSTRAINT `visite_infirmerie_ibfk_1` FOREIGN KEY (`id_eleve`) REFERENCES `fiche_eleve` (`id_eleve`) ON DELETE CASCADE,
  ADD CONSTRAINT `visite_infirmerie_ibfk_2` FOREIGN KEY (`id_infirmier`) REFERENCES `user` (`id_user`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
