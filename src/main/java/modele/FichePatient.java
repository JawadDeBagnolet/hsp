package modele;

public class FichePatient {
    private int idFichePatient;
    private String nom;
    private String prenom;
    private int numSecu;
    private String email;
    private int tel;
    private String rue;
    private int cp;
    private String ville;

    public FichePatient(int idFichePatient, String nom, String prenom, int numSecu, String email, int tel, String rue, int cp, String ville) {
        this.idFichePatient = idFichePatient;
        this.nom = nom;
        this.prenom = prenom;
        this.numSecu = numSecu;
        this.email = email;
        this.tel = tel;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
    }
    public FichePatient(String nom, String prenom, int numSecu, String email, int tel, String rue, int cp, String ville) {
        this.nom = nom;
        this.prenom = prenom;
        this.numSecu = numSecu;
        this.email = email;
        this.tel = tel;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
    }

    public int getIdFichePatient() {
        return idFichePatient;
    }

    public void setIdFichePatient(int idFichePatient) {
        this.idFichePatient = idFichePatient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNumSecu() {
        return numSecu;
    }

    public void setNumSecu(int numSecu) {
        this.numSecu = numSecu;
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

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}
