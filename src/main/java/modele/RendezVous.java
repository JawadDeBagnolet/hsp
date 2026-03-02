package modele;

import java.time.LocalDateTime;

public class RendezVous {
    private int idRdv;
    private int idPatient;
    private int idMedecin;
    private LocalDateTime dateHeure;
    private String motif;
    private String statut;
    private String notes;
    
    // Constructeurs
    public RendezVous() {
        this.statut = "PLANIFIE";
    }
    
    public RendezVous(int idPatient, int idMedecin, LocalDateTime dateHeure, String motif) {
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = "PLANIFIE";
    }
    
    public RendezVous(int idRdv, int idPatient, int idMedecin, LocalDateTime dateHeure, String motif, String statut, String notes) {
        this.idRdv = idRdv;
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = statut;
        this.notes = notes;
    }
    
    // Getters et Setters
    public int getIdRdv() {
        return idRdv;
    }
    
    public void setIdRdv(int idRdv) {
        this.idRdv = idRdv;
    }
    
    public int getIdPatient() {
        return idPatient;
    }
    
    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }
    
    public int getIdMedecin() {
        return idMedecin;
    }
    
    public void setIdMedecin(int idMedecin) {
        this.idMedecin = idMedecin;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Méthodes utilitaires
    public String getHeureDebut() {
        if (dateHeure != null) {
            return dateHeure.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }
    
    public String getDateFormatee() {
        if (dateHeure != null) {
            return dateHeure.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
    
    public String getJourSemaine() {
        if (dateHeure != null) {
            return dateHeure.format(java.time.format.DateTimeFormatter.ofPattern("EEEE"));
        }
        return "";
    }
    
    // Vérification si le rendez-vous est dans la semaine actuelle
    public boolean estDansSemaine(java.time.LocalDate debutSemaine) {
        if (dateHeure != null) {
            java.time.LocalDate dateRdv = dateHeure.toLocalDate();
            return !dateRdv.isBefore(debutSemaine) && 
                   !dateRdv.isAfter(debutSemaine.plusDays(6));
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "RendezVous{" +
                "idRdv=" + idRdv +
                ", idPatient=" + idPatient +
                ", idMedecin=" + idMedecin +
                ", dateHeure=" + dateHeure +
                ", motif='" + motif + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
    
    public boolean isValid() {
        return idPatient > 0 && idMedecin > 0 && dateHeure != null && 
               motif != null && !motif.trim().isEmpty();
    }
}
