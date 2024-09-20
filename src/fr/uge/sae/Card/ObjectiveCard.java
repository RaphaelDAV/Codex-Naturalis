package fr.uge.sae.Card;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.sae.Back.Board;
import fr.uge.sae.Back.Point;
import fr.uge.sae.Corner.Corner;

public class ObjectiveCard implements Card{
	private final int points;
	private final String typeObjective;
	private Point coordinates;
	private final String element;
	private final Map<String, List<String>> map;
	private final boolean twice;
	private final String background;
	
	
	
	public ObjectiveCard(int points, String typeObjective, Point coordinates, String element, Map<String, List<String>> map, boolean twice,String background) {
		Objects.requireNonNull(typeObjective);
		Objects.requireNonNull(element);
		Objects.requireNonNull(background);
		
		this.points = points;
		this.typeObjective = typeObjective;
		this.coordinates = coordinates;
		this.element = element;
		this.map = map;
		this.twice = twice;
		this.background=background;
	}

	public String getTypeObjective() {
		return typeObjective;
	}
	
	public String getElement() {
		return element;
	}
	
	public boolean getTwice() {
		return twice;
	}
	
	public String getBackground() {
		return background;
	}
	
	//Calcul les points rapporté par un objectifs ressource en lisant la board
	public int objectiveResource(Board board) {
		int copyNumber = board.numberOfRessource(element);
		int number = 0;
		while(copyNumber >= 3) {
			number++;
			copyNumber -= 3;
		}
		return number * points;
	}
	
	//Calcul les points rapporté par un artefact ressource en lisant la board
	public int objectiveArtifact(Board board, boolean twice) {
		int copyNumber = board.numberOfArtifact(element);
		int copyNumberQuill = board.numberOfQuill();
		int copyNumberManuscript = board.numberOfManuscript();
		int copyNumberInkwell = board.numberOfInkwell();
		int number = 0;
		if(twice) {
			number = twiceArtifatct(copyNumber);
		}
		 else {
			number = eachArtifact(copyNumberQuill, copyNumberManuscript, copyNumberInkwell);
		}
		return number * points;
	}
	
	//Calcul les objectifs artefact qui demandent d'avoir une paire d'artefact
	private int twiceArtifatct(int copyNumber) {
		int number = 0;
		while(copyNumber >= 2) {
			number++;
			copyNumber -= 2;
			}
		return number;
	}
	
	//Calcul les objectifs artefact qui demandent d'avoir un triplé de tous les artefact
	private int eachArtifact(int copyNumberQuill, int copyNumberManuscript, int copyNumberInkwell) {
		int number  = 0;
		while(copyNumberQuill >= 1 && copyNumberManuscript >= 1 && copyNumberInkwell >= 1) {
			number++;
			copyNumberQuill--;
			copyNumberManuscript--;
			copyNumberInkwell--;
		}
		return number;
	}
	
	private List<Point> USED_POSITION = new ArrayList<>();
	
	//Calcul les points rapporté par un objectifs pattern en lisant la board
	public int objectivePattern(Board board) {
	    int score = 0;
	    HashMap<Point, Card> copiedMap = new HashMap<>(board.getBoardMap());
	    Iterator<Map.Entry<Point, Card>> iterator = copiedMap.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry<Point, Card> boardEntry = iterator.next();
	        Card initialCard = boardEntry.getValue();
	        Point initialPosition = boardEntry.getKey();
	        // Si la position est déjà utilisée ou est une StarterCard, continuer
	        if (USED_POSITION.contains(initialPosition) || board.getCardAtPosition(initialPosition, board) instanceof StarterCard) {
	            continue;
	        }
	        // Vérifier si la carte initiale est égale à la première clé des objectifs
	        if (initialCard.getTypeRessource().equals(map.get("Center").get(0))) {
	            boolean respectPattern = true;
	            Point currentPosition = initialPosition;
	            // Vérifier chaque entrée dans la map des objectifs, sauf la première
	            Iterator<Map.Entry<String, List<String>>> objectiveIterator = map.entrySet().iterator();
	            objectiveIterator.next(); // Skip la première carte 
	            // Vérifier le pattern seulement si la position n'est pas déjà utilisée
	            if (!USED_POSITION.contains(currentPosition)) {
	                respectPattern = checkIfPatternIsRespected(objectiveIterator, currentPosition, board, USED_POSITION);
	            } else {
	                System.out.println("Carte déjà utilisée");
	                respectPattern = false;
	            }
	            if (respectPattern) {
	                score++;
	            }
	        }
	    }

	    return score * points;
	}
	
	//Vérifie si le pattern est respectée sur la board
	private boolean checkIfPatternIsRespected(Iterator<Map.Entry<String, List<String>>> objectiveIterator, Point currentPosition, Board board, List<Point> usedPosition) {
	    boolean respect;
	    while (objectiveIterator.hasNext()) {
	        Map.Entry<String, List<String>> objectiveEntry = objectiveIterator.next();
	        String direction = objectiveEntry.getKey();
	        List<String> expectedType = objectiveEntry.getValue();
	        Point targetPosition = getTargetPoint(currentPosition, direction);
	        // Vérifier si la position cible est déjà utilisée
	        if (!usedPosition.contains(targetPosition)) {
	            respect = checkObjective(expectedType, currentPosition, direction, board, usedPosition);
	        } else {
	            System.out.println("Carte déjà utilisée");
	            respect = false;
	        }
	        if (!respect) {
	            return false;
	        }
	        // Ajouter la position actuelle utilisée à la liste
	        currentPosition = targetPosition;
	        usedPosition.add(currentPosition);
	    }
	    return true;
	}
	
	//Vérifie si cette une carte valide pour le pattern
	private boolean checkObjective(List<String> expectedType, Point currentPosition, String direction, Board board, List<Point> usedPosition) {
	    for (int i = 0; i < expectedType.size(); i++) {
	        Point targetPosition = getTargetPoint(currentPosition, direction);
	        // Vérifier que la position cible est valide et existe sur le plateau
	        if (targetPosition == null || !board.getBoardMap().containsKey(targetPosition)) {
	            return false;
	        }
	        // Vérifier que la carte correspond au type attendu et n'est pas une StarterCard
	        if (!(board.getCardAtPosition(targetPosition, board) instanceof StarterCard)) {
	            if (!isMatchingCard(board, targetPosition, expectedType.get(i))) {
	                return false;
	            }
	        } else {
	            return false;
	        }
	        // Mettre à jour la position actuelle pour la prochaine vérification
	        currentPosition = targetPosition;
	        usedPosition.add(currentPosition);
	    }
	    return true;
	}
	
	//Donne le point visé par rapport à la direction 
	private Point getTargetPoint(Point origin, String direction) {
	    double x = origin.getX();
	    double y = origin.getY();

	    switch (direction) {
	        case "TopRight":
	            return new Point(x + 1, y - 1);
	        case "TopLeft":
	            return new Point(x - 1, y - 1);
	        case "BottomLeft":
	            return new Point(x - 1, y + 1);
	        case "BottomRight":
	            return new Point(x + 1, y + 1);
	        case "Top":
	            return new Point(x, y - 2);
	        case "Bottom":
	            return new Point(x, y + 2);
	        default:
	            return null;
	    }
	}
	
	//Vérifie si la carte à cette position existe et qu'elle est du bon type pour respecter le pattern
	private boolean isMatchingCard(Board board, Point position, String type) {
	    if (position == null || !board.getBoardMap().containsKey(position)) {
	        return false;
	    }
	    Card card = board.getCardAtPosition(position, board);
	    return card != null && card.getTypeRessource().equals(type);
	}

	@Override
	public Point getCoordinates() {
		return coordinates;
	}

	@Override
	public Corner getCorner(int i) {
		return null;
	}

	@Override
	public int getPoint() {
		return points;
	}

	@Override
	public String getTypeRessource() {
		return null;
	}

	@Override
	public List<String> getpermanentRessources() {
		return null;
	}

	@Override
	public boolean getRecto() {
		return false;
	}

	@Override
	public void cornerVerso() {	
	}

	@Override
	public void changeToVerso() {
	}

	@Override
	public void changeToRecto() {
	}

	@Override
	public HashMap<String, Integer> getCost() {
		return null;
	}

	@Override
	public void changeCoordinates(Point point) {
	}

	@Override
	public void setEmptyCorner1() {
	}

	@Override
	public void setEmptyCorner2() {
	}

	@Override
	public void setEmptyCorner3() {
	}

	@Override
	public void setEmptyCorner4() {
	}

	@Override
	public void toggleSide() {
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinates, element, map, points, twice, typeObjective);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ObjectiveCard))
			return false;
		ObjectiveCard other = (ObjectiveCard) obj;
		return Objects.equals(coordinates, other.coordinates) && Objects.equals(element, other.element)
				&& Objects.equals(map, other.map) && points == other.points && twice == other.twice
				&& Objects.equals(typeObjective, other.typeObjective);
	}

	@Override
	public String toString() {
		return "ObjectiveCard [points=" + points + ", typeObjective=" + typeObjective + ", coordinates=" + coordinates
				+ ", element=" + element + ", map=" + map + ", twice=" + twice +", background=" + background + "]";
	}
	
}
