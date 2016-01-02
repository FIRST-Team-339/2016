// ====================================================================
// FILE NAME: Potentiometer.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 19, 2009
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used to control a potentiometer.
// Radioshack Linear-Taper Potentiometer #271-0092 is a 300 degree
// pot.
// Wiring: Black = 1, White = 2, Red = 3
//
// Dig-Key(blue) potentiometer 3852A-282-104A is a 280 degree pot
//
// Wiring: Black = 1, White = 2, Red = 3
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.AnalogInput;

// -------------------------------------------------------
/**
 * This class is used to control a potentiometer.
 *
 * @class Potentiometer
 * @author Bob Brown
 * @written Feb 8, 2011
 *          -------------------------------------------------------
 */
public class Potentiometer extends AnalogInput
{
    /**
     * ------------------------------------------------------
     *
     * @description this holds the number of degrees of turn that
     *              this pot has available to it
     * @author Bob Brown
     * @written Feb 6, 2011
     *          --------------------------------------------------------
     */
    private final static double kDEFAULT_MAX_DEGREES = 270.0;

    /**
     * -------------------------------------------------------
     *
     * @description this holds the number of values returned from
     *              the raw AD converter for the entire range of
     *              potentiometer
     * @author Bob Brown
     * @written April 18, 2011
     *          --------------------------------------------------------
     */
    private final static double kTOTAL_RAW_UNITS = 1000.0;

    // -------------------------------------------------------
    /**
     * Create an instance of a LightSensor class.
     * Creates a digital input given a channel and the module
     * passed in.
     *
     * @method Potentiometer() - constructor
     * @param channel
     *            - the port for the digital input
     * @param slot
     *            - the slot where the digital board is located
     * @author Bob Brown
     * @written Feb 8, 2011
     *          -------------------------------------------------------
     */
    /*
     * public Potentiometer (
     * final int channel)
     * {
     * super(channel);
     * this.maxDegreesForPotentiometer = kDEFAULT_MAX_DEGREES;
     * } // end Potentiometer
     */

    // -------------------------------------------------------
    /**
     * Create an instance of a LightSensor class.
     * Creates a digital input given a channel and the module
     * passed in.
     *
     * @method Potentiometer() - constructor
     * @param channel
     *            - the port for the digital input
     * @param slot
     *            - the slot where the digital board is located
     * @param maxDegrees
     *            - maximum number of degrees that this potentiometer
     *            could travel
     * @author Bob Brown
     * @written Feb 8, 2011
     *          -------------------------------------------------------
     */
    /*
     * public Potentiometer (
     * final int slot,
     * final int channel,
     * final double maxDegrees)
     * {
     * super(slot, channel);
     * this.maxDegreesForPotentiometer = maxDegrees;
     * } // end Potentiometer
     */

    /**
     * ------------------------------------------------------
     *
     * @description this holds the number of degrees of turn that
     *              this pot has available to it
     * @author Bob Brown
     * @written Feb 6, 2011
     *          --------------------------------------------------------
     */
    private double maxDegreesForPotentiometer = kDEFAULT_MAX_DEGREES;

    /**
     * ------------------------------------------------------
     *
     * @description variable used to determine the state of
     *              print to screen option, default to false
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
    private boolean reversed = false;

    // -------------------------------------------------------
    /**
     * Create an instance of a Potentiometer class.
     * Creates a analog channel given a channel and uses the default module.
     *
     * @method Potentiometer() - constructor
     * @param channel
     *            - the port for the digital input
     * @author Bob Brown
     * @written Feb 8, 2011
     *          -------------------------------------------------------
     */
    public Potentiometer (final int channel)
        {
        super(channel);
        this.maxDegreesForPotentiometer = kDEFAULT_MAX_DEGREES;
        } // end LightSensor

    // -------------------------------------------------------
    /**
     * Create an instance of a Potentiometer class.
     * Creates a analog channel given a channel and uses the default module.
     *
     * @method Potentiometer() - constructor
     * @param channel
     *            - the port for the digital input
     * @param maxDegrees
     *            - maximum number of degrees that this potentiometer
     *            could travel
     * @author Bob Brown
     * @written Feb 8, 2011
     *          -------------------------------------------------------
     */
    public Potentiometer (final int channel, final double maxDegrees)
        {
        super(channel);
        this.maxDegreesForPotentiometer = maxDegrees;
        } // end LightSensor

    // -------------------------------------------------------
    /**
     * This function takes the value we get from the
     * potentiometer, normalizes it (between 0 and 1000)
     * and outputs a degree value. The range should be
     * between 0.0 and 100.0 within the range of degrees
     * up to the maxDegreesForPotentiometer
     *
     * @method get
     * @return double - returns the Potentiometer degrees
     * @author Kevin McVey
     * @written Feb 19, 2010
     * @modified by Bob Brown
     * @modified date Feb 8, 2011
     *           -------------------------------------------------------
     */
    public double get ()
        {
        int tempValue;
        double returnValue;
        // ---------------------------------
        // First, normalize the potentiometer
        // value by ensuring it falls between
        // zero and one thousand.
        // ---------------------------------
        tempValue =
            Math.min(Math.max(this.getValue(), 0), (int) kTOTAL_RAW_UNITS);
        // ---------------------------------
        // Then convert the value of the
        // normalized potentiometer to
        // usable degrees. The potentiometer
        // gives us values between 0 and 1000
        // so we must multiply this by the
        // total number of degrees that the
        // pot has.
        // ---------------------------------
        if (this.reversed == false)
        {
        returnValue =
            (tempValue * (this.maxDegreesForPotentiometer / kTOTAL_RAW_UNITS));
        }
        else
        {
        returnValue =
            (this.maxDegreesForPotentiometer - (tempValue * (this.maxDegreesForPotentiometer / kTOTAL_RAW_UNITS)));
        }
        // ---------------------------------
        // if we requested to print out the angle
        // do so here
        // ---------------------------------
        if (this.getPrintToScreen() == true)
        {
        System.out.println("Pot Angle: " + returnValue);
        }
        // ---------------------------------
        // return the computed value
        // ---------------------------------
        return returnValue;
        } // end get

    // -------------------------------------------------------
    /**
     * returns the maximum degrees that this potentiometer
     * can actually turn
     *
     * @method getMaxDegrees
     * @return double - the maximum degrees that this potentiometer
     *         can actually turn
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
     * This function returns weather or not we will print the
     * poteniometer's angle to the drivers station
     *
     * @return boolean - true or false value for our print to screen
     *         option
     * @method getPrintToScreen
     * @author Bob Brown
     * @written Jan 14, 2012
     *          -------------------------------------------------------
     */
    public boolean getPrintToScreen ()
        {
        return (this.printToScreen);
        } // end getPrintToScreen()

    // -------------------------------------------------------
    /**
     * returns whether or not the potentiometer is sending
     * values that are reversed
     *
     * @method isReversed
     * @return boolean - whether the potentiometer is sending us
     *         reversed values
     * @author Bob Brown
     * @written Feb 08, 2011
     *          -------------------------------------------------------
     */
    public boolean isReversed ()
        {
        return (this.reversed);
        } // end setReverse

    // -------------------------------------------------------
    /**
     * sets and returns the maximum degrees that this potentiometer
     * can actually turn
     *
     * @method setMaxDegrees
     * @param maxDegrees
     *            - maximum degrees that this pot can actually
     *            turn
     * @return double - the maximum degrees that this potentiometer
     *         can actually turn
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
     * This function sets and returns decision on weather or not we
     * want to print the potentiometer's angle to drivers station
     *
     * @param newValue
     *            - new number of items to check when we get
     *            a new true or false value
     * @return boolean - true or false setting for option to print distance
     *         to Drivers station
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

    // -------------------------------------------------------
    /**
     * sets and returns whether or not the potentiometer is sending
     * values that are reversed
     *
     * @method setReverse
     * @param reversed
     *            - true or false
     * @return reversed or not
     * @author Bob Brown
     * @written Feb 08, 2011
     *          -------------------------------------------------------
     */
    public boolean setReverse (final boolean reversed)
        {
        return (this.reversed = reversed);
        } // end setReverse

} // end class Potentiometer