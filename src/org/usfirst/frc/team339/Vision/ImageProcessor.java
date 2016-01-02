package org.usfirst.frc.team339.Vision;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
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
        public int center_mass_x;// TODO: actually initialize these valuse
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
    private final ArrayList<VisionOperatorInterface> operators =
        new ArrayList();

    ParticleReport[] reports = null;

    public ImageProcessor (KilroyCamera camera)
        {
        // this.operators.add(new SaveColorImageJPEGOperator(
        // "/home/lvuser/images/Test.jpg"));
        this.operators.add(new LoadColorImageJPEGOperator(
            "/home/lvuser/images/ToteR.jpg"));
        this.operators
        .add(new ColorThresholdOperator(26, 50, 59, 150, 79, 233));
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

    public int doStuff2015 ()
        {
        return this.reports.length;
        }

    // Fetches and processes the image, then updates the Particle Reports
    // DOES NOT do anything with reports.
    public void processImage ()
        {
        this.updateImage();
        this.applyOperators();
        this.updateParticalAnalysisReports();
        }

    public void updateImage ()
        {
        try
            {
            this.currentImage = this.camera.getImage().image;
            }
        catch (final NIVisionException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }

        }

    public void updateParticalAnalysisReports ()
        {
        final int numParticles =
            NIVision.imaqCountParticles(this.currentImage, 0);

        System.out.println("Object removal blobs: " +
            NIVision.imaqCountParticles(this.currentImage, 0));

        // Measure particles and sort by particle size
        final Vector<ParticleReport> particles = new Vector<ParticleReport>();

        if (numParticles > 0)
            {

            for (int particleIndex = 0; particleIndex < numParticles; particleIndex++)
                {

                final ParticleReport par = new ParticleReport();
                par.PercentAreaToImageArea =
                    NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
                par.Area =
                    NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0, NIVision.MeasurementType.MT_AREA);
                par.ConvexHullArea =
                    NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
                par.boundingRectTop =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
                par.boundingRectLeft =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
                par.boundingRectBottom =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                par.boundingRectRight =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
                par.boundingRectWidth =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);// par.boundingRectRight
                // -
                // par.boundingRectLeft;
                par.center_mass_x =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
                par.center_mass_y =
                    (int) NIVision.imaqMeasureParticle(this.currentImage,
                        particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
                par.imageWidth =
                    NIVision.imaqGetImageSize(this.currentImage).width;
                particles.add(par);
                }
            particles.sort(null);

            }
        this.reports = new ParticleReport[particles.size()];
        particles.copyInto(this.reports);
        }
}
