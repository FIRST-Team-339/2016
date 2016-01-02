// ====================================================================
// FILE NAME: ReturnableDouble.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 22, 2009
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file contains a returnable double so that a double value
// can be given to a method and updated by that method and later
// the (possibly) changed value of the double can be retrieved
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.Utils;

// -------------------------------------------------------
/**
 * This class contains a double that can be retrieved
 *
 * @class ReturnableDouble
 * @author Bob Brown
 * @written Sep 22, 2009
 *          -------------------------------------------------------
 */
public class ReturnableDouble
{
    /**
     * -------------------------------------------------------
     *
     * @description
     * @author Bob Brown
     * @written Sep 21, 2009
     *          -------------------------------------------------------
     */
    private double doubleValue;

    // -------------------------------------------------------
    /**
     * constructor
     *
     * @method ReturnableDouble
     * @param doubleValue
     *            - initial value for this Double class
     * @author Bob Brown
     * @written Sep 21, 2009
     *          -------------------------------------------------------
     */
    public ReturnableDouble (final double doubleValue)
        {
        this.doubleValue = doubleValue;
        } // end ReturnableDouble

    // -------------------------------------------------------
    /**
     * constructor
     *
     * @method ReturnableDouble
     * @param doubleValue
     *            - initial value for this Double class
     * @author Bob Brown
     * @written Sep 21, 2009
     *          -------------------------------------------------------
     */
    public ReturnableDouble (final ReturnableDouble doubleValue)
        {
        this.doubleValue = doubleValue.get();
        } // end ReturnableDouble

    // -------------------------------------------------------
    /**
     * returns the present value of this class
     *
     * @method get
     * @return double value for of this class
     * @author Bob Brown
     * @written Sep 21, 2009
     *          -------------------------------------------------------
     */
    public double get ()
        {
        return (this.doubleValue);
        } // end get

    // -------------------------------------------------------
    /**
     * updates the value of this double class
     *
     * @method set
     * @param doubleValue
     *            - updated value for this class
     * @author Bob Brown
     * @written Sep 21, 2009
     *          -------------------------------------------------------
     */
    public void set (final double doubleValue)
        {
        this.doubleValue = doubleValue;
        } // end set

} // end class
