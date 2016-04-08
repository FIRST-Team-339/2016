package org.usfirst.frc.team339.Utils;

import java.util.Comparator;
import java.util.Vector;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.ParticleFilterCriteria2;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

/**
 * Utility class for taking and processing images from an axis camera
 *
 * @author Will Stuckey and NOAH GOLMANT
 * @modified by Noah Golmant on 29 May 2014, added criteria functionality to
 *           replace
 *           competition-specific functions
 * @modified by Noah Golmant on 13 August 2015, marked deprecated for Vision
 *           package
 */
@Deprecated
public class ImageProcessing
{

// fake enum to hold types of binary image object removal in processing
public enum ObjectRemoval
    {
    // no object removal
    NONE,
    // large object removal
    LARGE,
    // remove small objects
    SMALL;
    }

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
public int center_mass_x;// TODO: actually initialize these values
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

// HSL values
// competition (no lexan): 80, 145, 0, 255, 145, 220
// lab (no lexan): 80, 145, 25, 255, 120, 220
// lab (lexan): 80, 145, 10, 255, 120, 220
// HSL values
int hueLow = 0, hueHigh = 255, saturationLow = 0,
        saturationHigh = 255,
        luminenceLow = 0, luminenceHigh = 255;

// debug states
private boolean debugConsole = false;

// camera
private KilroyCamera camera = null;

// active image
private ColorImage image = null;

// active reports
// private ParticleAnalysisReport[] reports = null;
private ParticleReport[] reports = null;

// Criteria collection
// private CriteriaCollection criteriaCollection = new CriteriaCollection();
private ParticleFilterCriteria2[] criteriaArray =
        new ParticleFilterCriteria2[0];

// middle of the screen on x axis
private int middleScreenWidth = 80;

// flag to determine whether to apply a convex hull operation
private boolean useConvexHull = false;

private ObjectRemoval objectRemoval = ObjectRemoval.NONE;

private int objectErosion = 1;

/**
 * Uses default HSL values (80, 145, 0, 255, 95, 150).
 * Camera must be set in robotInit() using setCamera()
 * MotorSafetyHelper must be set in robotInit() using setMotorSafetyHelper()
 *
 * @see setCamera()
 * @see setMotorSafetyHelper()
 * @author Will Stuckey
 * @written 28 Jan 2014
 */
public ImageProcessing ()
{
}

/**
 * Uses default HSL values (80, 145, 0, 255, 95, 150).
 *
 * @param camera
 *            the axis camera used for image capture
 * @author Will Stuckey
 * @written 28 Jan 2014
 */
public ImageProcessing (KilroyCamera camera)
{
    this.camera = camera;
}

/**
 * Initializes with all important objects and variables.
 *
 * @param camera
 *            the axis camera used for image capture
 * @param hueLow
 *            the HSL value
 * @param hueHigh
 *            the HSL value
 * @param saturationLow
 *            the HSL value
 * @param saturationHigh
 *            the HSL value
 * @param luminenceLow
 *            the HSL value
 * @param luminenceHigh
 *            the HSL value
 * @see ColorImage.thresholdHSL()
 * @author Will stuckey
 * @written 28 Jan 2014
 */
public ImageProcessing (KilroyCamera camera, int hueLow,
        int hueHigh,
        int saturationLow, int saturationHigh, int luminenceLow,
        int luminenceHigh)
{
    this.camera = camera;
    this.setHSLValues(hueLow, hueHigh, saturationLow,
            saturationHigh,
            luminenceLow, luminenceHigh);
}

public ImageProcessing (KilroyCamera camera, int hueLow,
        int hueHigh,
        int saturationLow, int saturationHigh, int luminenceLow,
        int luminenceHigh, ParticleFilterCriteria2[] criteria)
{
    this.camera = camera;

    this.setHSLValues(hueLow, hueHigh, saturationLow,
            saturationHigh,
            luminenceLow, luminenceHigh);

    this.criteriaArray = criteria;
}

/**
 * Adds a criteria to our criteria collection that is used
 * to filter the image.
 *
 * @param type
 *            measurement type to use, given from the list of
 *            information that each blob has in NIVision.MeasurementType.
 * @param lower
 *            lower range of this measurement type
 * @param upper
 *            upper range of this measurement type
 * @param exclude
 *            (not sure about this one yet)
 * @param outsideRange
 *            - true if we want to get rid of particles
 *            within the specified range, false if we want those within the
 *            range.
 *
 *            Any blob who's measurement of the given
 *            NIVision.MeasurementType falls outside
 *            of the given range will be removed from the
 *            particleAnalysisReports array.
 * @author Noah Golmant
 * @written 29 May 2014
 */
public void addCriteria (NIVision.MeasurementType type, float lower,
        float upper, int exclude, int outsideRange)
{
    // Create a new criteria object from our arguments
    final ParticleFilterCriteria2 criteria =
            new ParticleFilterCriteria2(
                    type, lower, upper, exclude,
                    outsideRange);
    // Create a new criteria array, the size of our old array + 1 to
    // accomodate the new one
    final ParticleFilterCriteria2[] tempCriteria =
            new ParticleFilterCriteria2[this.criteriaArray.length
                    + 1];
    // Copy the old criteria array into our new one
    System.arraycopy(this.criteriaArray, 0, tempCriteria, 0,
            this.criteriaArray.length);
    // set the last value of this new array to the new criteria
    tempCriteria[tempCriteria.length - 1] = criteria;
    // set the class criteria array to our temporary one that has the new
    // entry
    this.criteriaArray = tempCriteria;
    // this.criteriaCollection.addCriteria(type, lower, upper,
    // outsideRange);
}

/**
 * Clears our collection of criteria for image and particle analysis.
 *
 * @author Noah Golmant
 * @written 29 May 2014
 */
public void clearCriteriaCollection ()
{
    this.criteriaArray = new ParticleFilterCriteria2[0];
}

//TODO temporary code to find the center of a bounding rectangle.
//We really need to clean this up
public double getCenterOfBoundingRectangleX (ParticleReport desiredBlob)
{
    return 0.0;
}

/**
 * The number of blobs we see in our current image.
 *
 * @return the number of blobs seen in the image
 * @author Will
 * @written 28 Jan 2014
 */
public int getNumBlobs ()
{
    if (this.reports == null)
        return 0;

    if (this.debugConsole == true)
        {
        for (int i = this.reports.length - 1; i >= 0; i--)
            {
            // print if we want to display on the console.

            System.out.println("Blob number: " + i);
            System.out.println("Location: " +
                    this.reports[i].center_mass_x + ", " +
                    this.reports[i].center_mass_y);
            System.out
                    .println("Height: "
                            + this.reports[i].imageHeight);

            }
        }

    // if we want to print, then print to system.out.
    if (this.debugConsole == true)
        {
        System.out
                .println("Number of blobs: " + this.reports.length);
        }

    return this.reports.length;
}

/**
 * Gets the array of blobs that we have gathered.
 *
 * @return particle analysis reports (aka blob reports)
 * @author Will
 * @written 28 Jan 2014
 */
public ParticleReport[] getParticleAnalysisReports ()
{
    return this.reports;
}

/**
 * Determines the difference between the rightmost blob's right edge and the
 * right side of the screen
 *
 * @return the the difference between the right edge of the screen and the
 *         right blob (a positive value)
 */
public int getRightBlobOffset ()
{
    if (this.reports == null)
        return 0;

    int rightmostX = -1000; // a very low value, beyond the range of the
    // camera

    int screenWidth = 0;

    if (this.reports.length == 0)
        {
        if (this.debugConsole == true)
            {
            System.out.println("No blobs were found. Returning 0");
            }
        return 0;
        }
    // we have at least 1 image, so set the screen width
    screenWidth = this.reports[0].imageWidth;

    for (int i = this.reports.length - 1; i >= 0; i--)
        {

        // update right if the particles' bounding box right is closer to
        // the
        // edge of the screen
        if ((this.reports[i].boundingRectLeft
                + this.reports[i].boundingRectWidth) > rightmostX)
            {
            rightmostX = this.reports[i].boundingRectLeft +
                    this.reports[i].boundingRectWidth;
            }

        // // print if we want to display on the console.
        if (this.debugConsole == true)
            {
            System.out.println("Blob number: " + i);
            System.out.println("Right Bound: " +
                    this.reports[i].boundingRectLeft +
                    this.reports[i].boundingRectWidth);
            }
        }

    // the return value
    final int rightOffset = (screenWidth - rightmostX);

    // if we requested printing, then print to the system.out.
    if (this.debugConsole == true)
        {
        System.out.println("Right offset: " + rightOffset);
        }

    return rightOffset;
}

/**
 * Gets the width of the widest blob that we see in our particle
 * analysis reports array.
 *
 * @author Noah Golmant
 * @written 13 March 2014
 * @return width of the widest blob
 */
public int getWidestBlobWidth ()
{
    if (this.reports == null)
        return 0;

    int maxBlobWidth = 0;
    for (int i = this.reports.length - 1; i >= 0; i--)
        {
        maxBlobWidth = Math.max(maxBlobWidth,
                this.reports[i].boundingRectWidth);

        // print if we want to display on the console.
        if (this.debugConsole == true)
            {
            System.out.println("Blob number: " + i);
            System.out.println("Location: " +
                    this.reports[i].center_mass_x + ", " +
                    this.reports[i].center_mass_y);
            System.out.println(
                    "Width: " + this.reports[i].imageWidth);
            }
        }

    // if we want to print, then print to system.out.
    if (this.debugConsole == true)
        {
        System.out.println("Blob width: " + maxBlobWidth);
        }

    return maxBlobWidth;
}

public int getXOffsetOfYellowTote ()
{
    if ((this.reports == null) || (this.reports.length == 0))
        {
        if (this.debugConsole == true)
            {
            System.out
                    .println(
                            "We found no blobs! (Or we haven't yet processed the image)");
            Hardware.errorMessage.printError("No blobs were found");
            }
        return 0;
        }
    int maxRight = 0;
    for (final ParticleReport blob : this.reports)
        {
        // if our blob has a center of mass in the bottom quarter of the
        // image
        // TODO: replace the center of mass with the bounding rect. bottom
        // once we have learned how to exclude the top part of the image
        // from processing (see teh Find Lower Yellow Totes Preliminary
        // Script2.vascr image processing script on the desktop for the
        // numbers
        if (blob.center_mass_y > ((blob.imageHeight * 3) / 4))
            {
            // and if our blob has an area greater than 1000 (may need to be
            // lowered)
            if (blob.// particleArea
                    Area > 1000.0)
                {
                // update the max
                maxRight =
                        Math.max(maxRight, blob.boundingRectWidth +
                                blob.boundingRectLeft);
                }
            }
        }
    // return the farthest right edge of a matching blob in the image;
    // compare this value to a determined range (somewhere around 250 in the
    // 320x240 image)
    return maxRight;
    // HSL Values: 32, 48, 136, 255, 107, 243
    // we need to: image mask (this is the part that needs to be done) -
    // color threshold - remove small objects - test the bottom of the blob
    // - test the area - test the far right edge and return the farthest
    // right matching blob
}

/**
 * Determines whether or not most blobs are on the right half of the
 * screen.
 *
 * @return true if most blobs are on the right
 * @author Noah Golmant
 * @written 20 March 2014
 */
public boolean higherHalfIsRight ()
{
    if (this.reports == null)
        return true;

    int numLeft = 0, numRight = 0;
    for (int i = 0; i < this.reports.length; i++)
        {
        if (this.reports[i].center_mass_x > this.middleScreenWidth)
            {
            numRight++;
            }
        else
            {
            numLeft++;
            }

        }

    if (this.debugConsole == true)
        {
        System.out.println("LEFT BLOBS: " + numLeft + ", RIGHT: " +
                numRight);
        }

    return (numRight >= numLeft);
}

/**
 * Sets the camera to use while processing
 *
 * @param camera
 * @author Will
 * @written 28 Jan 2014
 */
public void setCamera (KilroyCamera camera)
{
    this.camera = camera;
}

/**
 * Set the criteria array to a new set
 *
 * @param criteriaArray
 *            collection of criteria to use in image filtering
 * @author Noah Golmant
 * @written 29 May 2014
 */
// @TODO replace collection with primitive criteria array
public void
        setCriteriaArray (ParticleFilterCriteria2[] criteriaArray)
{
    this.criteriaArray = criteriaArray;
}

/**
 * Sets whether to debug to the console
 *
 * @param debug
 * @author Will
 * @written 28 Jan 2014
 */
public void setDebugConsole (boolean debug)
{
    this.debugConsole = debug;
}

/**
 * Set the hue, saturation, and luminence value ranges for our
 * particle analysis.
 *
 * @param hueLow
 *            min hue value
 * @param hueHigh
 *            max hue value
 * @param saturationLow
 *            min saturation value
 * @param saturationHigh
 *            max saturation value
 * @param luminenceLow
 *            min luminence value
 * @param luminenceHigh
 *            max luminence value
 * @see NI Vision Assistant software to determine correct HSL values
 * @author Noah Golmant
 * @written 29 May 2014
 */
public void setHSLValues (int hueLow, int hueHigh,
        int saturationLow,
        int saturationHigh, int luminenceLow, int luminenceHigh)
{
    this.hueLow = hueLow;
    this.hueHigh = hueHigh;
    this.saturationLow = saturationLow;
    this.saturationHigh = saturationHigh;
    this.luminenceLow = luminenceLow;
    this.luminenceHigh = luminenceHigh;
}

/**
 * Set the middleScreenWidth (half the camera image width)
 *
 * @param width
 *            the new width to set the middleScreenWidth to
 * @return the middleScreenWidth
 */
public int setMiddleScreenWidth (int width)
{
    this.middleScreenWidth = width;
    return this.middleScreenWidth;
}

/**
 * Sets our removal type, defaults to none. In our processing, we may
 * remove large or small objects with a certain erosion level.
 *
 * @param removal
 * @author Noah Golmant
 */
public void setObjectRemoval (ObjectRemoval removal)
{
    this.objectRemoval = removal;
    this.objectErosion = 1; // default erosion level
}

/**
 * Sets our removal type, defaults to none. In our processing, we may
 * remove large or small objects with a certain erosion level.
 *
 * @param removal
 * @param erosion
 *            how much to erode each object; how much each iteration of
 *            erosion removes particles
 * @Noah
 */
public void setObjectRemoval (ObjectRemoval removal, int erosion)
{
    this.objectRemoval = removal;
    this.objectErosion = erosion;
}

/**
 * Sets whether or not we will apply a convex hull operation in image
 * processing, which fills in large, hollow rectangles to create one
 * blob
 *
 * @param useConvex
 *            whether or not to use convex hull
 * @author Noah Golmant
 */
public void setUseConvexHull (boolean useConvex)
{
    this.useConvexHull = useConvex;
}

/**
 * Captures and updates the current working image
 *
 * @return successful image capture
 * @author Will
 * @written 28 Jan 2014
 */
public boolean updateImage ()
{
    try
        {
        this.image = this.camera.getImage();
        }
    catch (final NIVisionException e)
        {
        this.image = null;
        return false;
        }
    if (this.image == null)
        return false;
    return true;
}

/**
 * Updates the current working image
 *
 * @param image
 *            the image to be used as the working image
 */
public void updateImage (ColorImage image)
{
    this.image = image;
}

/**
 * Updates the working reports array (aka blob reports array)
 * from the working image
 *
 * @see getParticleAnalysisReports()
 * @return successful analysis
 */
public boolean updateParticleAnalysisReports ()
{
    try
        {
        if ((this.image == null) || (this.image.getWidth() == 0))
            return false;
        }
    catch (final NIVisionException e1)
        {
        // TODO Auto-generated catch block
        e1.printStackTrace();
        return false;
        }
    /*
     * NIVision
     * .imaqReadFile(this.image.image, "/home/lvuser/images/ToteR.jpg");
     */
    try
        {
        // BinaryImage thresholdImage =
        // this.image.thresholdHSL(this.hueLow, this.hueHigh,
        // this.saturationLow, this.saturationHigh, this.luminenceLow,
        // this.luminenceHigh);
        Image thresholdImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);

        // check the image versus any criteria we have
        // Any blob who's measurement of the given
        // NIVision.MeasurementType
        // (in some criteria) falls outside of the given range will
        // be removed from the particleAnalysisReports array.

        NIVision.imaqColorThreshold(thresholdImage,
                this.image.image,
                255,
                NIVision.ColorMode.HSL,
                new NIVision.Range(this.hueLow,//TODO set colormode to HSL
                        this.hueHigh),
                new NIVision.Range(this.saturationLow,
                        this.saturationHigh),
                new NIVision.Range(this.luminenceLow,
                        this.luminenceHigh));

        //			System.out.println("HSL blobs: " +
        //			        NIVision.imaqCountParticles(thresholdImage, 0));

        // initial size of the criteria array is ZERO so we have to
        // check this
        Image criteriaImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);
        if ((this.criteriaArray == null) ||
                (this.criteriaArray.length == 0))
            {
            criteriaImage = thresholdImage;
            }
        else
            {
            final NIVision.ParticleFilterOptions2 filterOptions =
                    new NIVision.ParticleFilterOptions2(
                            0, 0, 0, 0);
            NIVision.imaqParticleFilter4(criteriaImage,
                    thresholdImage,
                    this.criteriaArray, filterOptions, null);
            // criteriaImage =
            // thresholdImage.particleFilter(this.criteriaArray);
            }

        //			System.out.println("Criteria blobs: " +
        //			        NIVision.imaqCountParticles(criteriaImage, 0));

        // fill in occluded rectangles
        Image convexHullImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 3);
        if (this.useConvexHull == true)
            {
            NIVision.imaqConvexHull(convexHullImage, criteriaImage,
                    0);
            // convexHullImage = criteriaImage.convexHull(false);

            }
        else
            {
            convexHullImage = criteriaImage;
            }

        //			System.out.println("Convex hull blobs: " +
        //			        NIVision.imaqCountParticles(convexHullImage, 0));

        // remove small artifacts
        Image objectRemovalImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);
        if (this.objectRemoval == ObjectRemoval.SMALL)
            {

            final NIVision.StructuringElement retStruct =
                    new NIVision.StructuringElement(
                            3, 3, 0);
            NIVision.imaqSizeFilter(objectRemovalImage,
                    convexHullImage,
                    0,
                    this.objectErosion,
                    NIVision.SizeType.KEEP_LARGE,
                    null);// new
            // NIVision.StructuringElement(3,
            // 3,
            // 0));//

            // objectRemovalImage =
            // convexHullImage.removeSmallObjects(false,
            // this.objectErosion);
            }
        else if (this.objectRemoval == ObjectRemoval.LARGE)
            {
            // objectRemovalImage =
            // convexHullImage.removeLargeObjects(false,
            // this.objectErosion);
            NIVision.imaqSizeFilter(objectRemovalImage,
                    convexHullImage,
                    0,
                    this.objectErosion,
                    NIVision.SizeType.KEEP_SMALL,
                    new NIVision.StructuringElement(3, 3, 0));// null);
            }
        else
            {
            objectRemovalImage = convexHullImage;
            }

        // get list of results
        // this.reports =
        // objectRemovalImage.getOrderedParticleAnalysisReports();

        final int numParticles = NIVision
                .imaqCountParticles(objectRemovalImage, 0);

        //			System.out.println("Object removal blobs: " +
        //			        NIVision.imaqCountParticles(objectRemovalImage, 0));

        // Measure particles and sort by particle size
        final Vector<ParticleReport> particles =
                new Vector<ParticleReport>();

        if (numParticles > 0)
            {

            for (int particleIndex =
                    0; particleIndex < numParticles; particleIndex++)
                {

                final ParticleReport par = new ParticleReport();
                par.PercentAreaToImageArea = NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
                par.Area = NIVision.imaqMeasureParticle(
                        objectRemovalImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_AREA);
                par.ConvexHullArea = NIVision.imaqMeasureParticle(
                        objectRemovalImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
                par.boundingRectTop = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
                par.boundingRectLeft = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
                par.boundingRectBottom = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                par.boundingRectRight = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
                par.boundingRectWidth = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);// par.boundingRectRight
                // -
                // par.boundingRectLeft;
                par.center_mass_x =
                        (int) NIVision.imaqMeasureParticle(
                                objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
                par.center_mass_y =
                        (int) NIVision.imaqMeasureParticle(
                                objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
                par.imageWidth = NIVision
                        .imaqGetImageSize(objectRemovalImage).width;
                particles.add(par);
                }
            particles.sort(null);

            }
        this.reports = new ParticleReport[particles.size()];
        particles.copyInto(this.reports);
        // this.reports = (ParticleReport[]) particles.toArray();
        this.image.free();
        this.image = null;
        thresholdImage.free();
        thresholdImage = null;

        criteriaImage.free();
        criteriaImage = null;
        convexHullImage.free();
        convexHullImage = null;
        objectRemovalImage.free();
        objectRemovalImage = null;
        }
    catch (final NIVisionException e)
        {
        return false;
        }

    return true;
}

/**
 * Updates the working reports array (aka blob reports array)
 * from the working image
 *
 * @see getParticleAnalysisReports()
 * @return successful analysis
 */
public boolean updateParticleAnalysisReports2015 ()
{
    try
        {
        if ((this.image == null) || (this.image.getWidth() == 0))
            {
            if (this.debugConsole == true)
                {
                System.out.println(
                        "Image is invallid -- null or 0 width.");
                }
            return false;
            }
        }
    catch (final NIVisionException e1)
        {
        if (this.debugConsole == true)
            {
            System.out
                    .println(
                            "Getting Image width failed in UpdateParticleAnalysisReports.");
            }
        e1.printStackTrace();
        return false;
        }
    /*
     * NIVision
     * .imaqReadFile(this.image.image, "/home/lvuser/images/ToteR.jpg");
     */
    try
        {
        // BinaryImage thresholdImage =
        // this.image.thresholdHSL(this.hueLow, this.hueHigh,
        // this.saturationLow, this.saturationHigh, this.luminenceLow,
        // this.luminenceHigh);
        Image thresholdImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);

        // check the image versus any criteria we have
        // Any blob who's measurement of the given
        // NIVision.MeasurementType
        // (in some criteria) falls outside of the given range will
        // be removed from the particleAnalysisReports array.

        NIVision.imaqColorThreshold(thresholdImage,
                this.image.image,
                255,
                NIVision.ColorMode.HSL,
                new NIVision.Range(this.hueLow,
                        this.hueHigh),
                new NIVision.Range(this.saturationLow,
                        this.saturationHigh),
                new NIVision.Range(this.luminenceLow,
                        this.luminenceHigh));

        System.out.println("HSL blobs: " +
                NIVision.imaqCountParticles(thresholdImage, 0));

        // initial size of the criteria array is ZERO so we have to
        // check this
        Image criteriaImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);
        if ((this.criteriaArray == null) ||
                (this.criteriaArray.length == 0))
            {
            criteriaImage = thresholdImage;
            }
        else
            {
            final NIVision.ParticleFilterOptions2 filterOptions =
                    new NIVision.ParticleFilterOptions2(
                            0, 0, 0, 0);
            NIVision.imaqParticleFilter4(criteriaImage,
                    thresholdImage,
                    this.criteriaArray, filterOptions, null);
            // criteriaImage =
            // thresholdImage.particleFilter(this.criteriaArray);
            }

        System.out.println("Criteria blobs: " +
                NIVision.imaqCountParticles(criteriaImage, 0));

        // fill in occluded rectangles
        Image convexHullImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 3);
        if (this.useConvexHull == true)
            {
            NIVision.imaqConvexHull(convexHullImage, criteriaImage,
                    0);
            // convexHullImage = criteriaImage.convexHull(false);

            }
        else
            {
            convexHullImage = criteriaImage;
            }

        System.out.println("Convex hull blobs: " +
                NIVision.imaqCountParticles(convexHullImage, 0));

        // remove small artifacts
        Image objectRemovalImage = NIVision
                .imaqCreateImage(ImageType.IMAGE_U8, 0);
        if (this.objectRemoval == ObjectRemoval.SMALL)
            {

            NIVision.imaqSizeFilter(objectRemovalImage,
                    convexHullImage,
                    0,
                    this.objectErosion,
                    NIVision.SizeType.KEEP_LARGE,
                    new NIVision.StructuringElement(3, 3, 0));// null);

            // objectRemovalImage =
            // convexHullImage.removeSmallObjects(false,
            // this.objectErosion);
            }
        else if (this.objectRemoval == ObjectRemoval.LARGE)
            {
            // objectRemovalImage =
            // convexHullImage.removeLargeObjects(false,
            // this.objectErosion);
            NIVision.imaqSizeFilter(objectRemovalImage,
                    convexHullImage,
                    0,
                    this.objectErosion,
                    NIVision.SizeType.KEEP_SMALL,
                    new NIVision.StructuringElement(3, 3, 0));// null);
            }
        else
            {
            objectRemovalImage = convexHullImage;
            }

        // get list of results
        // this.reports =
        // objectRemovalImage.getOrderedParticleAnalysisReports();

        final int numParticles = NIVision
                .imaqCountParticles(objectRemovalImage, 0);

        System.out.println("Object removal blobs: " +
                NIVision.imaqCountParticles(objectRemovalImage, 0));

        // Measure particles and sort by particle size
        final Vector<ParticleReport> particles =
                new Vector<ParticleReport>();

        if (numParticles > 0)
            {

            for (int particleIndex =
                    0; particleIndex < numParticles; particleIndex++)
                {

                final ParticleReport par = new ParticleReport();
                par.PercentAreaToImageArea = NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
                par.Area = NIVision.imaqMeasureParticle(
                        objectRemovalImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_AREA);
                par.ConvexHullArea = NIVision.imaqMeasureParticle(
                        objectRemovalImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
                par.boundingRectTop = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
                par.boundingRectLeft = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
                par.boundingRectBottom = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                par.boundingRectRight = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
                par.boundingRectWidth = (int) NIVision
                        .imaqMeasureParticle(objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);// par.boundingRectRight
                // -
                // par.boundingRectLeft;
                par.center_mass_x =
                        (int) NIVision.imaqMeasureParticle(
                                objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
                par.center_mass_y =
                        (int) NIVision.imaqMeasureParticle(
                                objectRemovalImage,
                                particleIndex, 0,
                                NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
                par.imageWidth = NIVision
                        .imaqGetImageSize(objectRemovalImage).width;
                particles.add(par);
                }
            particles.sort(null);

            }
        this.reports = new ParticleReport[particles.size()];
        particles.copyInto(this.reports);
        // this.reports = (ParticleReport[]) particles.toArray();

        this.image.free();
        this.image = null;
        thresholdImage.free();
        thresholdImage = null;

        criteriaImage.free();
        criteriaImage = null;
        convexHullImage.free();
        convexHullImage = null;
        objectRemovalImage.free();
        objectRemovalImage = null;
        }
    catch (final NIVisionException e)
        {
        return false;
        }

    return true;
}

}
