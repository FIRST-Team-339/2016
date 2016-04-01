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
 * @param robotHasBall
 * 
 *            if the armIR senses that the robot already has
 *            a ball, send that info to the SmartDashboard.
 *            This will also turn the Driver Station green.
 * 
 * @author Ryan McGee
 */
public static void updateBallStatus (boolean robotHasBall)
{
    if (robotHasBall == true)
        {
        SmartDashboard.putBoolean("Has Ball", true);
        }
    else
        {
        SmartDashboard.putBoolean("Has Ball", false);
        }
}


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
            SmartDashboard.putBoolean("Straight", false);
            SmartDashboard.putBoolean("Stop", false);
            break;

        case right:
            SmartDashboard.putBoolean("Right", true);
            SmartDashboard.putBoolean("Left", false);
            SmartDashboard.putBoolean("Straight", false);
            SmartDashboard.putBoolean("Stop", false);
            break;

        case neutral:
            SmartDashboard.putBoolean("Right", false);
            SmartDashboard.putBoolean("Left", false);
            SmartDashboard.putBoolean("Straight", false);
            SmartDashboard.putBoolean("Stop", false);
            break;

        case linedUp:
            SmartDashboard.putBoolean("Straight", false);
            SmartDashboard.putBoolean("Right", false);
            SmartDashboard.putBoolean("Left", false);
            SmartDashboard.putBoolean("Stop", false);
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

/**
 * Set the state of blinky lights on the SmartDashboard.
 * 
 * @param direction
 */
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
 *         neutral keeps both off. linedUp lights
 *         up the background green, stop lights up
 *         the background red.
 * 
 *         NOTE: Remember to add update()
 *         or else this code will not work!
 *
 */
public static enum Direction
    {
    /**
     * lights up an orange arrow on the left.
     */
    left,
    /**
     * lights up an orange arrow on the right.
     */
    right,
    /**
     * Nicht. Nein. Nada.
     */
    neutral,
    /**
     * flashes the world green.
     */
    linedUp
    }


}



/**
 * Once there was a traveler traveling down an endless road.
 * He was looking for a place to stay for a place to stay for the night as he
 * had been traveling
 * for who knows how long. Endlessly perhaps. He was hungry and tired and his
 * feet hand turned to Literal
 * sandpaper. Literal being the company who manufactures the sandpaper. He was
 * about halfway through the
 * night which lasts about half a day of eternity. An eternity is the time it
 * takes for a bird to
 * wear down a mountain of diamonds. When he came across a town, or village, or
 * pogwomp, or whatever you
 * to call it. He knocked on the first door that he encountered, but it was
 * turned away by the bats that
 * inhabited the hut. The traveler went to the 2nd door that he found. He
 * discovered that the door
 * was not a door at all, but rather a very large Venus fly trap. The 3rd door
 * he encountered however,
 * was home to the strange creatures we call humans. The humans welcomed him
 * into his home. They gave him
 * a place sleep, and breakfast in the morning. They were rather annoyed that
 * his Literal TM sandpaper feet
 * were scratching up the floor. The traveler set off to leave, but was stopped
 * by a boy by the name of
 * Stubby. "Let me come with you" said Stubby.
 * "I want to be a traveler as well, like you."
 * "Don't be silly" said the traveler. "You are but a boy, a small boy, this
 * road is endless. You will
 * have been reborn by the time you come back."
 * "But please sir" said the little boy. "I can't be a farmer."
 * "Why not?" said the farmer.
 * "Because in truth, I'm a rock named Bartholomew."
 * "Oh, well come a long then. There are many wonders on this endless road"
 * And so the traveler and rock traveled for many days, nights, and miles, until
 * they came across a
 * cross roads. "I thought you said this was endless." said the rock. "A road
 * such as this is bound
 * to have many choices." said the traveler. "How do they know that it is the
 * right one?" inquired
 * Bartholomew.
 * "The right one?" replied the Traveller. "Why, that is a very complicated
 * question. It implies the existance of a right way to go. A right way of life.
 * No, in order to know which way is right and which way is wrong, there is
 * something you need that is very hard to come by."
 * "And what is that?" asked the rock.
 * "Guidance."
 * 
 */
