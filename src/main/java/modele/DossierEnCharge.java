package modele;

import java.time.LocalDate;

public class DossierEnCharge {
    private int idDossier;
    private int idEleve;
    private String antecedents;
    private String allergies;
    private String traitementsChroniques;
    private LocalDate dateCreation;

    public DossierEnCharge(int idEleve, String antecedents, String allergies, String traitementsChroniques) {
        this.idEleve = idEleve;
        this.antecedents = antecedents != null ? antecedents : "";
        this.allergies = allergies != null ? allergies : "";
        this.traitementsChroniques = traitementsChroniques != null ? traitementsChroniques : "";
        this.dateCreation = LocalDate.now();
    }

    public DossierEnCharge(int idDossier, int idEleve, String antecedents, String allergies, String traitementsChroniques, LocalDate dateCreation) {
        this.idDossier = idDossier;
        this.idEleve = idEleve;
        this.antecedents = antecedents != null ? antecedents : "";
        this.allergies = allergies != null ? allergies : "";
        this.traitementsChroniques = traitementsChroniques != null ? traitementsChroniques : "";
        this.dateCreation = dateCreation;
    }

    public int getIdDossier() { return idDossier; }
    public void setIdDossier(int idDossier) { this.idDossier = idDossier; }

    public int getIdEleve() { return idEleve; }
    public void setIdEleve(int idEleve) { this.idEleve = idEleve; }

    public String getAntecedents() { return antecedents; }
    public void setAntecedents(String antecedents) { this.antecedents = antecedents; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getTraitementsChroniques() { return traitementsChroniques; }
    public void setTraitementsChroniques(String traitementsChroniques) { this.traitementsChroniques = traitementsChroniques; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}
