package fr.uge.sae.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Player.Player;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.KeyboardKey;
import fr.umlv.zen5.Event.Action;

public class HallOfFame {

    private static final String FILENAME = "data/hallOfFame.txt";

/*__________________________________________________________________GESTION DE L'ECRITURE ET DE LA LECTURE DU FICHIER__________________________________________________________________*/
    //Affiche l'écran des 10 meilleurs joueurs dans le hallOfFame.txt
    public static void writeHallOfFame(List<Player> allPlayers, Player winner, String winnerName, int finalScore) {
    	Objects.requireNonNull(allPlayers);
    	Objects.requireNonNull(winner);
    	Objects.requireNonNull(winnerName);
    	
        List<String> fileContent = readHallOfFameFile();
        boolean lineInserted = checkTheBests(fileContent, finalScore, winnerName);
        //Uniquement les 10 meilleurs scores
        if (fileContent.size() > 10) {
            fileContent = fileContent.subList(0, 10);
        }
        //Si le joueur à le score le plus bas mais qu'il y a moins de 10 noms dans le classement
        if (!lineInserted && fileContent.size() < 10) {
            fileContent.add("Joueur " + winnerName + ": " + finalScore + " points");
        }

        writeHallOfFameFile(fileContent);
    }
    
    //Vérifie si le score est parmis les meilleurs
    private static boolean checkTheBests(List<String> fileContent, int finalScore, String winnerName) {
    	Objects.requireNonNull(fileContent);
    	Objects.requireNonNull(winnerName);
    	
    	for (int i = 0; i < fileContent.size() && i < 10; i++) {
            String line = fileContent.get(i);
            if (line.startsWith("Joueur ")) {
                String[] parts = line.split(": ");
                if (parts.length > 1) {
                    int score = Integer.parseInt(parts[1].split(" ")[0]);
                    if (finalScore > score) {
                        fileContent.add(i, "Joueur " + winnerName + ": " + finalScore + " points");
                        return true;
                    }
                }
            }
        }
    	return false;
    }
    
    //Lit le hallOfFame.txt
    private static List<String> readHallOfFameFile() {
        List<String> fileContent = new ArrayList<>();
        File file = new File(FILENAME);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Erreur lors de la création du fichier : " + e.getMessage());
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return fileContent;
    }
    
    //Inscrit le joueur dans le hall of fame 
    private static void writeHallOfFameFile(List<String> fileContent) {
    	Objects.requireNonNull(fileContent);
    	
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (String line : fileContent) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
    
    //Lance la boucle 
    private static void launchHallOfFame(ApplicationContext context) throws IOException {
        Objects.requireNonNull(context);

        var screenInfo = context.getScreenInfo();
        var width = 1536 / (1536 / screenInfo.getWidth());
        var height = 960 / (960 / screenInfo.getHeight());
        List<String> fileContent = readHallOfFameFile();
        var scores = new ArrayList<Integer>();
        var players = new ArrayList<String>();
        
        initializeInformations(players, scores, fileContent);
        drawNames(context, players, scores, width, height);
        setupButtonCoordinates(context);
        showQuitButton(context);
        showMenuButton(context);
        
        while (true) {
            var event = context.pollOrWaitEvent(10);
            if (event == null) {
                continue;
            }
            var action = event.getAction();
            if (action == Action.KEY_PRESSED) {
                KeyboardKey key = event.getKey();
                if (key == KeyboardKey.Q) {
                    System.out.println(key);
                    context.exit(0);
                    return;
                }
            } else if (action == Event.Action.POINTER_DOWN) {
            	var location = event.getLocation();
            	handlePointerDown(context, location);
            }
        }
    }
    
    //Récupérer les noms des joueurs et leur scores dans le hallofFame.txt
    private static void initializeInformations(List<String> players, List<Integer> scores, List<String> fileContent) {
    	Objects.requireNonNull(players);
    	Objects.requireNonNull(scores);
    	Objects.requireNonNull(fileContent);
    	
    	for (int i = 0; i < fileContent.size() && i < 10; i++) {
            String line = fileContent.get(i);
            if (line.startsWith("Joueur ")) {
                String[] parts = line.split(": ");
                String player = parts[0].substring(7); //Récupérer le nom du joueur
                int score = Integer.parseInt(parts[1].split(" ")[0]);
                players.add(player);
                scores.add(score);
            }
        }
    }
    
    //Dessine les noms des joueurs et leur scores
    private static void drawNames(ApplicationContext context, List<String> players, List<Integer> scores, float width, float height) {
    	Objects.requireNonNull(players);
    	Objects.requireNonNull(scores);
    	Objects.requireNonNull(context);
    	
    	context.renderFrame(graphics -> {
            graphics.setColor(Color.BLACK);
            graphics.fill(new Rectangle2D.Float(0, 0, width, height));
            graphics.setFont(new Font("Arial", Font.BOLD, 60));
            graphics.setColor(Color.WHITE);

            int yOffset = 100;
            for (int i = 0; i < players.size(); i++) {
                int place = i + 1;
                String player = players.get(i);
                int score = scores.get(i);
                graphics.drawString(place + " - Joueur " + player + " a gagné avec " + score + " points !", width / 2 - 500, yOffset);
                yOffset += 100;
            }
        });
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

        xButtonQuit = screenWidth * 0.8f;
        yButtonQuit = screenHeight * 0.9f;
        widthButtonQuit = screenWidth * 0.14f;
        heightButtonQuit = screenHeight * 0.06f;

        xButtonMenu = screenWidth * 0.8f;
        yButtonMenu = screenHeight * 0.83f;
        widthButtonMenu = screenWidth * 0.14f;
        heightButtonMenu = screenHeight * 0.06f;
    }
    
    //Afficher bouton quitter
    private static void showQuitButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit, "Quitter", Color.RED, Color.WHITE);
	}
	

	 //Afficher bouton Credit
    private static void showMenuButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xButtonMenu, yButtonMenu, widthButtonMenu, heightButtonMenu, "Menu principal", Color.ORANGE, Color.WHITE);
	}
    
    private static void handlePointerDown(ApplicationContext context, Point2D location) {
    	if (Menu.isButtonClicked(location, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit)) {
            context.exit(0);
        } else if (Menu.isButtonClicked(location, xButtonMenu, yButtonMenu, widthButtonMenu, heightButtonMenu)) {
            MenuChoix.main(null);
        }
    }
    
    public static void main(String[] args) {
        Application.run(Color.BLACK, context -> {
            try {
                launchHallOfFame(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
