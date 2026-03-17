package modele;

import java.time.LocalDateTime;

public class Commande {
    private int idCommande;
    private int idUser;
    private int numCommande;
    private String libelle;
    private int idFournisseur;
    private LocalDateTime dateCommande;
    private String statut;

    public Commande(int idCommande, int idUser, int numCommande, String libelle,
                    int idFournisseur, LocalDateTime dateCommande, String statut) {
        this.idCommande = idCommande;
        this.idUser = idUser;
        this.numCommande = numCommande;
        this.libelle = libelle;
        this.idFournisseur = idFournisseur;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    /** Constructeur pour la création (sans id ni date) */
    public Commande(int idUser, int numCommande, String libelle, int idFournisseur) {
        this.idUser = idUser;
        this.numCommande = numCommande;
        this.libelle = libelle;
        this.idFournisseur = idFournisseur;
        this.statut = "En attente";
    }

    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getNumCommande() { return numCommande; }
    public void setNumCommande(int numCommande) { this.numCommande = numCommande; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public int getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
