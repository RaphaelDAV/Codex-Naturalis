package fr.uge.sae.Corner;

import java.util.Objects;

public class ArtifactCorner implements Corner{
	private final String typeArtifact;
	
	public ArtifactCorner(String typeArtifact) {
		Objects.requireNonNull(typeArtifact);
		this.typeArtifact = typeArtifact;
	}
	
	public String getTypeArtifact() {
		return typeArtifact;
	}

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
		return false;
	}
	
	@Override
	public boolean isArtifactCorner() {
		return true;
	}

	@Override
	public String toString() {
		return "ArtifactCorner [typeArtifact=" + typeArtifact + "]";
	}

}