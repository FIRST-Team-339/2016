// ====================================================================
// FILE NAME: DoubleSolenoid.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 18, 2009
// CREATED BY: Bob Brown
// MODIFIED ON: Jan 13, 2011
// MODIFIED BY: Bob Brown
// ReWritten to make use of the new DoubleSolenoid class from
// the WPI library
// ABSTRACT:
// This file represents a double solenoid. A double solenoid
// has (in essence) two single solenoids which are supplied by
// the library. The double solenoid keeps one side of the
// solenoid the opposite of the other side.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

/**
 * This class is a double solenoid, which in reality is actually
 * two single solenoids. The two solenoids are always opposites
 * of each other
 *
 * @class DoubleSolenoid
 * @author Bob Brown
 * @written Sep 18, 2009
 *          -------------------------------------------------------
 */
public class DoubleSolenoid extends edu.wpi.first.wpilibj.DoubleSolenoid
{
    /**
     * -------------------------------------------------------
     *
     * @description holds the channel number for the forward side
     *              of the double solenoid
     * @author Bob Brown
     * @written 07 Nov, 2009
     *          -------------------------------------------------------
     */
    int forwardChannel;

    /**
     * -------------------------------------------------------
     *
     * @description holds the channel number for the reverse side
     *              of the double solenoid
     * @author Bob Brown
     * @written 07 Nov, 2009
     *          -------------------------------------------------------
     */
    int reverseChannel;

    // -------------------------------------------------------
    /**
     * constructor
     *
     * @method DoubleSolenoid
     * @param channelNumForward
     *            - forward channel number
     * @param channelNumReverse
     *            - reverse channel number
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public DoubleSolenoid (final int channelNumForward, // channel number for
        // the fwrd solenoid
        final int channelNumReverse) // channel number for the rev solenoid
        {
        super(channelNumForward, channelNumReverse);
        this.forwardChannel = channelNumForward;
        this.reverseChannel = channelNumReverse;
        this.init();
        } // end DoubleSolenoid

    // -------------------------------------------------------
    /**
     * constructor
     *
     * @method DoubleSolenoid
     * @param slotNumber
     *            - forward slot number
     * @param channelNumForward
     *            - forward channel number
     * @param channelNumReverse
     *            - reverse channel number
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public DoubleSolenoid (final int slotNumber,       // slot number for the fwrd
        // solenoid
        final int channelNumForward, // channel number for the fwrd solenoid
        final int channelNumReverse) // channel number for the rev solenoid
        {
        super(slotNumber, channelNumForward, channelNumReverse);
        this.forwardChannel = channelNumForward;
        this.reverseChannel = channelNumReverse;
        this.init();
        } // end DoubleSolenoid

    // -------------------------------------------------------
    /**
     * return the state of the forward side of the solenoid
     *
     * @method getForward
     * @return the state of the forward solenoid
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public boolean getForward ()
        {
        if (this.get() == Value.kForward)
            return (true);
        return (false);
        } // end getForward

    // -------------------------------------------------------
    /**
     * returns the forward channel number
     *
     * @method getForwardChannel
     * @return int
     * @author Bob Brown
     * @written 7 Nov 2009
     *          -------------------------------------------------------
     */
    public int getForwardChannel ()
        {
        return (this.forwardChannel);
        } // end getForwardChannel

    // -------------------------------------------------------
    /**
     * return the state of the reverse side of the solenoid
     *
     * @method getReverse
     * @return the state of the reverse side of the solenoid
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public boolean getReverse ()
        {
        if (this.get() == Value.kReverse)
            return (true);
        return (false);
        } // end getReverse

    // -------------------------------------------------------
    /**
     * returns the reverse channel number
     *
     * @method getReverseChannel
     * @return int
     * @author Bob Brown
     * @written 7 Nov 2009
     *          -------------------------------------------------------
     */
    public int getReverseChannel ()
        {
        return (this.reverseChannel);
        } // end getReverseChannel

    // -------------------------------------------------------
    /**
     * initializes things in the class
     *
     * @method init
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    private void init ()
        {
        this.setForward(true);
        } // end init

    // -------------------------------------------------------
    /**
     * this method runs the double solenoid forward, by setting
     * the forward solenoid on and the reverse solenoid the
     * opposite
     *
     * @method: setForward
     * @return boolean - value of what forward now is
     * @param on
     *            - boolean - denotes whether to set the foward
     *            Solenoid on or off.
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public boolean setForward (final boolean on)    // forward on or off
        {
        if (on == true)
        {
        this.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
        this.setReverse(!on);
        }
        return (on);
        } // end setForward

    // -------------------------------------------------------
    /**
     * this method runs the double solenoid reverse, by setting
     * the forward() solenoid to the inverse of what the caller
     * passes in and forward() sets both the forward and reverse
     * solenoids correctly
     *
     * @method setReverse
     * @return boolean - value of what forward now is
     * @param on
     *            - boolean - denotes whether to set the reverse
     *            Solenoid on or off.
     * @author Bob Brown
     * @written Sep 18, 2009
     *          -------------------------------------------------------
     */
    public boolean setReverse (final boolean on)
        {
        if (on == true)
        {
        this.set(DoubleSolenoid.Value.kReverse);
        }
        else
        {
        this.setForward(!on);
        }
        return (on);
        } // end setReverse

    // -------------------------------------------------------
    /**
     * This method sets both the forward solenoid and sets
     * the reverse solenoid off.
     *
     * @method setToNeutral
     * @author Bob Brown
     * @written Sep 18 2009
     *          -------------------------------------------------------
     */
    public void setToNeutral ()
        {
        this.set(DoubleSolenoid.Value.kOff);
        } // end setToNeutral
} // end class
