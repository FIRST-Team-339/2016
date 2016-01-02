// ====================================================================
// FILE NAME: DoubleThrowSwitch.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 19, 2009
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used when a double throw switch is created. A double
// throw switch has three positions. One to each side and one in the
// middle. We use these switches as one of three positions. So
// if the switch is in the middle position, (in reality the middle
// is off), we consider that as on for the third choice. We take
// in two SinglePoleSwitches as the means to check the other
// switches.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

// -------------------------------------------------------
/**
 * This class is used when a double throw switch is created. A double
 * throw switch has three positions. One to each side and in the
 * middle. We use these switches as one of three positions. So
 * if the switch is in the middle position, (in reality the middle
 * is off), we consider that as on for the third choice. We take
 * in two SinglePoleSwitches as the means to check the other
 * switches.
 *
 * @class DoubleThrowSwitch
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public class DoubleThrowSwitch
{
    /**
     * -------------------------------------------------------
     *
     * @description This is one of the switches that are part of the double
     *              pole
     * @author Bob Brown
     * @written Sep 19, 2009
     *          -------------------------------------------------------
     */
    private final SingleThrowSwitch switch1;

    /**
     * -------------------------------------------------------
     *
     * @description This is one of the switches that are part of the double
     *              pole
     * @author Bob Brown
     * @written Sep 19, 2009
     *          -------------------------------------------------------
     */
    private final SingleThrowSwitch switch2;

    // -------------------------------------------------------
    /**
     * constructor which takes in the two switches that will
     * be checked to see if the this switch, (the OFF position) of
     * a double pole switch is considered ON. It is considered
     * ON when the other two switches are off
     *
     * @method DoubleThrowSwitch
     * @param switch1In
     *            - SingleThrowSwitch - first single throw switch of the
     *            double pole switch
     * @param switch2In
     *            - SingleThrowSwitch - second single throw switch of the
     *            double pole switch
     * @author Bob Brown
     * @written Sep 19, 2009
     *          -------------------------------------------------------
     */
    public DoubleThrowSwitch (final SingleThrowSwitch switch1In,
        final SingleThrowSwitch switch2In)
        {
        this.switch1 = switch1In;
        this.switch2 = switch2In;
        } // end DoubleThrowSwitch

    // -------------------------------------------------------
    /**
     * This function returns whether or not the switch is on
     * or not.
     *
     * @method isOn
     * @return true if the switch is "ON", false otherwise
     * @author Bob Brown
     * @written Sep 19, 2009
     *          -------------------------------------------------------
     */
    public boolean isOn ()
        {
        if ((this.switch1 == null) || (this.switch1.isOn() == true)
            || (this.switch2 == null) || (this.switch2.isOn() == true))
            return (false);
        else
            return (true);
        } // end isOn
} // end class
