package fr.uge.sae.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Objects;


import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.KeyboardKey;
import fr.umlv.zen5.Event.Action;

public class MenuChoix {
	
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
            Image.drawImage(graphics,image2,screenWidth/4, screenWidth/30, screenWidth/2, 200);
        });
	}
	
	
	
/*__________________________________________________________________GESTION DES BOUTONS__________________________________________________________________*/	
	
	//On initialise toutes les tailles et positions:
	public static float getWidth(ApplicationContext context){
		Objects.requireNonNull(context);
		return context.getScreenInfo().getWidth();
	}
	
	public static float getHeight(ApplicationContext context){
		Objects.requireNonNull(context);
		return context.getScreenInfo().getHeight();
	}
	
	
	private static float xButtonQuit;
    private static float yButtonQuit;
    private static float widthButtonQuit;
    private static float heightButtonQuit;

    private static float xCreditButton;
    private static float yCreditButton;
    private static float widthButtonCredit;
    private static float heightButtonCredit;
    
    private static float xHallOfFameButton;
    private static float yHallOfFameButton;
    private static float widthButtonHallOfFame;
    private static float heightButtonHallOfFame;
    
    private static float rectX;
	private static float rectY;
	
	private static float xPlusButton;
	private static float yPlusButton;
	private static float widthPlus;
	private static float heightPlus;
	
	private static float xMoinsButton;
	private static float yMoinsButton;
	private static float widthMoins;
	private static float heightMoins;
	
	private static float xButton1;
    private static float yButton1;
    private static float widthButton = 80;
    private static float heightButton = 80;

    private static float xButton2 = 250;
    private static float yButton2 = yButton1;

    private static float xButton3 = 400;
    private static float yButton3 = yButton1;

    private static float xButton4 = 550;
    private static float yButton4 = yButton1;
    
	//Afficher bouton quitter
    private static void showQuitButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit, "Quitter", Color.RED, Color.WHITE);
	}
	
	//Afficher bouton crédit
    private static void showCreditButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xCreditButton, yCreditButton, widthButtonCredit, heightButtonCredit, "Crédits", Color.GRAY, Color.WHITE);
	}

	 //Afficher bouton HallOfFame
    private static void showHallOfFameButton(ApplicationContext context) {
		Objects.requireNonNull(context);
	    Menu.drawButton(context, xHallOfFameButton, yHallOfFameButton, widthButtonHallOfFame, heightButtonHallOfFame, "Hall Of Fame", Color.ORANGE, Color.WHITE);
	}
  		
	
    //Afficher le grand rectangle blanc
	private static void PlayerMenu(ApplicationContext context) {
		Objects.requireNonNull(context);
		
		float screenWidth = context.getScreenInfo().getWidth();
        float screenHeight = context.getScreenInfo().getHeight();
        
        float rectX = screenWidth / 4;
		float rectY = screenHeight / 4;
		float rectWidth = screenWidth / 2;
		float rectHeight = screenHeight / 1.8f;

		Menu.drawRoundedRectangleWithBorder(context, rectX, rectY, rectWidth, rectHeight, 50, 50, Color.GRAY, Color.WHITE);
		
		context.renderFrame(graphics -> {
		    String text = "Paramètres de la partie";
		    
		    graphics.setFont(new Font("Monospaced", Font.BOLD, 35));
		    FontMetrics metrics = graphics.getFontMetrics();

		    int x = (int) ((screenWidth - metrics.stringWidth(text)) / 2);
		    int y = (int) rectY + metrics.getHeight();

		    graphics.setColor(Color.BLACK);
		    graphics.drawString(text, x, y);
 
		});		
	}
	
	
	//CHOIX DU NOMBRE DE JOUEUR
	
	private static void choosePlayer(ApplicationContext context, int numberOfPlayer) {
		Objects.requireNonNull(context);
		
		float screenWidth = context.getScreenInfo().getWidth();
        float screenHeight = context.getScreenInfo().getHeight();
		var link1 = Image.loadImage("data/link1.png");
		var link2 = Image.loadImage("data/link2.png");
		var link3 = Image.loadImage("data/link3.png");
		var link4 = Image.loadImage("data/link4.png");
		float widthLink = screenWidth / 2*0.8f;
		float heightLink = screenHeight / 1.8f*0.4f;
		context.renderFrame(graphics -> {
			switch(numberOfPlayer) {
				case 1:
					bouton1(context,new Color(0, 102, 0));
					bouton2(context,Color.darkGray);
					bouton3(context,Color.darkGray);
					bouton4(context,Color.darkGray);
					Image.drawImage(graphics,link1,(rectX*0.7f) + (widthButton/2),rectY*1.5f,widthLink,heightLink);
					break;
				case 2:
					bouton1(context,Color.darkGray);
					bouton2(context,Color.red);
					bouton3(context,Color.darkGray);
					bouton4(context,Color.darkGray);
					Image.drawImage(graphics,link1,rectX*0.6f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link2,rectX*0.8f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					break;
				case 3:
					bouton1(context,Color.darkGray);
					bouton2(context,Color.darkGray);
					bouton3(context,Color.blue);
					bouton4(context,Color.darkGray);
					Image.drawImage(graphics,link1,rectX*0.5f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link2,rectX*0.7f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link3,rectX*0.9f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					break;
				case 4:
					bouton1(context,Color.darkGray);
					bouton2(context,Color.darkGray);
					bouton3(context,Color.darkGray);
					bouton4(context,new Color(153, 51, 255));
					Image.drawImage(graphics,link1,rectX*0.4f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link2,rectX*0.6f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link3,rectX*0.8f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					Image.drawImage(graphics,link4,rectX*1f+widthButton/2,rectY*1.5f,widthLink,heightLink);
					break;
			}
			String text = "Choisissez le nombre de joueur";
		    
		    graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
		    FontMetrics metrics = graphics.getFontMetrics();

		    int x = (int) ((xButton4+ xButton1 + widthButton -metrics.stringWidth(text))/2);
		    int y = (int) yButton1 - metrics.getHeight();

		    graphics.setColor(Color.BLACK);
		    graphics.drawString(text, x, y);
		});
	}
	
	//CHOIX DU BOT
	
	private static final String GANON_OFF_PATH = "data/ganon_off.png";
    private static final String GANON_UPP_PATH = "data/ganon_upp.png";
    
	private static void chooseBot(ApplicationContext context, int bot) {
		Objects.requireNonNull(context);
		float screenWidth = context.getScreenInfo().getWidth();
        float screenHeight = context.getScreenInfo().getHeight();
        
		float rectWidth = screenWidth / 2;
		float rectHeight = screenHeight / 1.8f;
		
		context.renderFrame(graphics -> {
		    var ganon2 = Image.loadImage(GANON_OFF_PATH);
			float xGanon2= rectX*2- ganon2.getWidth() + rectWidth*0.1f -80;
			float yGanon2= rectY*1.7f- ganon2.getHeight() + rectHeight*0.1f;
			float widthGanon2= rectWidth*0.6f;
			float heightGanon2=rectHeight*0.3f;
			
			var ganon1 = Image.loadImage(GANON_UPP_PATH);
			float xGanon1= rectX*2- ganon1.getWidth()-80;
			float yGanon1= rectY*1.8f- ganon1.getHeight();
			float widthGanon1= rectWidth*0.8f;
			float heightGanon1=rectHeight*0.4f;
			
			
			
			switch(bot){
				case 0:
					Image.drawImage(graphics, ganon2,xGanon2 ,yGanon2 ,widthGanon2 ,heightGanon2 );
					boutonPlus(context,Color.darkGray);
					boutonMoins(context,Color.red);
					break;
				case 1:			
					Image.drawImage(graphics, ganon1,xGanon1 ,yGanon1 ,widthGanon1 ,heightGanon1 );	
					boutonPlus(context,Color.red);
					boutonMoins(context,Color.darkGray);
					break;
			}
			
			
			String textBot = "Activer / Désactiver le BOT";
		    graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
		    FontMetrics metricsBot = graphics.getFontMetrics();
		    int xBot = (int) (xGanon1 + widthGanon1/2 - (metricsBot.stringWidth(textBot)/2));
		    int yBot = (int) yButton1 - metricsBot.getHeight();
		    graphics.setColor(Color.BLACK);
		    graphics.drawString(textBot, xBot, yBot);
			
		});
	}
	
	
	
	private static final int CORNER_RADIUS = 50;
    private static final int FONT_SIZE_LARGE = 100;
    private static final int FONT_SIZE_SMALL = 50;
    private static final Color BORDER_COLOR = Color.WHITE;


    private static void renderButton(ApplicationContext context, Color color, float x, float y, float width, float height, int fontSize, String label) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(color, "color cannot be null");
        Objects.requireNonNull(label, "label cannot be null");

        context.renderFrame(graphics -> {
            Menu.drawRoundedRectangleWithBorder(context, x, y, width, height, CORNER_RADIUS, CORNER_RADIUS, color, BORDER_COLOR);
            graphics.setColor(BORDER_COLOR);
            graphics.setFont(new Font("Monospaced", Font.BOLD, fontSize));
            FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
            int xPos = (int) (x + (width - metrics.stringWidth(label)) / 2);
            int yPos = (int) (y + ((height - metrics.getHeight()) / 2) + metrics.getAscent()) - 7;
            graphics.drawString(label, xPos, yPos);
        });
    }


    private static void boutonPlus(ApplicationContext context, Color color) {
        renderButton(context, color, xPlusButton, yPlusButton, widthPlus, heightPlus, FONT_SIZE_LARGE, "+");
    }


    private static void boutonMoins(ApplicationContext context, Color color) {
        renderButton(context, color, xMoinsButton, yMoinsButton, widthMoins, heightMoins, FONT_SIZE_LARGE, "-");
    }


    private static void bouton1(ApplicationContext context, Color color) {
        renderButton(context, color, xButton1, yButton1, widthButton, heightButton, FONT_SIZE_SMALL, "1");
    }


    private static void bouton2(ApplicationContext context, Color color) {
        renderButton(context, color, xButton2, yButton2, widthButton, heightButton, FONT_SIZE_SMALL, "2");
    }


    private static void bouton3(ApplicationContext context, Color color) {
        renderButton(context, color, xButton3, yButton3, widthButton, heightButton, FONT_SIZE_SMALL, "3");
    }


    private static void bouton4(ApplicationContext context, Color color) {
        renderButton(context, color, xButton4, yButton4, widthButton, heightButton, FONT_SIZE_SMALL, "4");
    }
	
    private static float StartX;
    private static float StartY;
    private static float StartHeight;
    private static float StartWidth;
    
	private static void startButton(ApplicationContext context) {
		context.renderFrame(graphics -> {
            Menu.drawRoundedRectangleWithBorder(context, StartX, StartY, StartWidth, StartHeight, 50, 50, new Color(0, 102, 0), Color.WHITE);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
            String text = "Démarrer";
            FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
            int x = (int) (StartX + (StartWidth - metrics.stringWidth(text)) / 2);
            int y = (int) (StartY + ((StartHeight - metrics.getHeight()) / 2) + metrics.getAscent());
            graphics.drawString(text, x, y);
        });
	}
/*__________________________________________________________________GESTION DU MENU__________________________________________________________________*/	
	
	private final static int[] selectedBot = { 1 };
	private final static int[] selectedPlayer = { 1 };
	
	public static boolean getStateBot() {
		if (selectedBot[0]==1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int getNumberOfPlayer() {
		int number=selectedPlayer[0];
		if (number<=4 && number>=1) {
			return number;
		}
		return 0;
	}
	
	//Méthode pour lancer le menu
	public static void LaunchMenuChoix(ApplicationContext context, String[] args) throws IOException {
		Objects.requireNonNull(context);
		
		// Initialisation des tailles de boutons
		initializeButtonSizes(context);
	    
        // Affichage du fond d'écran et des boutons
        showBackground(context);
        showCreditButton(context);
        showQuitButton(context);
        PlayerMenu(context);
        chooseBot(context,1);
		choosePlayer(context,1);
		startButton(context);
		showHallOfFameButton(context);
		

        while (true) { 
        	  
            Event event = context.pollOrWaitEvent(10);            
            if (event == null) {continue;}            
            var action = event.getAction();
            
            if (action == Action.KEY_PRESSED) {
	            if (event.getKey() == KeyboardKey.SPACE) { 
	                MenuChoix.main(args);
	                return;
	            }
            }
            
            if (action == Action.POINTER_DOWN) {
            	//Appui sur un bouton
            	handlePointerDownEvent(context, event);
            	
            	PlayerMenu(context);
            	chooseBot(context,selectedBot[0]);
            	choosePlayer(context,selectedPlayer[0]);
            	startButton(context);
            	
            }                     
        }
	}
	
	// Gestion de l'événement de clic
		 private static void handlePointerDownEvent(ApplicationContext context, Event event) {
			 Objects.requireNonNull(context);
			 Objects.requireNonNull(event);
		     var location = event.getLocation();
		
		     if (Menu.isButtonClicked(location, xButton1, yButton1, widthButton, heightButton)) {
		    	 selectedPlayer[0]=1;
		     } else if (Menu.isButtonClicked(location, xButton2, yButton2, widthButton, heightButton)) {
		    	 selectedPlayer[0]=2;
		     } else if (Menu.isButtonClicked(location, xButton3, yButton3, widthButton, heightButton)) {
		    	 selectedPlayer[0]=3;
		     } else if (Menu.isButtonClicked(location, xButton4, yButton4, widthButton, heightButton)) {
		    	 selectedPlayer[0]=4;
		     } else if (Menu.isButtonClicked(location, xPlusButton, yPlusButton, widthPlus, heightPlus)) {
		    	 selectedBot[0]=1;
		     } else if (Menu.isButtonClicked(location, xMoinsButton, yMoinsButton, widthMoins, heightMoins)) {
		         selectedBot[0]=0;
		     } else if (Menu.isButtonClicked(location, StartX, StartY, StartWidth, StartHeight)) {
		    	 BoardView.main(null);
		     } else if (Menu.isButtonClicked(location, xButtonQuit, yButtonQuit, widthButtonQuit, heightButtonQuit)) {
		    	 context.exit(0);
		     } else if (Menu.isButtonClicked(location, xCreditButton, yCreditButton, widthButtonCredit, heightButtonCredit)) {
		    	 MenuCredit.main(null);
		     } else if (Menu.isButtonClicked(location, xHallOfFameButton, yHallOfFameButton, widthButtonHallOfFame, heightButtonHallOfFame)) {
		    	HallOfFame.main(null);
		     }
		 }
		 
		// Constantes pour les tailles relatives des boutons
		 private static final float BUTTON_WIDTH_RATIO = 0.14f;
		 private static final float BUTTON_HEIGHT_RATIO = 0.06f;
		 private static final float BUTTON_Y_RATIO = 0.85f;
		 private static final float QUIT_BUTTON_X_RATIO = 0.85f;
		 private static final float CREDIT_BUTTON_X_RATIO = 0.70f;
		 private static final float HALL_OF_FAME_BUTTON_X_RATIO = 0.55f;
		 private static final float PLAYER_Y_RATIO = 2.6f;
		 private static final float PLAYER_WIDTH_RATIO = 80;
		 
		// Initialisation des tailles et positions des boutons
		 private static void initializeButtonSizes(ApplicationContext context) {
		    float screenWidth = getWidth(context);
		    float screenHeight = getHeight(context);
	        
		    //BOUTON QUITTER
	        xButtonQuit = screenWidth * QUIT_BUTTON_X_RATIO;
	        yButtonQuit = screenHeight * BUTTON_Y_RATIO;
	        widthButtonQuit = screenWidth * BUTTON_WIDTH_RATIO;
	        heightButtonQuit = screenHeight * BUTTON_HEIGHT_RATIO;
	        
	        //BOUTON CREDIT
	        xCreditButton = screenWidth *CREDIT_BUTTON_X_RATIO;
	        yCreditButton = screenHeight * BUTTON_Y_RATIO;
	        widthButtonCredit = screenWidth * BUTTON_WIDTH_RATIO;
	        heightButtonCredit = screenHeight * BUTTON_HEIGHT_RATIO;
	        
	        //BOUTON HALL OF FAME
	        xHallOfFameButton = screenWidth * HALL_OF_FAME_BUTTON_X_RATIO;
	        yHallOfFameButton = screenHeight * BUTTON_Y_RATIO;
	        widthButtonHallOfFame = screenWidth * BUTTON_WIDTH_RATIO;
	        heightButtonHallOfFame = screenHeight * BUTTON_HEIGHT_RATIO;
	        
	        rectX = screenWidth / 4;
			rectY = screenHeight / 4;
			
			//BOUTON PLUS
		    xPlusButton = rectX*2.55f- 120;
		    yPlusButton = rectY*PLAYER_Y_RATIO ;
		    widthPlus = PLAYER_WIDTH_RATIO;
		    heightPlus =PLAYER_WIDTH_RATIO;
		    
		    //BOUTON MOINS
			xMoinsButton = rectX*2.8f - 120;
		    yMoinsButton =  rectY*PLAYER_Y_RATIO;
		    widthMoins = PLAYER_WIDTH_RATIO;
		    heightMoins =PLAYER_WIDTH_RATIO;
		    
		    //BOUTONS JOUEURS
		    xButton1=rectX*1.2f;
		    xButton2=rectX*1.4f;
		    xButton3=rectX*1.6f;
		    xButton4=rectX*1.8f;
		    
		    yButton1=rectY*PLAYER_Y_RATIO;
		    yButton2=rectY*PLAYER_Y_RATIO;
		    yButton3=rectY*PLAYER_Y_RATIO;
		    yButton4=rectY*PLAYER_Y_RATIO;
		    
		    //BOUTON DEMARRER
		    StartX = screenWidth/2 - screenWidth * (0.05f);
		    StartY = screenHeight / 4+ screenHeight / 1.8f - screenHeight * (0.04f)/2;
		    StartHeight=screenHeight * (0.04f);
		    StartWidth = screenWidth * (0.1f);
		 }
/*__________________________________________________________________LANCEMENT DU MENU__________________________________________________________________*/	
	
	
	
	//Permet d'executer le menu principal
    public static void main(String[] args) {
        Application.run(Color.BLACK, context -> {
        	try {
				LaunchMenuChoix(context, args);
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
    }

}
