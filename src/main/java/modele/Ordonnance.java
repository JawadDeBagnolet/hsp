package modele;

public class Ordonnance {
    private int idOrdonnance;
    private int dateOrdonnance;
    private String contenuOrdonnance;

    public Ordonnance(int idOrdonnance, int dateOrdonnance, String contenuOrdonnance) {
        this.idOrdonnance = idOrdonnance;
        this.dateOrdonnance = dateOrdonnance;
        this.contenuOrdonnance = contenuOrdonnance;
    }


    public Ordonnance( int dateOrdonnance, String contenuOrdonnance) {
        this.dateOrdonnance = dateOrdonnance;
        this.contenuOrdonnance = contenuOrdonnance;
    }

    public int getIdOrdonnance() {
        return idOrdonnance;
    }

    public void setIdOrdonnance(int idOrdonnance) {
        this.idOrdonnance = idOrdonnance;
    }

    public int getDateOrdonnance() {
        return dateOrdonnance;
    }

    public void setDateOrdonnance(int dateOrdonnance) {
        this.dateOrdonnance = dateOrdonnance;
    }

    public String getContenuOrdonnance() {
        return contenuOrdonnance;
    }

    public void setContenuOrdonnance(String contenuOrdonnance) {
        this.contenuOrdonnance = contenuOrdonnance;
    }
}
