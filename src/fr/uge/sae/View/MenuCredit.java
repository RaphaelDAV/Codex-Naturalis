package fr.uge.sae.View;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Player.Player;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.KeyboardKey;
import fr.umlv.zen5.Event.Action;

public class MenuCredit {
	
	//Lance la boucle
	private static void launchCredit(ApplicationContext context) throws IOException {
        Objects.requireNonNull(context);
        var screenInfo = context.getScreenInfo();
        var width = screenInfo.getWidth();
        var height = screenInfo.getHeight();
        var image = Image.loadImage("data/credit.png");
        context.renderFrame(graphics -> {
        	Image.drawImage(graphics, image, 0, 0, width, height);
        });
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
                    context.exit(0);
                    return;
                }
            } else if (action == Event.Action.POINTER_DOWN) {
            	var location = event.getLocation();
            	handlePointerDown(context, location);
            }
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
            	launchCredit(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
