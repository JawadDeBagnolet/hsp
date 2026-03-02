package modele;

import java.time.LocalDateTime;

public class Demande {
    private int idDemande;
    private int idUser;
    private LocalDateTime dateDemande;
    private int quantite;

    public Demande(int idDemande, int idUser, LocalDateTime dateDemande, int quantite) {
        this.idDemande = idDemande;
        this.idUser = idUser;
        this.dateDemande = dateDemande;
        this.quantite = quantite;
    }

    public Demande(int idUser, LocalDateTime dateDemande, int quantite) {
        this.idUser = idUser;
        this.dateDemande = dateDemande;
        this.quantite = quantite;
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
}
