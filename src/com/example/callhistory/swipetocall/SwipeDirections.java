package com.example.callhistory.swipetocall;

import java.util.Arrays;
import java.util.List;

/**
 * Class containing a set of constant directions used throughout the package
 *
 * Created by wdullaer on 02.07.14.
 */
public class SwipeDirections {
    // Constants
    public static final int DIRECTION_NORMAL_LEFT = -1;
    public static final int DIRECTION_FAR_LEFT = -2;
    public static final int DIRECTION_NORMAL_RIGHT = 1;
    public static final int DIRECTION_FAR_RIGHT = 2;
    public static final int DIRECTION_NEUTRAL = 0;

    static List<Integer> getAllDirections(){
        return Arrays.asList(
                DIRECTION_FAR_LEFT,
                DIRECTION_FAR_RIGHT,
                DIRECTION_NEUTRAL,
                DIRECTION_NORMAL_LEFT,
                DIRECTION_NORMAL_RIGHT
        );
    }
}
