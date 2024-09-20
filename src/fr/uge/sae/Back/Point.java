package fr.uge.sae.Back;

public record Point(double x, double y) {
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
}