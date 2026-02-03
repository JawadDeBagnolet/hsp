package modele;

public class FicheProduit {
    private int idProduit;
    private String libelle;
    private String description;
    private int nivDangerosite;
    private int stockActuel;

    public FicheProduit(int idProduit, String libelle, String description, int nivDangerosite, int stockActuel) {
        this.idProduit = idProduit;
        this.libelle = libelle;
        this.description = description;
        this.nivDangerosite = nivDangerosite;
        this.stockActuel = stockActuel;
    }

    public FicheProduit(String libelle, String description, int nivDangerosite, int stockActuel) {
        this.libelle = libelle;
        this.description = description;
        this.nivDangerosite = nivDangerosite;
        this.stockActuel = stockActuel;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNivDangerosite() {
        return nivDangerosite;
    }

    public void setNivDangerosite(int nivDangerosite) {
        this.nivDangerosite = nivDangerosite;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }
}

