package fr.uge.sae.Corner;

public interface Corner {

	//Methode pour reconnaitre le type de corner
	boolean isRessourceCorner();
	boolean isInvisibleCorner();
	boolean isEmptyCorner();
	boolean isArtifactCorner();
}