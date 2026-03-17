package modele;

public class FichePatient {
    private int idFichePatient;
    private String nom;
    private String prenom;
    private String num_etudiant;
    private String email;
    private String tel;
    private String rue;
    private int cp;
    private String ville;
    // null = candidature en cours, 0 = refus, 1 = validé
    private Integer candidature;

    public FichePatient(int idFichePatient, String nom, String prenom, String num_etudiant, String email, String tel, String rue, int cp, String ville, Integer candidature) {
        this.idFichePatient = idFichePatient;
        this.nom = nom;
        this.prenom = prenom;
        this.num_etudiant = num_etudiant;
        this.email = email;
        this.tel = tel;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
        this.candidature = candidature;
    }

    public FichePatient(String nom, String prenom, String num_etudiant, String email, String tel, String rue, int cp, String ville, Integer candidature) {
        this.nom = nom;
        this.prenom = prenom;
        this.num_etudiant = num_etudiant;
        this.email = email;
        this.tel = tel;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
        this.candidature = candidature;
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

    public String getNum_etudiant() {
        return num_etudiant;
    }

    public void setNum_etudiant(String num_etudiant) {
        this.num_etudiant = num_etudiant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
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

    public Integer getCandidature() {
        return candidature;
    }

    public void setCandidature(Integer candidature) {
        this.candidature = candidature;
    }

    public String getCandidatureLibelle() {
        if (candidature == null) return "En cours";
        if (candidature == 0) return "Refusé";
        if (candidature == 1) return "Validé";
        return "Inconnu";
    }
}
