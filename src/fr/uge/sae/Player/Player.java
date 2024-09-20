package fr.uge.sae.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.uge.sae.Back.Board;
import fr.uge.sae.Card.*;

public class Player {
	private final int idPlayer;
	private final Board board;
	private List<Card> hand;
	private Deck deck;
	private final List<Card> objective;
	private List<Card> starterCards;
	private boolean playing;
	private boolean ready;
	private final List<Card> commonObjective;
	
	
	public Player(int idPlayer, Board board, List<Card> hand, Deck deck, List<Card> objective, List<Card> starterCards, List<Card> commonObjective, boolean playing, boolean ready) {
		this.idPlayer = idPlayer;
		this.board = board;
		this.hand = hand;
		this.deck = deck;
		this.objective = objective;
		this.starterCards = starterCards;
		this.playing = playing;
		this.ready = ready;
		this.commonObjective = commonObjective;
		
	}

	/*public void showPlayerGame(ApplicationContext context) {
		BoardView.showBoard(context, this);
		BoardView.showHand(context, this);
	}*/
	
	public boolean isReady() {
		return ready;
	}
	
	public List<Card> getCommonObjective() {
		return commonObjective;
	}
	
	public List<Card> getStarterCards(){
		return starterCards;
	}
	public void removeStarterCard(int i) {
		starterCards.remove(i);
	}
	public boolean isPlaying() {
		return playing;
	}
	
	public void dontPlay() {
		playing = false;
	}
	
	public void play() {
		playing = true;
	}
	
	public List<Card> getObjective(){
		return objective;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public List<Card> getHand(){
		return hand;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public void becomeReady() {
		if(objective.size() < 2 && starterCards.size() < 2) {
			ready = true;
		} else {
			ready = false;
		}
	}

	//Initialise la main du joueur en lui donnant 2 cartes ressource et une carte gold au hasard
	public void initializeHand(Stack stack) {
		Objects.requireNonNull(stack);
		Collections.shuffle(stack.getRessourceStack());
		Collections.shuffle(stack.getGoldStack());
	    addLastElementToHand(stack.getRessourceStack());
	    addLastElementToHand(stack.getRessourceStack());
	    addLastElementToHand(stack.getGoldStack());
	}
	
	private void addLastElementToHand(List<Card> stack) {
		Objects.requireNonNull(stack);
	    if (!stack.isEmpty()) {
	        hand.add(stack.remove(stack.size() - 1));
	    }
	}

	
	public void toggleHand() {
	    for (Card card : hand) {
	        if (!isPlaying()) {
	            card.changeToVerso();
	        } else {
	            card.changeToRecto();
	        }
	    }
	}

	@Override
	public int hashCode() {
		return Objects.hash(board, deck, hand, idPlayer, objective, playing, ready, starterCards);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Player))
			return false;
		Player other = (Player) obj;
		return Objects.equals(board, other.board) && Objects.equals(commonObjective, other.commonObjective)
				&& Objects.equals(deck, other.deck) && Objects.equals(hand, other.hand) && idPlayer == other.idPlayer;
	}

	@Override
	public String toString() {
		return "Player [idPlayer=" + idPlayer + ", board=" + board + ", hand=" + hand + ", deck=" + deck
				+ ", objective=" + objective + ", starterCards=" + starterCards + ", playing=" + playing + ", ready="
				+ ready + ", commonObjective=" + commonObjective + "]";
	}

	
	
}