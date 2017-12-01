package org.usfirst.frc.team339.Vision.opencv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.VideoCamera;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * MACHINE REQUIREMENTS:
 * GRIP must be installed
 * https://github.com/WPIRoboticsProjects/GRIP/releases/tag/v1.5.2
 */

/**
 * This class contains vision code that uses OpenCV and the auto-generated
 * code from the program GRIP. To properly set up GRIP to work with this
 * class, make sure to set the package name, the class name to
 * AutoGenVision.java
 * and the directory to the correct package.
 * 
 * NOTE: The GRIP project MUST end with a "filter contours" modifier
 * 
 * @author Ryan McGee
 * @written 6/22/17
 *
 */
public class VisionProcessor extends AutoGenVision
{

/**
 * A class that holds several statistics about particles.
 * 
 * The measures include:
 * area: The area, in pixels of the blob
 * boundingRect: The rectangle around the blob
 * center: the point of the center of the blob
 * 
 * @author Ryan McGee
 *
 */
public class ParticleReport implements Comparator<ParticleReport>,
        Comparable<ParticleReport>
{
/**
 * The area of the bounding rectangle around the blob
 */
public double area = 0;

/**
 * The rectangle around the blob
 */
public Rect boundingRect = new Rect(new Point(0, 0), new Point(0, 0));

/**
 * the center of the bounding rectangle around the blob
 */
public Point center = new Point(0, 0);

@Override
public int compare (ParticleReport r1, ParticleReport r2)
{
    return (int) (r1.area - r2.area);
}

@Override
public int compareTo (ParticleReport r)
{
    return (int) (r.area - this.area);
}
}

/**
 * The user must set which camera is connected for correct field of views and
 * focal lengths.
 * 
 * @author Ryan McGee
 *
 */
public enum CameraModel
    {
/**
 * The USB camera supplied by FIRST, model Lifecam HD-3000
 */
LIFECAM,
/**
 * The OLD model of the IP camera supplied by FIRST
 */
AXIS_M1011,
/**
 * The NEW model of the IP camera supplied by FIRST
 */
AXIS_M1013
    }

/**
 * A list of the different kind of images, for accessing the images directly.
 * 
 * @author Ryan McGee
 */
public enum ImageType
    {
/**
 * An image straight from the camera.
 */
RAW,
/**
 * An image that has gone through post processing.
 */
PROCESSED
    }

// In order to calculate the horizontal / vertical field of view,
// you can use the formula: a = 2arctan(d/2f) where 'a' is the angle,
// 'd' is the size of the sensor (in millimeters and in the direction
// needed), and f is the focal length (again in millimeters).
// source:
// https://photo.stackexchange.com/questions/21536/how-can-i-calculate-vertical-field-of-view-from-horizontal-field-of-view.

// Remember to use all info available. If the datasheet says 47 degrees
// horizontal and the calculated answer is
// different, remember that video cuts off a certain amount of data and
// recalculate presuming only that portion
// of the sensor is used.

// ========M1011 SPECS========
private final int M1011_HORIZ_FOV = 47;

private final int M1011_VERT_FOV = 36;

// ========M1013 SPECS========
private final int M1013_HORIZ_FOV = 67;

private final int M1013_VERT_FOV = 51;

// ========LIFECAM SPECS========
/*
 * There is not enough information on the technical data sheet to find this
 * info. They must instead be calculated manually.
 */

private final String SAVE_IMAGE_PATH = "/home/lvuser/images/";

private Mat image = new Mat(); // The stored "image" (in a matrix format)

private ParticleReport[] particleReports = new ParticleReport[0];

private final int horizontalFieldOfView;

private final int verticalFieldOfView;

private final CameraModel cameraModel;

private final VideoCamera camera;

/**
 * Creates the object and starts the camera servera
 * 
 * @param ip
 *            the IP of the .mjpg the axis camera outputs
 * @param camera
 *            the brand / model of the camera
 */
public VisionProcessor (String ip, CameraModel camera)
{
    // Adds the camera to the cscore CameraServer, in order to grab the
    // stream.
    this.camera = CameraServer.getInstance()
            .addAxisCamera("Vision Camera", ip);

    // Based on the selected camera type, set the field of views and focal
    // length.
    this.cameraModel = camera;
    switch (this.cameraModel)
        {
        case AXIS_M1011:
            this.horizontalFieldOfView = M1011_HORIZ_FOV;
            this.verticalFieldOfView = M1011_VERT_FOV;
            break;
        case AXIS_M1013:
            this.horizontalFieldOfView = M1013_HORIZ_FOV;
            this.verticalFieldOfView = M1013_VERT_FOV;
            break;

        default: // Data will default to one to avoid any "divide by zero"
                 // errors.
            this.horizontalFieldOfView = 1;
            this.verticalFieldOfView = 1;
        }

}

/**
 * Creates the object and starts the camera server
 * 
 * @param usbPort
 *            The USB camera port number. '0' for default.
 * @param camera
 *            the brand / model of the camera
 */
public VisionProcessor (int usbPort, CameraModel camera)
{
    // Adds the camera to the cscore CameraServer, in order to grab the
    // stream.
    this.camera = CameraServer.getInstance()
            .startAutomaticCapture("Vision Camera", usbPort);

    // Based on the selected camera type, set the field of views and focal
    // length.
    this.cameraModel = camera;
    switch (this.cameraModel)
        {
        // case LIFECAM: //Not enough information to properly find this data.
        // see above.
        // this.horizontalFieldOfView =
        // this.verticalFieldOfView =
        // this.focalLength =
        // break;
        default: // Data will default to one to avoid any "divide by zero"
                 // errors.
            this.horizontalFieldOfView = 1;
            this.verticalFieldOfView = 1;
        }

}

// ==========================END INIT===================================

/**
 * The method that processes the image and inputs it into the particle reports
 */
public void processImage ()
{
    // Gets the error code while getting the new image from the camera.
    // If the error code is not 0, then there is no error.
    long errorCode = CameraServer.getInstance()
            .getVideo("Vision Camera").grabFrame(image);

    if (image.empty())
        {
        System.out.println("Image is Empty! Unable to process image!");
        return;
        }

    if (errorCode == 0)
        {
        System.out.println(
                "There was an error grabbing the image. See below:");
        System.out.println(
                CameraServer.getInstance().getVideo().getError());
        }

    // The process image function found in the AutoGenVision class.
    super.process(image);
    // If this throws an error, make sure the GRIP project ends with a
    // filterContours function.
    this.createParticleReports(super.filterContoursOutput());
    // Sort the particles from largest to smallest
    Arrays.sort(particleReports);
    // for (int i = 0; i < particleReports.length; i++)
    // {
    // System.out.println(i + " " + particleReports[i].area);
    // }
}

/**
 * Sets the camera image settings for use in image processing.
 * 
 * @param exposure
 *            How much light will hit the sensor, in percentage.
 * @param whiteBalence
 *            The white balence of the camera. Constants are found in the
 *            VideoCamera class
 * @param brightness
 *            How bright the image is in post processing, in percentage.
 */
public void setCameraSettings (int exposure, int whiteBalence,
        int brightness)
{
    this.camera.setBrightness(brightness);
    this.camera.setExposureManual(exposure);
    this.camera.setWhiteBalanceManual(whiteBalence);
}

/**
 * Sets the camera back to default settings for switching between vision
 * processing and driver assisting mode.
 */
public void setDefaultCameraSettings ()
{
    this.camera.setExposureAuto();
    this.camera.setBrightness(50);
    this.camera.setWhiteBalanceAuto();
}

/**
 * Saves an image to the roborio. This has a max of 26 images per type, before
 * it starts to overwrite.
 * The image will be saved to the SAVE_IMAGE_PATH defined above.
 * 
 * @param type
 *            What kind of image will be saved. If it is ImageType.RAW, then an
 *            image will be saved directly
 *            from the camera. if ImageType.PROCESSED is chosen, then the robot
 *            will save the image after it
 *            has gone through the filters.
 */
public void saveImage (ImageType type)
{
    String fileName = "";

    // Create the path the images will be saved in. If the path already
    // exists, do nothing.
    try
        {
        Runtime.getRuntime().exec("mkdir -p /home/lvuser/images");
        }
    catch (IOException e)
        {
        e.printStackTrace();
        }
    // grab the image
    Mat tempImage = new Mat();
    CameraServer.getInstance().getVideo("Vision Camera")
            .grabFrame(tempImage);

    // Chosses which type of image will be saved: raw or processed.
    switch (type)
        {
        case RAW:
            // Creating the file name. Only 26 images will be saved before
            // overwrite.
            if (rawImageNum > 25)
                rawImageNum = 0;
            fileName = "raw_image_" + rawImageNum++ + ".png";
            break;
        case PROCESSED:
            // Creating the file name. Only 26 images will be saved before
            // overwrite.
            if (processedImageNum > 25)
                processedImageNum = 0;
            fileName = "proc_image_" + processedImageNum++ + ".png";
            // Only process the image if it is chosen as the image type.
            super.process(tempImage);
            tempImage = super.hslThresholdOutput();
            break;
        default:
            // Should not run, but will if another imageType is added and
            // chosen.
            System.out.println(
                    "Failed to save image: Image type not recognized.");
            break;
        }
    // Save the image to the folder specified with the name specified
    Imgcodecs.imwrite(SAVE_IMAGE_PATH + fileName, tempImage);
}

private int rawImageNum = 0;

private int processedImageNum = 0;

/**
 * Saves an image once, no matter how long the button is pressed down on the
 * joystick.
 * 
 * @param button
 *            Whether or not the button is pressed
 * @param type
 *            What kind of image should be saved to the RoboRIO's storage.
 */
public void saveImageSafely (boolean button, ImageType type)
{
    if (button == true && saveImageButtonState == false)
        {
        this.saveImage(type);
        }
    saveImageButtonState = button;
}

private boolean saveImageButtonState = false;

// =====================USER ACCESSABLE METHODS========================
/*
 * Any methods that will allow the user to directly access raw data outside
 * the class will be stored below.
 */

/**
 * @return the list of blobs generated after processing the image, in
 *         descending order of size.
 */
public ParticleReport[] getParticleReports ()
{
    return particleReports;
}

/**
 * @return Whether or not the camera can see any retro-reflective tape
 */
public boolean hasBlobs ()
{
    if (this.particleReports.length > 0)
        {
        return true;
        }

    return false;
}

/**
 * Gets a report of the index the user requests.
 * 
 * @param n
 *            The index of the size requested. 0 is the largest, and
 *            gradually gets smaller until the end of the array is reached.
 * @return The blob thats the Nth largest in the particleReports array.
 */
public ParticleReport getNthSizeBlob (int n)
{
    return particleReports[n];
}

// ======================POST PROCESSING METHODS========================
/*
 * Any methods that DO NOT require direct OpenCV access and are NOT game
 * specific can be placed below.
 */

/**
 * Takes the base OpenCV list of contours and changes the output to be easier to
 * work with.
 * 
 * @param contours
 *            The input from the base OpenCV contours output
 */
private void createParticleReports (List<MatOfPoint> contours)
{
    ParticleReport[] reports = new ParticleReport[contours.size()];

    for (int i = 0; i < reports.length; i++)
        {
        reports[i] = new ParticleReport();
        Rect r = Imgproc.boundingRect(contours.get(i));
        reports[i].area = r.area();
        reports[i].center = new Point(r.x + (r.width / 2),
                r.y + (r.height / 2));
        reports[i].boundingRect = r;
        }

    this.particleReports = reports;
}

/**
 * TODO TEST THIS
 * 
 * Calculates the angle the target is at from the center line.
 * The formula can be cut into two easier sections, one for the focal
 * length and one for the angle.
 * 
 * Focal length (in pixels): Resolution / 2 x tan(FOV / 2)
 * Angle (in radians): arctan(distanceFromCenter / focalLength)
 * 
 * @param target
 *            The input: takes the Y axis from the center point.
 * @return the angle, in degrees. If the target is above the center line,
 *         it will show positive. If it is below the line, it will show
 *         negative.
 */
public double getPitchAngleDegrees (ParticleReport target)
{
    int distFromCenterLine = (int) Math
            .abs((image.size().height / 2) - target.center.y);

    // The focal length is dependent on the resolution of the image, since
    // units must remain in pixels, and the field of view must not change.
    double focalLengthPixels = image.size().height
            / (2 * Math.tan(verticalFieldOfView / 2.0));

    // Conditions for the return statement based on the position of the
    // target.
    if ((image.size().height / 2) - target.center.y > 0)
        return Math.toDegrees(
                Math.atan(distFromCenterLine / focalLengthPixels));

    return -Math.toDegrees(
            Math.atan(distFromCenterLine / focalLengthPixels));
}

/**
 * TODO TEST THIS
 * 
 * Calculates the angle the target is at from the center line.
 * The formula can be cut into two easier sections, one for the focal
 * length and one for the angle.
 * 
 * Focal length (in pixels): Resolution / 2 x tan(FOV / 2)
 * Angle (in radians): arctan(distanceFromCenter / focalLength)
 * 
 * @param target
 *            The input: takes the X axis from the center point.
 * @return the angle, in degrees. If the target is to the right of the center
 *         line,
 *         it will show positive. If it is to the left, it will show negative.
 */
public double getYawAngleDegrees (ParticleReport target)
{
    int distFromCenterLine = (int) Math
            .abs((image.size().width / 2) - target.center.x);

    // The focal length is dependent on the resolution of the image, since
    // units must remain in pixels, and the field of view must not change.
    double focalLengthPixels = image.size().width
            / (2 * Math.tan(horizontalFieldOfView / 2.0));

    // Conditions for the return statement based on the position of the
    // target.
    if ((image.size().width / 2) - target.center.x < 0)
        return Math.toDegrees(
                Math.atan(distFromCenterLine / focalLengthPixels));

    return -Math.toDegrees(
            Math.atan(distFromCenterLine / focalLengthPixels));
}

// ======================GAME SPECIFIC METHODS======================
/*
 * Contains methods that ARE specific to the game each year. Any methods
 * that control the robot's drive system should be placed in the DRIVE
 * class. This is strictly for finding distances, angles, number of targets,
 * etc.
 */

}
