package modele;

public class Hospitalisation {
    private int idHospitalisation;
    private int dateDebut;
    private int dateFin;
    private String desc_maladie;

    public Hospitalisation(int idHospitalisation, int dateDebut, int dateFin, String desc_maladie) {
        this.idHospitalisation = idHospitalisation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.desc_maladie = desc_maladie;
    }

    public Hospitalisation( int dateDebut, int dateFin, String desc_maladie) {
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

    public int getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(int dateDebut) {
        this.dateDebut = dateDebut;
    }

    public int getDateFin() {
        return dateFin;
    }

    public void setDateFin(int dateFin) {
        this.dateFin = dateFin;
    }

    public String getDesc_maladie() {
        return desc_maladie;
    }

    public void setDesc_maladie(String desc_maladie) {
        this.desc_maladie = desc_maladie;
    }
}
