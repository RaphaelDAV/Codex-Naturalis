package fr.uge.sae.Back;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Card.Card;
import fr.uge.sae.Card.Stack;
import fr.uge.sae.Player.Player;
import fr.uge.sae.View.BoardView;
import fr.uge.sae.View.CardView;
import fr.uge.sae.View.Menu;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;

public class Game {
	
	
/*__________________________________________________________________GESTION DE LA MAIN__________________________________________________________________*/	
	
	//Définit les variables utiles pour la gestion des cartes dans la main
	
	private final static float WIDTH_CARD = CardView.widthFloat;
	private final static float HEIGHT_CARD = CardView.heightFloat;
	private static int xOffset = (int) WIDTH_CARD + 50; // Décalage horizontal entre les cartes
	private static int yOffset = 0;
	

	//Choisir le coté des cartes de la main
	public static void chooseHandSide(ApplicationContext context, Event event, Player player) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(event);
		Objects.requireNonNull(player);	
		var location = event.getLocation();    
        var screenInfo = context.getScreenInfo();
        var width = (screenInfo.getWidth() * (0.27f));
	    var height = (screenInfo.getHeight() * (0.84f));
        Point initialPoint = new Point(width, height);      
        int yOffset = 0;
        for (int i = 0; i < player.getHand().size(); i++) {
            Point position = new Point((float) initialPoint.getX() + i * xOffset + (WIDTH_CARD-200)/2, (float) (initialPoint.getY()+20 + i * yOffset) + 115); //Gère l'endroit du bouton
            Card card = player.getHand().get(i);
            if (location.x >= position.getX() && location.x <= position.getX() + 200 && location.y >= position.getY() && location.y <= position.getY() + 40) { //Reconnait si l'on clique sur un bouton        	
                card.toggleSide(); //Echange le coté de la carte        
                 //Affiche la main avec la carte retournée
                BoardView.showHand(context, player);
                break; // Sortir de la boucle après avoir trouvé la carte correspondante
            }
        }

	}
	
	
	
	//Permet de choisir une carte dans notre main
	public static int chooseCardInHand(ApplicationContext context, Event event, List<Card> hand) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(event);
		Objects.requireNonNull(hand);
		
		var location = event.getLocation();
	    
        var screenInfo = context.getScreenInfo();
        var width = (screenInfo.getWidth() * (0.27f));
	    var height = (screenInfo.getHeight() * (0.84f));
        Point initialPoint = new Point(width, height);

        for (int i = 0; i < hand.size(); i++) {
            Point position = new Point(initialPoint.getX() + i * xOffset, initialPoint.getY() + i * yOffset); //Définit la position de la carte sur l'écran

            if (location.x >= position.getX() && location.x <= position.getX() + (float)  WIDTH_CARD && location.y >= position.getY() && location.y <= position.getY() + (float)  HEIGHT_CARD) { //Vérifie qu'on a choisi une carte sur l'écran
            	BoardView.highlightCardInHand(context, position); //Surligne la carte selectionnée
                return i+1; // Renvoit le numéro de la carte choisie 
            }
        }
        return 0;
	}
	
	
	
	
/*__________________________________________________________________VERIFICATIONS DE POINTAGE__________________________________________________________________*/	
	
	// Verifie si l'on pointe une carte dans la main (hand)
		public static boolean IsCardSelected(ApplicationContext context, Event event, List<Card> hand) {
		    Objects.requireNonNull(context);
		    Objects.requireNonNull(event);
		    Objects.requireNonNull(hand);
		    
		    // Récupération de la position de l'événement
		    var location = event.getLocation();
		    var screenInfo = context.getScreenInfo();
		    var width = (screenInfo.getWidth() * (0.27f));
		    var height = (screenInfo.getHeight() * (0.84f));
		    Point initialPoint = new Point(width, height);
	  
		    for (int i = 0; i < hand.size(); i++) {// Parcours de la main
		        Point position = new Point((float) initialPoint.getX() + i * xOffset, (float) (initialPoint.getY() + i * yOffset));
		        if (location.x >= position.getX() && location.x <= position.getX() + (float) WIDTH_CARD && // Vérification si la position de l'événement est dans la main
		            location.y >= position.getY() && location.y <= position.getY() + (float) HEIGHT_CARD) {
		            return true; // Retourne vrai si la main est sélectionnée
		        }
		    }
		    return false; // Retourne faux si aucune carte de la main n'est sélectionnée
		}
		
		
		
		
	
	// Verifie si l'on pointe dans la zone de jeu (board)
	public static boolean zoneSelected(ApplicationContext context, Event event, Board board) {
	    Objects.requireNonNull(context);
	    Objects.requireNonNull(event);
	    Objects.requireNonNull(board);
	    var location = event.getLocation();
	    
	    List<Point> validPlacements = Board.getFreePoints(board); // Récupération des positions valides sur le plateau de jeu
	    for (Point point : validPlacements) {
	        Point thisPoint = CardView.CoordinatesToPixel(context, point);
	        
	        if (location.x >= thisPoint.getX() && location.x <= thisPoint.getX() + (float) WIDTH_CARD && // Vérification si la position de l'événement est dans la zone de jeu
	            location.y >= thisPoint.getY() && location.y <= thisPoint.getY() + (float) HEIGHT_CARD) {
	            return true; // Retourne vrai si la zone de jeu est sélectionnée
	        }
	    }
	    return false; // Retourne faux si aucune zone de jeu n'est sélectionnée
	}

	
	
	// Verifie si l'on pointe dans la zone de pioche
	public static boolean zoneSelectedStack(ApplicationContext context, Event event) {
	    Objects.requireNonNull(context);
	    Objects.requireNonNull(event);
	    
	    // Récupération de la position de l'événement
	    var location = event.getLocation();
	    var screenInfo = context.getScreenInfo();
	    var width = screenInfo.getWidth();
	    var height = screenInfo.getHeight();	  
	   
        if (location.x >= 0 && location.x <= width * (0.2f)  && // Vérification si la position de l'événement est dans la pioche
            location.y >= 0 && location.y <= height) {
            return true; // Retourne vrai si la pioche est sélectionnée
        }
	    
	    return false; // Retourne faux si la pioche n'est sélectionnée
	}
	
	// Verifie si l'on pointe dans la zone de pioche
		public static boolean zoneSelectedPoint(ApplicationContext context, Event event) {
		    Objects.requireNonNull(context);
		    Objects.requireNonNull(event);
		    
		    // Récupération de la position de l'événement
		    var location = event.getLocation();
		    var screenInfo = context.getScreenInfo();
		    var width = screenInfo.getWidth();
		    var height = screenInfo.getHeight();	  
		   
	        if (location.x >= width * (0.8f)+10 && location.x <= width && // Vérification si la position de l'événement est dans la pioche
	            location.y >= 0 && location.y <= height * (0.6f)) {
	            return true; // Retourne vrai si la pioche est sélectionnée
	        }
		    
		    return false; // Retourne faux si la pioche n'est sélectionnée
		}
		
	// Verifie si l'on pointe dans la zone de pioche
			public static boolean zoneSelectedHand(ApplicationContext context, Event event) {
			    Objects.requireNonNull(context);
			    Objects.requireNonNull(event);
			    
			    // Récupération de la position de l'événement
			    var location = event.getLocation();
			    var screenInfo = context.getScreenInfo();
			    var width = screenInfo.getWidth();
			    var height = screenInfo.getHeight();	  
			   
		        if (location.x >= (width * (0.25f)) && location.x <= (width * (0.75f)) && // Vérification si la position de l'événement est dans la pioche
		            location.y >= (height * (0.8f))+10 && location.y <= height ) {
		            return true; // Retourne vrai si la pioche est sélectionnée
		        }
			    
			    return false; // Retourne faux si la pioche n'est sélectionnée
			}
	
/*__________________________________________________________________GESTION DES TOURS ET DE LA PIOCHE DES CARTES__________________________________________________________________*/
	
    //Méthode pour changer de tour après le choix d'une carte dans la pioche
	public static Player changeTour(ApplicationContext context, Event event, String typeStack, Stack stack, List<Player> allPlayers, Player currentPlayer, int cardSelected) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(event);
		Objects.requireNonNull(typeStack);
		Objects.requireNonNull(allPlayers);
		Objects.requireNonNull(currentPlayer);
		Objects.requireNonNull(stack);
		
		var location = event.getLocation();
		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();
        
	    List<Point> coordinates = typeStack.equals("Ressource") ? stack.getCoordinatesRessource() : stack.getCoordinatesGold();
	    List<Card> stackCards = typeStack.equals("Ressource") ? stack.getRessourceStack() : stack.getGoldStack();

	    var width2 = (screenInfo.getWidth() * 0.144f);
        var heightResource = (screenInfo.getHeight() * 0.043f);
        var heightGold = screenInfo.getHeight() * 0.55f;   
        var topResourceCardPosition = new Point(width2 * 0.182f, heightResource);
        var topGoldCardPosition = new Point(width2 * 0.182f, heightGold);
        
        		    
	    currentPlayer = getTopCardStack(context, event, topResourceCardPosition, topGoldCardPosition, currentPlayer, stack, allPlayers);
	    if (coordinates.size() > 0 && stackCards.size() > 0) {
	    	for (int i = 0; i < coordinates.size(); i++) { 
	    	    Point position = coordinates.get(i); 
	    	    Card card = stackCards.get(stackCards.size() - 1 - i); 

	    	    if (location.x >= position.getX() && location.x <= position.getX() + CardView.widthFloat &&
	    	        location.y >= position.getY() && location.y <= position.getY() + CardView.heightFloat &&
	    	        currentPlayer.getHand().size() < 3) { 
	    	        currentPlayer.getHand().add(card); 
	    	        stack.remove(card); 
	    	        currentPlayer = nextPlayer(context, stack, allPlayers, currentPlayer); 
	    	    }
	    	}
	    }
	    choiceObjective(context, stack, width, height, currentPlayer, allPlayers, cardSelected);
    	return currentPlayer;
	}
	
	//Passe au joueur suivant
	private static Player nextPlayer(ApplicationContext context, Stack stack, List<Player> allPlayers, Player currentPlayer) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(allPlayers);
		Objects.requireNonNull(currentPlayer);
		Objects.requireNonNull(stack);
		
		for (int x = 0; x < allPlayers.size(); x++) {
		    Player player = allPlayers.get(x);
		    if (player.equals(currentPlayer)) {  
		    	player.dontPlay();
		        if (x + 1 < allPlayers.size()) {
		            currentPlayer = allPlayers.get(x + 1);
		            currentPlayer.play();
		            currentPlayer.toggleHand();
		            BoardView.refreshAll(context, stack, currentPlayer,allPlayers);
		        	break;
		        } else {
		            currentPlayer = allPlayers.get(0); 
		            currentPlayer.play();
		            currentPlayer.toggleHand();
		            BoardView.refreshAll(context, stack, currentPlayer, allPlayers);
		        	break;
		        }

		    }
		}
		return currentPlayer;
	}
	
	//Méthode pour détecter si le joueur clique sur la carte en haut de la pioche
	private static Player getTopCardStack(ApplicationContext context, Event event, Point topResourceCardPosition, Point topGoldCardPosition, Player currentPlayer, Stack stack, List<Player> allPlayers) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(event);
		Objects.requireNonNull(topResourceCardPosition);
		Objects.requireNonNull(topGoldCardPosition);
		Objects.requireNonNull(allPlayers);
		Objects.requireNonNull(currentPlayer);
		Objects.requireNonNull(stack);
		
		var location = event.getLocation();
		if(location.x >= topResourceCardPosition.getX() && location.x <= topResourceCardPosition.getX() + CardView.widthFloat && // Vérification si la position de l'événement est sur une carte de la pile et si la main n'est pas pleine
                location.y >= topResourceCardPosition.getY() && location.y <= topResourceCardPosition.getY() + CardView.heightFloat && currentPlayer.getHand().size() < 3) {
        	currentPlayer.getHand().add(stack.getRessourceStack().get(stack.getRessourceStack().size() - 3));
        	stack.remove(stack.getRessourceStack().get(stack.getRessourceStack().size() - 3));
        	currentPlayer = nextPlayer(context, stack, allPlayers, currentPlayer);
        } else if(location.x >= topGoldCardPosition.getX() && location.x <= topGoldCardPosition.getX() + CardView.widthFloat && // Vérification si la position de l'événement est sur une carte de la pile et si la main n'est pas pleine
                location.y >= topGoldCardPosition.getY() && location.y <= topGoldCardPosition.getY() + CardView.heightFloat && currentPlayer.getHand().size() < 3) {
        	currentPlayer.getHand().add(stack.getGoldStack().get(stack.getGoldStack().size() - 3));
        	stack.remove(stack.getGoldStack().get(stack.getGoldStack().size() - 3));
        	currentPlayer = nextPlayer(context, stack, allPlayers, currentPlayer);
        }
		return currentPlayer;
	}
	
	//Méthode qui fait apparaître l'écran qui propose les cartes objectifs
	private static void choiceObjective(ApplicationContext context, Stack stack, float width, float height, Player currentPlayer, List<Player> allPlayers, int cardSelected) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(allPlayers);
		Objects.requireNonNull(currentPlayer);
		Objects.requireNonNull(stack);
		
		if (currentPlayer.getObjective().size() != 1){
	    	 BoardView.refreshScreen(context, currentPlayer.getBoard(), currentPlayer,allPlayers);
	    	 if(cardSelected==-1) {
	    		 BoardView.refreshCard(context,stack, currentPlayer);
	    	 }
	 	    
	    	drawShadow(context, width, height);
	    	BoardView.showObjective(context, currentPlayer);
	    }
	}

/*__________________________________________________________________GESTION DES CARTES OBJECTIFS__________________________________________________________________*/
	
	//Permet de choisir une des deux cartes objectifs
	public static void chooseObjectiveCard(ApplicationContext context, Stack stack, Event event, Player player, List<Player> allPlayers) {
	    Objects.requireNonNull(context);
	    Objects.requireNonNull(event);
	    Objects.requireNonNull(stack);
	    Objects.requireNonNull(player);
	    Objects.requireNonNull(allPlayers);
	    
	    var location = event.getLocation();
	    var screenInfo = context.getScreenInfo();
	    var width = screenInfo.getWidth();
	    var height = screenInfo.getHeight();
	    var initialPoint = new Point(width * 0.3f, height * 0.4f);
	    int xOffset = 600;
	    
	    BoardView.showObjective(context, player);
	    
	    for (int i = 0; i < player.getObjective().size(); i++) {
	        Card card = player.getObjective().get(i);
	        Point position = new Point(initialPoint.getX() + i * xOffset, initialPoint.getY());
	        if (isCardClicked(location, position) && player.getObjective().size() > 1) {
	            player.getObjective().clear();
	            player.getObjective().add(card);
	            refreshBoard(context, stack, player, allPlayers);
	        }
	    }
	    drawShadow(context, width, height);
	}
	
	//Vérifier si le joueur clique sur une des cartes objectifs
	private static boolean isCardClicked(Point2D.Float location, Point position) {
	    return location.getX() >= position.getX() && location.getX()  <= position.getX() + 300
	        && location.getY() >= position.getY() && location.getY() <= position.getY() + 300;
	}
	
	//Rafraîchir la table du joueur
	private static void refreshBoard(ApplicationContext context, Stack stack, Player player, List<Player> allPlayers) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(stack);
		Objects.requireNonNull(player);
		Objects.requireNonNull(allPlayers);
		
	    BoardView.refreshScreen(context, player.getBoard(), player, allPlayers);
	    BoardView.refreshCard(context, stack, player);
	    BoardView.showStarterCard(context, player);
	}
	
	//Dessine une ombre pour mettre en avant le choix des cartes
	private static void drawShadow(ApplicationContext context, float width, float height) {
		Objects.requireNonNull(context);
		context.renderFrame(graphics -> {
	        graphics.setColor(new Color(0, 0, 0, 200));
	        graphics.fill(new Rectangle2D.Float(0, 0, width, height));
	    });
	}
	
	/*__________________________________________________________________GESTION DE LA CARTE DE DéPART__________________________________________________________________*/

	//Permet de choisir le côté de la carte de départ 
	public static boolean chooseStarterCardSide(ApplicationContext context, Stack stack, Event event, Player player,List<Player> allPlayers) {
	    Objects.requireNonNull(context);
	    Objects.requireNonNull(event);
	    Objects.requireNonNull(player);
	    Objects.requireNonNull(allPlayers);
	    var location = event.getLocation();
	    var screenInfo = context.getScreenInfo();	    
	    var widthStarter = (screenInfo.getWidth() * (0.27f));
	    var heightStarter = (screenInfo.getHeight() * (0.4f));
	    Point initialPoint = new Point(widthStarter, heightStarter); // Point initial pour afficher la première carte de la main	    
	    int xOffset = 600; // Décalage horizontal entre les cartes
	    int yOffset = 0;   // Décalage vertical entre les cartes
	    
	    for (int i = 0; i < player.getStarterCards().size(); i++) { 
	    	Card card = player.getStarterCards().get(i);
	        Point position = new Point(initialPoint.getX() + i * xOffset, initialPoint.getY() + i * yOffset);
	        int number = i;
	        // Calcul de la position du bouton
	        float xButton=(float) (initialPoint.getX() + number * xOffset)-20;
            float yButton=(float) (initialPoint.getY() + number * yOffset)+150;
            float widthButton= 300;
            float heightButton=50;
            drawButton(context, xButton, yButton, widthButton, heightButton);
            CardView.showCardByPosition(context, card, position);
            clickOnButton(context, stack, location, xButton, yButton, player, allPlayers, number, widthButton, heightButton);
	    }
	    return false;
	}
	
	//Vérifier si le joueur appuie sur un des boutons pour choisir la carte de départ
	private static boolean clickOnButton(ApplicationContext context, Stack stack, Point2D.Float location, float xButton, float yButton, Player player, List<Player> allPlayers, int i, float widthButton, float heightButton) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(stack);
		Objects.requireNonNull(location);
		Objects.requireNonNull(player);
		Objects.requireNonNull(allPlayers);
		
		// Vérification si la position de l'événement est sur le bouton
        if (location.x >= xButton && location.x <= xButton + widthButton &&
            location.y >= yButton && location.y <= yButton + heightButton && player.getStarterCards().size() > 1) {
        	player.getDeck().add(player.getStarterCards().get(i));
    		player.removeStarterCard(i);
        	player.getBoard().associateCardsWithPoints(player.getDeck());
            BoardView.refreshScreen(context, player.getBoard(), player,allPlayers);
            BoardView.refreshCard(context, stack, player);
            return true;
        }
        return false;
	}
	
	//Dessine le bouton qui permet de choisir le côté de la carte de départ
	private static void drawButton(ApplicationContext context, float xButton, float yButton, float widthButton, float heightButton) {
		Objects.requireNonNull(context);
		context.renderFrame(graphics -> { //Affichage des boutons pour changer de coté    	
            Menu.drawRoundedRectangleWithBorder(context,xButton,yButton,widthButton,heightButton,50,50,Color.ORANGE, Color.WHITE);
  			graphics.setColor(Color.WHITE);
  			graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
  			String text = "Choisir ce côté";

  			FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
  			int x = (int) (xButton + (widthButton - metrics.stringWidth(text)) / 2);
  			int y = (int) (yButton + ((heightButton - metrics.getHeight()) / 2) + metrics.getAscent());
  			graphics.drawString(text, x, y);
        });
	}
}