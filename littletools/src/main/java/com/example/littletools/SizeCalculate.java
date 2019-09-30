package com.example.littletools;

public class SizeCalculate {
    public static void main(String[] args) {

    }

    public void scaleSize(int width , int height){
        int maxImageWidth = 200;
        int maxImageHeight = 100;

        double scale = 1.0f;

        if (height > width) {
            if (height > maxImageHeight) {
                scale = (double) maxImageHeight / (double) height;
                height = maxImageHeight;
                width = (int) (width * scale);
            }
        } else {
            if (width > maxImageWidth) {
                scale = (double) maxImageWidth / (double) width;
                width = maxImageWidth;
                height = (int) (height * scale);
            }
        }
    }
}
