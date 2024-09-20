package fr.uge.sae.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.Objects;


import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.KeyboardKey;
import fr.umlv.zen5.Event.Action;

public class Menu {

/*__________________________________________________________________GESTION DU FOND D'ECRAN__________________________________________________________________*/	
	
	//Afficher le fond d'écran
	private static void showBackground(ApplicationContext context) {
		Objects.requireNonNull(context);
		
        float screenWidth = context.getScreenInfo().getWidth();
        float screenHeight = context.getScreenInfo().getHeight();

        context.renderFrame(graphics -> {
            graphics.setColor(Color.BLACK);
            graphics.fill(new Rectangle2D.Float(0, 0, screenWidth, screenHeight));
            var image = Image.loadImage("data/fond.jpg");
            Image.drawImage(graphics,image,0, 0, screenWidth, screenHeight);
            var image2 = Image.loadImage("data/Titre.png");
            Image.drawImage(graphics,image2,screenWidth/4, screenHeight/20, screenWidth/2, screenHeight/2);
        });
	}
	
	
	
/*__________________________________________________________________GESTION DES BOUTONS__________________________________________________________________*/

	// Initialisation des tailles et positions des boutons
	private static float xButtonQuit;
	private static float yButtonQuit ;
	private static float widthButtonQuit;
	private static float heightButtonQuit;

	private static float xCreditButton;
	private static float yCreditButton;
	private static float widthButtonCredit;
	private static float heightButtonCredit;

	private static float xHallOfFameButton;
	private static float yHallOfFameButton;
	private static float widthButtonHallOfFame ;
	private static float heightButtonHallOfFame;

	
	// Méthode pour obtenir la largeur de l'écran
	public static float getWidth(ApplicationContext context) {
		Objects.requireNonNull(context);
		return context.getScreenInfo().getWidth();
	}
	

	// Méthode pour obtenir la hauteur de l'écran
	public static float getHeight(ApplicationContext context) {
		Objects.requireNonNull(context);
		return context.getScreenInfo().getHeight();
	}
	

	// Méthode pour dessiner un bouton avec texte
	public static void drawButton(ApplicationContext context, float x, float y, float width, float height, String text, Color backgroundColor, Color textColor) {
		Objects.requireNonNull(context);
		
	    context.renderFrame(graphics -> {
	        drawRoundedRectangleWithBorder(context, x, y, width, height, 50, 50, backgroundColor, Color.WHITE);
	        graphics.setColor(textColor);
	        graphics.setFont(new Font("Monospaced", Font.BOLD, 30));

	        FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
	        int textX = (int) (x + (width - metrics.stringWidth(text)) / 2);
	        int textY = (int) (y + ((height - metrics.getHeight()) / 2) + metrics.getAscent());
	        graphics.drawString(text, textX, textY);
	    });
	}
	

	// Afficher bouton quitter
	private static void showQuitButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    drawButton(context, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit, "Quitter", Color.RED, Color.WHITE);
	}
	

	// Afficher bouton crédit
	private static void showCreditButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    drawButton(context, xCreditButton, yCreditButton, widthButtonCredit, heightButtonCredit, "Crédits", Color.GRAY, Color.WHITE);
	}
	

	// Afficher bouton Hall of Fame
	private static void showHallOfFameButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    drawButton(context, xHallOfFameButton, yHallOfFameButton, widthButtonHallOfFame, heightButtonHallOfFame, "Hall Of Fame", Color.ORANGE, Color.WHITE);
	}
  	
	
	public static void drawRoundedRectangleWithBorder(ApplicationContext context, float x, float y, float width, float height, 
            float arcWidth, float arcHeight, Color fillColor, Color borderColor) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(fillColor);
		Objects.requireNonNull(borderColor);
		
		context.renderFrame(graphics -> {
			// Ombre
			graphics.setColor(new Color(0, 0, 0, 100));
			graphics.fill(new RoundRectangle2D.Float(x + 5, y + 5, width, height, arcWidth, arcHeight));
			
			// Remplissage avec dégradé
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color[] colors = { fillColor.brighter(), fillColor.darker() };
			float[] fractions = { 0f, 1f };
			
			// Vérification si les points de début et de fin sont identiques
			float gradientStartY = y;
			float gradientEndY = y + height;
			if (gradientStartY == gradientEndY) {
				// Modifier légèrement le point de fin pour éviter l'erreur
				gradientEndY = gradientStartY + 1;
			}
			
			LinearGradientPaint gradientPaint = new LinearGradientPaint(x, gradientStartY, x, gradientEndY, fractions, colors);
			graphics.setPaint(gradientPaint);
			graphics.fill(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
			
			// Bordures
			graphics.setColor(borderColor);
			graphics.draw(new RoundRectangle2D.Float(x - 2, y - 2, width + 2, height + 2, arcWidth, arcHeight));
		});
	}
	
	
  	
	
/*__________________________________________________________________GESTION DU MENU__________________________________________________________________*/

	 // Constantes pour les tailles relatives des boutons
	 private static final float BUTTON_WIDTH_RATIO = 0.14f;
	 private static final float BUTTON_HEIGHT_RATIO = 0.06f;
	 private static final float QUIT_BUTTON_X_RATIO = 0.85f;
	 private static final float BUTTON_Y_RATIO = 0.85f;
	 private static final float CREDIT_BUTTON_X_RATIO = 0.70f;
	 private static final float HALL_OF_FAME_BUTTON_X_RATIO = 0.02f;
	
	
	
	 // Méthode pour lancer le menu
	 private static void launchMenu(ApplicationContext context, String[] args) throws IOException {
	     Objects.requireNonNull(context, "Le contexte ne peut pas être nul");
	
	     // Initialisation des tailles et positions
	     initializeButtonSizes(context);
	
	     // Affichage du fond d'écran et des boutons
	     showBackground(context);
	     showCreditButton(context);
	     showQuitButton(context);
	     showHallOfFameButton(context);
	
	     // Boucle principale du menu
	     runMenuLoop(context, args);
	 }
	
	 
	 // Initialisation des tailles et positions des boutons
	 private static void initializeButtonSizes(ApplicationContext context) {
	     float screenWidth = getWidth(context);
	     float screenHeight = getHeight(context);
	
	     xButtonQuit = screenWidth * QUIT_BUTTON_X_RATIO;
	     yButtonQuit = screenHeight * BUTTON_Y_RATIO;
	     widthButtonQuit = screenWidth * BUTTON_WIDTH_RATIO;
	     heightButtonQuit = screenHeight * BUTTON_HEIGHT_RATIO;
	
	     xCreditButton = screenWidth * CREDIT_BUTTON_X_RATIO;
	     yCreditButton = screenHeight * BUTTON_Y_RATIO;
	     widthButtonCredit = screenWidth * BUTTON_WIDTH_RATIO;
	     heightButtonCredit = screenHeight * BUTTON_HEIGHT_RATIO;
	
	     xHallOfFameButton = screenWidth * HALL_OF_FAME_BUTTON_X_RATIO;
	     yHallOfFameButton = screenHeight * BUTTON_Y_RATIO;
	     widthButtonHallOfFame = screenWidth * BUTTON_WIDTH_RATIO;
	     heightButtonHallOfFame = screenHeight * BUTTON_HEIGHT_RATIO;
	 }
	 
	
	 // Boucle principale du menu
	 private static void runMenuLoop(ApplicationContext context, String[] args) {
	     while (true) {
	         try {
	             displayStartText(context, Color.WHITE);
	             Thread.sleep(500);
	             displayStartText(context, Color.GRAY);
	             Thread.sleep(500);
	         } catch (InterruptedException e) {
	             e.printStackTrace();
	         }
	
	         Event event = context.pollOrWaitEvent(10);
	
	         if (event == null) continue;
	
	         var action = event.getAction();
	
	         if (action == Action.KEY_PRESSED) {
	             if (event.getKey() == KeyboardKey.SPACE) {
	                 MenuChoix.main(args);
	                 return;
	             }
	         }
	
	         if (action == Action.POINTER_DOWN) {
	             handlePointerDownEvent(context, event, args);
	         }
	     }
	 }
	 
	
	 // Affichage du texte de démarrage
	 private static void displayStartText(ApplicationContext context, Color color) {
		 Objects.requireNonNull(context);
		 Objects.requireNonNull(color);
		 
	     float screenWidth = getWidth(context);
	     float screenHeight = getHeight(context);
	     String text = "Appuyez sur START pour commencer";
	
	     context.renderFrame(graphics -> {
	         graphics.setFont(new Font("Monospaced", Font.BOLD, 30));
	         FontMetrics metrics = graphics.getFontMetrics();
	         int x = (int) ((screenWidth - metrics.stringWidth(text)) / 2);
	         int y = (int) (((screenHeight - metrics.getHeight()) / 1.3) + metrics.getAscent());
	         graphics.setColor(color);
	         graphics.drawString(text, x, y);
	     });
	 }
	 
	
	 // Gestion de l'événement de clic
	 private static void handlePointerDownEvent(ApplicationContext context, Event event, String[] args) {
		 Objects.requireNonNull(context);
		 Objects.requireNonNull(event);
		 Objects.requireNonNull(args);
	     var location = event.getLocation();
	
	     if (isButtonClicked(location, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit)) {
	         context.exit(0);
	     } else if (isButtonClicked(location, xCreditButton, yCreditButton, widthButtonCredit, heightButtonCredit)) {
	         MenuCredit.main(null);
	     } else if (isButtonClicked(location, xHallOfFameButton, yHallOfFameButton, widthButtonHallOfFame, heightButtonHallOfFame)) {
	         HallOfFame.main(null);
	     }
	 }
	
	 // Vérification si un bouton a été cliqué
	 public static boolean isButtonClicked(Point2D location, float x, float y, float width, float height) {
	     return location.getX() >= x && location.getX() <= x + width && location.getY() >= y && location.getY() <= y + height;
	 }

	
	
 
/*__________________________________________________________________LANCEMENT DU MENU__________________________________________________________________*/	
		
	//Permet d'executer le menu principal
    public static void main(String[] args) {
    	Application.run(Color.BLACK, context -> {
        	try {
				launchMenu(context, args);
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
    }
}