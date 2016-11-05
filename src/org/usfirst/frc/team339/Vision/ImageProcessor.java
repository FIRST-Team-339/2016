package org.usfirst.frc.team339.Vision;

import java.util.Comparator;
import java.util.Vector;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.Vision.operators.ConvexHullOperator;
import org.usfirst.frc.team339.Vision.operators.HSLColorThresholdOperator;
import org.usfirst.frc.team339.Vision.operators.LoadColorImageJPEGOperator;
import org.usfirst.frc.team339.Vision.operators.RemoveSmallObjectsOperator;
import org.usfirst.frc.team339.Vision.operators.SaveBinaryImagePNGOperator;
import org.usfirst.frc.team339.Vision.operators.VisionOperatorInterface;
import edu.wpi.first.wpilibj.image.NIVisionException;

public class ImageProcessor
{

/**
 * A class that holds several statistics about particles.
 * 
 * The measures include:
 * area: The area, in pixels of the blob
 * boundingRectLeft: The x coordinate of the left side of the blob
 * boundingRectTop: The y coordinate on the top of the bounding rectangle of the
 * blob
 * boundingRectRight: The x coordinate of a point which lies on the right side
 * of the bounding rectangle of the blob
 * boundingRectBottom: The y coordinate of a point which lies on the bottom side
 * of the bounding rectangle of the blob
 * center_mass_x: the weighted center of mass of the particle, If the particle
 * were a solid object of uniform density and thickness, this would be the x
 * coord of the balancing point.
 * center_mass_y: the weighted center of mass of the particle, If the particle
 * were a solid object of uniform density and thickness, this would be the y
 * coord of the balancing point.
 * imageHeight: The height of the image containing the blob
 * imageWidth: the width of the image containing the blob
 * boundingRectWidth: the width of the rectangle which would perfectly bound the
 * blob
 * PercentAreaToImageArea: The percent of the image this blob fills
 * ConvexHullArea: The area filled by the convex hull of the image
 * 
 * @author Kilroy
 *
 */
public class ParticleReport implements Comparator<ParticleReport>,
        Comparable<ParticleReport>
{
// TODO: actually initialize these values
// TODO add boundingRectHeight (and aspect ratio?)
public double area;

// double BoundingRectLeft;
// double BoundingRectTop;
// double BoundingRectRight;
// double BoundingRectBottom;
public int boundingRectLeft;

public int boundingRectTop;

public int boundingRectRight;

public int boundingRectBottom;

public int center_mass_x;

public int center_mass_y;

public int imageHeight;

public int imageWidth;

public int boundingRectWidth;

public double PercentAreaToImageArea;

public double ConvexHullArea;

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
};

private KilroyCamera camera = null;

private Image currentImage = null;

// @AHK TODO create a visionScript object to further object orient the code?
private VisionScript operators = new VisionScript();

private double offsetFromCenterX;// offset along line orthoganal to the primary
                                 // vector of travel that intersects the center
                                 // potistive towards the starboard, negative
                                 // towards the port

private double offsetFromCenterY;// offset along primary vector of travel
                                 // positive is towards the front, negative
                                 // towards the back

private double cameraFocalLength;


public ParticleReport[] reports = null;

private boolean newImageIsFresh = true;// TODO @AHK use to determine if we
                                       // actually process an image

/**
 * Creates an ImageProcessor object with camera <camera> and a default
 * processing script. The script consists of:
 * 1. LoadColorImageJPEGOperator
 * 2. ColorThresholdOperator
 * 3. RemoveSmallObjectsOperator
 * 4. ConvexHullOperator
 * 5. SaveBinaryImagePNGOperator
 * 
 * @param camera
 *            The KiloryCamera object that corresponds to the camera on the
 *            robot capturing images for processing. DO NOT PASS NULL HERE!
 */
public ImageProcessor (KilroyCamera camera)
{
    // this.operators.add(new SaveColorImageJPEGOperator(
    // "/home/lvuser/images/Test.jpg"));
    // this.camera.getImage().image;
    this.operators.add(new LoadColorImageJPEGOperator(
            "/home/lvuser/images/Firstpic.jpg"));
    this.operators
            .add(new HSLColorThresholdOperator(0, 153, 0, 75, 5, 141));
    this.operators.add(new RemoveSmallObjectsOperator(2, true));
    this.operators.add(new ConvexHullOperator(true));
    this.operators.add(new SaveBinaryImagePNGOperator(
            "/home/lvuser/images/Out.png"));
    this.camera = camera;
}

public ImageProcessor (KilroyCamera camera, VisionScript script)
{
    this.camera = camera;
    this.operators = script;
}

public ImageProcessor (KilroyCamera camera,
        VisionOperatorInterface... ops)
{
    this.camera = camera;
    this.operators = new VisionScript();
    for (VisionOperatorInterface operator : ops)
        {
        this.operators.put(operator);
        }
}

@Deprecated
public ParticleReport[] getParticleAnalysisReports ()
{
    return this.reports;
}


public void applyOperators ()
{
    // Goes through all operators and applies whatever changes they are
    // programmed to apply. The currentImage is replaced with the altered
    // image.
    if (this.currentImage != null && this.newImageIsFresh == true)
        {
        for (int i = 0; i < operators.size(); i++)
            {
            this.currentImage = this.operators.get(i)
                    .operate(this.currentImage);
            }
        }
}

public void replaceVisionScript (VisionScript newScript)
{
    this.operators = newScript;
}

/**
 * Adds a new vision operator to the operator list, in zero-based position
 * <index>.
 * 
 * @param index
 *            The position in the list to add the operator (the list is
 *            traversed in order to process an image)
 * @param operator
 *            The VisionOperator to add to the list.
 * @author Alexander H. Kneipp
 */
public void addOperator (int index, VisionOperatorInterface operator)
{
    this.operators.add(index, operator);
}

/**
 * Removes the operator at position <index> in the processor list.
 * 
 * @param index
 *            The zero-based position from which to remove the operator.
 */
public void removeOperator (int index)
{
    this.operators.remove(index);
}

/**
 * Removes operators by operator type. Will only remove the first occurrence of
 * the operator unless <removeAllInstances> is true.
 * 
 * @param operatorToRemove
 *            The operator type to remove from the processing list.
 * @param removeAllInstances
 *            Boolean to determine if all occurrences of <operatorToRemove>.
 *            True removes all, false removes first.
 */
public void removeOperator (VisionOperatorInterface operatorToRemove,
        boolean removeAllInstances)
{
    for (int i = 0; i < this.operators.size(); i++)
        {
        // If the operator template is of the same class as the currently
        // viewed operator in the processor script, remove it
        if (operatorToRemove.getClass()
                .equals(this.operators.get(i).getClass()))
            {
            this.removeOperator(i);
            if (removeAllInstances == false)
                break;
            }
        }
}

/**
 * Removes all testing operators from the processing list.
 */
public void clearOperatorList ()
{
    operators.clear();
}

/**
 * Pulls a new image from the camera and processes the image through the
 * operator list, only if the new image it received was fresh.
 */
public void processImage ()
{
    this.updateImage();
    if (this.newImageIsFresh == true)
        {
        this.applyOperators();
        this.updateParticalAnalysisReports();// TODO test for mem usage and time
        }
}

/**
 * Captures an image from the camera given to the class.
 */
public void updateImage ()
{
    try
        {
        this.currentImage = this.camera.getImage().image;
        if (this.camera.freshImage() == true)
            {
            // this.currentImage = this.camera.getImage().image;
            this.newImageIsFresh = true;
            }
        else
            {
            this.newImageIsFresh = true;// false;
            }
        }// TODO @AHK only process new images
    catch (final NIVisionException e)
        {
        // Auto-generated catch block
        e.printStackTrace();
        }

}

/**
 * Takes the processed image and writes information on each particle (blob) into
 * the global <reports> array, in order of overall particle area.
 */
public void updateParticalAnalysisReports ()
{
    final int numParticles = NIVision
            .imaqCountParticles(this.currentImage, 0);

    System.out.println("Object removal blobs: " +
            NIVision.imaqCountParticles(this.currentImage, 0));

    // Measure particles and sort by particle size
    final Vector<ParticleReport> particles = new Vector<ParticleReport>();

    if (numParticles > 0)
        {

        for (int particleIndex = 0; particleIndex < numParticles; particleIndex++)
            {

            final ParticleReport particle = new ParticleReport();
            particle.PercentAreaToImageArea = NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
            particle.area = NIVision.imaqMeasureParticle(
                    this.currentImage,
                    particleIndex, 0,
                    NIVision.MeasurementType.MT_AREA);
            particle.ConvexHullArea = NIVision
                    .imaqMeasureParticle(
                            this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
            particle.boundingRectTop = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
            particle.boundingRectLeft = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
            particle.boundingRectBottom = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
            particle.boundingRectRight = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
            particle.boundingRectWidth = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);// par.boundingRectRight
            // -
            // par.boundingRectLeft;
            particle.center_mass_x = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
            particle.center_mass_y = (int) NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
            particle.imageWidth = NIVision
                    .imaqGetImageSize(this.currentImage).width;
            particles.add(particle);
            }
        particles.sort(null);

        }
    this.reports = new ParticleReport[particles.size()];
    particles.copyInto(this.reports);
}

// Positive right, negative left
// @AHK TODO improve parameter list
public double getYawAngleToTarget (int targetIndex)
{
    return Math.atan((this.reports[targetIndex].center_mass_x
            - (Hardware.drive.cameraXResolution / 2) - .5)
            / Hardware.CAMERA_FOCAL_LENGTH_PIXELS);
}

public double getPitchAngleToTarget (int targetIndex)
{
    return Math.atan((this.reports[targetIndex].center_mass_y
            - (Hardware.drive.cameraYResolution / 2) - .5)
            / Hardware.CAMERA_FOCAL_LENGTH_PIXELS)
            + Hardware.CAMERA_MOUNT_ANGLE_ABOVE_HORIZONTAL_RADIANS;
}

// TODO return ultrasonic value if we have one.
public double getZDistanceToTargetFT (int targetIndex)
{
    return (Hardware.VISION_GOAL_HEIGHT_FT
            * Math.cos(this.getYawAngleToTarget(targetIndex)))
            / Math.tan(this.getPitchAngleToTarget(targetIndex));
}
}
