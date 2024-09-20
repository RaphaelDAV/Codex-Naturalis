package fr.uge.sae.Corner;

import java.util.Objects;

public class ResourceCorner implements Corner {
	
	String typeRessource;
	
	public ResourceCorner(String typeRessource) {
		Objects.requireNonNull(typeRessource);
		this.typeRessource = typeRessource;
	}
	
	public String getCornerTypeRessource() {
		return typeRessource;
	}
	
	//MÃ©thodes pour reconnnaitre le type de corner
	@Override
	public boolean isRessourceCorner() {
		return true;
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
		return "ResourceCorner [typeRessource=" + typeRessource + "]";
	}

}