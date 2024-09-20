package fr.uge.sae.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.uge.sae.Back.Point;
import fr.uge.sae.Corner.ArtifactCorner;
import fr.uge.sae.Corner.Corner;
import fr.uge.sae.Corner.EmptyCorner;
import fr.uge.sae.Corner.InvisibleCorner;
import fr.uge.sae.Corner.ResourceCorner;
import fr.uge.sae.Player.Player;
import fr.uge.sae.View.CardView;
import fr.umlv.zen5.ApplicationContext;

public class Deck {
    private List<Card> deck; // Liste des cartes du deck

    // Constructeur pour initialiser le deck avec une liste de cartes
    public Deck(List<Card> deck) {
        Objects.requireNonNull(deck);
        this.deck = deck;
    }
    
    // Méthode pour lire un deck à partir d'un fichier et ajouter les cartes dans une pile
    public static void readStack(Path src, Stack stack) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(stack);
        
        try (var reader = Files.newBufferedReader(src, StandardCharsets.UTF_8)) {
        	installStack(reader, stack);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    //Méthode pour installer la pioche avec toutes ses cartes
    private static void installStack(BufferedReader reader, Stack stack) throws IOException {
    	Objects.requireNonNull(reader);
    	Objects.requireNonNull(stack);
    	
    	String line;
        // Lire chaque ligne du fichier
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            // Ajouter une carte de ressource ou une carte d'or en fonction du type
            if (parts[0].equals("ResourceCard")) {
            	stack.add(createResourceCard(parts));
            } else if (parts[0].equals("GoldCard")) {
            	stack.add(createGoldCard(parts));
            }
        }
        
    }
    
    //Méthode pour lire les cartes de départ dans le deck.txt et les convertir en carte
    public static void readStarterCard(Path src, List<Player> allPlayers) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(allPlayers);
        
        List<List<Card>> starterCards = new ArrayList<>();
        try (var reader = Files.newBufferedReader(src, StandardCharsets.UTF_8)) {
            retrieveStarterCardsLine(starterCards, reader);
            Collections.shuffle(starterCards);
            distributeStarterCards(allPlayers, starterCards);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e; 
        }
    }
    //Lire chaque ligne du fichier
    private static void retrieveStarterCardsLine(List<List<Card>> starterCards, BufferedReader reader) throws IOException {
    	Objects.requireNonNull(starterCards);
    	Objects.requireNonNull(reader);
    	
    	String line;
    	while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length > 0 && "StarterCard".equals(parts[0])) {
            		starterCards.add(createStarterCard(parts));
            }
        }
    }
    
    //Distribue les deuc côté d'une carte de départ aux joueurs pour qu'ils puissent la choisir
    public static void distributeStarterCards(List<Player> allPlayers, List<List<Card>> starterCards) {
    	Objects.requireNonNull(allPlayers);
    	Objects.requireNonNull(starterCards);
    	
    	Random random = new Random();
        for (int i = 0; i < allPlayers.size(); i++) { 
            int cardIndex = random.nextInt(starterCards.size());
            allPlayers.get(i).getStarterCards().add(starterCards.get(cardIndex).get(0));
            allPlayers.get(i).getStarterCards().add(starterCards.get(cardIndex).get(1));
            starterCards.remove(cardIndex);
        }
    }
    
  //Méthode pour lire les cartes objectifs dans le deck.txt et les convertir en carte
    public static void readObjectiveCard(Path src, List<Player> allPlayers) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(allPlayers);
        
        List<Card> objectiveCards = new ArrayList<>();
        try (var reader = Files.newBufferedReader(src, StandardCharsets.UTF_8)) {
        	distributeObjectiveCards(objectiveCards, reader);      
            retrieveObjectiveCards(objectiveCards, allPlayers);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e; 
        }
    }
    
    //Distribue les cartes objectifs 
    private static void distributeObjectiveCards(List<Card> cards, BufferedReader reader) throws IOException{
    	Objects.requireNonNull(cards);
    	Objects.requireNonNull(reader);
    	
    	String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length > 0 && "Objective".equals(parts[0])) {
            		cards.add(createObjectiveCard(parts));
            }
        }     
    }
    
    //Méthode pour attribuer les cartes objectifs et objectifs communs à chaque joueur
    private static void retrieveObjectiveCards(List<Card> objectiveCards, List<Player> allPlayers) {
    	Objects.requireNonNull(objectiveCards);
    	Objects.requireNonNull(allPlayers);
    	
    	Collections.shuffle(objectiveCards);
        List<Card> commonObjectives = new ArrayList<>();
        commonObjectives.add(objectiveCards.remove(0));
        commonObjectives.add(objectiveCards.remove(0));       
        Random random = new Random();
        for (Player player : allPlayers) {
            List<Card> playerObjectives = new ArrayList<>();
            playerObjectives.add(objectiveCards.remove(random.nextInt(objectiveCards.size())));
            playerObjectives.add(objectiveCards.remove(random.nextInt(objectiveCards.size())));
            player.getObjective().addAll(playerObjectives);
            player.getCommonObjective().addAll(commonObjectives);
        }
    }

    // Méthode pour créer une carte de ressource à partir des parties de la ligne du fichier
    private static ResourceCard createResourceCard(String[] parts) {
        Objects.requireNonNull(parts);
        
        List<Corner> corners = createCorners(parts, 2, 6); // Créer les coins de la carte
        String typeCard = parts[7];
        int score = parts[9].equals("None") ? 0 : Integer.parseInt(parts[9].substring(2));
        return new ResourceCard(score, typeCard, corners.get(0), corners.get(1), corners.get(2), corners.get(3), null, null, true);
    }

    // Méthode pour créer une carte d'or à partir des parties de la ligne du fichier
    private static GoldCard createGoldCard(String[] parts) {
        Objects.requireNonNull(parts);
        
        List<Corner> corners = createCorners(parts, 2, 6); // Créer les coins de la carte
        HashMap<String, Integer> map = new HashMap<>();
        String typeCard = parts[7];
        int number = 9;
        // Lire les coûts jusqu'à rencontrer "Scoring"
        while (!parts[number].equals("Scoring")) {
            String cost = parts[number];
            map.put(cost, map.getOrDefault(cost, 0) + 1);
            number++;
        }
        // Déterminer le type de score et sa valeur
        String typeScoring = determineScoringType(parts[number + 1]);
        int score = Integer.parseInt(parts[number + 1].substring(2));
        return new GoldCard(score, typeScoring, typeCard, corners.get(0), corners.get(1), corners.get(2), corners.get(3), map, null, null, true);
    }
    
    // Méthode pour créer une carte de départ à partir des parties de la ligne du fichier
    private static List<Card> createStarterCard(String[] parts) {
        Objects.requireNonNull(parts);
        
        List<Corner> cornersRecto;
        List<Corner> cornersVerso;
        List<Card> starterCards = new ArrayList<Card>();    
        cornersRecto = createCorners(parts, 2, 6); // Créer les coins de la carte
        cornersVerso = createCorners(parts, 7, 11); // Créer les coins de la carte
        List<String> lst = new ArrayList<>();
        int number = 12;
        // Lire les coûts jusqu'à rencontrer une chaîne vide ou atteindre la fin du fichier
        while (number < parts.length && !parts[number].equals("")) {
            lst.add(parts[number].substring(2));
            number++;
        }
        starterCards.add(new StarterCard(cornersRecto.get(0), cornersRecto.get(1), cornersRecto.get(2), cornersRecto.get(3), new Point(0, 0), new ArrayList<>(), false));
        starterCards.add(new StarterCard(cornersVerso.get(0), cornersVerso.get(1), cornersVerso.get(2), cornersVerso.get(3), new Point(0, 0), lst, false));
        return starterCards;
    }

 // Méthode pour créer une carte d'objectif à partir des parties de la ligne du fichier
    private static ObjectiveCard createObjectiveCard(String[] parts) {
        Objects.requireNonNull(parts);
        
        String type = parts[1];
        String typeSpecific = parts[2];
        String background = parts[parts.length - 1];
        int score = Integer.parseInt(parts[parts.length - 2].substring(2));
        boolean twice = type.equals("Artifact") && typeSpecific.equals(parts[3]);
        if (type.equals("Resource") || type.equals("Artifact")) {
            return new ObjectiveCard(score, type, null, typeSpecific, null, twice, background);
        }
        Map<String, List<String>> map = new LinkedHashMap<>();
        int number = initializePatternMap(parts, map);

        if (number < parts.length && parts[number].equals("Scoring")) {
            return new ObjectiveCard(score, "Pattern", null, "", map, false, background);
        } else {
            throw new IllegalArgumentException("Pas de scoring");
        }
    }
    
    //Permet d'initialiser la map qui contiendra le pattern de la carte
    private static int initializePatternMap(String[] parts, Map<String, List<String>> map) {
    	Objects.requireNonNull(parts);
    	Objects.requireNonNull(map);
    	
        int number = 2;
        List<String> first = new ArrayList<>();
        first.add(parts[number]);
        map.put("Center", first);
        number++;
        return determinePattern(number, parts, map);
    }

    //Complète la map en intégrant les type de ressource avec la direction où elle doit aller par rapport au deck.txt
    private static int determinePattern(int number, String[] parts, Map<String, List<String>> map) {
    	Objects.requireNonNull(parts);
    	Objects.requireNonNull(map);
    	
        while (number < parts.length && !parts[number].equals("Scoring")) {
            if (number % 2 != 0) {
                String direction = parts[number];
                List<String> lst = new ArrayList<>();
                lst.add(parts[number + 1]);
                if (parts[number + 2].equals(direction)) {
                    lst.add(parts[number + 3]);
                    number += 3;
                } else {
                    number += 2;
                }
                map.put(direction, lst);
            } else {
                number++;
            }
        }
        return number; // Retourner la position mise à jour
    }


    // Méthode pour créer les coins d'une carte à partir des parties de la ligne du fichier
    private static List<Corner> createCorners(String[] parts, int start, int end) {
        Objects.requireNonNull(parts);
        List<Corner> corners = new ArrayList<>();
        for (int i = start; i < end; i++) {
            switch (parts[i]) {
                case "Empty":
                    corners.add(new EmptyCorner());
                    break;
                case "Invisible":
                    corners.add(new InvisibleCorner());
                    break;
                default:
                    if (parts[i].startsWith("R:")) {
                        corners.add(new ResourceCorner(parts[i].substring(2)));
                    } else if (parts[i].startsWith("A:")) {
                        corners.add(new ArtifactCorner(parts[i].substring(2)));
                    }
                    break;
            }
        }
        return corners;
    }

    // Méthode pour déterminer le type de score à partir de la chaîne de caractères
    private static String determineScoringType(String part) {
        Objects.requireNonNull(part);
        char firstLetter = part.charAt(0);
        switch (firstLetter) {
            case 'C':
                return "coverCorner";
            case 'Q':
                return "nb_Quill";
            case 'I':
                return "nb_Inkwell";
            case 'M':
                return "nb_Manuscript";
            case 'D':
            default:
                return "normal";
        }
    }
    
    @Override
	public int hashCode() {
		return Objects.hash(deck);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Deck))
			return false;
		Deck other = (Deck) obj;
		return Objects.equals(deck, other.deck);
	}

	// Méthode pour afficher toutes les cartes d'un deck
    public void showDeck(ApplicationContext context) {
        Objects.requireNonNull(context);
        for (Card card : deck) {
            if (!card.getRecto()) {
                card.cornerVerso();
            }
            CardView.showCard(context, card);
        }
    }

    // Méthode pour retirer une carte d'un deck
    public void remove(Card card){
        Objects.requireNonNull(card);
        deck.remove(card);
    }

    // Méthode pour ajouter une carte dans un deck
    public void add(Card card){
        Objects.requireNonNull(card);
        deck.add(card);
    }

    // Méthode pour vérifier si une carte est présente dans le deck
    public boolean contains(Card card) {
        Objects.requireNonNull(card);
        return deck.contains(card);
    }

    // Méthode pour récupérer l'ensemble des cartes
    public List<Card> getCards() {
        return deck;
    }

    @Override
    public String toString() {
        return "Deck [deck=" + deck + "]";
    }
}