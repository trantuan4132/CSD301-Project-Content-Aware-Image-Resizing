/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seamcarving;

/**
 *
 * @author Admin
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class SeamCarving {

    /**
     * @param args the command line arguments
     */
    static BufferedImage readFromFile(Scanner scanner) throws IOException{
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine();
	BufferedImage image = ImageIO.read(new File(filename));
        System.out.println("Current size: " + image.getWidth() + " x " + image.getHeight() + " (W x H)");
        return image;
    }
    
    static BufferedImage generateEnergyImage(double[][] energy, double norm_factor) throws IOException{
        int width = energy.length, height = energy[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Set image pixel color with normalized energy value
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                float gray = (float)(energy[x][y] / norm_factor);
                Color color = new Color(gray, gray, gray);
                image.setRGB(x, y, color.getRGB());
            }
        }
        ImageIO.write(image, "png", new File("energy.png"));
        return image;
    }
    
    static void generateSeam(int[] seam, BufferedImage image, 
                             String seamType, String outputFile) throws IOException{
        // Generate vertical seam
        if (seamType.equals("vertical")){
            // Iterate over each row of image
            for (int y = 0; y < image.getHeight(); y++){
                image.setRGB(seam[y], y, Color.RED.getRGB());
            }                        
        }
        // Generate horizontal seam
        else if (seamType.equals("horizontal")){
            // Iterate over each column of image
            for (int x = 0; x < image.getWidth(); x++){
                image.setRGB(x, seam[x], Color.RED.getRGB());
            }  
        }
        ImageIO.write(image, "png", new File(outputFile + ".png"));
    }
    
    static void generateResizedImage(SeamCarver seamCarver, Scanner scanner, boolean demo) throws IOException{
        // Remove images from previous run
        if (demo == true){         
            for (File file: new File("demo").listFiles()) 
                if (!file.isDirectory()) 
                    file.delete();
        }
        
        double[][] energy;
        int[] verticalSeam, horizontalSeam;
        int width = seamCarver.image.getWidth(), height = seamCarver.image.getHeight();
        
        // Generate energy image with and without vertical seam
//        if (demo == true){
//            energy = seamCarver.computeEnergy();
//            BufferedImage energyImage = generateEnergyImage(energy, seamCarver.BORDER_ENERGY);
//            verticalSeam = seamCarver.findVerticalSeam(energy);
//            generateSeam(verticalSeam, energyImage, "vertical", "vertical-seam");
//            horizontalSeam = seamCarver.findHorizontalSeam(energy);
//            generateSeam(verticalSeam, energyImage, "horizontal", "horizontal-seam");
//        }
        
        System.out.print("Enter new width (max = " + width + "): ");
        int newWidth = scanner.nextInt();
        System.out.print("Enter new height (max = " + height + "): ");
        int newHeight = scanner.nextInt();

        // Remove vertical seam
        for (int i = 0; i < width - newWidth; i++){
            energy = seamCarver.computeEnergy();
            verticalSeam = seamCarver.findVerticalSeam(energy);
            if (demo == true) 
                generateSeam(verticalSeam, seamCarver.image, "vertical", "demo/vertical_" + i);         
            seamCarver.removeVerticalSeam(verticalSeam);
        }
        
        // Remove horizontal seam
        for (int i = 0; i < height - newHeight; i++){
            energy = seamCarver.computeEnergy();
            horizontalSeam = seamCarver.findHorizontalSeam(energy);
            if (demo == true) 
                generateSeam(horizontalSeam, seamCarver.image, "horizontal", "demo/horizontal_" + i);
            seamCarver.removeHorizontalSeam(horizontalSeam);
        }
        
        ImageIO.write(seamCarver.image, "png", new File("compressed.png"));
    }
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Scanner scanner = new Scanner(System.in);
        BufferedImage image = readFromFile(scanner);
        SeamCarver seamCarver = new SeamCarver(image);
        
        long startTime = System.currentTimeMillis();
        generateResizedImage(seamCarver, scanner, true);
        long endTime = System.currentTimeMillis();
        System.out.println("Finished in " + (endTime - startTime)/1000.0 + " seconds.");
    }
    
}
