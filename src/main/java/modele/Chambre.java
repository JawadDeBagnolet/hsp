package modele;

public class Chambre {

    private int idChambre;
    private int numeroChambre;
    private boolean disponible;

    public Chambre(int idChambre, int numeroChambre, boolean disponible) {
        this.idChambre = idChambre;
        this.numeroChambre = numeroChambre;
        this.disponible = disponible;
    }

    public Chambre(int numeroChambre, boolean disponible) {
        this.numeroChambre = numeroChambre;
        this.disponible = disponible;

    }

    public int getIdChambre() {
        return idChambre;
    }

    public void setIdChambre(int idChambre) {
        this.idChambre = idChambre;
    }

    public int getNumeroChambre() {
        return numeroChambre;
    }

    public void setNumeroChambre(int numeroChambre) {
        this.numeroChambre = numeroChambre;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
