package modele;

public class Commande {
    private int idCommande;
    private int numCommande;
    private String libelle;

    public Commande(int idCommande, int numCommande, String libelle) {
        this.idCommande = idCommande;
        this.numCommande = numCommande;
        this.libelle = libelle;
    }

    public Commande(int numCommande, String libelle) {
        this.numCommande = numCommande;
        this.libelle = libelle;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
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
