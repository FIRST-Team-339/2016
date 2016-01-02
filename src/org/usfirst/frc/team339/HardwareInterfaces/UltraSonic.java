// ====================================================================
// FILE NAME: UltraSonic.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 14, 2012
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used to control an Ultrasonic Rangefinder. We
// built this code with the MaxBotix - LV-MaxSonar-EZ1.
// The following was how it was wired:
// Red Wire - +5V
// Black Wire - GRN (ground)
// White Wire - AN - Analog Output Voltage
// NOTE the white and black wire are seperated on the board
// Scaling factor: 0.5

// MaxBotix - LV-MaxSonar-MB1023
// The following was how it was wired:
// Red Wire - +5V
// Black Wire - GRN (ground)
// White Wire - AN - Analog Output Voltage
// NOTE the white and black wire are seperated on the board
// Scaling factor: 0.223
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.AnalogInput;

// -------------------------------------------------------
/** This class is used to control an ultrasonic rangefinder.
 *
 * @class UltraSonic
 * @author Bob Brown
 * @written Jan 14, 2012
 *          ------------------------------------------------------- */

public final class UltraSonic extends AnalogInput
{

    // ---------------------------------------------
    // static declarations
    // ---------------------------------------------
    private final double DISTANCE_TRAVELED_IN_SEC =
        ((8.0 /* ft */* 12.0) + 6.0);

    private final double WEIGHT_FACTOR = 1.4;

    // ---------------------------------
    // amount of time to keep past sonar values
    // in seconds. usually about 1 sec
    // ---------------------------------
    private double amountOfTimeToKeepPastValues = 1.0;

    // ---------------------------------
    // array which stores the sonars values
    // after they have been processed for
    // confidence. This means that if we get
    // a value that doesn't make sense we
    // throw it out. In other words, values
    // need to be near the last value.
    // ---------------------------------
    private final int[] confidenceValueArray =
    // new int[(int)((1000.0 / this.sensorPeriodicity)
    // /* num of responces from sonar per second 1000 / 50 (ms per
    // response)*/ *
    // amountOfTimeToKeepPastValues )];
        new int[200];

    // ---------------------------------
    // Counter variable used for print
    // inches update rate
    // ---------------------------------
    private int counter = 0;

    // ---------------------------------
    // distance that the ultrasonic board
    // is offset from. This will give us
    // a distance that takes into account
    // the bumpers and location of the
    // board itself from the "front" of
    // the robot
    // ---------------------------------
    private int distanceFromNearestBumper = 0;

    // ---------------------------------
    // the last good value found (when using
    // confidence) by the sonar. Since the
    // values are very poor, we keep the last
    // good value to return if the value we just
    // got seems bad
    // ---------------------------------
    private int lastGoodValue = java.lang.Integer.MAX_VALUE;

    // ---------------------------------
    // number of items in the stored value
    // array to check backwards for past
    // values to make sure that they are
    // "good"
    // ----------------------------------
    private int numberOfItemsToCheckForValidity = 3;

    // ---------------------------------
    // present index into the confidenceArray
    // ---------------------------------
    private int presentConfidenceValueIndex = 0;

    // ---------------------------------
    // variable used to determine the state of
    // print to screen option, default to
    // false
    // ---------------------------------
    private boolean printToScreen = false;

    // ---------------------------------
    // scaling factor - used to multiply the
    // raw value from the Analog channel output
    // by to get a range that this ultrasonic board
    // is within
    // ---------------------------------
    private double scalingFactor = 1.0;

    // ---------------------------------
    // periodicity of the sonar sensor
    // defaults to 50 ms.
    // ---------------------------------
    private double sensorPeriodicity = 50.0;

    // ---------------------------------
    // denotes whether or not we use confidence
    // to return the distance back to the caller.
    // Confidence makes sure that the distance
    // seems "reasonable"
    // ---------------------------------
    private boolean useConfidenceCalculations = true;

    // ---------------------------------
    // weighted distance - distance within
    // which we give more credence to the
    // distance coming in from the sonar
    // ---------------------------------
    private int weightedDistance = (10 /* ft */* 12);

    // -------------------------------------------------------
    /** Create an instance of an UltraSonic class.
     * Creates a analog channel given a channel and uses the default
     * slot (module).
     *
     * @method UltraSonic() - constructor
     * @param channel
     *            - the port for the digital input
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public UltraSonic (final int channel)
        {
        super(channel);
        } // end UltraSonic()

    // -------------------------------------------------------
    /** Create an instance of an UltraSonic class.
     * Creates a analog channel given a channel and uses the default
     * slot (module).
     *
     * @method UltraSonic() - constructor
     * @param channel
     *            - the port for the digital input
     * @param scalingFactor
     *            - how to scale the Analog Channel to keep
     *            the correct range on the Ultrasonic range finder
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public UltraSonic (final int channel, double scalingFactor)
        {
        super(channel);
        this.setScalingFactor(scalingFactor);
        } // end UltraSonic()

    // -------------------------------------------------------
    /** Create an instance of an UltraSonic class..
     * Creates a analog channel given a channel and a slot (module).
     *
     * @method UltraSonic class() - constructor
     * @param channel
     *            - the port for the digital input
     * @param slot
     *            - the slot where the digital board is located
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    /* public UltraSonic (
 * final int slot,
 * final int channel)
 * {
 * super(slot, channel);
 * } // end UltraSonic() */
    // -------------------------------------------------------
    /** Create an instance of an UltraSonic class..
     * Creates a analog channel given a channel and a slot (module).
     *
     * @method UltraSonic class() - constructor
     * @param channel
     *            - the port for the digital input
     * @param slot
     *            - the slot where the digital board is located
     * @param scalingFactor
     *            - how to scale the Analog Channel to keep
     *            the correct range on the Ultrasonic range finder
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    /* public UltraSonic (
 * final int slot,
 * final int channel,
 * double scalingFactor)
 * {
 * super(slotchannel);
 * this.setScalingFactor(scalingFactor);
 * } // end UltraSonic() */
    // -------------------------------------------------------
    /** This function returns the amount of time to keep past sonar
     * values for to be used for past calculations
     *
     * @return double - amount of time to keep past values for - usually
     *         about 1 sec. worth
     * @method getAmountOfTimeToKeepPastValues
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double getAmountOfTimeToKeepPastValues ()
        {
        return (this.amountOfTimeToKeepPastValues);
        } // end getAmountOfTimeToKeepPastValues()

    // -------------------------------------------------------
    /** This function returns the whether or not we are using
     * Confidence processing in our calculations for this sensor.
     *
     * @return boolean - true - if we are using Confidence
     *         false - if we are not
     * @method getConfidenceCalculationStatus
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean getConfidenceCalculationStatus ()
        {
        // System.out.println("Confidence is presently" +
        // useConfidenceCalculations);
        return (this.useConfidenceCalculations);
        } // end getConfidenceCalculationStatus()

    // -------------------------------------------------------
    /** This function returns the distance from Kilroy that the
     * nearest object is.
     *
     * @return int - returns the distance from Kilroy
     * @method getDistanceFromNearestBumper
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int getDistanceFromNearestBumper ()
        {
        int normalizedDistance;

        // -------------------------------------
        // get the raw value
        // -------------------------------------
        normalizedDistance = this.getRefinedDistanceValue();

        // -------------------------------------
        // check to make sure that the ultrasonic is
        // giving us legitimate values. If so
        // subtract out the distance from the
        // nearest edge to give us the distance
        // to Kilroy
        // -------------------------------------
        if (normalizedDistance > this.distanceFromNearestBumper)
        {
        normalizedDistance =
            normalizedDistance - this.distanceFromNearestBumper;
        }
        // -------------------------------------
        // check to make sure that we want to print the distance
        // to the Dev laptop
        // -------------------------------------
        if (this.printToScreen == true)
            {

        System.out.println("The distance is " + (normalizedDistance / 12) +
            "' and " + (normalizedDistance % 12) + "\" or " +
            normalizedDistance);
            this.counter++;
            // -------------------------------------
            // check to see if counter reached 50 so the
            // distance value does not change too quickly
            // to read on the laptops
            // -------------------------------------
            if (this.counter == 50)
                {
                this.counter = 0;
                }
            } // end if

        return (normalizedDistance);
        } // end getKilroyDistanceValue()

    // -------------------------------------------------------
    /** This function returns the distance value using the
     * confidence array. This function is using weighted proximity.
     * If the latest value is close to the last 2 values, then this
     * is considered good. If the latest value is < 10 ft, then it
     * is considered better than a value that is > 10 ft.
     *
     * @return int - distance from an object we presently are
     * @method getDistanceValueWithConfidence
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    private int getDistanceValueWithConfidence ()
        {
        int possibleGoodValue = java.lang.Integer.MAX_VALUE;
        int numberToCheck = this.numberOfItemsToCheckForValidity;
        int index = 0;
        boolean outlier = false;
        int workConfidenceValueIndex = this.presentConfidenceValueIndex;
        double distanceTraveledPerTic =
            (this.DISTANCE_TRAVELED_IN_SEC / this.sensorPeriodicity) * 2;
        // The distance traveled is what Kilroy can
        // travel and what another robot can travel
        // towards Kilroy (*2)
        // -------------------------------------
        // determine the number of items that
        // need to be in a row that we are going
        // to check. Make sure that that the
        // number that we are requesting fits
        // within the table itself
        // -------------------------------------
        if (numberToCheck >= (this.confidenceValueArray.length - 1))
        {
        numberToCheck = this.confidenceValueArray.length - 1;
        }
        // -------------------------------------
        // if we haven't populated enough entries
        // in the table yet, return the value as
        // the gospel
        // -------------------------------------
        if ((this.confidenceValueArray[this.presentConfidenceValueIndex] == java.lang.Integer.MAX_VALUE) &&
        (this.presentConfidenceValueIndex < numberToCheck))
        {
        this.lastGoodValue =
            this.confidenceValueArray[this.presentConfidenceValueIndex - 1];
        // -------------------------------------
        // otherwise there are enough entries in
        // stored array to check to make sure that we
        // don't have an outlier
        // -------------------------------------
        }
        else
        {
        // -------------------------------------
        // loop for all the requested entries until
        // we run out of entries or we find an outlier
        // -------------------------------------
        for (index = 1; (index <= (numberToCheck + 1)) &&
            (outlier == false); index++)
            {
            // -------------------------------------
            // if the working index == 0, then we
            // need to set it to the last item in
            // the array
            // -------------------------------------
            if (workConfidenceValueIndex == 0)
                {
                workConfidenceValueIndex =
                    this.confidenceValueArray.length - 1;
                // -------------------------------------
                // otherwise go back in the array to get
                // the last value
                // -------------------------------------
                }
            else
                {
                workConfidenceValueIndex--;
                }
            // -------------------------------------
            // if the distance we are checking on is
            // within the weight area, then we will allow
            // for double the distance
            // -------------------------------------
            if (index == 1)
                {
                // System.out.println("Original Distance value is "
                // +
                // confidenceValueArray[workConfidenceValueIndex]);
                possibleGoodValue =
                    this.confidenceValueArray[workConfidenceValueIndex];
                if (this.confidenceValueArray[workConfidenceValueIndex] < this.weightedDistance)
                    {
                    distanceTraveledPerTic =
                        distanceTraveledPerTic * this.WEIGHT_FACTOR;
                    }
                } // if
            // -------------------------------------
            // if this is not the zeroth item and
            // the distance is too large, then set
            // the outlier to TRUE
            // -------------------------------------
            if (workConfidenceValueIndex != 0)
                if (Math
                    .abs(this.confidenceValueArray[workConfidenceValueIndex] -
                        this.confidenceValueArray[workConfidenceValueIndex - 1]) > distanceTraveledPerTic)
                    {
                    outlier = true;
                    }
            // -------------------------------------
            // if this is the zeroth item, then we need
            // to be carefull that we wrap around, and it
            // the distance between the two items is
            // greater than we want then set this as
            // an outlier
            // -------------------------------------
            if ((workConfidenceValueIndex == 0) &&
                (Math
                    .abs(this.confidenceValueArray[workConfidenceValueIndex] -
                        this.confidenceValueArray[this.confidenceValueArray.length - 1]) > distanceTraveledPerTic))
                {
                outlier = true;
                }
            } // for
        // -------------------------------------
        // if there wasn't an outlier, then we
        // need to save the last good value in
        // case we don't get a good value next
        // time
        // -------------------------------------
        if (outlier == false)
            {
            this.lastGoodValue = possibleGoodValue;
            }
        } // else

        // System.out.println("Distance with Confidence is " + lastGoodValue);
        return (this.lastGoodValue);
        } // end getDistanceValueWithConfidence

    // -------------------------------------------------------
    /** This function returns the number of items that we
     * are going to search backwards in the value array to
     * make sure that everything is good.
     *
     * @return int - number of items to check backwards in the
     *         confidence array to make sure that the latest sonar
     *         value is "good"
     * @method getNumberOfItemsToCheckBackwardForValidity
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int getNumberOfItemsToCheckBackwardForValidity ()
        {
        return (this.numberOfItemsToCheckForValidity);
        } // end getNumberOfItemsToCheckBackwardForValidity()

    // -------------------------------------------------------
    /** gets the distance from the nearest bumper that the
     * Ultrasonic board.
     *
     * @return distance value held by this class that represents the
     *         distance from the nearest bumper to the Ultrasonic
     *         board
     * @method getOffsetDistanceFromNearestBummper
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int getOffsetDistanceFromNearestBummper ()
        {
        return (this.distanceFromNearestBumper);
        } // end getOffsetDistanceFromNearestBummper()

    // -------------------------------------------------------
    /** This function returns weather or not we will print the
     * print distance value to drivers station
     *
     * @return boolean - true or false value for our print to screen
     *         option
     * @method getPrintToScreen
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean getPrintToScreen ()
        {
        return (this.printToScreen);
        } // end getPrintToScreen()

    // -------------------------------------------------------
    /** This function returns the distance from the ultrasonic board that the
     * nearest object is.
     *
     * @return int - returns the distance from the ultrasonic board
     * @method getRefinedDistanceValue
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int getRefinedDistanceValue ()
        {
        int value = (int) (this.getValue() * this.scalingFactor);

        // System.out.println("Raw distance is: " + value);
        // -------------------------------------
        // if we are using confidence with our
        // values, we will need to save this
        // value into the confidence array.
        // Increment the index for next time and
        // make sure that it is in range. Then
        // get the value with confidence back and
        // return it to the caller
        // -------------------------------------
        if (this.getConfidenceCalculationStatus() == true)
        {
        this.setDistanceValueIntoConfidenceArray(value);
        value = this.getDistanceValueWithConfidence();
        } // if
        return (value);
        } // end getRefinedDistanceValue()

    // -------------------------------------------------------

    /** This function returns the scaling factor that the Ultrasonic is
     * presently using. The scaling factor is used to multiple the
     * Analog Channel output by. The multiplication factor keeps the
     * range of the Ultrasonic in the correct range. The default
     * scaling factor is 0.5
     *
     * @return double - the present scaling factor
     * @method getScalingFactor
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double getScalingFactor ()
        {
        return (this.scalingFactor);
        } // end getScalingFactor()

    // ---------------------------------------------
    // private declarations
    // ---------------------------------------------

    // -------------------------------------------------------
    /** This function returns the sensor Periodicity. It is usually
     * about 50 ms. unless changed by a call setSensorPeriodicity()
     *
     * @return double - the present sensor periodicity (usually 50 ms.)
     * @method getSensorPeriodicity
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double getSensorPeriodicity ()
        {
        return (this.sensorPeriodicity);
        } // end getSensorPeriodicity()

    // -------------------------------------------------------

    /** This function returns the weighted distance where withing
     * we give more weight to the values input from the sonar
     *
     * @return int - distance within which we weight the sonar values
     * @method getWeightedDistance
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int getWeightedDistance ()
        {
        return (this.weightedDistance);
        } // end getWeightedDistance()

    // -------------------------------------------------------

    /** This function returns whether or not we are outside a distance close
     * to Kilroy or not.
     *
     * @param rangeValue
     *            - value to be checked to see if we are outside of
     *            this value
     * @return boolean - true - if we are farther than the specified value to
     *         Kilroy
     *         false - if we are too close
     * @method isOutsideRange
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean isOutsideRange (int rangeValue)
        {
        if (this.getDistanceFromNearestBumper() > rangeValue)
            return (true);
        return (false);
        } // end isOutsideRange

    // -------------------------------------------------------

    /** This function returns whether or not we are within a distance close
     * to Kilroy or not.
     *
     * @param rangeValue
     *            - value to be checked to see if we are inside of
     *            this value
     * @return boolean - true - if we are closer than the specified value to
     *         Kilroy
     *         false - if we are not
     * @method isWithinRange
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean isWithinRange (int rangeValue)
        {
        if (this.getDistanceFromNearestBumper() < rangeValue)
            return (true);
        return (false);
        } // end isWithinRange()

    // -------------------------------------------------------

    /** This function resets the confidence array so that it is initialized.
     * This would normally be done at the beginning of the run and any time
     * that the sensor is moved.
     *
     * @method resetConfidenceArray
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public void resetConfidenceArray ()
        {
        for (int index = 0; index < this.confidenceValueArray.length; index++)
        {
        this.confidenceValueArray[index] = java.lang.Integer.MAX_VALUE;
        }
        } // end resetConfidenceArray()

    // -------------------------------------------------------

    /** This function sets and returns the amount of time to keep past sonar
     * values for to be used for past calculations
     *
     * @param newValue
     *            - new amount of time to keep past values for
     * @return double - amount of time to keep past values for - usually
     *         about 1 sec. worth
     * @method setAmountOfTimeToKeepPastValues
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double setAmountOfTimeToKeepPastValues (double newValue)
        {
        this.amountOfTimeToKeepPastValues = newValue;
        return (this.getAmountOfTimeToKeepPastValues());
        } // end setAmountOfTimeToKeepPastValues()

    // -------------------------------------------------------

    /** This function sets the confidence value for this sensor and
     * returns the whether or not we are using
     * Confidence processing in our calculations for this sensor.
     *
     * @param on
     *            - denotes whether or not to turn confidence calculations on
     * @return boolean - confidence is either on or not
     * @method setConfidenceCalculationsOn
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean setConfidenceCalculationsOn (boolean on)
        {
        this.useConfidenceCalculations = on;
        return (this.getConfidenceCalculationStatus());
        } // end setConfidenceCalculationsOn()

    // -------------------------------------------------------

    /** This function adds the value being place in and then updates
     * the index into the array. Then it calculates whether or not
     * the index is now out of range and if so, resets it.
     *
     * @param newValue
     *            - value to be placed into the confidenceArray
     * @return int - the value that was placed into the confidence array
     * @method setDistanceValueIntoConfidenceArray
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    private int setDistanceValueIntoConfidenceArray (int newValue)
        {
        this.confidenceValueArray[this.presentConfidenceValueIndex] = newValue;
        this.presentConfidenceValueIndex++;
        this.presentConfidenceValueIndex =
            this.presentConfidenceValueIndex % this.confidenceValueArray.length;
        // System.out.println("Ultrasonic Confidence Index is presently " +
        // this.onfidenceValueIndex);
        return (newValue);
        } // end setDistanceValueIntoConfidenceArray()

    // -------------------------------------------------------

    /** This function sets and returns the number of items that we
     * are going to search backwards in the value array to
     * make sure that everything is good.
     *
     * @param newValue
     *            - new number of items to check when we get
     *            a new sonar value
     * @return int - number of items to check backwards in the
     *         confidence array to make sure that the latest sonar
     *         value is "good"
     * @method setNumberOfItemsToCheckBackwardForValidity
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int setNumberOfItemsToCheckBackwardForValidity (int newValue)
        {
        this.numberOfItemsToCheckForValidity = newValue;
        return (this.getNumberOfItemsToCheckBackwardForValidity());
        } // end setNumberOfItemsToCheckBackwardForValidity()

    // -------------------------------------------------------

    /** sets and gets the distance from the nearest bumper that the
     * Ultrasonic board.
     *
     * @param newDistance
     *            - set offset from bumpers to this distance
     * @return distance value held by this class that represents the
     *         distance from the nearest bumper to the Ultrasonic
     *         board
     * @method setOffsetDistanceFromNearestBummper
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int setOffsetDistanceFromNearestBummper (int newDistance)
        {
        this.distanceFromNearestBumper = newDistance;
        return (this.getOffsetDistanceFromNearestBummper());
        } // end setOffsetDistanceFromNearestBummper()

    // -------------------------------------------------------

    /** This function sets and returns decision on weather or not we
     * want to print distance value to drivers station
     *
     * @param newValue
     *            - new number of items to check when we get
     *            a new true or false value
     * @return boolean - true or false setting for option to print distance
     *         to Drivers station
     * @method setPrintToScreen
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public boolean setPrintToScreen (boolean newValue)
        {
        this.printToScreen = newValue;
        return (this.getPrintToScreen());
        } // end setPrintToScreen()

    // -------------------------------------------------------

    /** This function sets and returns the scaling factor that the Ultrasonic is
     * presently using. The scaling factor is used to multiple the
     * Analog Channel output by. The multiplication factor keeps the
     * range of the Ultrasonic in the correct range. The default
     * scaling factor is 0.5
     *
     * @param scalingFactor
     *            - new scaling factor to be set into the system
     * @return double - the newly revised scaling factor
     * @method setScalingFactor
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double setScalingFactor (double scalingFactor)
        {
        this.scalingFactor = scalingFactor;
        return (this.getScalingFactor());
        } // end setScalingFactor()

    // -------------------------------------------------------
    /** This function sets and returns the sensor Periodicity. It is usually
     * about 50 ms. unless changed by a call setSensorPeriodicity()
     *
     * @param newValue
     *            - the new sensor periodicity
     * @return double - the present sensor periodicity (usually 50 ms.)
     * @method setSensorPeriodicity
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public double setSensorPeriodicity (double newValue)
        {
        this.sensorPeriodicity = newValue;
        return (this.getSensorPeriodicity());
        } // end setSensorPeriodicity()

    // -------------------------------------------------------
    /** This function sets and returns the weighted distance where withing
     * we give more weight to the values input from the sonar
     *
     * @param newValue
     *            - new weighted distance
     * @return int - weighted distance
     * @method setWeightedDistance
     * @author Bob Brown
     * @written Jan 14, 2012
     *          ------------------------------------------------------- */
    public int setWeightedDistance (int newValue)
        {
        this.weightedDistance = newValue;
        return (this.getWeightedDistance());
        } // end setWeightedDistance()

} // end class Ultrasonic
