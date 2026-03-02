package modele;

import java.time.LocalDateTime;

public class Hospitalisation {
    private int idHospitalisation;
    private int idDossier;
    private int idChambre;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String desc_maladie;

    public Hospitalisation(int idHospitalisation, int idDossier, int idChambre, LocalDateTime dateDebut, LocalDateTime dateFin, String desc_maladie) {
        this.idHospitalisation = idHospitalisation;
        this.idDossier = idDossier;
        this.idChambre = idChambre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.desc_maladie = desc_maladie;
    }

    public Hospitalisation(int idDossier, int idChambre, LocalDateTime dateDebut, LocalDateTime dateFin, String desc_maladie) {
        this.idDossier = idDossier;
        this.idChambre = idChambre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.desc_maladie = desc_maladie;
    }

    public int getIdHospitalisation() {
        return idHospitalisation;
    }

    public void setIdHospitalisation(int idHospitalisation) {
        this.idHospitalisation = idHospitalisation;
    }

    public int getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(int idDossier) {
        this.idDossier = idDossier;
    }

    public int getIdChambre() {
        return idChambre;
    }

    public void setIdChambre(int idChambre) {
        this.idChambre = idChambre;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getDesc_maladie() {
        return desc_maladie;
    }

    public void setDesc_maladie(String desc_maladie) {
        this.desc_maladie = desc_maladie;
    }
}
