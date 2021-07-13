/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seamcarving;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Admin
 */
class SeamCarver {
    
    BufferedImage image;
    double BORDER_ENERGY = 1000;   // Maximum energy
    
    public SeamCarver(BufferedImage image) {
        this.image = image;
    }
    
    double[][] computeEnergy(){
        double[][] energy = new double[image.getWidth()][image.getHeight()];
        // Iterate over each row of image
        for (int y = 0; y < image.getHeight(); y++){
            // Iterate over each column of image
            for (int x = 0; x < image.getWidth(); x++){
                if (x == 0 || y == 0 || x == image.getWidth()-1 || y == image.getHeight()-1) 
                    energy[x][y] = BORDER_ENERGY;
                else
                    energy[x][y] = Math.sqrt(gradX(x, y) + gradY(x, y));
            }
        }
        return energy;
    }

    double gradX(int x, int y) {
        Color leftPixel = new Color(image.getRGB(x - 1, y));
        Color rightPixel = new Color(image.getRGB(x + 1, y));
        double Rx = rightPixel.getRed() - leftPixel.getRed();
        double Gx = rightPixel.getGreen() - leftPixel.getGreen();
        double Bx = rightPixel.getBlue() - leftPixel.getBlue();
        return (Rx*Rx) + (Gx*Gx) + (Bx*Bx);
    }

    double gradY(int x, int y) {
        Color topPixel = new Color(image.getRGB(x, y - 1));
        Color bottomPixel = new Color(image.getRGB(x, y + 1));
        double Ry = topPixel.getRed() - bottomPixel.getRed();
        double Gy = topPixel.getGreen() - bottomPixel.getGreen();
        double By = topPixel.getBlue() - bottomPixel.getBlue();
        return (Ry*Ry) + (Gy*Gy) + (By*By);
    }
    
    int[] findVerticalSeam(double[][] energy){
        double[][] M = new double[image.getWidth()][image.getHeight()];  // seam energy matrix
        int[][] edgeTo = new int[image.getWidth()][image.getHeight()];   // location matrix
        
        
        // Initialize first row of the distance and location matrices
        for (int x = 0; x < image.getWidth(); x++){
            M[x][0] = energy[x][0];
            edgeTo[x][0] = -1;
        }
        
        // Iterate from second to last row of image
        for (int y = 1; y < image.getHeight(); y++){
            // Iterate over each column of image
            for (int x = 0; x < image.getWidth(); x++){
                // Look for min distance between pixel and its neighbors
                M[x][y] = Double.POSITIVE_INFINITY;
                for (int i = x-1; i <= x+1; i++){
                    if (i > 0 && i < image.getWidth()){
                        if (M[i][y-1] + energy[x][y] < M[x][y]){
                            M[x][y] = M[i][y-1] + energy[x][y];
                            edgeTo[x][y] = i;
                        }
                    }
                }
            }
        }
        
        // Find location of pixel with smallest energy in the last row
        double minEnergy = Double.POSITIVE_INFINITY;
        int xMin = -1;
        for (int x = 0; x < image.getWidth(); x++){
            if (M[x][image.getHeight()-1] < minEnergy){
                minEnergy = M[x][image.getHeight()-1];
                xMin = x;
            }
        }
        
        // Find vertical seam starting from that pixel
        int[] seam = new int[image.getHeight()];
        for (int y = image.getHeight()-1; y >= 0; y--){
            seam[y] = xMin;
            xMin = edgeTo[xMin][y];
        }
        return seam;
    }
    
    void removeVerticalSeam(int[] seam){
        BufferedImage newImage = new BufferedImage(image.getWidth()-1, image.getHeight(), BufferedImage.TYPE_INT_RGB);
        // Iterate over each row of image
        for (int y = 0; y < image.getHeight(); y++){
            // Copy the left part of image
            for (int x = 0; x < seam[y]; x++){
                newImage.setRGB(x, y, image.getRGB(x, y));
            }
            // Copy the right part of image
            for (int x = seam[y]+1; x < image.getWidth(); x++){
                newImage.setRGB(x-1, y, image.getRGB(x, y));
            }
        }
        image = newImage;
    }
    
    int[] findHorizontalSeam(double[][] energy){
        double[][] M = new double[image.getWidth()][image.getHeight()];  // seam energy matrix
        int[][] edgeTo = new int[image.getWidth()][image.getHeight()];   // location matrix
        
        
        // Initialize first column of the distance and location matrices
        for (int y = 0; y < image.getHeight(); y++){
            M[0][y] = energy[0][y];
            edgeTo[0][y] = -1;
        }
        
        // Iterate from second to last column of image
        for (int x = 1; x < image.getWidth(); x++){
            // Iterate over each row of image
            for (int y = 0; y < image.getHeight(); y++){
                // Look for min distance between pixel and its neighbors 
                M[x][y] = Double.POSITIVE_INFINITY;
                for (int i = y-1; i <= y+1; i++){
                    if (i > 0 && i < image.getHeight()){
                        if (M[x-1][i] + energy[x][y] < M[x][y]){
                            M[x][y] = M[x-1][i] + energy[x][y];
                            edgeTo[x][y] = i;
                        }
                    }
                }
            }
        }
        
        // Find location of pixel with smallest energy in the last column
        double minEnergy = Double.POSITIVE_INFINITY;
        int yMin = -1;
        for (int y = 0; y < image.getHeight(); y++){
            if (M[image.getWidth()-1][y] < minEnergy){
                minEnergy = M[image.getWidth()-1][y];
                yMin = y;
            }
        }
        
        // Find vertical seam starting from that pixel
        int[] seam = new int[image.getWidth()];
        for (int x = image.getWidth()-1; x >= 0; x--){
            seam[x] = yMin;
            yMin = edgeTo[x][yMin];
        }
        return seam;
    }
    
    void removeHorizontalSeam(int[] seam){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight()-1, BufferedImage.TYPE_INT_RGB);
        // Iterate over each column of image
        for (int x = 0; x < image.getWidth(); x++){
            // Copy the upper part of image
            for (int y = 0; y < seam[x]; y++){
                newImage.setRGB(x, y, image.getRGB(x, y));
            }
            // Copy the lower part of image
            for (int y = seam[x]+1; y < image.getHeight(); y++){
                newImage.setRGB(x, y-1, image.getRGB(x, y));
            }
        }
        image = newImage;
    }
    
//    void resize(int newWidth, int newHeight){
//        int[] verticalSeam, horizontalSeam;
//        double[][] energy;
//        int width = image.getWidth(), height = image.getHeight();
//        for (int i = 0; i < width - newWidth; i++){
//            energy = computeEnergy();
//            verticalSeam = findVerticalSeam(energy);
//            removeVerticalSeam(verticalSeam);
//        }
//        for (int i = 0; i < height - newHeight; i++){
//            energy = computeEnergy();
//            horizontalSeam = findHorizontalSeam(energy);
//            removeHorizontalSeam(horizontalSeam);
//        }
//    }
}
