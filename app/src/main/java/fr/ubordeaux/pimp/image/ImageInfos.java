package fr.ubordeaux.pimp.image;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.Date;

/**
 * Class to pack some informations get from picture file.
 */
public class ImageInfos {
    private int height;
    private int width;
    private double captorResolution;
    private double weight;
    private Date date;
    private String deviceModel;
    private double maxAperture;
    private double aperture;
    private double focalLength;
    private int ISO;
    private String longitude;
    private String latitude;
    private String path;

    /**
     * Extract image information from picture File.
     *
     * @param uri Uri of the file
     */
    public ImageInfos(Uri uri) {
        try {
            assert (uri.getPath() != null);
            ExifInterface exifInterface = new ExifInterface(uri.getPath()); // TODO
            Log.v("LOG", exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + " height");
            Log.v("LOG", exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH) + "focal width");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Original height of the picture file in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Original height of the picture file in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Return resolution of the device which took this picture (Mpx)
     */
    public double getCaptorResolution() {
        return captorResolution;
    }

    /**
     * @return File size (Mo)
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return Shooting date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return Device model which took the picture
     */
    public String getDeviceModel() {
        return deviceModel;
    }

    /**
     * @return Maximale aperture, like "f/1,8"
     */
    public double getMaxAperture() {
        return maxAperture;
    }

    /**
     * @return Captor aperture time (s)
     */
    public double getAperture() {
        return aperture;
    }

    /**
     * @return Focal length (mm)
     */
    public double getFocalLength() {
        return focalLength;
    }

    /**
     * @return ISO sensibility
     */
    public int getISO() {
        return ISO;
    }

    /**
     * @return GPS longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @return GPS latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @return File path
     */
    public String getPath() {
        return path;
    }
}
