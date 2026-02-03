package modele;

public class DossierEnCharge {
    private int idDossier;
    private int dateArrivee;
    private int heureArrivee;
    private String symptomes;
    private int nivGravite;

    public DossierEnCharge(int idDossier, int dateArrivee, int heureArrivee, String symptomes, int nivGravite) {
        this.idDossier = idDossier;
        this.dateArrivee = dateArrivee;
        this.heureArrivee = heureArrivee;
        this.symptomes = symptomes;
        this.nivGravite = nivGravite;
    }

    public DossierEnCharge( int dateArrivee, int heureArrivee, String symptomes, int nivGravite) {
        this.dateArrivee = dateArrivee;
        this.heureArrivee = heureArrivee;
        this.symptomes = symptomes;
        this.nivGravite = nivGravite;
    }

    public int getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(int idDossier) {
        this.idDossier = idDossier;
    }

    public int getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(int dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public int getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(int heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public int getNivGravite() {
        return nivGravite;
    }

    public void setNivGravite(int nivGravite) {
        this.nivGravite = nivGravite;
    }
}
