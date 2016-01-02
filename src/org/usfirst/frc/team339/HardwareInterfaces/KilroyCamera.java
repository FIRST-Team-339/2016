// ====================================================================
// FILE NAME: KilroyCamera.java (Team 339 - Kilroy)
//
// CREATED ON: Oct 16, 2014
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class is used to hold an AxisCamera instance or null if we
// say that we do not have a camera. If we say that we do not have
// a camera, all of the methods dealing with the camera values return
// bogus values/don't acutally set anything when asked to write a
// value. All of the methods are identical to the AxisCamera methods,
// except for the constructor, those dealing with having a camera,
// and getCameraInstance().
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import java.io.IOException;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.vision.AxisCamera;

/** A camera container/interface that holds an instance of the AxisCamera
 * class and a boolean used to determine if we have a camera on the robot.
 * This class has methods that are the same as the AxisCamera classes (it
 * does not include all of methods, only the ones that we use at this
 * point). If we do not have a camera, the class does nothing when the
 * camera methods are called, returning inaccurate values if a get was called
 *
 * @author Nathanial Lydick
 * @written Oct 16, 2014 */
public class KilroyCamera
{
// holds the camera instance, or null if we have no camera
private final AxisCamera camera;

// holds our boolean used to check whether we have a camera
private boolean haveCamera;

// the return value for integers when we have no camera
public static final int noCameraIntReturnValue = -1;

private static String KILROY_CAMERA_IP = "10.3.39.11";

// --------------------------------------------------------------
// All methods below this point simply call the methods for the
// Axis camera, if we have one, else it does nothing or it returns
// a bogus value (sometimes an impossible value, such as the ints)
// --------------------------------------------------------------

// Constructor, passes in whether we have a camera, and uses the
// default AxisCamera.getInstance() to get the camera instance
// if we say that we have one
// -------------------------------------------------------
/** Sets the Camera boolean, in case a change is necessary
 *
 * @method KilroyCamera() - constructor
 * @param hasCamera
 *            - whether we have a camera
 * @author Nathan Lydick
 * @written Oct 16, 2014
 *          ------------------------------------------------------- */
public KilroyCamera (boolean hasCamera)
    {
    this.haveCamera = hasCamera;
    if (hasCamera)
        {
            this.camera = new AxisCamera(KILROY_CAMERA_IP);
        }
    else
        {
            this.camera = null;
        }
    }

public boolean freshImage ()
    {
        if (this.haveCamera)
            return this.camera.isFreshImage();
        else
            // returns true so that if we ever try to wait for an image, we will
            // just continue
            return true;
    }

public int getBrightness ()
    {
        if (this.haveCamera)
            return this.camera.getBrightness();
        else
            // returns a value that it shouldn't be
            return noCameraIntReturnValue;
    }

// -------------------------------------------------------
/** Returns the AxisCamera (final), in case direct access is necessary
 * For the most part, this should not be used, and if a new camera
 * method is needed, it should just be added to this class
 *
 * @method getCameraInstance()
 * @return AxisCamera - the camera instance (or null)
 * @author Nathan Lydick
 * @written Oct 16, 2014
 *          ------------------------------------------------------- */
public final AxisCamera getCameraInstance ()
    {
        return this.camera;
    }

public int getColorLevel ()
    {
        if (this.haveCamera)
            return this.camera.getColorLevel();
        else
            // returns a value that it shouldn't be
            return noCameraIntReturnValue;
    }

public int getCompression ()
    {
        if (this.haveCamera)
            return this.camera.getCompression();
        else
            // returns a value that it shouldn't be
            return noCameraIntReturnValue;
    }

public AxisCamera.ExposureControl getExposureControl ()
    {
        if (this.haveCamera)
            return this.camera.getExposureControl();
        else
            // returns automatic
            return AxisCamera.ExposureControl.kAutomatic;
    }

// -------------------------------------------------------
/** Returns whether we have a camera
 *
 * @method gethaveCamera()
 * @return boolean - returns whether we have a camera
 * @author Nathan Lydick
 * @written Oct 16, 2014
 *          ------------------------------------------------------- */
public boolean gethaveCamera ()
    {
        return this.haveCamera;
    }

// -------------------------------------------------------
/** Returns the AxisCamera (final), in case direct access is necessary
 * For the most part, this should not be used, and if a new camera
 * method is needed, it should just be added to this class
 *
 * @method getCameraInstance()
 * @return ColorImage - Camera.getImage() or a 0x0 hsl image
 * @author Nathan Lydick
 * @written Oct 16, 2014
 *          ------------------------------------------------------- */
public ColorImage getImage () throws NIVisionException
    {
        if (this.haveCamera)
            return this.camera.getImage();
        else
            // returns a 0x0 image
            return new HSLImage();
    }

public int getMaxFPS ()
    {
        if (this.haveCamera)
            return this.camera.getMaxFPS();
        else
            // returns a value that it shouldn't be
            return noCameraIntReturnValue;
    }

// --------------------------------------------------------------
// The above methods simply call the methods for the
// Axis camera, if we have one, else it does nothing or it returns
// a bogus value (sometimes an impossible value, such as the ints
// --------------------------------------------------------------
public AxisCamera.Resolution getResolution ()
    {
        if (this.haveCamera)
            return this.camera.getResolution();
        else
            // returns the smallest resolution and our default
            return AxisCamera.Resolution.k160x120;
    }

public AxisCamera.Rotation getRotation ()
    {
        if (this.haveCamera)
            return this.camera.getRotation();
        else
            // returns 0 rotation
            return AxisCamera.Rotation.k0;
    }

public AxisCamera.WhiteBalance getWhiteBalance ()
    {
        if (this.haveCamera)
            return this.camera.getWhiteBalance();
        else
            // returns automatic
            return AxisCamera.WhiteBalance.kAutomatic;
    }

/** Takes an image from the camera and stores it in the specified file path.
 * This will override old images of the same name.
 *
 * @param fileName
 *            no extensions necessary */

    // TODO Merge commands and fix comments, etc (Everything)
public void saveImage (String fileName)
    {

        final String in =
        // "/bin/rm -rf /home/lvuser/images/" + fileName
        // + ".jpg;"
        // + // removes old files of the same name
            " /usr/bin/wget http://10.3.39.11/jpg/image.jpg -O /home/lvuser/images/" +
                fileName + ".jpg > /home/lvuser/images/log.txt";

        System.out.println("\n\n\nthe command is : " + in);

    try
            {
            Runtime.getRuntime().exec(
                "/bin/rm -rf /home/lvuser/images/" + fileName + ".jpg");
            }
        catch (final IOException e)
        {
            e.printStackTrace();
            System.out.println("saveImage threw something");
        }

    try
            {
            Runtime.getRuntime().exec(in);
            // saves new file
            }
        catch (final IOException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
    }

// -------------------------------------------------------
/** Sets the Camera boolean, in case a change is necessary
 *
 * @method setHaveCamera() - constructor
 * @param value
 *            - the new value as to whether we have a camera
 * @author Nathan Lydick
 * @written Oct 16, 2014
 *          ------------------------------------------------------- */
public void setHaveCamera (boolean value)
    {
        this.haveCamera = value;
    }

public void writeBrightness (int brightness)
    {
        if (this.haveCamera)
            {
            this.camera.writeBrightness(brightness);
        }
        else
            {
            // returns nothing

            }
    }

public void writeColorLevel (int value)
    {
        if (this.haveCamera)
            {
            this.camera.writeColorLevel(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeCompression (int value)
    {
        if (this.haveCamera)
            {
            this.camera.writeCompression(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeExposureControl (AxisCamera.ExposureControl value)
    {
        if (this.haveCamera)
            {
            this.camera.writeExposureControl(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeMaxFPS (int value)
    {
        if (this.haveCamera)
            {
            this.camera.writeMaxFPS(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeResolution (AxisCamera.Resolution value)
    {
        if (this.haveCamera)
            {
            this.camera.writeResolution(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeRotation (AxisCamera.Rotation value)
    {
        if (this.haveCamera)
            {
            this.camera.writeRotation(value);
        }
        else
            {
            // returns nothing

            }
    }

public void writeWhiteBalance (AxisCamera.WhiteBalance whiteBalance)
    {
        if (this.haveCamera)
            {
            this.camera.writeWhiteBalance(whiteBalance);
        }
        else
            {
            // returns nothing

            }
    }
}
