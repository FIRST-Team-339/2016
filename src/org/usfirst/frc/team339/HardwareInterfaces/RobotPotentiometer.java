// ====================================================================
// FILE NAME: RobotPotentiometer.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 19, 2009
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used to control a potentiometer.
// Radioshack Linear-Taper RobotPotentiometer #271-0092 is a 300 degree
// pot.
// Wiring: Black = 1, White = 2, Red = 3
//
// Dig-Key(blue) potentiometer 3852A-282-104A is a 280 degree pot
//
// Wiring: Black = 1, White = 2, Red = 3
//
// Bourns 3590S-2-502L string potentiometer is a 360.0 degree pot
//
// NOTE: pin order is 2,1,3 on the potentiometer.
// Wiring: Red = 2, White = 1, Black = 3
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import org.usfirst.frc.team339.HardwareTemplates.Potentiometer;
import edu.wpi.first.wpilibj.AnalogInput;

// -------------------------------------------------------
/**
 * This class is used to control a potentiometer.
 *
 * @class Potentiometer
 * @author Bob Brown
 * @written Feb 8, 2011 -------------------------------------------------------
 */
public class RobotPotentiometer extends AnalogInput
        implements Potentiometer
{
/**
 * ------------------------------------------------------
 *
 * @description this holds the number of degrees of turn that this pot has
 *              available to it
 * @author Bob Brown
 * @written Feb 6, 2011
 *          --------------------------------------------------------
 */
private final static double kDEFAULT_MAX_DEGREES = 270.0;

/**
 * -------------------------------------------------------
 *
 * @description this holds the number of values returned from the raw AD
 *              converter for the entire range of potentiometer
 * @author Bob Brown
 * @written April 18, 2011
 *          --------------------------------------------------------
 */
private final static double kTOTAL_RAW_UNITS = 4055;

/**
 * ------------------------------------------------------
 *
 * @description this holds the number of degrees of turn that this pot has
 *              available to it
 * @author Bob Brown
 * @written Feb 6, 2011
 *          --------------------------------------------------------
 */
private double maxDegreesForPotentiometer = kDEFAULT_MAX_DEGREES;

/**
 * ------------------------------------------------------
 *
 * @description variable used to determine the state of print to screen
 *              option, default to false
 * @author Bob Brown
 * @written Feb 19, 2011
 *          --------------------------------------------------------
 */
private boolean printToScreen = false;

/**
 * ------------------------------------------------------
 *
 * @description this denotes whether or not the pot is keeping
 *              values reversed (backwards)
 * @author Bob Brown
 * @written Feb 19, 2011
 *          --------------------------------------------------------
 */
private boolean isReversed = false;

// -------------------------------------------------------
/**
 * Create an instance of a Potentiometer class. Creates a analog channel
 * given a channel and uses the default module.
 *
 * @method Potentiometer() - constructor
 * @param channel
 *            - the port for the digital input
 * @author Bob Brown
 * @written Feb 8, 2011
 *          -------------------------------------------------------
 */
public RobotPotentiometer (final int channel)
{
    super(channel);
    this.maxDegreesForPotentiometer = kDEFAULT_MAX_DEGREES;
} // end LightSensor

// -------------------------------------------------------
/**
 * Create an instance of a Potentiometer class. Creates a analog channel
 * given a channel and uses the default module.
 *
 * @method Potentiometer() - constructor
 * @param channel
 *            - the port for the digital input
 * @param maxDegrees
 *            - maximum number of degrees that this potentiometer could
 *            travel
 * @author Bob Brown
 * @written Feb 8, 2011
 *          -------------------------------------------------------
 */
public RobotPotentiometer (final int channel, final double maxDegrees)
{
    super(channel);
    this.maxDegreesForPotentiometer = maxDegrees;
} // end LightSensor


// -------------------------------------------------------
/**
 * This function takes the raw value of the potentiometer (out of the max
 * raw units, kTOTAL_RAW_UNITS) and converts it to degrees
 *
 * @method get
 * @return int - returns the potentiometer degrees
 * @author Kevin McVey
 * @written Feb 19, 2010
 * @modified by Noah Golmant
 * @modified date Jan 22 2014
 *           -------------------------------------------------------
 */
@Override
public int get ()
{
    int retValue = (int) ((this.getValue() / kTOTAL_RAW_UNITS)
            * this.maxDegreesForPotentiometer);
    // --------------------------------
    // if the pot is not reversed -
    // compute the normalized degrees
    // --------------------------------
    if (this.isReversed == false)
        {
        // ---------------------------------
        // if we requested to print out the Normalized
        // value
        // ---------------------------------
        if (this.getPrintToScreen() == true)
            System.out.println("Pot - Normalized value: " +
                    retValue);
        return (retValue);
        }
    // ---------------------------------
    // we are reversed so compute the value
    // for a reversed pot
    // --------------------------------
    retValue = (int) ((this.maxDegreesForPotentiometer
            - (this.getValue() / kTOTAL_RAW_UNITS)
                    * this.maxDegreesForPotentiometer));
    // ---------------------------------
    // if we requested to print out the Normalized
    // value
    // ---------------------------------
    if (this.getPrintToScreen() == true)
        System.out.println("Pot - Normalized value: " +
                retValue);
    // ---------------------------------
    // If we are reversed, get it from the opposite direction
    // e.g. if we're at 200/300 and are reversed we want to really send
    // (300 - 200) / 300 = 100 / 300
    // ---------------------------------
    return (retValue);
} // end get

// -------------------------------------------------
/**
 * Gets the location within the range
 *
 * @param range
 *            Total range to use
 *            ex. range of 100 corresponds to -50 to +50
 * @return location in the range
 * @author Noah Golmant
 * @written February 9, 2013
 *          ------------------------------------------------
 */
@Override
public double get (double range)
{
    // (percent off from middle) * (range of values)
    double retVal = (((2 * this.get())
            - this.maxDegreesForPotentiometer)
            / this.maxDegreesForPotentiometer)
            * (range / 2);
    if (this.isReversed == false)
        {
        // ---------------------------------
        // if we requested to print out the Normalized
        // value
        // ---------------------------------
        if (this.getPrintToScreen() == true)
            System.out.println("Pot - Normalized value: " + retVal);
        return (retVal);
        }
    retVal = (retVal * -1.0);
    // ---------------------------------
    // if we requested to print out the Normalized
    // value
    // ---------------------------------
    if (this.getPrintToScreen() == true)
        System.out
                .println("Pot - Normalized value: " + retVal);
    // If the potentiometer is reversed, negate our position
    // within the range.
    return (retVal);
} // end get()

// ---------------------------------------------
/**
 * Gets the location within the specified ranged
 *
 * @param minRange
 *            minimum value in the range
 * @param maxRange
 *            maximum value in the range
 * @return location in the specified range
 *         ---------------------------------------------
 */
@Override
public double get (double minRange, double maxRange)
{
    double retVal = ((this.getValue() / kTOTAL_RAW_UNITS)
            * (maxRange - minRange))
            + minRange;
    if (this.isReversed == false)
        {
        // ---------------------------------
        // if we requested to print out the Normalized
        // value
        // ---------------------------------
        if (this.getPrintToScreen() == true)
            System.out.println("Pot - Normalized value: " + retVal);
        return retVal;
        }
    // ---------------------------------
    // if we requested to print out the Normalized
    // value
    // ---------------------------------
    retVal = retVal * -1.0;
    if (this.getPrintToScreen() == true)
        System.out
                .println("Pot - Normalized value: " + retVal);
    // If the potentiometer is reversed, negate our position
    // within the range.
    return (retVal);
} // end get()

// -------------------------------------------------------
/**
 * This function takes the raw value of the potentiometer (out of the max
 * raw units, kTOTAL_RAW_UNITS) and converts it to a given degree range
 *
 * @method get
 * @param degrees
 *            max degree value of the potentiometer
 * @return int - returns the potentiometer degrees
 * @author Noah Golmant
 * @written Jan 22, 2014
 *          -------------------------------------------------------
 */
@Override
public int get (int degrees)
{
    int retVal = (int) ((this.getValue() / kTOTAL_RAW_UNITS) * degrees);
    if (this.isReversed == false)
        {
        // ---------------------------------
        // if we requested to print out the Normalized
        // value
        // ---------------------------------
        if (this.getPrintToScreen() == true)
            System.out.println("Pot - Normalized value: " + retVal);
        return retVal;
        } // if
    retVal = (int) (this.maxDegreesForPotentiometer
            - ((this.getValue() / kTOTAL_RAW_UNITS) * degrees));
    // ---------------------------------
    // if we requested to print out the Normalized
    // value
    // ---------------------------------
    if (this.getPrintToScreen() == true)
        System.out
                .println("Pot - Normalized value: " + retVal);
    // If we are reversed, get it from the opposite direction
    // e.g. if we're at 200/300 and are reversed we want to really send
    // (300 - 200) / 300 = 100 / 300
    return (retVal);
} // end get()

// -------------------------------------------------
/**
 * Gets the location within the range using
 * our max degree value as the range.
 *
 * @return location in the range
 * @author Noah Golmant
 * @written February 9, 2013
 *          ------------------------------------------------
 */
@Override
public double getFromRange ()
{
    // (percent off from middle) * (range of values)
    double retVal = (((2 * this.get()) - this.getMaxDegrees())
            / this.getMaxDegrees())
            * (this.getMaxDegrees() / 2);
    if (this.isReversed == false)
        {
        // ---------------------------------
        // if we requested to print out the Normalized
        // value
        // ---------------------------------
        if (this.getPrintToScreen() == true)
            System.out.println("Pot - Normalized value: " + retVal);
        return retVal;
        }
    // ---------------------------------
    // if we requested to print out the Normalized
    // value
    // ---------------------------------
    retVal = (retVal * -1.0);
    if (this.getPrintToScreen() == true)
        System.out.println("Pot - Normalized value: " + retVal);
    // If the potentiometer is reversed, negate our position
    // within the range.
    return (retVal);
} // end getFromRange()

// -------------------------------------------------------
/**
 * returns the maximum degrees that this potentiometer can actually turn
 *
 * @method getMaxDegrees
 * @return double - the maximum degrees that this potentiometer can actually
 *         turn
 * @author Bob Brown
 * @written Feb 08, 2011
 *          -------------------------------------------------------
 */
public double getMaxDegrees ()
{
    return (this.maxDegreesForPotentiometer);
} // end getMaxDegrees

// -------------------------------------------------------
/**
 * This function returns weather or not we will print the poteniometer's
 * angle to the drivers station
 *
 * @return boolean - true or false value for our print to screen option
 * @method getPrintToScreen
 * @author Bob Brown
 * @written Jan 14, 2012
 *          -------------------------------------------------------
 */
public boolean getPrintToScreen ()
{
    return (this.printToScreen);
} // end getPrintToScreen()

/**
 * Whether or not this potentiometer is reversed
 *
 * @return if we are reversed
 * @author Noah Golmant
 * @written 27 Feb 2014
 */
public boolean isReversed ()
{
    return this.isReversed;
}

// -------------------------------------------------------
/**
 * sets and returns the maximum degrees that this potentiometer can actually
 * turn
 *
 * @method setMaxDegrees
 * @param maxDegrees
 *            - maximum degrees that this pot can actually turn
 * @return double - the maximum degrees that this potentiometer can actually
 *         turn
 * @author Bob Brown
 * @written Feb 08, 2011
 *          -------------------------------------------------------
 */
public double setMaxDegrees (final double maxDegrees)
{
    return (this.maxDegreesForPotentiometer = maxDegrees);
} // end setMaxDegrees

// -------------------------------------------------------

/**
 * This function sets and returns decision on weather or not we want to
 * print the potentiometer's angle to drivers station
 *
 * @param newValue
 *            - new number of items to check when we get a new true or false
 *            value
 * @return boolean - true or false setting for option to print distance to
 *         Drivers station
 * @method setPrintToScreen
 * @author Bob Brown
 * @written Jan 14, 2012
 *          -------------------------------------------------------
 */
public boolean setPrintToScreen (boolean newValue)
{
    this.printToScreen = newValue;
    return (this.getPrintToScreen());
} // end setPrintToScreen()

/**
 * Sets whether the potentiometer is reversed/wired backwards.
 *
 * @param isReversed
 * @author Noah Golmant
 * @written 27 Feb 2014
 */
public void setReverse (boolean isReversed)
{
    this.isReversed = isReversed;
}

} // end class RobotPotentiometer
