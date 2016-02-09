package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * 
 * Used to light up arrows on the Smart Dashboard.
 * 
 * @author Michael Andrzej Klaczynski
 *
 */
public class Guidance
{

private static Direction direction = Direction.neutral;

/**
 * Changes value of arrows on Smart Dashboard based on the set direction.
 */
public void update ()
{
    switch (direction)
    {
        case left:
            SmartDashboard.putBoolean("Left", true);
            SmartDashboard.putBoolean("Right", false);
            break;

        case right:
            SmartDashboard.putBoolean("Right", true);
            SmartDashboard.putBoolean("Left", false);
            break;

        case neutral:
            SmartDashboard.putBoolean("Right", false);
            SmartDashboard.putBoolean("Left", false);
            break;
    }
}

/**
 * 
 * @return the direction
 */
public static Direction getDirection ()
{
    return direction;
}

public void setDirection (Direction direction)
{
    Guidance.direction = direction;
}

/**
 * 
 * @author Michael Andrzej Klaczynski
 * 
 *         left lights up the left arrow.
 *         right lights up the right arrow.
 *         neutral keeps both off.
 *
 */
public static enum Direction
{
    left, right, neutral
}
}
