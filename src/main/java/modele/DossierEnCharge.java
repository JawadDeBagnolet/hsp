package modele;

import java.time.LocalDate;
import java.time.LocalTime;

public class DossierEnCharge {
    private int idDossier;
    private LocalDate dateArrivee;
    private LocalTime heureArrivee;
    private String symptomes;
    private int niveauGravite;
    private int refUser;

    public DossierEnCharge(int idDossier, LocalDate dateArrivee, LocalTime heureArrivee, String symptomes, int niveauGravite, int refUser) {
        this.idDossier = idDossier;
        this.dateArrivee = dateArrivee;
        this.heureArrivee = heureArrivee;
        this.symptomes = symptomes;
        this.niveauGravite = niveauGravite;
        this.refUser = refUser;
    }

    public DossierEnCharge(LocalDate dateArrivee, LocalTime heureArrivee, String symptomes, int niveauGravite, int refUser) {
        this.dateArrivee = dateArrivee;
        this.heureArrivee = heureArrivee;
        this.symptomes = symptomes;
        this.niveauGravite = niveauGravite;
        this.refUser = refUser;
    }

    public int getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(int idDossier) {
        this.idDossier = idDossier;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalTime heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public int getNiveauGravite() {
        return niveauGravite;
    }

    public void setNiveauGravite(int niveauGravite) {
        this.niveauGravite = niveauGravite;
    }

    public int getRefUser() {
        return refUser;
    }

    public void setRefUser(int refUser) {
        this.refUser = refUser;
    }

    @Override
    public String toString() {
        return "DossierEnCharge{" +
                "idDossier=" + idDossier +
                ", dateArrivee=" + dateArrivee +
                ", heureArrivee=" + heureArrivee +
                ", symptomes='" + symptomes + '\'' +
                ", niveauGravite=" + niveauGravite +
                ", refUser=" + refUser +
                '}';
    }
}
