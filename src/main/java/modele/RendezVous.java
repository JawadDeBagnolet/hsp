package modele;

import java.time.LocalDateTime;

public class RendezVous {
    private int idRdv;
    private int idEleve;
    private int idProf;
    private LocalDateTime dateHeure;
    private String motif;
    private String statut;
    private String notes;

    // Constructeurs
    public RendezVous() {
        this.statut = "PLANIFIE";
    }

    public RendezVous(int idEleve, int idProf, LocalDateTime dateHeure, String motif) {
        this.idEleve = idEleve;
        this.idProf = idProf;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = "PLANIFIE";
    }

    public RendezVous(int idRdv, int idEleve, int idProf, LocalDateTime dateHeure, String motif, String statut, String notes) {
        this.idRdv = idRdv;
        this.idEleve = idEleve;
        this.idProf = idProf;
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

    public int getIdEleve() {
        return idEleve;
    }

    public void setIdEleve(int idEleve) {
        this.idEleve = idEleve;
    }

    public int getIdProf() {
        return idProf;
    }

    public void setIdProf(int idProf) {
        this.idProf = idProf;
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
                ", idEleve=" + idEleve +
                ", idProf=" + idProf +
                ", dateHeure=" + dateHeure +
                ", motif='" + motif + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
    
    public boolean isValid() {
        return idEleve > 0 && idProf > 0 && dateHeure != null &&
               motif != null && !motif.trim().isEmpty();
    }
}
