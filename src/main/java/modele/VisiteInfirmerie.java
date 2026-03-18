package modele;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class VisiteInfirmerie {
    private int idVisite;
    private int idEleve;
    private String nomEleve;
    private String prenomEleve;
    private LocalDate dateVisite;
    private LocalTime heureVisite;
    private String motif;
    private String traitement;
    private String statut;
    private Integer idInfirmier;

    public VisiteInfirmerie() {}

    public VisiteInfirmerie(int idEleve, LocalDate dateVisite, LocalTime heureVisite, String motif, String traitement, String statut, Integer idInfirmier) {
        this.idEleve = idEleve;
        this.dateVisite = dateVisite;
        this.heureVisite = heureVisite;
        this.motif = motif;
        this.traitement = traitement;
        this.statut = statut != null ? statut : "Terminée";
        this.idInfirmier = idInfirmier;
    }

    public VisiteInfirmerie(int idVisite, int idEleve, LocalDate dateVisite, LocalTime heureVisite, String motif, String traitement, String statut, Integer idInfirmier) {
        this.idVisite = idVisite;
        this.idEleve = idEleve;
        this.dateVisite = dateVisite;
        this.heureVisite = heureVisite;
        this.motif = motif;
        this.traitement = traitement;
        this.statut = statut != null ? statut : "Terminée";
        this.idInfirmier = idInfirmier;
    }

    public int getIdVisite() { return idVisite; }
    public void setIdVisite(int idVisite) { this.idVisite = idVisite; }

    public int getIdEleve() { return idEleve; }
    public void setIdEleve(int idEleve) { this.idEleve = idEleve; }

    public String getNomEleve() { return nomEleve; }
    public void setNomEleve(String nomEleve) { this.nomEleve = nomEleve; }

    public String getPrenomEleve() { return prenomEleve; }
    public void setPrenomEleve(String prenomEleve) { this.prenomEleve = prenomEleve; }

    public LocalDate getDateVisite() { return dateVisite; }
    public void setDateVisite(LocalDate dateVisite) { this.dateVisite = dateVisite; }

    public LocalTime getHeureVisite() { return heureVisite; }
    public void setHeureVisite(LocalTime heureVisite) { this.heureVisite = heureVisite; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) { this.traitement = traitement; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Integer getIdInfirmier() { return idInfirmier; }
    public void setIdInfirmier(Integer idInfirmier) { this.idInfirmier = idInfirmier; }

    public String getDateFormatee() {
        if (dateVisite != null) {
            return dateVisite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    public String getHeureFormatee() {
        if (heureVisite != null) {
            return heureVisite.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }

    @Override
    public String toString() {
        String eleveInfo = (prenomEleve != null && nomEleve != null)
            ? prenomEleve + " " + nomEleve
            : "Élève #" + idEleve;
        String statutInfo = statut != null ? " | " + statut : "";
        return "ID: " + idVisite + " | " + eleveInfo + " | " + getDateFormatee() + " " + getHeureFormatee() +
               (motif != null && !motif.isEmpty() ? " | " + motif : "") + statutInfo;
    }
}
