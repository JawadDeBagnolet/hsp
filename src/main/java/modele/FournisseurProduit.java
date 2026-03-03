package modele;

public class FournisseurProduit {
    private int idFournisseurProduit;
    private int idFournisseur;
    private int idProduit;
    private double prix;

    public FournisseurProduit(int idFournisseurProduit, int idFournisseur, int idProduit, double prix) {
        this.idFournisseurProduit = idFournisseurProduit;
        this.idFournisseur = idFournisseur;
        this.idProduit = idProduit;
        this.prix = prix;
    }

    public FournisseurProduit(int idFournisseur, int idProduit, double prix) {
        this.idFournisseur = idFournisseur;
        this.idProduit = idProduit;
        this.prix = prix;
    }

    public int getIdFournisseurProduit() {
        return idFournisseurProduit;
    }

    public void setIdFournisseurProduit(int idFournisseurProduit) {
        this.idFournisseurProduit = idFournisseurProduit;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}
