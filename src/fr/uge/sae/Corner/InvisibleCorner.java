package fr.uge.sae.Corner;

public class InvisibleCorner implements Corner {
	//Méthodes pour reconnnaitre le type de corner
	@Override
	public boolean isRessourceCorner() {
		return false;
	}
	
	@Override
	public boolean isInvisibleCorner() {
		return true;
	}
	
	@Override
	public boolean isEmptyCorner() {
		return false;
	}
	
	@Override
	public boolean isArtifactCorner() {
		return true;
	}

	@Override
	public String toString() {
		return "InvisibleCorner []";
	}
}