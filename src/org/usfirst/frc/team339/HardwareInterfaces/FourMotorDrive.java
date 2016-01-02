// ====================================================================
// FILE NAME: FourMotorDrive.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 15, 2011
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class is used to drive one sprocket with two motors. Choose
// one speed controller to be the active speed controller and use it
// normally. Then call synchronize() in the periodic loop to poll
// the active motor, determine its voltage, and send the same voltage
// to the passive motor.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.SpeedController;

// -------------------------------------------------------
/**
 * This class is used to drive one sprocket with two motors and two
 * sprockets via two speed controllers. Choose one speed controller
 * to be the active speed controller and use it normally.
 * Then call synchronize() in the periodic loop to poll
 * the active motor, determine its voltage, and send the same voltage
 * to the passive motor. If the passive motor is CAN, it will be
 * forced into voltage mode. The active motor can be any control
 * mode.
 *
 * @class FourMotorDrive
 * @author Josh Shields
 * @written Jan 15, 2011
 *          -------------------------------------------------------
 */
public class FourMotorDrive
{
    /**
     * -------------------------------------------------------
     *
     * @description The speed controller for the user to control
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    private final SpeedController activeController;

    /**
     * -------------------------------------------------------
     *
     * @description The speed controller to be automatically
     *              synchronized with the active controller
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    private final SpeedController passiveController;

    // -------------------------------------------------------
    /**
     * constructor
     *
     * @method FourMotorDrive
     * @param activeController
     *            The speed controller for the user to
     *            control
     * @param passiveController
     *            This speed controller will be set to
     *            the same voltage as the active controller when
     *            synchronize() is called
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    public FourMotorDrive (final SpeedController activeController,
        final SpeedController passiveController)
        {
        this.activeController = activeController;
        this.passiveController = passiveController;
        } // end FourMotorDrive

    // -------------------------------------------------------
    /**
     * create this class by declaring the two controllers, which
     * are two CANJaguars
     *
     * @method makeTwoMotorDrive
     * @param activeController
     *            - controller with the encoder attached and
     *            deemed to be the MASTER controller
     * @param passiveController
     *            - controller that must sync with the
     *            activeController to get its speed inputs
     * @return A CANTwoMotorDrive made from two CAN speed controllers
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    public FourMotorDrive makeTwoMotorDrive (final CANJaguar activeController,
        final CANJaguar passiveController)
        {
        return new CANTwoMotorDrive(activeController, passiveController);
        } // end makeTwoMotorDrive

    // -------------------------------------------------------
    /**
     * create this class by declaring the two controllers, which
     * are two generic speedControllers (either Jaguars or Victors)
     *
     * @method makeTwoMotorDrive
     * @param activeController
     *            - controller deemed to be the MASTER
     * @param passiveController
     *            - controller deemed to be the slave to the MASTER
     * @return A FourMotorDrive made from two non-CAN speed controllers
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    public FourMotorDrive makeTwoMotorDrive (
        final SpeedController activeController,
        final SpeedController passiveController)
        {
        return new FourMotorDrive(activeController, passiveController);
        } // end makeTwoMotorDrive

    // -------------------------------------------------------
    /**
     * Sets the passive controller to match the active controller
     *
     * @method synchronize
     * @author Josh Shields
     * @written Jan 15, 2011
     *          -------------------------------------------------------
     */
    public void synchronize ()
        {
        this.passiveController.set(this.activeController.get());
        } // end synchronize
} // end class
