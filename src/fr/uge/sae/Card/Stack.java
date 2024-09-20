
package fr.uge.sae.Card;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Back.Point;
import fr.uge.sae.View.CardView;
import fr.umlv.zen5.ApplicationContext;
 
public class Stack {
	private List<Card> ressourceStack;
	private List<Card> goldStack;
	private List<Card> stack;
	private List<Point> coordinatesRessource;
	private List<Point> coordinatesGold;
    
    public Stack() {
        this.ressourceStack = new ArrayList<Card>(); 
        this.goldStack = new ArrayList<Card>(); 
        this.stack = new ArrayList<Card>();
        List<Point> lst = new ArrayList<Point>(List.of(new Point(50, 200), new Point(50, 350)));
        List<Point> lst2 = new ArrayList<Point>(List.of(new Point(50, 750), new Point(50, 900)));
        this.coordinatesRessource = lst;
        this.coordinatesGold = lst2;
    }
    
    //Renvoie la liste des cartes dans la pioches des ressourceCard
    public List<Card> getRessourceStack() {
    	return ressourceStack;
    }
    
    //Renvoie la liste des cartes dans la pioches des goldCard
    public List<Card> getGoldStack() {
    	return goldStack;
    }
    
    //Renvoie la liste des cartes dans les deux pioches
    public List<Card> getStack() {
    	return stack;
    }
    
    //Renvoie la liste des coordonnées des 2 cartes ressources retournés 
    public List<Point> getCoordinatesRessource(){
    	return coordinatesRessource;
    }
    
    //Renvoie la liste des coordonnées des 2 cartes gold retournés 
    public List<Point> getCoordinatesGold(){
    	return coordinatesGold;
    }
    
 // Affiche toutes les cartes de la pioche des ResourceCard
    public void showRessourceStack(ApplicationContext context) {
    	Objects.requireNonNull(context);
        showCardStack(context, ressourceStack, coordinatesRessource, 0.043f, "ressource");
    }

    // Affiche toutes les cartes de la pioche des GoldCard
    public void showGoldStack(ApplicationContext context) {
    	Objects.requireNonNull(context);
        showCardStack(context, goldStack, coordinatesGold, 0.55f, "gold");
    }

    // Méthode générique pour afficher une pile de cartes
    private void showCardStack(ApplicationContext context, List<Card> stack, List<Point> coordinates, float heightMultiplier, String stackType) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(stack);
        Objects.requireNonNull(coordinates);
        Objects.requireNonNull(stackType);
        var screenInfo = context.getScreenInfo();
        var width = (screenInfo.getWidth() * 0.144f);
        var height = (screenInfo.getHeight() * heightMultiplier);
        if (stack.size() > 2) {
            Card card = stack.get(stack.size() - 3);
            card.changeToVerso();
            card.cornerVerso();
            context.renderFrame(graphics -> {
            	graphics.setColor(Color.DARK_GRAY);
            	graphics.fill(new RoundRectangle2D.Float((width * 0.182f)-10, height, CardView.widthFloat+10, CardView.heightFloat+10, 25, 25));
            });
            CardView.showCardByPosition(context, card, new Point(width * 0.182f, height));
        }
        for (int i = 0; i < Math.min(stack.size(), 2); i++) {
            Card card = stack.get(stack.size() - 1 - i);
            Point point = coordinates.get(i);
            if (i <= 1) {
                card.changeToRecto();
            }
            CardView.showCardByPosition(context, card, point);
        }
    }

  //Retirer une carte d'un deck
    public void remove(Card card){
    	Objects.requireNonNull(card);
    	if(card instanceof ResourceCard) {
    		ressourceStack.remove(card);
    	} else if(card instanceof GoldCard){
    		goldStack.remove(card);
    	}
    	stack.remove(card);
    	
    }
    
    public void showStack(ApplicationContext context) {
    	Objects.requireNonNull(context);
    	showRessourceStack(context);
    	showGoldStack(context);
    }
    
    
    
  //Ajouter une carte dans un deck
    public void add(Card card){
    	Objects.requireNonNull(card);
    	if(card instanceof ResourceCard) {
    		ressourceStack.add(card);
    	} else if(card instanceof GoldCard){
    		goldStack.add(card);
    	}
    	stack.add(card);
    }
    
 // Vérifier si une carte est présente dans le deck
    public boolean contains(Card card) {
    	Objects.requireNonNull(card);
        return ressourceStack.contains(card) || goldStack.contains(card);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Card card : stack) {
			sb.append(card + "\n");
		}
		return sb.toString();
	}

	
    
    
}