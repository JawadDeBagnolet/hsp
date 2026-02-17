package modele;

public class Produit {
    private int idProduit;
    private String nom;
    private String description;
    private double prix;
    private int quantite;
    private int idFournisseur;
    
    public Produit(int idProduit, String nom, String description, double prix, int quantite, int idFournisseur) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.idFournisseur = idFournisseur;
    }
    
    public Produit(String nom, String description, double prix, int quantite, int idFournisseur) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.idFournisseur = idFournisseur;
    }
    
    // Getters et Setters
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public int getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }
    
    @Override
    public String toString() {
        return "Produit{" +
                "idProduit=" + idProduit +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", idFournisseur=" + idFournisseur +
                '}';
    }
}
