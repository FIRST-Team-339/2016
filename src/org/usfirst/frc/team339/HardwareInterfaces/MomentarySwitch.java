// ====================================================================
// FILE NAME: MomentarySwitch.java (Team 339 - Kilroy)
//
// CREATED ON: Feb 06, 2011
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used when we want to use a momentary switch
// as denoting ON or Off. It keeps track of whether or not
// the switch was called being held and what state the
// switch should be considered (ON or OFF)
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.Joystick;

// -------------------------------------------------------
/**
 * This class is used when we want to use a momentary switch
 * as denoting ON or Off.
 *
 * @class MomentarySwitch
 * @author Bob Brown
 * @written Feb 06, 2011
 *          -------------------------------------------------------
 */
public class MomentarySwitch
{

    /**
     * ------------------------------------------------------
     *
     * @description this holds the joystick that will be used to
     *              check for a button to see if the state has
     *              changed
     * @author Bob Brown
     * @written Feb 6, 2011
     *          --------------------------------------------------------
     */
    private int buttonNumber = 0;

    /**
     * -------------------------------------------------------
     *
     * @description This denotes the previous state of the switch
     *              as of the last time it was either initialized
     *              or it was set
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    private boolean isOn = false;

    /**
     * ------------------------------------------------------
     *
     * @description this holds the joystick that will be used to
     *              check for a button to see if the state has
     *              changed
     * @author Bob Brown
     * @written Feb 6, 2011
     *          --------------------------------------------------------
     */
    private Joystick joystickToCheck = null;

    /**
     * -------------------------------------------------------
     *
     * @description This denotes whether or not the very last time
     *              this class was called, whether or not the
     *              momentary was on or off
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    private boolean previouslyOn = false;

    // -----------------------------------------------------
    /**
     * constructor
     *
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public MomentarySwitch ()
        {
        this.isOn = false;
        this.previouslyOn = false;
        this.joystickToCheck = null;
        } // end MomentarySwitch

    // -----------------------------------------------------
    /**
     * constructor
     *
     * @param startingState
     *            what state we should start as
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public MomentarySwitch (final boolean startingState)
        {
        this.isOn = startingState;
        this.previouslyOn = false;
        this.joystickToCheck = null;
        } // end MomentarySwitch

    // -----------------------------------------------------
    /**
     * constructor
     *
     * @param joystick
     *            - joystick which will be used to check
     *            to see if the button has been pushed
     * @param buttonNumber
     *            - which button is to be checked on
     *            this joystick
     * @param startingState
     *            what state we should start as
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public MomentarySwitch (final Joystick joystick, final int buttonNumber,
        final boolean startingState)
        {
        this.isOn = startingState;
        this.previouslyOn = false;
        this.joystickToCheck = joystick;
        this.buttonNumber = buttonNumber;
        } // end MomentarySwitch

    // ---------------------------------------------------------
    /**
     * returns the state of the momentary switch
     *
     * @method get
     * @return boolean - on or off
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public boolean get ()
        {
        return this.isOn;
        } // end get()

    // ---------------------------------------------------------
    /**
     * returns the button number that will be checked
     *
     * @method getButtonNumber
     * @return int - button number that will be checked to see if
     *         the momentary switch has been pushed
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public int getButtonNumber ()
        {
        return this.buttonNumber;
        } // end getButtonNumber()

    // ---------------------------------------------------------
    /**
     * returns the joystick that holds the button that will be
     * checked
     *
     * @method getJoystick
     * @return Joystick - joystick used to hold the button that
     *         will be used to check to see if the momentary
     *         switch has been pushed
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public Joystick getJoystick ()
        {
        return this.joystickToCheck;
        } // end getJoystick()

    // ---------------------------------------------------------
    /**
     * returns the state of the momentary switch
     *
     * @method isOn
     * @return boolean - on or off
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public boolean isOn ()
        {
        return this.isOn;
        } // end isOn()

    // ---------------------------------------------------------
    /**
     * returns the state of the momentary switch after first
     * checking the state of the momentary switch that was
     * previously specified
     *
     * @method isOnCheckNow
     * @return boolean - on or off
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public boolean isOnCheckNow ()
        {
        this.update();
        return this.isOn;
        } // end isOnCheckNow()

    // ---------------------------------------------------------
    /**
     * sets and returns the button number that will be checked
     *
     * @method setButtonNumber
     * @param buttonNumber
     *            - button number that we want to check
     * @return int - button number that will be checked to see if
     *         the momentary switch has been pushed
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public int setButtonNumber (final int buttonNumber)
        {
        return (this.buttonNumber = buttonNumber);
        } // end setButtonNumber()

    // ---------------------------------------------------------
    /**
     * sets and returns the joystick that holds the button that will be
     * checked
     *
     * @method getJoystick
     * @return Joystick - joystick used to hold the button that
     *         will be used to check to see if the momentary
     *         switch has been pushed
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public Joystick setJoystick ()
        {
        return this.joystickToCheck;
        } // end getJoystick()

    // ---------------------------------------------------------
    /**
     * update the present state of the momentary switch. uses
     * the preset button on the preset joystick
     *
     * @method update
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public void update ()
        {
        // -------------------------------------
        // if the user has previously specified a
        // joystick to use then do so here
        // -------------------------------------
        if (this.getJoystick() != null)
        {
        this.update(this.getJoystick().getRawButton(this.getButtonNumber()));
        }
        } // end update

    // ---------------------------------------------------------
    /**
     * update the present state of the momentary switch
     *
     * @method update
     * @param presentState
     *            - tells the class what the present state
     *            of the state changer (button) is.
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public void update (final boolean presentState)
        {
        // -------------------------------------
        // if the user has pushed the momentary
        // switch and ...
        // -------------------------------------
        if (presentState == true)
        {
        // -------------------------------------
        // if this is the first time that the user
        // has pushed this switch in a row, then
        // toggle the state of isOn. Otherwise
        // do nothing
        // -------------------------------------
        if (this.previouslyOn == false)
        {
        this.previouslyOn = true;
        this.isOn = !this.isOn;
        } // if
        } // if
        else 
        {
        // -------------------------------------
        // otherwise turn off previouslyOn
        // -------------------------------------
        this.previouslyOn = false;
        }
        } // end update
} // end class
