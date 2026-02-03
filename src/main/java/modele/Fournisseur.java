package modele;

public class Fournisseur {
    private int idFournisseur;
    private String nom;
    private String email;
    private int tel;

    public Fournisseur(int idFournisseur, String nom, String email, int tel) {
        this.idFournisseur = idFournisseur;
        this.nom = nom;
        this.email = email;
        this.tel = tel;
    }

    public Fournisseur(String nom, String email, int tel) {
        this.nom = nom;
        this.email = email;
        this.tel = tel;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }
}
