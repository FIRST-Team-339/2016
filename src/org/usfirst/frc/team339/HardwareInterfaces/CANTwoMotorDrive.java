// ====================================================================
// FILE NAME: CANTwoMotorDrive.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 15, 2011
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class is used to drive one sprocket with two motors using
// CAN Jaguars. The active controller's control mode will be
// unaffected. The passive controller will be forced to voltage mode.
// See FourMotorDrive for more info.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.CANJaguar;

// -------------------------------------------------------
/**
 * This class is used to drive one sprocket with two motors using
 * CAN Jaguars. The active controller's control mode will be
 * unaffected. The passive controller will be forced to voltage mode.
 * See FourMotorDrive for more info.
 * 
 * @class CANTwoMotorDrive
 * @author Josh Shields
 * @written Jan 15, 2011
 *          -------------------------------------------------------
 */
public class CANTwoMotorDrive extends FourMotorDrive
{
    /**
     * -------------------------------------------------------
     * 
     * @description The speed controller for the user to control
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    private final CANJaguar activeController;

    /**
     * -------------------------------------------------------
     * 
     * @description The speed controller to be automatically
     *              synchronized with the active controller
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    private final CANJaguar passiveController;

    // -------------------------------------------------------
    /**
     * constructor
     * 
     * @method CANTwoMotorDrive
     * @param activeController
     *            - The speed controller for the user to
     *            control
     * @param passiveController
     *            - This speed controller will be set to
     *            the same voltage as the active controller when
     *            synchronize() is called
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    public CANTwoMotorDrive (final CANJaguar activeController,  // The speed
                                                               // controller to
                                                               // control with
                                                               // velocity
                                                               // commands
        final CANJaguar passiveController) // The maximum speed that a
                                           // command of 1.0 should
                                           // represent
        {
        super(activeController, passiveController);

        this.activeController = activeController;
        this.passiveController = passiveController;

        /*
         * try
         * {
         */
        // Force passive controller into voltage control mode
        passiveController.setVoltageMode();
        /*
         * }
         * catch (CANTimeoutException e)
         */
        {/* keep going */
        }
        } // end CANTwoMotorDrive

    // -------------------------------------------------------
    /**
     * Sets the passive controller to match the active controller
     * 
     * @method synchronize
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    @Override
    public void synchronize ()
        {
        /*
         * try
         * {
         */
        this.passiveController.set(this.activeController.getOutputVoltage());
        /*
         * }
         * catch (CANTimeoutException e)
         */
        {/* keep going */
        }
        } // end synchronize
} // end class
