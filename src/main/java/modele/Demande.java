package modele;

public class Demande {
    private int idDemande;
    private int dateDemande;
    private boolean statut;
    private int quantite;

    public Demande(int idDemande, int dateDemande, boolean statut, int quantite) {
        this.idDemande = idDemande;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.quantite = quantite;
    }
    public Demande(int dateDemande, boolean statut, int quantite) {
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.quantite = quantite;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(int dateDemande) {
        this.dateDemande = dateDemande;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
