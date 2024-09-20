package fr.uge.sae.Back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.sae.Card.*;
import fr.uge.sae.Corner.*;
import fr.uge.sae.Player.Player;
import fr.uge.sae.View.*;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;

public class Board {
	
/*__________________________________________________________________GESTION DU BOARD__________________________________________________________________*/	

    // La carte est associée à chaque point de la board
    private final Map<Point, Card> boardMap = new HashMap<>();

    
    // Récupère la carte associée à chaque point de la board
    public Map<Point, Card> getBoardMap() {
        return boardMap;
    }
    
    
    // Associe les cartes avec les points correspondants sur la board
    public void associateCardsWithPoints(Deck deck) {
        Objects.requireNonNull(deck);
        for (Card card : deck.getCards()) {
            boardMap.put(card.getCoordinates(), card);
        }
    }
    
    
    // Place une carte à une position donnée sur la board
    public void putCard(Point position, Card card) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(card);
        boardMap.put(position, card);
    }

    
    // Renvoie la carte à une position donnée sur la board
    public Card getCardAtPosition(Point position, Board board) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(board);
        return board.boardMap.get(position);
    }
    

    
/*__________________________________________________________________GESTION DU PLACEMENT DES CARTES__________________________________________________________________*/	
    
 // Vérifie si une carte peut être placée sur la board
    public boolean canPlaceGoldCard(Card card, Deck deck) {
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(deck);
    	
        if (!(card instanceof GoldCard)) {
            throw new IllegalArgumentException("Cette carte n'est pas une GoldCard");
        }
             
        for (HashMap.Entry<String, Integer> entry : card.getCost().entrySet()) { // Parcourir les coûts de la carte Gold            
            if (numberOfRessource(entry.getKey()) < entry.getValue()) { // Vérifier si la ressource requise est suffisante dans le deck
                return false; // Retourner faux si la ressource est insuffisante
            }
        }        
        return true; // Retourner vrai si toutes les ressources sont suffisantes
    }


    

    // Renvoie les points libres sur la board
    public static List<Point> getFreePoints(Board board) {
    	Objects.requireNonNull(board);
        List<Point> freePoints = new ArrayList<>();
        for (Map.Entry<Point, Card> entry : board.boardMap.entrySet()) {
            Card card = entry.getValue();
            Point coordinates = card.getCoordinates();
            for (int i = 1; i <= 4; i++) { // Vérifie les coins non invisibles pour obtenir les points libres
                Corner corner = card.getCorner(i);
                if (!(corner instanceof InvisibleCorner)) {
                    Point potentialPoint;
                    potentialPoint = definePotentialPoint(i, coordinates);
                    if (canPlaceCard(potentialPoint, board)) { // Vérifie si le point peut accueillir une nouvelle carte
                        freePoints.add(potentialPoint);
                    }
                }
            }
        }
        return freePoints;
    }
    
    private static Point definePotentialPoint(int i, Point coordinates) {
    	Objects.requireNonNull(coordinates);
    	Point potentialPoint;
	    switch (i) {
	        case 1: // Coin en haut à gauche
	            potentialPoint = new Point(coordinates.getX() - 1, coordinates.getY() - 1);
	            break;
	        case 2: // Coin en haut à droite
	            potentialPoint = new Point(coordinates.getX() + 1, coordinates.getY() - 1);
	            break;
	        case 3: // Coin en bas à droite
	            potentialPoint = new Point(coordinates.getX() + 1, coordinates.getY() + 1);
	            break;
	        case 4: // Coin en bas à gauche
	            potentialPoint = new Point(coordinates.getX() - 1, coordinates.getY() + 1);
	            break;
	        default:
	            throw new IllegalStateException("Unexpected value: " + i);
	    }
	   return potentialPoint;
    }


    // Renvoie le point à une position spécifique sur l'écran
    public Point getPointAtPosition(ApplicationContext context, Event event, Board board) {
    	Objects.requireNonNull(context);
    	Objects.requireNonNull(event);
    	Objects.requireNonNull(board);
        var location = event.getLocation();
        double WIDTH_CARD = 250;
        double HEIGHT_CARD = 125;

        for (Point point : getFreePoints(board)) {
            Point thisPoint = CardView.CoordinatesToPixel(context, point);
            if (location.x >= thisPoint.getX() && location.x <= thisPoint.getX() + (float) WIDTH_CARD && location.y >= thisPoint.getY() && location.y <= thisPoint.getY() + (float) HEIGHT_CARD) {
                return point;
            }
        }
        return null;
    }

    

    // Vérifie si une carte peut être placée à une position donnée sur la board
    public static boolean canPlaceCard(Point position, Board board) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(board);
        if (board.boardMap.containsKey(position)) { // Vérifie que l'emplacement n'est pas occupé
            return false;
        }      
        double x = position.getX();
        double y = position.getY();     
        // Définition des coins à vérifier
        Point[] adjacentPositions = {
            new Point(x + 1, y + 1), // Bas à droite
            new Point(x - 1, y + 1), // Bas à gauche
            new Point(x - 1, y - 1), // Haut à gauche
            new Point(x + 1, y - 1)  // Haut à droite
        };
        // Définition des indices des coins correspondants
        int[] cornerIndices = {1, 2, 3, 4};
        return checkAdjacentPosition(adjacentPositions, board, cornerIndices);
    }
    
    // Vérification des coins adjacents
    private static boolean checkAdjacentPosition(Point[] adjacentPositions, Board board, int[] cornerIndices) {	
    	Objects.requireNonNull(adjacentPositions);
    	Objects.requireNonNull(board);
        for (int i = 0; i < adjacentPositions.length; i++) {
            Point adjacentPosition = adjacentPositions[i];
            if (board.boardMap.containsKey(adjacentPosition)) { // Vérifie si la carte est présente à la position adjacente
                Card adjacentCard = board.boardMap.get(adjacentPosition); // Obtient la carte adjacente
                if (adjacentCard.getCorner(cornerIndices[i]) instanceof InvisibleCorner) { // Vérifie si le coin est invisible
                    return false; // Si le coin est invisible, on ne peut pas placer la carte
                }
            }
        }
        return true;
    }

/*__________________________________________________________________GESTION DES POINTS__________________________________________________________________*/	
    private List<Card> USED_CARDS = new ArrayList<Card>();
    private int POINTS = 0;
    
    // Calcule le total des points de la board
    public int getPointsOfBoard(Board board, Player player) {
    	Objects.requireNonNull(board);
    	Objects.requireNonNull(player);
        int result = 0;
        for (Map.Entry<Point, Card> entry : boardMap.entrySet()) {
            Card card = entry.getValue();
         
            if (card.getRecto() && !USED_CARDS.contains(card)) { // Vérifie si la carte est recto et qu'elle n'a pas déjà été utilisée             
                result += addStandardCardsPoints(card, entry, board);
                POINTS += result;
                USED_CARDS.add(card);
            }
        }
        return POINTS;
    }
    
    private int addStandardCardsPoints(Card card, Map.Entry<Point, Card> entry, Board board) {
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(entry);
    	Objects.requireNonNull(board);
    	int score = 0;
    	if (card instanceof GoldCard) { // Vérifie si la carte est une GoldCard
            GoldCard goldCard = (GoldCard) card;
            String quest = goldCard.getQuest();
            score += addPointsOfQuest(quest, card, entry, board);
        } else {
        	score += card.getPoint();// Si ce n'est pas une GoldCard, ajoute simplement les points de la carte
        }
    	return score;
    }
    
    private int addPointsOfQuest(String quest, Card card, Map.Entry<Point, Card> entry, Board board) {
    	Objects.requireNonNull(quest);
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(entry);
    	Objects.requireNonNull(board);
    	int score = 0;
	    switch (quest) { // Vérifie la quête associée à la GoldCard et ajuste les points en conséquence
	        case "nb_Quill":
	        	score += card.getPoint() * numberOfQuill();
	            break;
	        case "nb_Inkwell":
	        	score += card.getPoint() * numberOfInkwell();
	            break;
	        case "nb_Manuscript":
	        	score += card.getPoint() * numberOfManuscript();
	            break;
	        case "coverCorner":
	        	score += card.getPoint() * recoveredCorner(entry.getKey(), board);
	            break;
	        case "normal":
	        	score += card.getPoint();
	            break;
	    }
	    return score;
    }

/*__________________________________________________________________GESTION DE LA SUPERPOSITION DES CARTES__________________________________________________________________*/	

    // Met à jour les coins récupérés lorsqu'une nouvelle carte les recouvre
    public void updateRecoveredCorner(Point point, Board board) {
    	Objects.requireNonNull(point);
    	Objects.requireNonNull(board);
        if ((boardMap.containsKey(new Point(point.getX() + 1, point.getY() + 1)))) { //Carte en bas à droite
            getCardAtPosition(new Point(point.getX() + 1, point.getY() + 1), board).setEmptyCorner1();
        }

        if ((boardMap.containsKey(new Point(point.getX() - 1, point.getY() + 1)))) { //Carte en bas à gauche
            getCardAtPosition(new Point(point.getX() - 1, point.getY() + 1), board).setEmptyCorner2();
        }

        if ((boardMap.containsKey(new Point(point.getX() - 1, point.getY() - 1)))) { //Carte en haut à gauche
            getCardAtPosition(new Point(point.getX() - 1, point.getY() - 1), board).setEmptyCorner3();
        }
 
        if ((boardMap.containsKey(new Point(point.getX() + 1, point.getY() - 1)))) { //Carte en haut à droite
            getCardAtPosition(new Point(point.getX() + 1, point.getY() - 1), board).setEmptyCorner4();
        }
    }
    
    
    // Renvoie le nombre de coins récupérés autour d'une position donnée pour la quete associée
    public static int recoveredCorner(Point position, Board board) {
    	Objects.requireNonNull(position);
    	Objects.requireNonNull(board);
        Point[] adjacentPositions = {
                new Point(position.getX() + 1, position.getY() + 1),
                new Point(position.getX() - 1, position.getY() - 1),
                new Point(position.getX() - 1, position.getY() + 1),
                new Point(position.getX() + 1, position.getY() - 1)
        };
        
        Card card = board.boardMap.get(position);
        int score = 0;
        if (card instanceof GoldCard && ((GoldCard) card).getQuest().equals("coverCorner")) {
            for (Point adjacentPosition : adjacentPositions) {
                if (board.boardMap.containsKey(adjacentPosition)) {
                    score++;
                }
            }
        }
        return score;
    }

/*__________________________________________________________________GESTION DES RESSOURCES__________________________________________________________________*/	

    // Renvoie le nombre de ressources du type spécifié
    public int numberOfRessource(String typeRessource) {
    	Objects.requireNonNull(typeRessource);
        int number = 0; 
 
        for (Map.Entry<Point, Card> entry : boardMap.entrySet()) { //Parcours de toutes les cartes de la board
            Card card = entry.getValue(); //Obtient la carte à partir de l'entrée de la map
            number += numberOfResourceOfStarterCard(card, typeRessource); 
            number += getCornersResource(card, typeRessource); 
            if (!(card instanceof StarterCard) && !card.getRecto() && card.getTypeRessource().equals(typeRessource)) { //Vérifie si la carte est verso et si son type de ressource correspond à celui spécifié                
                number++; //Incrémente le compteur si la condition est vérifiée
            }
        }
        return number; //Renvoi le nombre total de ressources du type spécifié sur la board
    }
    
    //Récupère le nombre de ressource dans les coins qui sont de la ressource demandé
    private int getCornersResource(Card card, String typeRessource) {
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(typeRessource);
    	int number = 0;
    	if (!(card instanceof StarterCard) && card.getRecto()) { // Vérifie si la carte est recto 
            for (int i = 1; i <= 4; i++) { // Parcours des coins de la carte pour compter les ressources
                Corner corner = card.getCorner(i);        
                if (corner.isRessourceCorner()) { // Vérifie si le coin est une ResourceCorner et correspond au type de ressource spécifié
                    String ressource = ((ResourceCorner) corner).getCornerTypeRessource();
                    if (ressource.equals(typeRessource)) {
                        number++; // Incrémente le compteur si la ressource correspond
                    }
                }
            }
        }
    	return number;
    }
    
    //Permet de connaître le nombre de ressource d'une carte de départ en fonction de son côté
    private int numberOfResourceOfStarterCard(Card card, String typeRessource) {
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(typeRessource);
    	int number = 0;
    	if (card instanceof StarterCard) { // Vérifie si la carte est une StarterCard 
        	if(!card.getRecto()) {
        		number += countPermanentRessource(card, typeRessource);// Compte les ressources permanentes sur la StarterCard
        		for (int i = 1; i <= 4; i++) { // Parcours des coins de la carte pour compter les ressources
                    Corner corner = card.getCorner(i);        
                    if (corner.isRessourceCorner()) { // Vérifie si le coin est une ResourceCorner et correspond au type de ressource spécifié
                        String ressource = ((ResourceCorner) corner).getCornerTypeRessource();
                        if (ressource.equals(typeRessource)) {
                            number++; // Incrémente le compteur si la ressource correspond
                        }
                    }
                }
        	} else {
        		number++;
        	} 
        }
    	return number;
    }
    
    // Compte le nombre de ressources permanentes sur une carte de départ
    private static int countPermanentRessource(Card card, String typeRessource) {
    	Objects.requireNonNull(card);
    	Objects.requireNonNull(typeRessource);
        int count = 0;
        for (String ressource : card.getpermanentRessources()) {
            if (ressource.equals(typeRessource)) {
                count++;
            }
        }
        return count;
    }

    
    
    // Renvoie le nombre d'animaux
    public int numberOfAnimal() {
        return numberOfRessource("Animal");
    }

    // Renvoie le nombre de champignons
    public int numberOfFungi() {
        return numberOfRessource("Fungi");
    }

    // Renvoie le nombre d'insectes
    public int numberOfInsect() {
        return numberOfRessource("Insect");
    }

    // Renvoie le nombre de plantes
    public int numberOfPlant() {
        return numberOfRessource("Plant");
    }

    
    
/*__________________________________________________________________GESTION DES ARTEFACTS__________________________________________________________________*/	

    // Renvoie le nombre d'artefacts du type spécifié
    public int numberOfArtifact(String object) {
    	Objects.requireNonNull(object);
        int number = 0; // Initialise le compteur du nombre d'artefacts
  
        for (Map.Entry<Point, Card> entry : boardMap.entrySet()) { // Parcourt toutes les cartes de la board
            Card card = entry.getValue(); 
            for (int i = 1; i <= 4; i++) { // Parcourt les quatre coins de la carte
                Corner corner = card.getCorner(i);
                if (corner instanceof ArtifactCorner) { // Vérifie si le coin est un ArtifactCorner et correspond au type d'artefact spécifié
                    String artifact = ((ArtifactCorner) corner).getTypeArtifact();
                    if (artifact.equals(object)) {
                        number++; // Incrémente le compteur si l'artefact correspond
                    }
                }
            }
        }
        return number; 
    }


    // Renvoie le nombre d'encriers
    public int numberOfInkwell() {
        return numberOfArtifact("Inkwell");
    }

    // Renvoie le nombre de manuscrits
    public int numberOfManuscript() {
        return numberOfArtifact("Manuscript");
    }

    // Renvoie le nombre de plumes
    public int numberOfQuill() {
        return numberOfArtifact("Quill");
    }

    

    
    
    
    

/*__________________________________________________________________GESTION DES SURLIGNAGES DE CARTE__________________________________________________________________*/	

    // Surligne tous les points valides sur la board
    public void highlightValidPlacements(ApplicationContext context, Card card, Board board) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(card);
    	Objects.requireNonNull(board);
        for (Point point : getFreePoints(board)) {
            BoardView.highlightCardAtPosition(context, point);
        }
    }

}
