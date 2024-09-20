package fr.uge.sae.View;

import fr.umlv.zen5.*;
import fr.umlv.zen5.Event;
import fr.uge.sae.Card.ObjectiveCard;
import fr.uge.sae.Player.Player;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuWin {
	
	/*__________________________________________________________________GESTION DE LA VICTOIRE__________________________________________________________________*/	

	private static String playerName = "";
	private static int FINAL_SCORE = 0;
    //Afficher les scores des joueurs
    private static void showWin(ApplicationContext context, List<Player> allPlayers, int initialScore, int maxScore, Map<Player, Integer> scoresMap, Player winner, List<Player> losers) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(allPlayers);
        Objects.requireNonNull(scoresMap);
        Objects.requireNonNull(losers);

        var width = Menu.getWidth(context);
        var height = Menu.getHeight(context);
        
        for (Player player : allPlayers) {
            int score = calculatePlayerScore(player);

            if (score > maxScore) {
                maxScore = score;
                winner = player;
            }
            scoresMap.put(player, score);
        }
        FINAL_SCORE += maxScore;
        var finalWinner = winner;
        losers.remove(finalWinner);

        context.renderFrame(graphics -> drawWinScreen(graphics, width, height, finalWinner, scoresMap, losers));
    }


    //Calcul les scores des joueurs par rapport à leurs objectifs
    private static int calculatePlayerScore(Player player) {
        Objects.requireNonNull(player);

        int score = player.getBoard().getPointsOfBoard(player.getBoard(), player);

        score += calculateObjectiveScore((ObjectiveCard) player.getObjective().get(0), player);
        score += calculateObjectiveScore((ObjectiveCard) player.getCommonObjective().get(0), player);
        score += calculateObjectiveScore((ObjectiveCard) player.getCommonObjective().get(1), player);
        
        return score;
    }

    //Calcul les scores d'objectifs
    private static int calculateObjectiveScore(ObjectiveCard card, Player player) {
        Objects.requireNonNull(card);
        Objects.requireNonNull(player);

        switch (card.getTypeObjective()) {
            case "Resource":
                return card.objectiveResource(player.getBoard());
            case "Artifact":
                return card.objectiveArtifact(player.getBoard(), card.getTwice());
            default:
                return card.objectivePattern(player.getBoard());
        }
    }


    //Affiche les points des différents joueurs de la partie
    private static void drawWinScreen(Graphics2D graphics, float width, float height, Player winner,  Map<Player, Integer> scoresMap, List<Player> losers) {
        Objects.requireNonNull(graphics);
        Objects.requireNonNull(winner);
        Objects.requireNonNull(scoresMap);
        Objects.requireNonNull(losers);

        var image = Image.loadImage("data/fond_victoire.jpg");
        Image.drawImage(graphics, image, 0, 0, width, height);
        graphics.setFont(new Font("Arial", Font.BOLD, 60));
        graphics.setColor(Color.WHITE);
        graphics.drawString("Le joueur " + winner.getIdPlayer() + " a gagné avec " + scoresMap.get(winner) + " points !", (width / 2 - 500), (int) (height * 0.2));

        int yOffset = 100;
        int xOffset = 80;
        for (Player loser : losers) {
            graphics.setFont(new Font("Arial", Font.BOLD, 40));
            graphics.drawString("Le joueur " + loser.getIdPlayer() + " a perdu avec " + scoresMap.get(loser) + " points !", (width / 2 - 500) + xOffset, (int) (height * 0.2) + yOffset);
            yOffset += 100;
        }
    }

    
    
    
    /*__________________________________________________________________GESTION DE LA SAISIE DANS LE HALL OF FAME__________________________________________________________________*/	

    
    private static final String FONT_NAME = "Arial";
    private static final int PROMPT_FONT_SIZE = 36;
    private static final int INPUT_FONT_SIZE = 24;
    private static final int BUTTON_FONT_SIZE = 20;
    private static final int BUTTON_WIDTH = 130;
    private static final int BUTTON_HEIGHT = 40;
    private static final int INPUT_BOX_WIDTH = 300;
    private static final int INPUT_BOX_HEIGHT = 50;
    private static final float BUTTON_Y_OFFSET = 50.0f;


    //Lancer le menu de saisie du nom
    public static void launchMenuName(ApplicationContext context, List<Player> allPlayers, int maxScore, int score, Map<Player, Integer> map, Player winner, List<Player> losers) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(allPlayers);

        setupButtonCoordinates(context);

        showWin(context, allPlayers, score, maxScore, map, winner, losers);
        showMenuButton(context);
        showQuitButton(context);

        boolean running = true;

        while (running) {
            Event event = context.pollOrWaitEvent(10);
            if (event == null) {
                continue;
            }

            handleEvent(context, event, allPlayers, winner, maxScore);
        }
    }

    //Affiche tout les éléments de la saisie
    private static void renderFrame(ApplicationContext context, float centerX, float centerY, float submitX, float submitY, float backspaceX, float backspaceY) {
        context.renderFrame(graphics -> {
            drawSentence(graphics, centerX, centerY);
            drawInputBox(graphics, centerX, centerY);
            drawPlayerName(graphics, centerX, centerY);
            drawSubmitButton(graphics, submitX, submitY);
            drawBackspaceButton(graphics, backspaceX, backspaceY);
        });
    }

    //Affiche la phrase "Entrez le nom du gagnant:"
    private static void drawSentence(Graphics2D graphics, float centerX, float centerY) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(FONT_NAME, Font.BOLD, PROMPT_FONT_SIZE));
        String prompt = "Entrez le nom du gagnant:";
        int promptWidth = graphics.getFontMetrics().stringWidth(prompt);
        graphics.drawString(prompt, centerX - promptWidth / 2, centerY - 50);
    }

    //Affiche la zone de saisie
    private static void drawInputBox(Graphics2D graphics, float centerX, float centerY) {
        graphics.setColor(Color.DARK_GRAY);
        graphics.fill(new RoundRectangle2D.Float(centerX - INPUT_BOX_WIDTH / 2, centerY - 25, INPUT_BOX_WIDTH, INPUT_BOX_HEIGHT, 10, 10));
    }

    //Affiche le nom du joueur saisi
    private static void drawPlayerName(Graphics2D graphics, float centerX, float centerY) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(FONT_NAME, Font.PLAIN, INPUT_FONT_SIZE));
        graphics.drawString(playerName, centerX - INPUT_BOX_WIDTH / 2 + 10, centerY + 10);
    }

    //Affiche le bouton de soumission
    private static void drawSubmitButton(Graphics2D graphics, float submitX, float submitY) {
        graphics.setColor(new Color(0, 102, 0));
        graphics.fill(new RoundRectangle2D.Float(submitX, submitY, BUTTON_WIDTH, BUTTON_HEIGHT, 10, 10));
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(FONT_NAME, Font.BOLD, BUTTON_FONT_SIZE));
        graphics.drawString("Soumettre", submitX + 30, submitY + 28);
    }

    //Affiche le bouton de retour
    private static void drawBackspaceButton(Graphics2D graphics, float backspaceX, float backspaceY) {
        graphics.setColor(Color.RED);
        graphics.fill(new RoundRectangle2D.Float(backspaceX, backspaceY, BUTTON_WIDTH, BUTTON_HEIGHT, 10, 10));
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(FONT_NAME, Font.BOLD, BUTTON_FONT_SIZE));
        graphics.drawString("Retour", backspaceX + 20, backspaceY + 28);
    }
    
    
    /*__________________________________________________________________GESTION DES INPUT__________________________________________________________________*/	

  //Gère les actions
    private static void handleEvent(ApplicationContext context, Event event, List<Player> allPlayers, Player winner, int maxScore) {
        var action = event.getAction();
        var location = event.getLocation();

        float centerX = Menu.getWidth(context) * 0.5f;
        float centerY = Menu.getHeight(context) * 0.7f;

        float submitX = centerX - BUTTON_WIDTH - 10;
        float submitY = centerY + BUTTON_Y_OFFSET;
        
        float backspaceX = centerX + 10;
        float backspaceY = centerY + BUTTON_Y_OFFSET;

        renderFrame(context, centerX, centerY, submitX, submitY, backspaceX, backspaceY);

        if (action == Event.Action.POINTER_DOWN) {
            handlePointerDown(context, location, submitX, submitY, backspaceX, backspaceY, allPlayers, winner, maxScore);
        } else if (action == Event.Action.KEY_PRESSED) {
            handleKeyPressed(event);
        }
    }
    
    //Gère les actions de clic de la souris
    private static void handlePointerDown(ApplicationContext context, Point2D location, float submitX, float submitY, float backspaceX, float backspaceY, List<Player> allPlayers, Player winner, int maxScore) {
        if (Menu.isButtonClicked(location, submitX, submitY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            HallOfFame.writeHallOfFame(allPlayers, winner, playerName, FINAL_SCORE);
            context.exit(0);
        } else if (Menu.isButtonClicked(location, backspaceX, backspaceY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            if (!playerName.isEmpty()) {
                playerName = playerName.substring(0, playerName.length() - 1);
            }
        } else if (Menu.isButtonClicked(location, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit)) {
            context.exit(0);
        } else if (Menu.isButtonClicked(location, xButtonMenu, yButtonMenu, widthButtonMenu, heightButtonMenu)) {
            MenuChoix.main(null);
        }
    }


    //Gère les saisie de caractères
    private static void handleKeyPressed(Event event) {
        var key = event.getKey();
        char ch = getCharFromKey(key);
        if (Character.isLetterOrDigit(ch)) {
            playerName += ch;
        }
    }

    //Permet de récupérer n'importe quel caractère
    private static char getCharFromKey(KeyboardKey key) {
        switch (key) {
            case A: return 'a';
            case B: return 'b';
            case C: return 'c';
            case D: return 'd';
            case E: return 'e';
            case F: return 'f';
            case G: return 'g';
            case H: return 'h';
            case I: return 'i';
            case J: return 'j';
            case K: return 'k';
            case L: return 'l';
            case M: return 'm';
            case N: return 'n';
            case O: return 'o';
            case P: return 'p';
            case Q: return 'q';
            case R: return 'r';
            case S: return 's';
            case T: return 't';
            case U: return 'u';
            case V: return 'v';
            case W: return 'w';
            case X: return 'x';
            case Y: return 'y';
            case Z: return 'z';
            case SPACE: return ' ';
            default: return '\0';
        }
    }
    
    /*__________________________________________________________________BOUTONS EN BAS DE L'ECRAN__________________________________________________________________*/	
    private static float xButtonQuit;
    private static float yButtonQuit;
    private static float widthButtonQuit;
    private static float heightButtonQuit;
    
    private static float xButtonMenu;
    private static float yButtonMenu;
    private static float widthButtonMenu;
    private static float heightButtonMenu;
    
  //Met en place toutes les positions des boutons
    private static void setupButtonCoordinates(ApplicationContext context) {
        var screenWidth = Menu.getWidth(context);
        var screenHeight = Menu.getHeight(context);

        xButtonQuit = screenWidth * 0.85f;
        yButtonQuit = screenHeight * 0.85f;
        widthButtonQuit = screenWidth * 0.14f;
        heightButtonQuit = screenHeight * 0.06f;

        xButtonMenu = screenWidth * 0.70f;
        yButtonMenu = screenHeight * 0.85f;
        widthButtonMenu = screenWidth * 0.14f;
        heightButtonMenu = screenHeight * 0.06f;
    }
    
    //Afficher bouton quitter
    private static void showQuitButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit, "Quitter", Color.RED, Color.WHITE);
	}
	

	 //Afficher bouton HallOfFame
    private static void showMenuButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xButtonMenu, yButtonMenu, widthButtonMenu, heightButtonMenu, "Menu principal", Color.ORANGE, Color.WHITE);
	}

  	
}
