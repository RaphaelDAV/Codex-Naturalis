package fr.uge.sae.View;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import javax.imageio.ImageIO;

public class Image {
		//Permet de lire une imaga à partir d'un chemin de fichier
		public static BufferedImage loadImage(String filename) {
			Objects.requireNonNull(filename);
			
	        try {
	            return ImageIO.read(new File(filename));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
		
		//Permet d'afficher une image en lui donnant la position et les dimensions qu'elle doit avoir
		public  static void drawImage(Graphics2D graphics, BufferedImage image, float x, float y, float dimX, float dimY) {
			Objects.requireNonNull(graphics);
			Objects.requireNonNull(image);
			
			var width = image.getWidth();
			var height = image.getHeight();
			var scale = Math.min(dimX / width, dimY / height);
			var transform = new AffineTransform(scale, 0, 0, scale, x + (dimX - scale * width) / 2, y + (dimY - scale * height) / 2);
			graphics.drawImage(image, transform, null);
		}
}
