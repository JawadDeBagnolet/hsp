package modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    public static final String STATUT_ATTENTE    = "En attente infirmerie";
    public static final String STATUT_RETOUR     = "Retour en cours";
    public static final String STATUT_EA         = "En attente des parents";
    public static final String STATUT_MAISON     = "Retour maison";

    private int idTicket;
    private int idEleve;
    private int idSecretaire;
    private LocalDateTime dateCreation;
    private String motif;
    private String statut;
    private String prescription;

    // Champs dénormalisés pour l'affichage
    private String nomEleve;

    public Ticket(int idTicket, int idEleve, int idSecretaire,
                  LocalDateTime dateCreation, String motif, String statut) {
        this.idTicket = idTicket;
        this.idEleve = idEleve;
        this.idSecretaire = idSecretaire;
        this.dateCreation = dateCreation;
        this.motif = motif;
        this.statut = statut;
    }

    public int getIdTicket()           { return idTicket; }
    public int getIdEleve()            { return idEleve; }
    public int getIdSecretaire()       { return idSecretaire; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public String getMotif()           { return motif; }
    public String getStatut()               { return statut; }
    public void setStatut(String s)         { this.statut = s; }
    public String getPrescription()         { return prescription; }
    public void setPrescription(String p)   { this.prescription = p; }
    public String getNomEleve()             { return nomEleve; }
    public void setNomEleve(String n)       { this.nomEleve = n; }

    @Override
    public String toString() {
        String date = dateCreation != null
                ? dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
        String eleve = nomEleve != null ? nomEleve : "Élève #" + idEleve;
        return "#" + idTicket + " – " + eleve + " – " + date + " – " + statut;
    }
}
