package fr.uge.sae.Corner;

public class EmptyCorner implements Corner{
	//Méthodes pour reconnnaitre le type de corner
	@Override
	public boolean isRessourceCorner() {
		return false;
	}
	
	@Override
	public boolean isInvisibleCorner() {
		return false;
	}
	
	@Override
	public boolean isEmptyCorner() {
		return true;
	}
	
	@Override
	public String toString() {
		return "EmptyCorner []";
	}

	@Override
	public boolean isArtifactCorner() {
		return true;
	}
}