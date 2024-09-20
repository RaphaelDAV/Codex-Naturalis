package fr.uge.sae.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Corner.*;
import fr.uge.sae.Back.Point;
import fr.uge.sae.Card.Card;
import fr.uge.sae.Card.GoldCard;
import fr.uge.sae.Card.StarterCard;
import fr.uge.sae.Card.ObjectiveCard;
import fr.umlv.zen5.ApplicationContext;

public class CardView {

    // Définition de la taille des cartes
    private static final double WIDTH_CARD = 250;
    private static final double HEIGHT_CARD = 125;
    private static final double WIDTH_CORNER = 50;

    // Transformation des doubles en float pour éviter les problèmes
    public static float widthFloat = (float) WIDTH_CARD;
    public static float heightFloat = (float) HEIGHT_CARD;
    public static float widthCornerFloat = (float) WIDTH_CORNER;

    // Variables de décalage en X et en Y
    private static float decalage_X = 0;
    private static float decalage_Y = 0;
    
    /*__________________________________________________________________GESTION DU DEPLACEMENT DES CARTES__________________________________________________________________*/	

    // Méthodes pour gérer le décalage en hauteur et en largeur
    public static void YToUp() {
        decalage_Y -= 50;
    }

    public static void YToDown() {
        decalage_Y += 50;
    }

    public static void XToRight() {
        decalage_X += 50;
    }

    public static void XToLeft() {
        decalage_X -= 50;
    }
    
/*__________________________________________________________________AFFICHAGE D'UNE CARTE__________________________________________________________________*/	


    // Méthode pour convertir les coordonnées en Pixel
    public static Point CoordinatesToPixel(ApplicationContext context, Point initialCoordinates) {
        Objects.requireNonNull(initialCoordinates, "initialCoordinates ne peut pas être null");
        Objects.requireNonNull(context);
        // Définition des points de la carte de départ
        var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();
        double startingX = (width / 2) - (widthFloat / 2) + decalage_X;
        double startingY = (height / 2) - (heightFloat / 2) + decalage_Y;

        double xInPixel = startingX + ((WIDTH_CARD * initialCoordinates.getX()) - (WIDTH_CORNER * initialCoordinates.getX()));
        double yInPixel = startingY + ((HEIGHT_CARD * initialCoordinates.getY()) - (WIDTH_CORNER * initialCoordinates.getY()));

        return new Point(xInPixel, yInPixel);
    }

    
    
    
    // Affichage d'une carte
    public static void showCard(ApplicationContext context, Card card) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(card);

        Point coordinates = CoordinatesToPixel(context, card.getCoordinates());
        if (coordinates == null) {
            System.out.println("Les coordonnées de la carte sont null.");
            return;
        }

        double x = coordinates.getX();
        double y = coordinates.getY();
        context.renderFrame(graphics -> {
            if (card instanceof StarterCard) {
                drawStarterCard(context, graphics, card, (float) x, (float) y);
            } else {
                drawStandardCard(context, graphics, card, (float) x, (float) y);
            }
        });
    }
    
    
    
 // Affiche une carte selon une position donnée
    public static void showCardByPosition(ApplicationContext context, Card card, Point p) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(card);
        Objects.requireNonNull(p);

        double x = p.getX();
        double y = p.getY();

        context.renderFrame(graphics -> {
            if (card instanceof StarterCard) {
                drawStarterCard(context, graphics, card, (float) x, (float) y);
            } else if(card instanceof ObjectiveCard) {
            	drawObjectiveCard(context, graphics, card, (float) x, (float) y);
            } else {
                drawStandardCard(context, graphics, card, (float) x, (float) y);
            }
        });
    }

    
    
/*__________________________________________________________________DIFFERENTS TYPES DE CARTE__________________________________________________________________*/	

    // Dessine une carte de départ
    private static void drawStarterCard(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        var starterCard = Image.loadImage("data/Starter.png");
        Image.drawImage(graphics, starterCard, x, y, widthFloat, heightFloat);
        
        if (card.getRecto()) {
        	card.cornerVerso();
        } 
        // Affiche les ressources permanentes si elles existent
        if (card.getpermanentRessources().size() != 0 && !card.getRecto()) {
            graphics.setColor(Color.BLACK);
            drawPermanentResources(graphics, card, x, y);
        }

        drawCorners(context, graphics, card, x, y);
    }
    
    // Dessine une carte objectif avec le fond qui lui est associé
    private static void drawObjectiveCard(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        if (card instanceof ObjectiveCard) {
        	var image = Image.loadImage("data/"+((ObjectiveCard) card).getBackground()+".png");
	        Image.drawImage(graphics, image, x, y, 400, 200);
        }    
    }
        
    // Dessine une carte standard
    private static void drawStandardCard(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);

        var image = getCardColor(card.getTypeRessource());
                
        Image.drawImage(graphics,image , x, y, widthFloat, heightFloat);
        if (card.getRecto()) {           
            if (card instanceof GoldCard) { // Affiche le fond de la zone de coût pour les GoldCard
            	drawBackgroundCost(graphics, (GoldCard) card, x, y);
            } 
            
            drawPoints(context, graphics, card,x,y);// Affiche les points
        } else {   
        	//Affiche le verso des cartes
        	drawVersoRessource(context,graphics,card,x,y); // Affiche la ressource permanente
        }
        
        drawCorners(context, graphics, card, x, y);// Affiche les coins
        drawGoldCardQuest(graphics, card, x, y);
    }
    
    
    
    
    
    // Affiche la ressource du verso des cartes
    private static void drawVersoRessource(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        
        final var permanentRessource = Image.loadImage("data/PermanentRessource.png");
        Image.drawImage(graphics, permanentRessource, x + (float) (WIDTH_CARD / 2) - 25, y + (float) (HEIGHT_CARD / 2) - 40, 50, 80);
        
        String ressource = card.getTypeRessource();
        BufferedImage resourceImage = getCardLabel(ressource);
        if (resourceImage != null) {
            Image.drawImage(graphics, resourceImage, x + (float) ((WIDTH_CARD / 2) - 17.5), y + (float) ((HEIGHT_CARD / 2) - 20), 40, 40);
        }
    }


    // Affiche le nombre de points
    private static void drawPoints(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        
        int numberOfPoints = card.getPoint();
        String quest = card instanceof GoldCard ? ((GoldCard) card).getQuest() : "normal";
        
        String pointsPath = getPointsPath(numberOfPoints, quest);
        if (pointsPath != null) {
            int xOffset = quest.equals("normal") ? -20 : -60;
            int yOffset = quest.equals("normal") ? 40 : 120;
            var pointsImage = Image.loadImage(pointsPath);
            Image.drawImage(graphics, pointsImage, x + (float) (WIDTH_CARD / 2 + xOffset), y, yOffset, 40);
        }
    }


/*__________________________________________________________________DIFFERENTS TYPES DE CARTE ET DE SYMBOLES__________________________________________________________________*/	

    
    // Obtient la couleur de la carte en fonction du type de ressource
    private static BufferedImage getCardColor(String typeRessource) {
        Objects.requireNonNull(typeRessource);
        switch (typeRessource) {
            case "Animal":
                return Image.loadImage("data/Animal.png");
            case "Fungi":
                return Image.loadImage("data/Champignon.png");
            case "Insect":
                return Image.loadImage("data/Insecte.png");
            case "Plant":
                return Image.loadImage("data/Plante.png");
            default:
                return null;
        }
    }

    // Obtient le label de la carte en fonction du type de ressource
    private static BufferedImage getCardLabel(String typeRessource) {
        Objects.requireNonNull(typeRessource);
        switch (typeRessource) {
	        case "Animal":
	            return Image.loadImage("data/chicken.png");
	        case "Fungi":
	            return Image.loadImage("data/bomb.png");
	        case "Insect":
	            return Image.loadImage("data/fairy.png");
	        case "Plant":
	            return Image.loadImage("data/leaf.png");
	        default:
                return null;
        }
    }
    
 // Obtient le label de la carte en fonction du type de artefact
    private static BufferedImage getCardArtefact(String typeRessource) {
        Objects.requireNonNull(typeRessource);
        switch (typeRessource) {
	        case "Inkwell":
	            return Image.loadImage("data/potion.png");
	        case "Quill":
	            return Image.loadImage("data/arrow.png");
	        case "Manuscript":
	            return Image.loadImage("data/map.png");
	        default:
                return null;
        }
    }
    
 // Détermine le chemin de l'image des points
    private static String getPointsPath(int numberOfPoints, String quest) {
        String suffix = quest.equals("normal") ? "" : "bis";
        switch (numberOfPoints) {
            case 1:
                return "data/1" + suffix + ".png";
            case 2:
                return "data/2" + suffix + ".png";
            case 3:
                return "data/3" + suffix + ".png";
            case 4:
                return "data/4" + suffix + ".png";
            case 5:
                return "data/5" + suffix + ".png";
            default:
                return null;
        }
    }
    
/*__________________________________________________________________GESTION DES COINS__________________________________________________________________*/	

    // Dessine les coins de la carte
    private static void drawCorners(ApplicationContext context, Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        
        boolean isGoldCard = card instanceof GoldCard;
        boolean isStarterCard = card instanceof StarterCard;
        showCorner(context, card, x, y, isGoldCard,isStarterCard);
    }
    
    
    // Affiche les coins de la carte
    private static void showCorner(ApplicationContext context, Card card, double x, double y, boolean gold,boolean starter) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(card);

        float xFloat = (float) x;
        float yFloat = (float) y;
        float[][] corners = {
            {xFloat + (widthCornerFloat * (1.0f / 3.0f)), yFloat + (widthCornerFloat * (2.0f / 3.0f))},
            {xFloat + widthFloat - (widthCornerFloat * (2.0f / 3.0f)), yFloat + (widthCornerFloat * (2.0f / 3.0f))},
            {xFloat + (widthCornerFloat * (1.0f / 3.0f)), yFloat + heightFloat - (widthCornerFloat * (1.0f / 3.0f))},
            {xFloat + widthFloat - (widthCornerFloat * (2.0f / 3.0f)), yFloat + heightFloat - (widthCornerFloat * (1.0f / 3.0f))}
        };
        Corner[] cardCorners = {card.getCorner(1), card.getCorner(2), card.getCorner(4), card.getCorner(3)};

        context.renderFrame(graphics -> {
            for (int i = 0; i < 4; i++) {
                Corner corner = cardCorners[i];
                if (isNotInvisibleCorner(corner)) {
                    drawCornerBackground(graphics, xFloat, yFloat, i, gold,starter);
                    prepareCorner(graphics, corner, corners[i][0], corners[i][1]);
                }
            }
        });
    }
    
    
    //Indique si le coin n'est pas invisible
    private static boolean isNotInvisibleCorner(Corner corner) {
    	Objects.requireNonNull(corner);

        return (corner.isRessourceCorner() || corner.isArtifactCorner() || corner.isEmptyCorner()) && !(corner instanceof InvisibleCorner);
    }
    
    //Dessine l'arrière de coin par rapport à son type
    private static void drawCornerBackground(Graphics2D graphics, float xFloat, float yFloat, int cornerIndex, boolean gold,boolean starter) {
    	Objects.requireNonNull(graphics);

        float xOffset = (cornerIndex % 2 == 0) ? 0 : widthFloat - widthCornerFloat;
        float yOffset = (cornerIndex < 2) ? 0 : heightFloat - widthCornerFloat;
        
        BufferedImage goldCorner = null;
        BufferedImage Corner = null;
        BufferedImage starterCorner = null;
        switch (cornerIndex) {
        	case 0:
        		starterCorner = Image.loadImage("data/StarterCorner1.png");
        		goldCorner = Image.loadImage("data/GoldCorner1.png"); 
        		Corner = Image.loadImage("data/Corner1_3.png"); 
        		break;
        	case 1:
        		starterCorner = Image.loadImage("data/StarterCorner2.png");
        		goldCorner = Image.loadImage("data/GoldCorner2.png"); 
        		Corner = Image.loadImage("data/Corner2_4.png"); 
        		break;
        	case 2:
        		starterCorner = Image.loadImage("data/StarterCorner4.png");
        		goldCorner = Image.loadImage("data/GoldCorner4.png"); 
        		Corner = Image.loadImage("data/Corner2_4.png"); 
        		break;
        	case 3:
        		starterCorner = Image.loadImage("data/StarterCorner3.png");
        		goldCorner = Image.loadImage("data/GoldCorner3.png"); 
        		Corner = Image.loadImage("data/Corner1_3.png"); 
        		break;
        }
        if (gold) {
            float xGold = xOffset + xFloat - (cornerIndex % 2 == 0 ? 0 : 5);
            float yGold =  yOffset + yFloat - (cornerIndex < 2 ? 0 : 5);
            float widthGold = widthCornerFloat + 5;
            float heightGold = widthCornerFloat + 5;
            Image.drawImage(graphics, goldCorner, xGold, yGold, widthGold, heightGold);
        }
        if (starter) {
            float xStarter = xOffset + xFloat - (cornerIndex % 2 == 0 ? 0 : 5);
            float yStarter =  yOffset + yFloat - (cornerIndex < 2 ? 0 : 5);
            float widthStarter = widthCornerFloat + 5;
            float heightStarter = widthCornerFloat + 5;
            Image.drawImage(graphics, starterCorner, xStarter, yStarter, widthStarter, heightStarter);
        }
        
        Image.drawImage(graphics, Corner, xOffset + xFloat, yOffset + yFloat, widthCornerFloat, widthCornerFloat);
    
    }

    //Préparer le coin pour qu'il soit dessiner
    public static void prepareCorner(Graphics2D graphics, Corner corner, float x, float y) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(corner);
        graphics.setFont(new Font("Arial", Font.BOLD, (int) ((34 * widthCornerFloat) / 75.0)));

        drawResourceInCorner(graphics, corner, x, y);
        drawArtifactInCorner(graphics, corner, x, y);
    }
    
  //Dessine l'artefact appropriée sur le coin
    public static void drawArtifactInCorner(Graphics2D graphics, Corner corner, float x, float y) {
    	Objects.requireNonNull(graphics);
        Objects.requireNonNull(corner);

        if (corner instanceof ArtifactCorner) {
            String artifact = ((ArtifactCorner) corner).getTypeArtifact();
            
            Image.drawImage(graphics, getCardArtefact(artifact), x -12, y - 28, 40, 40);       
        }
    }
        
    //Dessine la ressource appropriée sur le coin
    public static void drawResourceInCorner(Graphics2D graphics, Corner corner, float x, float y) {
    	Objects.requireNonNull(graphics);
        Objects.requireNonNull(corner);

    	if (corner instanceof ResourceCorner) {
            String ressource = ((ResourceCorner) corner).getCornerTypeRessource();
            if (ressource == "Plant") {
            	Image.drawImage(graphics, getCardLabel(ressource),x-8, y-28 , 40, 40);
            }else {
            	Image.drawImage(graphics, getCardLabel(ressource),x-12, y-28 , 40, 40);
            }
    	}
    }
    
    
/*__________________________________________________________________GESTION DES QUETES__________________________________________________________________*/	
  
    
    

    // Dessine une quête sur une carte GoldCard si elle existe
    private static void drawGoldCardQuest(Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);

        if (isGoldCardWithRecto(card)) {
            String quest = ((GoldCard) card).getQuest();
            if (quest != null) {
                BufferedImage image = loadQuestImage(quest);
                if (image != null) {
                    drawQuestImage(graphics, image, x, y, quest);
                }
            }
        }
    }

    
    //Vérifie si la carte est une GoldCard avec le recto visible
    private static boolean isGoldCardWithRecto(Card card) {
        Objects.requireNonNull(card);

        return card instanceof GoldCard && card.getRecto();
    }

    
    //Charge l'image correspondant à la quête.
    private static BufferedImage loadQuestImage(String quest) {
        switch (quest) {
            case "coverCorner":
                return Image.loadImage("data/CoverCorner.png");
            case "nb_Inkwell":
                return Image.loadImage("data/potion.png");
            case "nb_Quill":
                return Image.loadImage("data/arrow.png");
            case "nb_Manuscript":
                return Image.loadImage("data/map.png");
            default:
                return null;
        }
    }

    
    //Dessine l'image de la quête sur la carte
    private static void drawQuestImage(Graphics2D graphics, BufferedImage image, float x, float y, String quest) {
    	Objects.requireNonNull(graphics);
        Objects.requireNonNull(image);

    	float imageX = (float) (x + WIDTH_CARD / 2 - 5);
        float imageY = y + 5;
        float imageWidth = 40;
        float imageHeight = 20;

        if ("coverCorner".equals(quest)) {
            imageX += 5;
            imageWidth = 30;
        }

        Image.drawImage(graphics, image, imageX, imageY, imageWidth, imageHeight);
    }
    


    
/*__________________________________________________________________GESTION DU COUT__________________________________________________________________*/	
    

    
    private static final int SPACE_BETWEEN_SYMBOLS = 24;
    private static final float HEIGHT_COST = 30;

    // Dessine les symboles représentant le coût de la carte
    public static void drawCostSymbols(Graphics2D graphics, Card card, float xFloat, float yFloat) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);

        int costNumber = ((GoldCard) card).getTotalCostElements();
        int xOffset = calculateXOffset(costNumber);
        int currentX = (int) (xFloat + WIDTH_CORNER + SPACE_BETWEEN_SYMBOLS + xOffset);

        for (HashMap.Entry<String, Integer> entry : card.getCost().entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            BufferedImage image = getCardLabel(key);

            drawCostSymbols(graphics, image, value, currentX, yFloat);
            currentX += value * SPACE_BETWEEN_SYMBOLS;
        }
    }

    // Calcule le décalage de départ en X en fonction du nombre d'éléments de coût
    private static int calculateXOffset(int costNumber) {
        switch (costNumber) {
            case 1:
                return 38;
            case 2:
                return 30;
            case 3:
                return 25;
            case 4:
                return 15;
            case 5:
                return 7;
            default:
                return 0;
        }
    }

    // Dessine le fond du coût d'une carte
    private static void drawBackgroundCost(Graphics2D graphics, GoldCard card, float x, float y) {
        Objects.requireNonNull(graphics, "Graphics2D object cannot be null");
        Objects.requireNonNull(card, "GoldCard object cannot be null");

        int costNumber = card.getTotalCostElements();
        BufferedImage costImage = loadCostImage(costNumber);

        if (costImage != null) {
            float xCost = (float) (x + WIDTH_CORNER + calculateXCostOffset(costNumber));
            float yCost = (float) (y + HEIGHT_CARD - HEIGHT_COST);
            float widthCost = calculateWidthCost(costNumber);
            Image.drawImage(graphics, costImage, xCost, yCost, widthCost, HEIGHT_COST);
        }

        drawCostSymbols(graphics, card, x, y);
    }

    // Charge l'image de coût en fonction du nombre d'éléments de coût
    private static BufferedImage loadCostImage(int costNumber) {
        switch (costNumber) {
            case 1:
                return Image.loadImage("data/Cost1.png");
            case 2:
                return Image.loadImage("data/Cost2.png");
            case 3:
                return Image.loadImage("data/Cost3.png");
            case 4:
                return Image.loadImage("data/Cost4.png");
            case 5:
                return Image.loadImage("data/Cost5.png");
            default:
                return null;
        }
    }

    // Calcule le décalage en X pour l'image de coût
    private static float calculateXCostOffset(int costNumber) {
        switch (costNumber) {
            case 1:
                return 40;
            case 2:
                return 30;
            case 3:
                return 20;
            case 4:
                return 10;
            case 5:
                return 2;
            default:
                return 0;
        }
    }

    // Calcule la largeur de l'image de coût
    private static float calculateWidthCost(int costNumber) {
        switch (costNumber) {
            case 1:
                return 50;
            case 2:
                return 75;
            case 3:
                return 100;
            case 4:
                return 125;
            case 5:
                return 150;
            default:
                return 0;
        }
    }

    // Dessine les symboles de coût
    private static void drawCostSymbols(Graphics2D graphics, BufferedImage image, int value, int currentX, float yFloat) {
        for (int i = 1; i <= value; i++) {
            if (image != null) {
                graphics.drawImage(image, currentX - 15, (int) (yFloat + HEIGHT_CARD - 28), 28, 28, null);
                currentX += SPACE_BETWEEN_SYMBOLS;
            }
        }
    }


/*__________________________________________________________________GESTION DES RESSOURCES PERMANENTES__________________________________________________________________*/	

    // Dessine les ressources permanentes sur une carte
    private static void drawPermanentResources(Graphics2D graphics, Card card, float x, float y) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(card);
        List<String> resources = card.getpermanentRessources();
        if (resources == null) return;
        int numberPermanentRessources = resources.size();
        switch (numberPermanentRessources) {
	        case 0:
	        	return;
	        case 1:
	        	var image = Image.loadImage("data/Ressource1.png"); 
        		Image.drawImage(graphics, image, x + (float) (WIDTH_CARD/2 - widthCornerFloat/2 ), y + (float) (HEIGHT_CARD/2 - widthCornerFloat/2 ), widthCornerFloat, widthCornerFloat);
	        	break;
	        case 2:
	        	var image2 = Image.loadImage("data/Ressource2.png"); 
        		Image.drawImage(graphics, image2, x + (float) (WIDTH_CARD/2 - widthCornerFloat/2), y + (float) (HEIGHT_CARD/2 - (widthCornerFloat+25)/2 ), widthCornerFloat, widthCornerFloat+25);
	        	break;
	        case 3:
	        	var image3 = Image.loadImage("data/Ressource3.png"); 
        		Image.drawImage(graphics, image3, x + (float) (WIDTH_CARD/2 - widthCornerFloat/2), y + (float) (HEIGHT_CARD/2 - (widthCornerFloat+50)/2 ), widthCornerFloat, widthCornerFloat+50);
	        	break;     	
	    }
        
        graphics.setFont(new Font("Arial", Font.BOLD, (int) ((40 * widthCornerFloat) / 75.0)));
        int yOffSet = 0;
        switch(resources.size()) {
        	case 0:
        		break;
        	case 1:
        		yOffSet = 20;
        		break;
        	case 2:
        		yOffSet = 5;
        		break;
        	case 3:
        		yOffSet = -8;
        		break;
        }
        int count =1;
        for (String resource : resources) {
            Image.drawImage(graphics, getCardLabel(resource),  x + (widthFloat / 2) - 15,  y +yOffSet + (30*count)-5, 30, 30);
            count++;
        }
    }

    

    


}
