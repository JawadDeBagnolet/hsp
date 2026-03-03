-- Table des fournisseurs
DROP TABLE IF EXISTS `fournisseur`;
CREATE TABLE IF NOT EXISTS `fournisseur` (
  `id_fournisseur` int NOT NULL AUTO_INCREMENT,
  `nom_fournisseur` varchar(100) NOT NULL,
  `adresse_fournisseur` varchar(255) DEFAULT NULL,
  `telephone_fournisseur` varchar(20) DEFAULT NULL,
  `email_fournisseur` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_fournisseur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table des produits (si elle n'existe pas déjà)
DROP TABLE IF EXISTS `produit`;
CREATE TABLE IF NOT EXISTS `produit` (
  `id_produit` int NOT NULL AUTO_INCREMENT,
  `nom_produit` varchar(100) NOT NULL,
  `description_produit` text DEFAULT NULL,
  `prix_unitaire` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT 0,
  PRIMARY KEY (`id_produit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Données de test pour les fournisseurs
INSERT INTO `fournisseur` (`nom_fournisseur`, `adresse_fournisseur`, `telephone_fournisseur`, `email_fournisseur`) VALUES
('MediSupply', '123 Rue de la Santé, Paris', '0142551234', 'contact@medisupply.fr'),
('PharmaPlus', '456 Avenue du Médicament, Lyon', '0478567890', 'info@pharmaplus.fr'),
('HospitalDirect', '789 Boulevard des Spécialités, Marseille', '0491234567', 'service@hospitaldirect.fr');

-- Données de test pour les produits
INSERT INTO `produit` (`nom_produit`, `description_produit`, `prix_unitaire`, `stock`) VALUES
('Pansements stériles', 'Pansements stériles de différentes tailles', 2.50, 500),
('Gants en latex', 'Gants médicaux en latex taille M', 0.80, 1000),
('Seringues 10ml', 'Seringues stériles de 10ml', 0.15, 2000),
('Masques chirurgicaux', 'Masques médicaux type IIR', 1.20, 800),
('Compresses froid', 'Compresses froid pour traumatologie', 3.75, 300);
