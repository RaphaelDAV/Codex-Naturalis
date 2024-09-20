package fr.uge.sae.Card;

import java.util.HashMap;
import java.util.List;

import fr.uge.sae.Back.Point;
import fr.uge.sae.Corner.Corner;


public interface Card {
	
	Point getCoordinates();
	Corner getCorner(int i);
	int getPoint();
	String getTypeRessource();
	List<String> getpermanentRessources();
	boolean getRecto();
	public void cornerVerso();
	public void changeToVerso();    
    	public void changeToRecto();
    	public int hashCode();
    	public boolean equals(Object obj);
    	public HashMap<String, Integer> getCost();
	void changeCoordinates(Point point);
	void setEmptyCorner1();
	void setEmptyCorner2();
	void setEmptyCorner3();
	void setEmptyCorner4();
	void toggleSide();
	
}
