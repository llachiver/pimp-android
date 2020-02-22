package fr.ubordeaux.pimp.image;

import android.content.Context;

import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import fr.ubordeaux.pimp.util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class to pack some informations about Image object.
 * See {@link Image}.
 */
public class ImageInfo implements Parcelable {
    private String height;
    private String width;
    private String date;
    private String deviceModel;
    private String expositionTime;
    private String focalLength;
    private String ISO;
    private String longitude;
    private String latitude;
    private String path;
    private long weight;
    private String fileName;

    private int loadedHeight;
    private int loadedWidth;


    /**
     * Extract image information from picture File.
     *
     * @param uri Uri of the file
     */
    ImageInfo(Uri uri, Context context) { // friendly because Image only normally use this constructor
        if (uri != null) {
            this.path = Utils.getRealPathFromURI(uri, context);
            File file = new File(this.path);
            this.weight = file.length();
            this.fileName = file.getName();
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                this.height = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                this.width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                this.date = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                this.deviceModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                this.expositionTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                this.focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                this.ISO = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                if (exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) == null || exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) == null)
                    this.longitude = null;
                else
                    this.longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) + "," + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                if (exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) == null || exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) == null)
                    this.latitude = null;
                else
                    this.latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "," + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Construct from antother pack of information
     *
     * @param original ImageInfo to copy
     */
    ImageInfo(ImageInfo original) {
        this.path = original.path;
        this.height = original.height;
        this.width = original.width;
        this.date = original.date;
        this.deviceModel = original.deviceModel;
        this.expositionTime = original.expositionTime;
        this.focalLength = original.focalLength;
        this.ISO = original.ISO;
        this.longitude = original.longitude;
        this.latitude = original.latitude;
        this.weight = original.weight;
        this.fileName = original.fileName;
        this.loadedHeight = original.loadedHeight;
        this.loadedWidth = original.loadedWidth;
    }

    /**
     * @return Original height of the picture file in pixels. This integer is formated as a String. Returns null if the image file doesn't contain the tag information.
     */
    public String getHeight() {
        return height;
    }

    /**
     * @return Original width of the picture file in pixels. This integer is formated as a String. Returns null if the image file doesn't contain the tag information.
     */
    public String getWidth() {
        return width;
    }

    /**
     * @return Size of the picture formated like this "Width x Height", or null if one or both dimension(s) is(are) null.
     */
    public String getSize() {
        if (width == null || height == null)
            return null;
        return width + " x " + height;
    }

    /**
     * @return Return resolution of the device, example : "16.4 Mpx", or null if one or both dimension(s) is(are) null.
     */
    public String getCaptorResolution() {
        if (width == null || height == null)
            return null;
        double resolution = Integer.valueOf(this.height) * Integer.valueOf(this.width) / 1_000_000d;
        return (Math.round(resolution * 10) / 10.0) + " Mpx";
    }

    /**
     * @return Shooting date with this format : "dd/mm/yyyy hh:mm" (french format), or null if date not defined.
     */
    public String getDate() {
        if (date == null) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US); //EXIF tag for date is using 24h format US standard
        Date d;
        try {
            d = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        if (d == null) return null;
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(d); // french format only for the moment.
    }

    /**
     * @return Device model which took the picture, or null it EXIF tag not defined.
     */
    public String getDeviceModel() {
        return deviceModel;
    }


    /**
     * @return Captor aperture time with format "1/vv s", or null if EXIF tag undefined.
     */
    public String getExpositionTime() {
        if (expositionTime == null) return null;
        int count = 1;
        double time = Double.valueOf(expositionTime);
        double value = time;
        while (value < 1.0) {
            value += time;
            count++;
        }
        return "1/" + count + " s";
    }

    /**
     * @return Focal length format "X.x mm", or null if EXIF tag undefined.
     */
    public String getFocalLength() {
        if (focalLength == null) return null;
        String[] split = focalLength.split("/");
        double dist = Double.valueOf(split[0]) / Double.valueOf(split[1]);
        return (Math.round(dist * 10) / 10.0) + " mm";
    }

    /**
     * @return ISO sensibility with format "ISO xxx", or null if EXIF tag undefined.
     */
    public String getISO() {
        if (ISO == null) return null;
        return "ISO " + ISO;
    }

    /**
     * @return GPS longitude numerical value, may be 0 if undefined.
     */
    public double getLongitude() {
        if (longitude == null) return 0;
        String[] split = longitude.split("[,/]");
        int sign = split[6].charAt(0) == 'E' ? 1 : -1;
        return sign * ((Double.valueOf(split[0]) / Double.valueOf(split[1])) +
                (Double.valueOf(split[2]) / (Double.valueOf(split[3]) * 60d)) +
                (Double.valueOf(split[4]) / (Double.valueOf(split[5]) * 3600d))
        );
    }

    /**
     * @return GPS latitude numerical value, may be 0 if undefined.
     */
    public double getLatitude() {
        if (latitude == null) return 0;
        String[] split = latitude.split("[,/]");
        int sign = split[6].charAt(0) == 'N' ? 1 : -1;
        return sign * ((Double.valueOf(split[0]) / Double.valueOf(split[1])) +
                (Double.valueOf(split[2]) / (Double.valueOf(split[3]) * 60d)) +
                (Double.valueOf(split[4]) / (Double.valueOf(split[5]) * 3600d))
        );
    }

    /**
     * @return GPS coordinates with format "XX°XX'XX"N XX°XX'XX"W" or null if latitude or longitude undefined.
     */
    public String getCoordinates() {
        if (latitude == null || longitude == null) return null;
        String[] splitLa = latitude.split("[,/]");
        String[] splitLo = longitude.split("[,/]");
        int degLa = (int) Math.round(Double.valueOf(splitLa[0]) / Double.valueOf(splitLa[1]));
        int minLa = (int) Math.round(Double.valueOf(splitLa[2]) / Double.valueOf(splitLa[3]));
        int secLa = (int) Math.round(Double.valueOf(splitLa[4]) / Double.valueOf(splitLa[5]));
        int degLo = (int) Math.round(Double.valueOf(splitLo[0]) / Double.valueOf(splitLo[1]));
        int minLo = (int) Math.round(Double.valueOf(splitLo[2]) / Double.valueOf(splitLo[3]));
        int secLo = (int) Math.round(Double.valueOf(splitLo[4]) / Double.valueOf(splitLo[5]));
        return degLa + "°" + minLa + "\'" + secLa + "\"" + splitLa[6] + " " + degLo + "°" + minLo + "\'" + secLo + "\"" + splitLo[6];
    }

    /**
     * @return File path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return File size with format "XX unit", with unit like B, kB, MB or GB. Mat return "0 B" if undefined.
     */
    public String getFileSize() {
        double size = weight;
        if (size < 100)
            return (int) size + " B";
        size /= 1024d;
        if (size < 100)
            return (Math.round(size * 100) / 100.0) + " kB";
        size /= 1024d;
        if (size < 100)
            return (Math.round(size * 100) / 100.0) + " MB";
        size /= 1024d;
        return (Math.round(size * 100) / 100.0) + " GB";
    }

    /**
     * @return File name, example "my_picture.jpg"
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Loaded height can be set with {@link #setLoadedHeight(int)}, see also {@link Image#getHeight()}.
     *
     * @return Height of the Image currently loaded in the app
     */
    public int getLoadedHeight() {
        return loadedHeight;
    }

    /**
     * @param height New height value
     */
    void setLoadedHeight(int height) {// friendly access, work with Image
        loadedHeight = height;
    }

    /**
     * Loaded width can be set with {@link #setLoadedWidth(int)} (int)}, see also {@link Image#getWidth()} ()}.
     *
     * @return Width of the Image currently loaded in the app
     */
    public int getLoadedWidth() {
        return loadedWidth;
    }

    /**
     * @param width New width value
     */
    void setLoadedWidth(int width) {// friendly access, work with Image
        loadedWidth = width;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(height);
        dest.writeString(width);
        dest.writeString(date);
        dest.writeString(deviceModel);
        dest.writeString(expositionTime);
        dest.writeString(focalLength);
        dest.writeString(ISO);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(path);
        dest.writeLong(weight);
        dest.writeString(fileName);
        dest.writeInt(loadedHeight);
        dest.writeInt(loadedWidth);
    }

    /**
     * Generated to use {@link Parcelable} interface
     *
     * @param in Parcel
     */
    protected ImageInfo(Parcel in) {
        height = in.readString();
        width = in.readString();
        date = in.readString();
        deviceModel = in.readString();
        expositionTime = in.readString();
        focalLength = in.readString();
        ISO = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        path = in.readString();
        weight = in.readLong();
        fileName = in.readString();
        loadedHeight = in.readInt();
        loadedWidth = in.readInt();
    }

    /**
     * Generated to use {@link Parcelable} interface
     */
    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
