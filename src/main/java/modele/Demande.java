package modele;

import java.time.LocalDateTime;

public class Demande {
    private int idDemande;
    private int idUser;
    private LocalDateTime dateDemande;
    private int quantite;
    private String statut;
    private String motifRefus;

    public Demande(int idDemande, int idUser, LocalDateTime dateDemande, int quantite, String statut) {
        this.idDemande = idDemande;
        this.idUser = idUser;
        this.dateDemande = dateDemande;
        this.quantite = quantite;
        this.statut = statut != null ? statut : "En attente";
    }

    public Demande(int idDemande, int idUser, LocalDateTime dateDemande, int quantite) {
        this(idDemande, idUser, dateDemande, quantite, "En attente");
    }

    public Demande(int idUser, LocalDateTime dateDemande, int quantite) {
        this.idUser = idUser;
        this.dateDemande = dateDemande;
        this.quantite = quantite;
        this.statut = "En attente";
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getStatut() {
        return statut != null ? statut : "En attente";
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotifRefus() { return motifRefus != null ? motifRefus : ""; }
    public void setMotifRefus(String motifRefus) { this.motifRefus = motifRefus; }
}
