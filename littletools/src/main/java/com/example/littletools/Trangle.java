package com.example.littletools;

/**
 * 三角形的一些计算
 */
public class Trangle {
    public static void main(String[] args) {

        int width = 3;
        int height = 4;
        //矩形里对角线的计算和相应的角度
        //计算对角线
        double diagonal = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

        //反正切 计算角度
        double angle = Math.toDegrees(Math.atan((double) width / (double) height));

    }
}
