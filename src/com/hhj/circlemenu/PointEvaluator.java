package com.hhj.circlemenu;

import android.animation.TypeEvaluator;


public class PointEvaluator implements TypeEvaluator {

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;
        float currentX = startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX());
        float currentY = startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY());
        Point currentPoint = new Point(currentX, currentY);
        return currentPoint;
    }

}
