-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Mar 10, 2026 at 10:38 AM
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
-- Table structure for table `chambre`
--

CREATE TABLE `chambre` (
  `id_chambre` int NOT NULL,
  `numero_chambre` int NOT NULL,
  `disponible` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `demande_produit`
--

CREATE TABLE `demande_produit` (
  `id_demande` int NOT NULL,
  `id_produit` int NOT NULL,
  `qte` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dossier_charge`
--

CREATE TABLE `dossier_charge` (
  `id_dossier` int NOT NULL,
  `id_patient` int NOT NULL,
  `id_user` int NOT NULL,
  `date_arrivee` datetime NOT NULL,
  `heure_arrivee` datetime NOT NULL,
  `symptomes` varchar(255) NOT NULL,
  `niveau_gravite` enum('1','2','3','4','5') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `dossier_charge`
--

INSERT INTO `dossier_charge` (`id_dossier`, `id_patient`, `id_user`, `date_arrivee`, `heure_arrivee`, `symptomes`, `niveau_gravite`) VALUES
(3, 1, 1, '2026-03-02 23:00:00', '2026-03-03 14:45:00', 'toux', '1'),
(4, 1, 1, '2026-03-01 23:00:00', '2026-03-02 14:50:00', 'toux', '4');

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
(1, 'lak', 'reda', '1234567891234', 'r@r.com', '763456080', '7 rue de afg', 94000, 'creteil', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `fiche_produit`
--

CREATE TABLE `fiche_produit` (
  `id_produit` int NOT NULL,
  `libelle` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `niveau_dangerosite` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fournisseur`
--

CREATE TABLE `fournisseur` (
  `id_fournisseur` int NOT NULL,
  `nom` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fournisseur_produit`
--

CREATE TABLE `fournisseur_produit` (
  `id_fournisseur` int NOT NULL,
  `id_produit` int NOT NULL,
  `prix` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hospitalisation`
--

CREATE TABLE `hospitalisation` (
  `id_hospitalisation` int NOT NULL,
  `id_dossier` int NOT NULL,
  `id_chambre` int NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime DEFAULT NULL,
  `desc_maladie` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ordonnance`
--

CREATE TABLE `ordonnance` (
  `id_ordonnance` int NOT NULL,
  `id_dossier` int NOT NULL,
  `date_ordonnance` datetime NOT NULL,
  `contenu` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rendez_vous`
--

CREATE TABLE `rendez_vous` (
  `id_rdv` int NOT NULL,
  `id_eleve` int NOT NULL,
  `id_prof` int NOT NULL,
  `date_heure` datetime NOT NULL,
  `motif` varchar(500) NOT NULL,
  `statut` varchar(50) NOT NULL DEFAULT 'PLANIFIE',
  `notes` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
-- Indexes for dumped tables
--

--
-- Indexes for table `chambre`
--
ALTER TABLE `chambre`
  ADD PRIMARY KEY (`id_chambre`),
  ADD UNIQUE KEY `uq_chambre_numero` (`numero_chambre`);

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
-- Indexes for table `dossier_charge`
--
ALTER TABLE `dossier_charge`
  ADD PRIMARY KEY (`id_dossier`),
  ADD KEY `idx_dossier_patient` (`id_patient`),
  ADD KEY `idx_dossier_user` (`id_user`);

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
  ADD KEY `fk_fp_produit` (`id_produit`);

--
-- Indexes for table `hospitalisation`
--
ALTER TABLE `hospitalisation`
  ADD PRIMARY KEY (`id_hospitalisation`),
  ADD UNIQUE KEY `uq_hospit_dossier` (`id_dossier`),
  ADD KEY `idx_hospit_chambre` (`id_chambre`);

--
-- Indexes for table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD PRIMARY KEY (`id_ordonnance`),
  ADD KEY `idx_ordonnance_dossier` (`id_dossier`);

--
-- Indexes for table `rendez_vous`
--
ALTER TABLE `rendez_vous`
  ADD PRIMARY KEY (`id_rdv`),
  ADD KEY `id_eleve` (`id_eleve`),
  ADD KEY `id_prof` (`id_prof`);

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
-- AUTO_INCREMENT for table `chambre`
--
ALTER TABLE `chambre`
  MODIFY `id_chambre` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `commande`
--
ALTER TABLE `commande`
  MODIFY `id_commande` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `demande`
--
ALTER TABLE `demande`
  MODIFY `id_demande` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `dossier_charge`
--
ALTER TABLE `dossier_charge`
  MODIFY `id_dossier` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `fiche_eleve`
--
ALTER TABLE `fiche_eleve`
  MODIFY `id_eleve` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `fiche_produit`
--
ALTER TABLE `fiche_produit`
  MODIFY `id_produit` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `fournisseur`
--
ALTER TABLE `fournisseur`
  MODIFY `id_fournisseur` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `hospitalisation`
--
ALTER TABLE `hospitalisation`
  MODIFY `id_hospitalisation` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ordonnance`
--
ALTER TABLE `ordonnance`
  MODIFY `id_ordonnance` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rendez_vous`
--
ALTER TABLE `rendez_vous`
  MODIFY `id_rdv` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `visite_infirmerie`
--
ALTER TABLE `visite_infirmerie`
  MODIFY `id_visite` int NOT NULL AUTO_INCREMENT;

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
-- Constraints for table `dossier_charge`
--
ALTER TABLE `dossier_charge`
  ADD CONSTRAINT `fk_dossier_patient` FOREIGN KEY (`id_patient`) REFERENCES `fiche_eleve` (`id_eleve`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_dossier_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `fournisseur_produit`
--
ALTER TABLE `fournisseur_produit`
  ADD CONSTRAINT `fk_fp_fournisseur` FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseur` (`id_fournisseur`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_fp_produit` FOREIGN KEY (`id_produit`) REFERENCES `fiche_produit` (`id_produit`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `hospitalisation`
--
ALTER TABLE `hospitalisation`
  ADD CONSTRAINT `fk_hospit_chambre` FOREIGN KEY (`id_chambre`) REFERENCES `chambre` (`id_chambre`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_hospit_dossier` FOREIGN KEY (`id_dossier`) REFERENCES `dossier_charge` (`id_dossier`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD CONSTRAINT `fk_ordonnance_dossier` FOREIGN KEY (`id_dossier`) REFERENCES `dossier_charge` (`id_dossier`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `rendez_vous`
--
ALTER TABLE `rendez_vous`
  ADD CONSTRAINT `rendez_vous_ibfk_1` FOREIGN KEY (`id_eleve`) REFERENCES `fiche_eleve` (`id_eleve`) ON DELETE CASCADE,
  ADD CONSTRAINT `rendez_vous_ibfk_2` FOREIGN KEY (`id_prof`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

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
