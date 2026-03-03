-- Table pour gérer les fournisseurs associés aux produits
DROP TABLE IF EXISTS `fournisseur_produit`;
CREATE TABLE IF NOT EXISTS `fournisseur_produit` (
  `id_fournisseur_produit` int NOT NULL AUTO_INCREMENT,
  `id_fournisseur` int NOT NULL,
  `id_produit` int NOT NULL,
  `prix` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_fournisseur_produit`),
  KEY `idx_fournisseur` (`id_fournisseur`),
  KEY `idx_produit` (`id_produit`),
  CONSTRAINT `fk_fournisseur_produit_fournisseur` FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseur` (`id_fournisseur`) ON DELETE CASCADE,
  CONSTRAINT `fk_fournisseur_produit_produit` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Données de test pour les fournisseurs produits
INSERT INTO `fournisseur_produit` (`id_fournisseur`, `id_produit`, `prix`) VALUES
(1, 1, 15.50),
(1, 2, 25.00),
(2, 1, 14.75),
(2, 3, 8.99),
(3, 2, 22.30),
(3, 4, 12.50);
