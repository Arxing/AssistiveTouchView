package org.arxing.library;

import android.graphics.PointF;

public enum PointSpace {
    SPACE1,
    SPACE2,
    SPACE3,
    SPACE4,
    X_PLUS,
    X_LESS,
    Y_PLUS,
    Y_LESS;

    /**
     * 取得角度所在的象限範圍
     *
     * @param degree 角度
     * @return 象限範圍
     */
    public static PointSpace getSpace(float degree) {
        degree = parseRealDegree(degree);
        if (degree == 0)
            return PointSpace.Y_LESS;
        else if (degree == 90)
            return PointSpace.X_PLUS;
        else if (degree == 180)
            return PointSpace.Y_PLUS;
        else if (degree == 270)
            return PointSpace.X_LESS;
        else if (degree > 0 && degree < 90)
            return PointSpace.SPACE4;
        else if (degree > 90 && degree < 180)
            return PointSpace.SPACE1;
        else if (degree > 180 && degree < 270)
            return PointSpace.SPACE2;
        else if (degree > 270 && degree < 360)
            return PointSpace.SPACE3;
        throw new Error("Invalid degree: " + degree);
    }

    /**
     * 輸入角度並轉換為真實角度
     *
     * @param degree 輸入的角度
     * @return 轉換後的角度
     * <pre>
     * ex:
     *  0 -> 0
     *  361 -> 1
     *  -1 -> 359
     */
    public static float parseRealDegree(float degree) {
        if (degree < 0) {
            while (degree < 0)
                degree += 360;
        } else {
            degree %= 360;
        }
        return degree;
    }

    public static PointF getPoint(float r, float degree) {
        PointSpace space = getSpace(degree);
        float radian = (float) Math.toRadians(degree);
        PointF p = new PointF();
        switch (space) {
            case SPACE1:
            case SPACE2:
            case SPACE3:
            case SPACE4:
                p.x = (float) +(r * Math.sin(radian));
                p.y = (float) -(r * Math.cos(radian));
                break;
            case X_PLUS:
                p.x = r;
                p.y = 0;
                break;
            case X_LESS:
                p.x = -r;
                p.y = 0;
                break;
            case Y_PLUS:
                p.x = 0;
                p.y = r;
                break;
            case Y_LESS:
                p.x = 0;
                p.y = -r;
                break;
        }
        return p;
    }
}
