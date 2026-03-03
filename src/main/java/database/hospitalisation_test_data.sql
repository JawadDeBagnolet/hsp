-- Données de test pour la table hospitalisation
INSERT INTO `hospitalisation` (`id_dossier`, `id_chambre`, `date_debut`, `date_fin`, `desc_maladie`) VALUES
(1, 1, '2025-01-15 09:00:00', '2025-01-20 14:00:00', 'Appendicite aiguë - Chirurgie réussie'),
(2, 2, '2025-02-01 10:30:00', NULL, 'Pneumonie - Traitement antibiotique en cours'),
(3, 3, '2025-02-10 08:15:00', '2025-02-15 11:30:00', 'Fracture du fémur - Opération et rééducation'),
(4, 4, '2025-03-01 14:00:00', NULL, 'Diabète décompensé - Équilibrage du traitement'),
(5, 5, '2025-03-05 16:45:00', NULL, 'Infarctus du myocarde - Soins intensifs');

-- Données de test pour la table chambre
INSERT INTO `chambre` (`id_chambre`, `numero_chambre`, `disponible`) VALUES
(1, 101, 1),
(2, 102, 0),
(3, 103, 1),
(4, 104, 0),
(5, 105, 0),
(6, 106, 1),
(7, 107, 1),
(8, 108, 1);

-- Données de test pour la table dossier_charge (nécessaires pour les foreign keys)
INSERT INTO `dossier_charge` (`id_dossier`, `id_patient`, `id_user`, `date_arrivee`, `heure_arrivee`, `symptomes`, `niveau_gravite`) VALUES
(1, 1, 1, '2025-01-15 09:00:00', '2025-01-15 09:00:00', 'Douleur abdominale aiguë côté droit', '3'),
(2, 2, 1, '2025-02-01 10:30:00', '2025-02-01 10:30:00', 'Toux, fièvre, difficultés respiratoires', '2'),
(3, 3, 1, '2025-02-10 08:15:00', '2025-02-10 08:15:00', 'Douleur intense à la jambe après chute', '4'),
(4, 4, 1, '2025-03-01 14:00:00', '2025-03-01 14:00:00', 'Soif intense, fatigue, vision trouble', '3'),
(5, 5, 1, '2025-03-05 16:45:00', '2025-03-05 16:45:00', 'Douleur thoracique intense, essoufflement', '5');

-- Données de test pour la table fiche_patient (nécessaires pour les foreign keys)
INSERT INTO `fiche_patient` (`id_patient`, `nom`, `prenom`, `num_secu`, `email`, `tel`, `rue`, `cp`, `ville`) VALUES
(1, 'Durand', 'Marie', '1850612345678', 'marie.durand@email.com', '0123456789', '15 Rue de la Santé', '75001', 'Paris'),
(2, 'Martin', 'Jean', '2850623456789', 'jean.martin@email.com', '0234567890', '22 Avenue des Hôpitaux', '69000', 'Lyon'),
(3, 'Bernard', 'Sophie', '1850634567890', 'sophie.bernard@email.com', '0345678901', '8 Rue du Médecin', '31000', 'Toulouse'),
(4, 'Petit', 'Robert', '2850645678901', 'robert.petit@email.com', '0456789012', '123 Boulevard de la Cure', '33000', 'Bordeaux'),
(5, 'Lefebvre', 'Isabelle', '1850656789012', 'isabelle.lefebvre@email.com', '0567890123', '5 Impasse de l''Urgence', '44000', 'Nantes');
