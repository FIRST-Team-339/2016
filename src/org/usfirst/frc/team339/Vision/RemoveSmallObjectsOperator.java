package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

public class RemoveSmallObjectsOperator implements VisionOperatorInterface
{

private final int erosions;
private final boolean connectivity8;

public RemoveSmallObjectsOperator (int iterations, boolean connectivity8)
    {
    this.erosions = iterations;
    this.connectivity8 = connectivity8;
    }

@Override
    public Image operate (Image Source)
        {
        try
            {
            if (this.connectivity8 == true)
                {
            // final Image newImage =
            // NIVision.imaqCreateImage(ImageType.IMAGE_U8, 1);
                // NIVision.imaqDuplicate(newImage, Source);
                NIVision.imaqSetBorderSize(Source, 1);

                NIVision.imaqSizeFilter(Source, Source, 1, this.erosions,
                    NIVision.SizeType.KEEP_LARGE,
                    new NIVision.StructuringElement(3, 3, 0));
                // Source.free();
                System.out.println("Good. Working???");
            // return newImage;
                }
            else
                {
                NIVision.imaqSizeFilter(Source, Source, 0, this.erosions,
                    NIVision.SizeType.KEEP_LARGE,
                    new NIVision.StructuringElement(3, 3, 0));
                System.out.println("Other. Working???");
                }
            }

        catch (final Exception ex)
            {
            ex.printStackTrace();
            }
        return Source;
        }
}
