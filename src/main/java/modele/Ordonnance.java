package modele;

import java.time.LocalDateTime;

public class Ordonnance {
    private int idOrdonnance;
    private int idDossier;
    private LocalDateTime dateOrdonnance;
    private String contenu;

    public Ordonnance(int idOrdonnance, int idDossier, LocalDateTime dateOrdonnance, String contenu) {
        this.idOrdonnance = idOrdonnance;
        this.idDossier = idDossier;
        this.dateOrdonnance = dateOrdonnance;
        this.contenu = contenu;
    }

    public Ordonnance(int idDossier, LocalDateTime dateOrdonnance, String contenu) {
        this.idDossier = idDossier;
        this.dateOrdonnance = dateOrdonnance;
        this.contenu = contenu;
    }

    public int getIdOrdonnance() {
        return idOrdonnance;
    }

    public void setIdOrdonnance(int idOrdonnance) {
        this.idOrdonnance = idOrdonnance;
    }

    public int getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(int idDossier) {
        this.idDossier = idDossier;
    }

    public LocalDateTime getDateOrdonnance() {
        return dateOrdonnance;
    }

    public void setDateOrdonnance(LocalDateTime dateOrdonnance) {
        this.dateOrdonnance = dateOrdonnance;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
