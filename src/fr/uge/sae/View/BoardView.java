package fr.uge.sae.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.sae.Back.*;
import fr.uge.sae.Card.*;
import fr.uge.sae.Player.Player;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.KeyboardKey;

public class BoardView {

	private final static float WIDTH_CARD = CardView.widthFloat;
	private final static float HEIGHT_CARD = CardView.heightFloat;
	private static boolean BreakOpen=false;
	private static boolean InventoryOpen=false;
	private static List<Player> initializePlayers(Stack stack) throws IOException {
        int numberOfPlayers = MenuChoix.getNumberOfPlayer();
        List<Player> allPlayers = new ArrayList<>();

        for (int i = 1; i <= numberOfPlayers; i++) {
            List<Card> objective = new ArrayList<>();
            List<Card> commonObjective = new ArrayList<>();
            List<Card> lst = new ArrayList<>();
            Deck cardOnTheBoard = new Deck(lst);
            List<Card> starterCards = new ArrayList<Card>();
            List<Card> hand = new ArrayList<>();
            var board = new Board();
            var player = new Player(i, board, hand, cardOnTheBoard, objective, starterCards, commonObjective, false, false);
            allPlayers.add(player);

            player.initializeHand(stack);
        }

        return allPlayers;
    }

	private static void CodexNaturalis(ApplicationContext context) throws IOException {
		Objects.requireNonNull(context);

/*__________________________________________________________________GESTION DES RESSOURCES INTIALES__________________________________________________________________*/	
		Stack stack = new Stack();		
        Deck.readStack(Paths.get("data/deck.txt"), stack);
        Collections.shuffle(stack.getRessourceStack());
        Collections.shuffle(stack.getGoldStack());
		List<Player> allPlayers = initializePlayers(stack);
		Deck.readObjectiveCard(Paths.get("data/deck.txt"), allPlayers);
		Deck.readStarterCard(Paths.get("data/deck.txt"), allPlayers);
        Player currentPlayer = allPlayers.get(0);
        currentPlayer.play();


        //Récupération des infos de l'écran
        var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();

        //Affichage de départ
        refreshScreen(context, currentPlayer.getBoard(), currentPlayer,allPlayers);
        refreshCard(context,stack, currentPlayer);


        context.renderFrame(graphics -> {

	        graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fill(new Rectangle2D.Float(0, 0, width, height));
        });
        BoardView.showObjective(context, currentPlayer);

/*__________________________________________________________________AFFICHAGE DE LA PARTIE EN TEMPS REEL__________________________________________________________________*/	
        //Permet de stocker la carte que l'on a sélectionné
        final int[] thisCard = { -1 };
        
        //Boucle du jeu
        while (true) {      

            var event = context.pollOrWaitEvent(10);            
            if (event == null) {continue;}  
            var action = event.getAction(); 
/*__________________________________________________________________GESTION DES INPUT DE LA SOURIS__________________________________________________________________*/
            if (action == Action.POINTER_DOWN) {

            	if (currentPlayer.getObjective().size() != 1){
            		Game.chooseObjectiveCard(context, stack, event, currentPlayer, allPlayers); 

            	}

            	if (currentPlayer.getObjective().size() == 1 && currentPlayer.getStarterCards().size() != 1) {	
        			Game.chooseStarterCardSide(context, stack, event, currentPlayer, allPlayers);	
        		}


    			currentPlayer.becomeReady();

                if(currentPlayer.isReady()) {

                	currentPlayer = Game.changeTour(context, event, "Ressource", stack, allPlayers, currentPlayer, thisCard[0]);
	            	currentPlayer = Game.changeTour(context, event, "Gold", stack, allPlayers, currentPlayer, thisCard[0]);       

		            //Affichage de la surbrillance après le choix d'une carte
		            if(Game.IsCardSelected(context, event, currentPlayer.getHand()) && !BreakOpen && !InventoryOpen) {
		            	thisCard[0] = Game.chooseCardInHand(context, event, currentPlayer.getHand())-1;
		                if((thisCard[0])>=0) {

		                	currentPlayer.getBoard().highlightValidPlacements(context, currentPlayer.getHand().get(thisCard[0]), currentPlayer.getBoard());	  
		                	refreshSreenHighlight(context,currentPlayer.getBoard(),currentPlayer,stack,allPlayers);
		                	Game.chooseCardInHand(context, event, currentPlayer.getHand());

		                }
		            }

		            //Pose d'une carte sur le plateau
		            if((thisCard[0])>=0) {
		            	if(currentPlayer.getHand().size()>=3) { //Verifie qu'on a 3 cartes et que l'on en a selectionné une
			                if (Game.zoneSelected(context, event, currentPlayer.getBoard()) &&
			                	!Game.zoneSelectedHand(context, event) &&
			                	!Game.zoneSelectedStack(context, event) &&
			                	!Game.zoneSelectedPoint(context, event)) { //Verifie si l'on pointe la zone de jeu

			                    Point thisPoint = currentPlayer.getBoard().getPointAtPosition(context, event, currentPlayer.getBoard());
			                    if(currentPlayer.getHand().get(thisCard[0]) instanceof GoldCard && currentPlayer.getHand().get(thisCard[0]).getRecto()) {
			                    	if(currentPlayer.getBoard().canPlaceGoldCard(currentPlayer.getHand().get(thisCard[0]), currentPlayer.getDeck()) && thisPoint!=null) {
			                    		currentPlayer.getBoard().putCard(thisPoint, currentPlayer.getHand().get(thisCard[0]));
			                    		currentPlayer.getHand().get(thisCard[0]).changeCoordinates(thisPoint);
			                    		currentPlayer.getHand().remove(thisCard[0]);
			                    		currentPlayer.getBoard().updateRecoveredCorner(thisPoint, currentPlayer.getBoard());
			                    	} 
			                    } else if (thisPoint!=null){
			                    	currentPlayer.getBoard().putCard(thisPoint, currentPlayer.getHand().get(thisCard[0]));
			                    	currentPlayer.getHand().get(thisCard[0]).changeCoordinates(thisPoint);
			                    	currentPlayer.getHand().remove(thisCard[0]);
			                    	currentPlayer.getBoard().updateRecoveredCorner(thisPoint, currentPlayer.getBoard());
			                    	thisCard[0] = -1;
			                    }

			                    showBoard(context, currentPlayer.getBoard());
			                    refreshScreen(context, currentPlayer.getBoard(), currentPlayer,allPlayers);
			                    refreshCard(context, stack, currentPlayer);


			                } 
		                } else if(Game.IsCardSelected(context, event, currentPlayer.getHand())) { //Affiche une erreur si l'on veut poser une carte sur la zone de jeu alors qu'on a pas trop carte dans la main
		                	context.renderFrame(graphics -> {
			                	graphics.setFont(new Font("Arial", Font.BOLD, 25));
				                graphics.setColor(Color.RED);
				                graphics.drawString("Votre main doit contenir 3 cartes pour en poser une!", (width * (0.25f)), (height * (0.8f))-20);
		                	});
			            }
		            }

		            if(!BreakOpen && !InventoryOpen) {
		            	Game.chooseHandSide(context, event, currentPlayer);	  
		            }



	                //Condition de victoire

	                if(Win(context, currentPlayer, allPlayers)) {
	                	break;
	                }

	                if(BreakOpen && !InventoryOpen) {
	                	var location = event.getLocation();
	                	//Appui sur le bouton quitter
	                	if (location.x >= xButtonQuit && location.x <= xButtonQuit + widthButtonQuit && location.y >= yButtonQuit && location.y <= yButtonQuit + heightButtonQuit) {
	                		context.exit(0);
	                        return;
	                	}
	                	//Appui sur le bouton menu
	                	else if (location.x >= xButtonMenu && location.x <= xButtonMenu + widthButtonMenu && location.y >= yButtonMenu && location.y <= yButtonMenu + heightButtonMenu) {
	                		Menu.main(null);
	                        return;
	                	} 
	                }

                } else {
                	context.renderFrame(graphics -> {
	                	graphics.setFont(new Font("Arial", Font.BOLD, 25));
		                graphics.setColor(Color.RED);
		                graphics.drawString("Vous devez retirer une des cartes objectifs  et choisir le coté de votre carte de départ!", (width * (0.25f)), (height * (0.8f))-20);
                	});
                }


            }

            //GESTION DES INPUT AU CLAVIER
            handleKeyPress(event, context, allPlayers, stack, currentPlayer);


        }      
	}
	/*__________________________________________________________________GESTION DES INPUT AU CLAVIER__________________________________________________________________*/


	//Gère tous les éléments lié au clavier
	private static void handleKeyPress(Event event, ApplicationContext context, List<Player> allPlayers, Stack stack, Player currentPlayer) {
        var action = event.getAction();

        if (action == Action.KEY_PRESSED) {
            KeyboardKey key = event.getKey();
            var screenInfo = context.getScreenInfo();
            var width = screenInfo.getWidth();
            var height = screenInfo.getHeight();
            float widthBreak = (float) (screenInfo.getWidth() * 0.35); 
            float heigthBreak = (float) (screenInfo.getHeight() * 0.80); 
    		float xBreak = (float) (width * 0.33);
            float yBreak = (float) (height * 0.15);
            switch (key) {
                case Q:
                	if(!BreakOpen && currentPlayer.isReady() && !InventoryOpen) {
                		showBreak(context, xBreak, yBreak, widthBreak, heigthBreak, 30, 30, Color.LIGHT_GRAY, Color.WHITE, event);
                		BreakOpen = true;
                	} else if(BreakOpen && currentPlayer.isReady() && !InventoryOpen){
                		refreshAll(context, stack, currentPlayer, allPlayers);
                		BreakOpen = false;
                	}
                	break;

                case A:
                    if (allPlayers.size() >= 1 && currentPlayer.getObjective().size() == 1 && currentPlayer.getStarterCards().size() == 1) {
                    	currentPlayer = togglePlayerHand(context, allPlayers, stack, 0,currentPlayer);
                    }
                    break;
                case Z:
                    if (allPlayers.size() >= 2 && currentPlayer.getObjective().size() == 1 && currentPlayer.getStarterCards().size() == 1) {
                    	currentPlayer = togglePlayerHand(context, allPlayers, stack, 1,currentPlayer);
                    }
                    break;
                case E:
                    if (allPlayers.size() >= 3 && currentPlayer.getObjective().size() == 1 && currentPlayer.getStarterCards().size() == 1) {
                    	currentPlayer = togglePlayerHand(context, allPlayers, stack, 2,currentPlayer);
                    }
                    break;
                case R:
                    if (allPlayers.size() >= 4 && currentPlayer.getObjective().size() == 1 && currentPlayer.getStarterCards().size() == 1) {
                    	currentPlayer = togglePlayerHand(context, allPlayers, stack, 3,currentPlayer);
                    }
                    break;
                case I:
                	if (!InventoryOpen && currentPlayer.isReady() && !BreakOpen) {
                		showInventory(context,currentPlayer.getBoard(),currentPlayer);
                		InventoryOpen=true;
                	}else if (InventoryOpen && currentPlayer.isReady() && !BreakOpen){
                		refreshAll(context, stack, currentPlayer, allPlayers);
                		InventoryOpen=false;
                	}

                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    moveCards(key,stack, context, currentPlayer, allPlayers);
                    break;
                default:
                    break;
            }


        }
    }


	/*__________________________________________________________________GESTION DU FOND D'ECRAN__________________________________________________________________*/	


	//Afficher le fond d'écran
	private static void showBackground(ApplicationContext context) {
		Objects.requireNonNull(context);

        float screenWidth = context.getScreenInfo().getWidth();
        float screenHeight = context.getScreenInfo().getHeight();

        context.renderFrame(graphics -> {
            graphics.setColor(Color.BLACK);
            graphics.fill(new Rectangle2D.Float(0, 0, screenWidth, screenHeight));
            var image = Image.loadImage("data/fond_jeu.jpg");
            Image.drawImage(graphics,image,0, 0, screenWidth, screenHeight);
        });
	}	

/*__________________________________________________________________AFFICHAGE DE LA ZONE DE JEU__________________________________________________________________*/	

	//Met à jour toutes les zones de jeu
	public static void refreshScreen(ApplicationContext context, Board board, Player player,List<Player> allPlayers) {
		Objects.requireNonNull(context);

		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();


		context.renderFrame(graphics -> {
	        graphics.setColor(Color.BLACK);
	        graphics.fill(new Rectangle2D.Float(0, 0, width, height)); //Fond d'écran noir
	        showBackground(context);
	        showBoard(context, board);

			showCurrentPlayer(graphics,context, player,width,height); //Affichage du joueur actuel

			showBackgroundPoint(context, graphics);			
			showPointPlayer(context,board,allPlayers);

	        var image2 = Image.loadImage("data/ZoneMain.png");
			Image.drawImage(graphics, image2, (width * (0.25f)), (height * (0.8f))+10, (width * (0.5f)), (height * (0.2f)));//Zone de main
		});
	}

	//Afficher le background de la zone de point
	private static void showBackgroundPoint(ApplicationContext context, Graphics2D graphics) {
		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
		int numberOfPlayers = MenuChoix.getNumberOfPlayer();
		BufferedImage image = null;
		switch(numberOfPlayers) {
			case 1: image = Image.loadImage("data/ZonePoint1.png");break;
			case 2: image = Image.loadImage("data/ZonePoint2.png");break;
			case 3: image = Image.loadImage("data/ZonePoint3.png");break;
			case 4: image = Image.loadImage("data/ZonePoint4.png");break;
		}
		Image.drawImage(graphics, image, width-385, 0, 400, 800);//Zone de point
	}

	//Afficher les points de chaque joueurs
	private static void showPointPlayer(ApplicationContext context,Board board,List<Player> allPlayers) {
		Objects.requireNonNull(context);

		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();

        int numberOfPlayers = MenuChoix.getNumberOfPlayer();
		context.renderFrame(graphics -> {
			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("Monospaced", Font.BOLD, 30));
			switch(numberOfPlayers) {
				case 4: 
					Player player4 = allPlayers.get(3);
					int PointJoueur4 = player4.getBoard().getPointsOfBoard(board, player4);
					graphics.drawString(""+PointJoueur4, width-180, 715);
				case 3: 
					Player player3 = allPlayers.get(2);
					int PointJoueur3 = player3.getBoard().getPointsOfBoard(board, player3);
					graphics.drawString(""+PointJoueur3, width-180, 560);				
				case 2: 
					Player player2 = allPlayers.get(1);
					int PointJoueur2 = player2.getBoard().getPointsOfBoard(board, player2);
					graphics.drawString(""+PointJoueur2, width-180, 405);
				case 1: 
					Player player1 = allPlayers.get(0);
					int PointJoueur1 = player1.getBoard().getPointsOfBoard(board, player1);
					graphics.drawString(""+PointJoueur1, width-180, 250);



			}
		});
	}



	//Met a jour les zones de jeu pour la surbrillance des cartes
	private static void refreshSreenHighlight(ApplicationContext context, Board board, Player player,Stack stack,List<Player> allPlayers) {
		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();
		context.renderFrame(graphics -> {

			showBackgroundPoint(context, graphics);
			showPointPlayer(context,board,allPlayers);

	        var image2 = Image.loadImage("data/ZoneMain.png");
			Image.drawImage(graphics, image2, (width * (0.25f)), (height * (0.8f))+10, (width * (0.5f)), (height * (0.2f)));//Zone de main
			refreshCard(context, stack, player);
		});
	}
	private static void showCurrentPlayer(Graphics2D graphics,ApplicationContext context, Player player,float width,float height) {

        BufferedImage image = null;
        BufferedImage image2 = null;
        switch(player.getIdPlayer()) {
	        case 1:
				image = Image.loadImage("data/link1.png");
				image2= Image.loadImage("data/Joueur1.png");
				break;
			case 2:
				image = Image.loadImage("data/link2.png");
				image2= Image.loadImage("data/Joueur2.png");
				break;
			case 3:
				image = Image.loadImage("data/link3.png");
				image2= Image.loadImage("data/Joueur3.png");
				break;
			case 4:
				image = Image.loadImage("data/link4.png");
				image2= Image.loadImage("data/Joueur4.png");
				break;
		}
		float widthImage=150;
		float heightImage=300;
		float x = (width * (0.83f)+(widthImage/2));
	    float y = height -heightImage;
		Image.drawImage(graphics, image, x,y,widthImage,heightImage);

		float multiplier = (float) 0.6;
		float widthImage2=539 * multiplier;
		float heightImage2=108 * multiplier;
		float x2 = (width -widthImage2)/2;
	    float y2 = 0;
		Image.drawImage(graphics, image2, x2,y2,widthImage2,heightImage2);
	}

	//Met à jour la pioche et la main
	public static void refreshCard(ApplicationContext context, Stack stack, Player player) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(stack);
		Objects.requireNonNull(player);
		var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();

		context.renderFrame(graphics -> {

			var image = Image.loadImage("data/ZonePioche.png");
			Image.drawImage(graphics, image, -20, 0, width * (0.2f), height); //Zone de pioche
	        stack.showStack(context);
	        showHand(context, player);

		});
	}

	private static void showInventory(ApplicationContext context, Board board, Player player) {
		Objects.requireNonNull(context);
		var screenInfo = context.getScreenInfo();
        var width = 1536 / (1536/screenInfo.getWidth());
        var height = 960 / (960/screenInfo.getHeight());
        
		context.renderFrame(graphics -> {

			float widthImage=1200;
			float heightImage=820;
			float x = (width - widthImage) / 2;
	        float y = (height - heightImage) / 2;
			graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fill(new Rectangle2D.Float(0, 0, width, height));
			showBackgroundInventory(graphics,context,player.getIdPlayer(),widthImage,heightImage,x,y); //Affiche du fond et de l'espace d'inventaire


			graphics.setFont(new Font("Monospaced", Font.BOLD, 35));
	        graphics.setColor(Color.WHITE);

	        float decalageX = (width * (0.068f));
	        float decalageY=(width * (0.073f));

	        graphics.drawString(""+ board.numberOfFungi(), x + (width * (0.2295f))+(decalageX*0),  y+(height * (0.355f))+(decalageY*0));
	        graphics.drawString("" + board.numberOfAnimal(), x + (width * (0.2295f))+(decalageX*0),  y+(height * (0.355f))+(decalageY*1));
	        graphics.drawString("" + board.numberOfInsect(), x + (width * (0.2295f))+(decalageX*2),  y+(height * (0.355f))+(decalageY*0));
	        graphics.drawString(""+ board.numberOfPlant(), x + (width * (0.2295f)) +(decalageX*1),  y+(height * (0.355f))+(decalageY*0));
	        graphics.drawString("" + board.numberOfQuill(), x + (width * (0.2295f))+(decalageX*2),  y+(height * (0.355f))+(decalageY*1));
	        graphics.drawString("" + board.numberOfManuscript(), x + (width * (0.2295f))+(decalageX*2),  y+(height * (0.355f))+(decalageY*2));
	        graphics.drawString("" + board.numberOfInkwell(), x + (width * (0.2295f))+(decalageX*1),  y+(height * (0.355f))+(decalageY*1)); 

	        graphics.setFont(new Font("Monospaced", Font.BOLD, 45));
	        int newScore = board.getPointsOfBoard(board, player);
	        graphics.drawString("" + newScore, x + (width * (0.25f)),  y+(height * (0.576f)));
	        
	        //Afficher l'objectif perso
	        List<Card> Objectives = player.getObjective();
			var image = Image.loadImage("data/"+((ObjectiveCard) Objectives.get(0)).getBackground()+".png");
			Image.drawImage(graphics, image, width*0.64f, height*0.33f, 250, 125);
			
			//Afficher les objectifs communs
			List<Card> commonsObjectives = player.getCommonObjective();
			for (int i=0; i<2;i++) {
				Image.drawImage(graphics, Image.loadImage("data/"+((ObjectiveCard) commonsObjectives.get(i)).getBackground()+".png"), width*0.64f, height*0.515f+(150*i), 250, 125);
			}
			
		});
		
		
	}

	//Affiche le menu de pause
	private static void showBreak(ApplicationContext context, float x, float y, float width, float height, float widthArc, float heightArc, Color fillColor, Color borderColor, Event event) {
		var screenInfo = context.getScreenInfo();
		var widthBack = screenInfo.getWidth();
		var heightBack = screenInfo.getHeight();
		var image = Image.loadImage("data/Titre.png");
		xButtonQuit = widthBack * (0.41f);
		yButtonQuit = heightBack * (0.55f);
		widthButtonQuit = widthBack * (0.2f);
		heightButtonQuit = heightBack * (0.08f);
		xButtonMenu = widthBack * (0.41f);
	    yButtonMenu = heightBack * (0.45f);
	    widthButtonMenu = widthBack * (0.2f);
	    heightButtonMenu = heightBack * (0.08f);
		context.renderFrame(graphics -> {
			graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fill(new Rectangle2D.Float(0, 0, widthBack, heightBack));
		});

		Menu.drawRoundedRectangleWithBorder(context, x, y, width, height, widthArc, heightArc, fillColor, borderColor);
		context.renderFrame(graphics -> {
			Image.drawImage(graphics, image, x, y, width, height * (0.3f));
		});
		MenuButton(context);
		QuitButton(context);
	}
	
	//Affiche l'inventaire associé au joueur
	private static void showBackgroundInventory(Graphics2D graphics,ApplicationContext context, int currentPlayerIndex,float widthImage, float heightImage ,float x, float y) {
		BufferedImage image = null;
		switch (currentPlayerIndex) {
			case 1:
				image = Image.loadImage("data/Inventaire1.png");
				break;
			case 2:
				image = Image.loadImage("data/Inventaire2.png");
				break;
			case 3:
				image = Image.loadImage("data/Inventaire3.png");
				break;
			case 4:
				image = Image.loadImage("data/Inventaire4.png");
				break;
		}

		Image.drawImage(graphics, image, x,y,widthImage,heightImage);
	}

	//Affiche toute les cartes présentent sur la board
	public static void showBoard(ApplicationContext context, Board board) {
	    Objects.requireNonNull(context);
	    Map<Point, Card> boardMap = board.getBoardMap(); // Récupération de la map depuis la méthode
	    for (Map.Entry<Point, Card> entry : boardMap.entrySet()) {
	        Card card = entry.getValue();
	        if (!card.getRecto()) {
	            card.cornerVerso();
	        }
	        CardView.showCard(context, card);
	    }
	}

	//Changer de vision de joueur
	private static Player togglePlayerHand(ApplicationContext context, List<Player> allPlayers, Stack stack, int playerIndex, Player currentPlayer) {
        if (playerIndex < allPlayers.size()) {
            Player player = allPlayers.get(playerIndex);

            player.toggleHand();;
            showBoard(context, player.getBoard());
            refreshAll(context, stack, player, allPlayers);
            return player;

        }
        return null;
    }

	public static void refreshAll(ApplicationContext context, Stack stack, Player currentPlayer,List<Player> allPlayers) {
		refreshScreen(context, currentPlayer.getBoard(), currentPlayer,allPlayers);
        refreshCard(context, stack, currentPlayer);

	}

/*__________________________________________________________________AFFICHAGE DES SURLIGNAGES DE CARTE__________________________________________________________________*/	

	//Surligne une carte dans sa main
	public static void highlightCardInHand(ApplicationContext context,Point point) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(point);

	    double x = point.getX();
	    double y = point.getY();

	    context.renderFrame(graphics -> {
	    	float xFloat = (float) x; //transformation des double en float
	    	float yFloat = (float) y;
	        // Dessine la carte
	        graphics.setColor(Color.YELLOW);
	        graphics.draw(new RoundRectangle2D.Float(xFloat-5, yFloat-5, WIDTH_CARD+10, HEIGHT_CARD+10,25,25));

	    });

    }

	//Surligne la carte située à une certaine position en pixel
	public static void highlightCardAtPosition(ApplicationContext context,Point point) {
		Objects.requireNonNull(context);
        Objects.requireNonNull(point);

        // Convertit les coordonnées en pixels
        Point coordinates = CardView.CoordinatesToPixel(context, point);
        if (coordinates == null) {
            System.out.println("Les coordonnées de la carte sont null.");
            return;
        }

        double x = coordinates.getX();
        double y = coordinates.getY();

        context.renderFrame(graphics -> {
            // Dessine la carte
            graphics.setColor(Color.YELLOW);
            graphics.draw(new RoundRectangle2D.Float((float) x, (float) y, WIDTH_CARD, HEIGHT_CARD,25,25));
        });
    }









/*__________________________________________________________________AFFICHAGE DE LA MAIN__________________________________________________________________*/	

	//Affichage de la main du joueur
	public static void showHand(ApplicationContext context, Player player) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(player);
	    var screenInfo = context.getScreenInfo();
	    var width = (screenInfo.getWidth() * (0.27f));
	    var height = (screenInfo.getHeight() * (0.84f));
	    Point initialPoint = new Point(width, height); // Point initial pour afficher la première carte de la main

	    int xOffset = (int) WIDTH_CARD + 50; // Décalage horizontal entre les cartes
	    int yOffset = 0;   // Décalage vertical entre les cartes

	    for (int i = 0; i < player.getHand().size(); i++) { 
	        Card card = player.getHand().get(i);
	        Point position = new Point(initialPoint.getX() + i * xOffset, initialPoint.getY() + i * yOffset);
	        int number = i;

	        context.renderFrame(graphics -> { //Affichage des boutons pour changer de coté
	            graphics.setColor(Color.ORANGE);
	            graphics.fill(new RoundRectangle2D.Float((float) initialPoint.getX() + number * xOffset , (float) (initialPoint.getY()+20 + number * yOffset) + 115, WIDTH_CARD, 35, 25, 25));
	            graphics.setFont(new Font("Monospaced", Font.BOLD, 24));
	            graphics.setColor(Color.WHITE);
	            graphics.drawString(" Changer de côté", (float) initialPoint.getX() + number * xOffset +5, (float) (initialPoint.getY()+10 + number * yOffset) + 150);

	            if(card.getRecto() == false) {
	        		card.cornerVerso();
	        	}

	            if(!player.isPlaying()) {
	            	card.changeToVerso();
	            }

	            CardView.showCardByPosition(context, card, position); // Dessiner la carte
	        });
	    }
	}


	public static void showObjective(ApplicationContext context, Player player) {
		var screenInfo = context.getScreenInfo();
	    var width = (screenInfo.getWidth() * (0.24f));
	    var height = (screenInfo.getHeight() * (0.4f));
	    Point initialPoint = new Point(width, height); // Point initial pour afficher la première carte de la main

	    int xOffset = 600; // Décalage horizontal entre les cartes
	    int yOffset = 0;   // Décalage vertical entre les cartes

	    for (int i = 0; i < player.getObjective().size(); i++) { 
	        Card card = player.getObjective().get(i);
	        Point position = new Point(initialPoint.getX() + i * xOffset, initialPoint.getY() + i * yOffset);
	        int number = i;
	        if(player.getObjective().size() > 1) {
	        	context.renderFrame(graphics -> { //Affichage des boutons pour changer de coté


		            float xButton=(float) (initialPoint.getX() + number * xOffset)+50;
		            float yButton=(float) (initialPoint.getY() + number * yOffset)+250;
		            float widthButton= 300;
		            float heightButton=50;
		            Menu.drawRoundedRectangleWithBorder(context,xButton,yButton,widthButton,heightButton,50,50,Color.ORANGE, Color.WHITE);
		  			graphics.setColor(Color.WHITE);
		  			graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
		  			String text = "Choisir cet objectif";

		  			FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
		  			int x = (int) (xButton + (widthButton - metrics.stringWidth(text)) / 2);
		  			int y = (int) (yButton + ((heightButton - metrics.getHeight()) / 2) + metrics.getAscent());
		  			graphics.drawString(text, x, y);

	        	});
	        }
	        CardView.showCardByPosition(context, card, position);
	    }

	}

	public static void showStarterCard(ApplicationContext context, Player player) {
	    var screenInfo = context.getScreenInfo();
	    var width = (screenInfo.getWidth() * 0.4);
	    var height = (screenInfo.getHeight() * 0.45f);
	    Point initialPoint = new Point(width, height); // Point initial pour afficher la première carte de la main
	    if(player.isReady()) {
	    	CardView.showCardByPosition(context, player.getStarterCards().get(0), new Point(initialPoint.getX(), initialPoint.getY()));
	    }    
	}




/*__________________________________________________________________AFFICHAGE DE LA VICTOIRE__________________________________________________________________*/	

	private static boolean Win(ApplicationContext context, Player initialPlayer, List<Player> allPlayers) {
	    Objects.requireNonNull(context);


	    int score = initialPlayer.getBoard().getPointsOfBoard(initialPlayer.getBoard(), initialPlayer);
	    int maxScore = 0;
	    Map<Player, Integer> map = new HashMap<Player, Integer>();
	    Player winner = initialPlayer;
	    List<Player> losers = new ArrayList<Player>();
	    losers.addAll(allPlayers);

	    if (score >= 20) {
	    	MenuWin.launchMenuName(context, allPlayers, maxScore,score,map, winner,losers); ;
	    	return true;
	    }
	    return false;
	}













/*__________________________________________________________________DEPLACEMENT DES CARTES AVEC LES FLECHES__________________________________________________________________*/	

	private static void moveCards(KeyboardKey direction, Stack stack,ApplicationContext context, Player currentPlayer,List<Player> allPlayers) {
		Objects.requireNonNull(direction);
		Objects.requireNonNull(context);
	    // Déplacement des cartes en fonction de la direction
	    switch (direction) {
	        case UP:
	            CardView.YToUp();
	            break;
	        case DOWN:
	            CardView.YToDown();
	            break;
	        case LEFT:
	            CardView.XToLeft();
	            break;
	        case RIGHT:
	            CardView.XToRight();
	            break;
	        default:
	            break;
	    }
	    refreshAll(context, stack, currentPlayer, allPlayers);
	}


/*__________________________________________________________________IMPLEMENTATION DES BOUTONS__________________________________________________________________*/	

	private static float xButtonQuit;
    private static float yButtonQuit;
    private static float widthButtonQuit;
    private static float heightButtonQuit;

	//Afficher bouton quitter
  	private static void QuitButton(ApplicationContext context) {
  		Objects.requireNonNull(context);

      	 context.renderFrame(graphics -> {
              Menu.drawRoundedRectangleWithBorder(context,xButtonQuit,yButtonQuit,widthButtonQuit,heightButtonQuit,50,50,Color.RED, Color.WHITE);
              graphics.setColor(Color.WHITE);
              graphics.setFont(new Font("Monospaced", Font.BOLD, 30));
              String text = "Quitter";

    			FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
    			int x = (int) (xButtonQuit + (widthButtonQuit - metrics.stringWidth(text)) / 2);
    			int y = (int) (yButtonQuit + ((heightButtonQuit - metrics.getHeight()) / 2) + metrics.getAscent());
    			graphics.drawString(text, x, y);
      	 });
  	}

  	private static float xButtonMenu;
    private static float yButtonMenu;
    private static float widthButtonMenu;
    private static float heightButtonMenu;

  //Afficher bouton menu
  	private static void MenuButton(ApplicationContext context) {
  		Objects.requireNonNull(context);

      	 context.renderFrame(graphics -> {
              Menu.drawRoundedRectangleWithBorder(context,xButtonMenu,yButtonMenu,widthButtonMenu,heightButtonMenu,50,50,Color.ORANGE, Color.WHITE);
              graphics.setColor(Color.WHITE);
              graphics.setFont(new Font("Monospaced", Font.BOLD, 30));
              String text = "Menu principal";

    			FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
    			int x = (int) (xButtonMenu + (widthButtonMenu - metrics.stringWidth(text)) / 2);
    			int y = (int) (yButtonMenu + ((heightButtonMenu - metrics.getHeight()) / 2) + metrics.getAscent());
    			graphics.drawString(text, x, y);
      	 });
  	}


/*__________________________________________________________________LANCEMENT DE LA PARTIE__________________________________________________________________*/	

	public static void main(String[] args) {
        Application.run(Color.BLACK, arg0 -> {
			try {
				CodexNaturalis(arg0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
    }
}