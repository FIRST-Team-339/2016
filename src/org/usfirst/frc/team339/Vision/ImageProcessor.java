package org.usfirst.frc.team339.Vision;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.Vision.operators.ColorThresholdOperator;
import org.usfirst.frc.team339.Vision.operators.ConvexHullOperator;
import org.usfirst.frc.team339.Vision.operators.LoadColorImageJPEGOperator;
import org.usfirst.frc.team339.Vision.operators.RemoveSmallObjectsOperator;
import org.usfirst.frc.team339.Vision.operators.SaveBinaryImagePNGOperator;
import org.usfirst.frc.team339.Vision.operators.VisionOperatorInterface;
import edu.wpi.first.wpilibj.image.NIVisionException;

public class ImageProcessor
{

// A structure to hold measurements of a particle
public class ParticleReport implements Comparator<ParticleReport>,
        Comparable<ParticleReport>
{
public double PercentAreaToImageArea;

public double Area;

public double ConvexHullArea;

// double BoundingRectLeft;
// double BoundingRectTop;
// double BoundingRectRight;
// double BoundingRectBottom;
public int boundingRectLeft;

public int boundingRectTop;

public int boundingRectRight;

public int boundingRectBottom;

public int center_mass_x; // TODO: actually initialize these values

public int center_mass_y;

public int imageHeight;

public int imageWidth;

public int boundingRectWidth;

@Override
public int compare (ParticleReport r1, ParticleReport r2)
{
    return (int) (r1.Area - r2.Area);
}

@Override
public int compareTo (ParticleReport r)
{
    return (int) (r.Area - this.Area);
}
};

private KilroyCamera camera = null;

private Image currentImage = null;

//@AHK TODO create a visionScript object to further object orient the code?
private final ArrayList<VisionOperatorInterface> operators =
        new ArrayList<VisionOperatorInterface>();

public ParticleReport[] reports = null;

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
            .add(new ColorThresholdOperator(0, 153, 0, 75, 5, 141));
    this.operators.add(new RemoveSmallObjectsOperator(2, true));
    this.operators.add(new ConvexHullOperator(true));
    this.operators.add(new SaveBinaryImagePNGOperator(
            "/home/lvuser/images/Out.png"));
    this.camera = camera;
}

public void applyOperators ()
{
    // Goes through all operators and applies whatever changes they are
    // programmed to apply. The currentImage is replaced with the altered
    // image.
    for (final VisionOperatorInterface operator : this.operators)
        {
        this.currentImage = operator.operate(this.currentImage);
        }
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
        //If the operator template is of the same class as the currently
        //viewed operator in the processor script, remove it
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
 * operator list. DOES NOT update the particle reports.
 */
public void processImage ()
{
    this.updateImage();
    this.applyOperators();
    // this.updateParticalAnalysisReports();
}

/**
 * Captures an image from the camera given to the class.
 */
public void updateImage ()
{
    try
        {
        this.currentImage = this.camera.getImage().image;
        }
    catch (final NIVisionException e)
        {
        //Auto-generated catch block
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
    final Vector<ParticleReport> particles =
            new Vector<ParticleReport>();

    if (numParticles > 0)
        {

        for (int particleIndex =
                0; particleIndex < numParticles; particleIndex++)
            {

            final ParticleReport particle = new ParticleReport();
            particle.PercentAreaToImageArea = NIVision
                    .imaqMeasureParticle(this.currentImage,
                            particleIndex, 0,
                            NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
            particle.Area = NIVision.imaqMeasureParticle(
                    this.currentImage,
                    particleIndex, 0,
                    NIVision.MeasurementType.MT_AREA);
            particle.ConvexHullArea = NIVision.imaqMeasureParticle(
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
}
