package modele;

public class Commande {
    private int idCommande;
    private int idUser;
    private int numCommande;
    private String libelle;

    public Commande(int idCommande, int idUser, int numCommande, String libelle) {
        this.idCommande = idCommande;
        this.idUser = idUser;
        this.numCommande = numCommande;
        this.libelle = libelle;
    }

    public Commande(int idUser, int numCommande, String libelle) {
        this.idUser = idUser;
        this.numCommande = numCommande;
        this.libelle = libelle;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getNumCommande() {
        return numCommande;
    }

    public void setNumCommande(int numCommande) {
        this.numCommande = numCommande;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
