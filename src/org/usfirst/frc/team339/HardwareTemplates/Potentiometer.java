// ====================================================================
// FILE NAME: Potentiometer.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 22, 2014
// CREATED BY: Noah Golmant
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used as the base class for either a potentiometer on the robot
// or on the cypress board.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareTemplates;

/**
 * @author Kilroy
 */
public interface Potentiometer
{
    // ---------------------------------------------------------
    /**
     * Gets the current degree value out of our max range
     * 
     * @return degree value of the potentiometer
     * @author Noah Golmant
     * @written December 12, 2013
     *          ----------------------------------------------------------
     */
    public int get ();

    // -------------------------------------------------
    /**
     * Gets the location within the range
     * 
     * @param range
     *            Total range to use
     *            ex. range of 100 corresponds to -50 to +50
     * @return location in the range
     * @author Noah Golmant
     * @written Jan 22, 2014
     *          ------------------------------------------------
     */
    public double get (double range);

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
    public double get (double minRange, double maxRange);

    // -------------------------------------------------------
    /**
     * Gets the current degree value out of our max range
     * 
     * @param degrees
     *            max degree value
     * @return degree value of the potentiometer
     * @author Noah Golmant
     * @written December 12, 2013
     *          ----------------------------------------------------------
     */
    public int get (int degrees);

    // -------------------------------------------------
    /**
     * Gets the location within the range
     * 
     * @return location in the range
     * @author Noah Golmant
     * @written February 9, 2013
     *          ------------------------------------------------
     */
    public double getFromRange ();

    // -------------------------------------------------
    /**
     * Gets the raw voltage of the potentiometer
     * 
     * @return The raw value of the potentiometer
     * @author Noah Golmant
     * @written February 7, 2013
     *          ------------------------------------------------
     */
    public double getVoltage ();

}
