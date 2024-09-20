package fr.uge.sae.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Back.Point;
import fr.uge.sae.Corner.Corner;
import fr.uge.sae.Corner.EmptyCorner;

public class ResourceCard implements Card {
    private final int point; // Points associés à la carte
    private final String typeRessource; // Type de ressource de la carte
    private Corner corner1; // Premier coin de la carte
    private Corner corner2; // Deuxième coin de la carte
    private Corner corner3; // Troisième coin de la carte
    private Corner corner4; // Quatrième coin de la carte
    private Point coordinates; // Coordonnées de la carte
    private final List<String> permanentRessources; // Ressources permanentes associées à la carte
    private boolean recto; // Indicateur si la carte est recto

    private Corner C1; // Sauvegarde du premier coin (pour restauration)
    private Corner C2; // Sauvegarde du deuxième coin (pour restauration)
    private Corner C3; // Sauvegarde du troisième coin (pour restauration)
    private Corner C4; // Sauvegarde du quatrième coin (pour restauration)

    // Constructeur de ResourceCard
    public ResourceCard(int point, String typeRessource, Corner corner1, Corner corner2, Corner corner3, Corner corner4, Point coordinates, List<String> permanentRessources, boolean recto) {
        Objects.requireNonNull(typeRessource);
        Objects.requireNonNull(corner1);
        Objects.requireNonNull(corner2);
        Objects.requireNonNull(corner3);
        Objects.requireNonNull(corner4);

        this.point = point;
        this.typeRessource = typeRessource;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.corner3 = corner3;
        this.corner4 = corner4;
        this.coordinates = coordinates;
        this.permanentRessources = permanentRessources;
        this.recto = recto;

        C1 = corner1;
        C2 = corner2;
        C3 = corner3;
        C4 = corner4;
    }

    // Implémentation des méthodes de l'interface Card

    // Définit le premier coin comme étant vide
    @Override
    public void setEmptyCorner1() {
        this.corner1 = new EmptyCorner();
    }

    // Définit le deuxième coin comme étant vide
    @Override
    public void setEmptyCorner2() {
        this.corner2 = new EmptyCorner();
    }

    // Définit le troisième coin comme étant vide
    @Override
    public void setEmptyCorner3() {
        this.corner3 = new EmptyCorner();
    }

    // Définit le quatrième coin comme étant vide
    @Override
    public void setEmptyCorner4() {
        this.corner4 = new EmptyCorner();
    }

    // Change les coordonnées de la carte
    @Override
    public void changeCoordinates(Point point) {
        this.coordinates = point;
    }

    // Retourne les coordonnées de la carte
    @Override
    public Point getCoordinates() {
        return coordinates;
    }
    
    // Retourne le coin de la carte demandé
    @Override
    public Corner getCorner(int i) {
        switch (i) {
            case 1:
                return corner1;
            case 2:
                return corner2;
            case 3:
                return corner3;
            case 4:
                return corner4;
            default:
                throw new IllegalArgumentException("Invalid corner index: " + i);
        }
    }
    
    

    // Retourne les points de la carte
    @Override
    public int getPoint() {
        return point;
    }

    // Retourne le type de ressource de la carte
    @Override
    public String getTypeRessource() {
        return typeRessource;
    }

    // Retourne les ressources permanentes de la carte
    @Override
    public List<String> getpermanentRessources() {
        return permanentRessources;
    }

    // Retourne si la carte est recto
    @Override
    public boolean getRecto() {
        return recto;
    }

    // Définit les coins en mode verso si la carte n'est pas recto
    public void cornerVerso() {
        if (!recto) {
            corner1 = new EmptyCorner();
            corner2 = new EmptyCorner();
            corner3 = new EmptyCorner();
            corner4 = new EmptyCorner();
        }
    }

    // Change l'état de la carte en verso
    public void changeToVerso() {
        recto = false;
    }
    
  //Echange le coté de la carte
    public void toggleSide() {
    	if (getRecto()) { 
    		changeToVerso();
    	} else {
    		changeToRecto();
    	} 
    }
    
    // Change l'état de la carte en recto et restaure les coins originaux
    public void changeToRecto() {
        recto = true;
        corner1 = this.C1;
        corner2 = this.C2;
        corner3 = this.C3;
        corner4 = this.C4;
    }

    // Retourne une représentation en chaîne de caractères de la carte
    @Override
    public String toString() {
        return "ResourceCard [point=" + point + ", typeRessource=" + typeRessource + ", corner1=" + corner1
                + ", corner2=" + corner2 + ", corner3=" + corner3 + ", corner4=" + corner4 + ", coordinates="
                + coordinates + ", permanentRessources=" + permanentRessources + ", recto=" + recto + "]";
    }

    // Génère un code de hachage pour la carte
    @Override
    public int hashCode() {
        return Objects.hash(coordinates, corner1, corner2, corner3, corner4, permanentRessources, point, recto, typeRessource);
    }

    // Vérifie si deux cartes sont égales
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ResourceCard))
            return false;
        ResourceCard other = (ResourceCard) obj;
        return Objects.equals(coordinates, other.coordinates) && Objects.equals(corner1, other.corner1)
                && Objects.equals(corner2, other.corner2) && Objects.equals(corner3, other.corner3)
                && Objects.equals(corner4, other.corner4) && Objects.equals(permanentRessources, other.permanentRessources)
                && point == other.point && recto == other.recto && Objects.equals(typeRessource, other.typeRessource);
    }

    // Retourne le coût de la carte (null pour ResourceCard)
    @Override
    public HashMap<String, Integer> getCost() {
        return null;
    }
}
