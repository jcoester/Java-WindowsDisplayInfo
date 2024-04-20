import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>This <code>WindowsDisplayInfo</code> class retrieves the current <code>Windows Resolution(s)</code>, <code>Aspect ratio(s)</code>,
 * and <code>Scale factor</code> throughout a <code>Java</code> applications' runtime.</p>
 *
 * <p>It uses <code>Java AWT</code> and the <code>Java Native Access (JNA) platform</code>
 * to access the <code>Display Device Context (DC)</code> from <code>Microsoft
 * Windows graphics device interface (GDI)</code></p>
 *
 * @implNote Requires <code>net.java.dev.jna.platform (Maven)</code>
 * <p>Full support for <code>Java 8</code>: Value updates throughout runtime</p>
 * <p>Partial support for <code>Java 11+</code>: Value from application startup</p>
 *
 * @author     <a href="https://github.com/jcoester/Java-WindowsDisplayInfo/">jcoester</a>
 * @version    1.1 (2024 Apr. 20)
 */
public class WindowsDisplayInfo {

    public static void main(String[] args) {
        // For demonstration: Check Windows Display every 5 seconds
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(WindowsDisplayInfo::update, 0, 5, TimeUnit.SECONDS);
    }

    private static void update() {
        // Usage
        System.out.println("Scale (double)  : " + retrieveScaleFactor()); // e.g. 1.0, 1.25, 1.5, 2.0
        System.out.println("Scale ( int% )  : " + retrieveScaleFactorPercentage()); // e.g. 100%, 125%, 150%, 200%
        System.out.println("Native          : " + retrieveNativeResolution()); // Native
        System.out.println("Adjusted        : " + retrieveAdjustedResolution()); // Native, adjusted by scale factor
        System.out.println("Maximum         : " + retrieveMaximumResolution()); // Maximum
        System.out.println("All (Ascending) : " + retrieveAllScreenResolutions(true)); // List of all, sorted smallest to largest
        System.out.println("All (Descending): " + retrieveAllScreenResolutions(false)); // List of all, sorted largest to smallest
        System.out.println(); // Empty line for easier readability during demonstration
    }

    public static double retrieveScaleFactor() {

        /* Retrieve AWT scale
         * (Detects the initial Windows Display Scale during application startup) */
        double awtScale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;

        // Retrieve HDC (Handle to Device Context (DC))
        WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(null);
        if (hdc == null)
            return 0;

        /* Retrieve HDC Display info
         * (Detects changes to the Windows Display Scale during the application runtime) */
        double a = GDI32.INSTANCE.GetDeviceCaps(hdc, 10); // 10=VERTRES
        double b = GDI32.INSTANCE.GetDeviceCaps(hdc, 117); // 117=DESKTOPVERTRES
        GDI32.INSTANCE.DeleteDC(hdc);
        if (a == 0 || b == 0)
            return 0;

        // Offset HDC with the initial AWT scale
        b = b * awtScale;

        // Calculate Windows Display Scale
        double windowsDisplayScale = a > b ? a / b : b / a;

        // Round to two decimals and return
        return BigDecimal.valueOf(windowsDisplayScale).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static int retrieveScaleFactorPercentage() {

        /* Retrieve AWT scale
         * (Detects the initial Windows Display Scale during application startup) */
        double awtScale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;

        // Retrieve HDC (Handle to Device Context (DC))
        WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(null);
        if (hdc == null)
            return 0;

        /* Retrieve HDC Display info
         * (Detects changes to the Windows Display Scale during the application runtime) */
        double a = GDI32.INSTANCE.GetDeviceCaps(hdc, 10); // 10=VERTRES
        double b = GDI32.INSTANCE.GetDeviceCaps(hdc, 117); // 117=DESKTOPVERTRES
        GDI32.INSTANCE.DeleteDC(hdc);
        if (a == 0 || b == 0)
            return 0;

        // Offset HDC with the initial AWT scale
        b = b * awtScale;

        // Calculate Windows Display Scale
        double windowsDisplayScale = a > b ? a / b : b / a;

        // Round to two decimals and return
        double scaleDecimal = BigDecimal.valueOf(windowsDisplayScale).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return (int) (scaleDecimal * 100);
    }

    public static Resolution retrieveNativeResolution() {
        // Retrieve HDC (Handle to Device Context (DC))
        WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(null);
        if (hdc == null)
            return null;

        /* Retrieve HDC Display info
         * (Detects changes to the Windows Display Scale during the application runtime) */
        double a = GDI32.INSTANCE.GetDeviceCaps(hdc, 118); // 118=DESKTOPHORZRES
        double b = GDI32.INSTANCE.GetDeviceCaps(hdc, 117); // 117=DESKTOPVERTRES

        GDI32.INSTANCE.DeleteDC(hdc);
        if (a == 0 || b == 0)
            return null;

        return new Resolution((int) a, (int) b, determineAspectRatio((int) a, (int) b));
    }

    public static Resolution retrieveEffectiveResolution() {

        /* Retrieve AWT scale
         * (Detects the Windows Display Scale once during application startup) */
        double awtScale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;

        // Retrieve HDC (Handle to Device Context (DC))
        WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(null);
        if (hdc == null)
            return null;

        /* Retrieve HDC Display info
         * (Detects changes to the Windows Display Scale during the application runtime) */
        double a = GDI32.INSTANCE.GetDeviceCaps(hdc, 8); // 8=HORZRES
        double b = GDI32.INSTANCE.GetDeviceCaps(hdc, 10); // 10=VERTRES

        GDI32.INSTANCE.DeleteDC(hdc);
        if (a == 0 || b == 0)
            return null;

        // Offset HDC with the initial AWT scale
        a = a / awtScale;
        b = b / awtScale;

        return new Resolution((int) a, (int) b, determineAspectRatio((int) a, (int) b));
    }

    public static Resolution retrieveAdjustedResolution() {
        Resolution effective = retrieveEffectiveResolution();
        List<Resolution> allResolutions = retrieveAllScreenResolutions(true);

        if (allResolutions.contains(effective)) // If effective is valid, return
            return effective;

        for (Resolution res : allResolutions) { // If effective is not valid, look for closest larger match
            if (effective != null && res.getAspectRatio().equals(effective.getAspectRatio())) // Must be same aspect ratio
                if (res.getWidth() >= effective.getWidth()) // First matching item
                    return res;
        }

        return retrieveNativeResolution(); // Fallback: Return Native
    }

    public static Resolution retrieveMaximumResolution() {
        List<Resolution> resolutions = retrieveAllScreenResolutions(false);
        return resolutions.isEmpty() ? null : resolutions.get(0);
    }

    public static List<Resolution> retrieveAllScreenResolutions(boolean ascending) {
        Set<Resolution> resolutionSet = new HashSet<>(); // set for unique resolutions
        GraphicsDevice dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]; // [0] main display

        for (DisplayMode m : dev.getDisplayModes())
            if (m.getWidth() >= 800 && m.getHeight() >= 600) // Minimum resolution available in Windows 11
                resolutionSet.add(new Resolution(m.getWidth(), m.getHeight(), determineAspectRatio(m.getWidth(), m.getHeight())));

        List<Resolution> sortResolutions = new ArrayList<>(resolutionSet);
        sortResolutions.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());

        return sortResolutions;
    }

    /**
     * <p>Returns the aspect ratio of given width and height in "X:Y"-format </p>
     * <p>
     * e.g. <code>1024 x 768</code> > <code>4:3</code><br>
     * e.g  <code>1920 x 1080</code> > <code>16:9</code><br>
     * e.g. <code>3440 x 1440</code> > <code>21:9</code>
     * </p>
     * <p>This also works for <code>1366 x 768</code> which is mathematically not <code>16:9</code> but marketed as such.</p>
     *
     * @param width         e.g. 1920
     * @param height        e.g. 1080
     * @return aspectRatio  "16:9"
     */
    private static String determineAspectRatio(int width, int height) {

        // Define aspect ratios and calculate their decimal conversions
        // List from https://en.wikipedia.org/wiki/Display_aspect_ratio
        List<String> ratios = Arrays.asList("1:1", "5:4", "4:3", "3:2", "16:10", "16:9", "17:9", "21:9", "32:9");
        List<Double> ratiosDecimals = ratioDecimalFromStrings(ratios);

        // Calculate absolute difference between given the resolution's aspect ratio and the defined list of aspect ratios
        double ratioArgs = (double) width / height;
        List<Double> ratiosDiffs = new ArrayList<>();
        for (Double ratioDec : ratiosDecimals) {
            ratiosDiffs.add(Math.abs(ratioArgs - ratioDec));
        }

        // Determine and return closest available aspect ratio
        int index = ratiosDiffs.indexOf(Collections.min(ratiosDiffs));
        return ratios.get(index);
    }

    private static List<Double> ratioDecimalFromStrings(List<String> ratios) {
        List<Double> ratiosDecimals = new LinkedList<>();
        for (String ratio : ratios) {
            int w = Integer.parseInt(ratio.split(":")[0]);
            int h = Integer.parseInt(ratio.split(":")[1]);
            ratiosDecimals.add((double) w / h);
        }
        return ratiosDecimals;
    }

    public static class Resolution implements Comparable<Resolution> {
        private final int width;
        private final int height;
        private final String aspectRatio;

        public Resolution(int width, int height, String aspectRatio) {
            this.width = width;
            this.height = height;
            this.aspectRatio = aspectRatio;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getAspectRatio() {
            return aspectRatio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Resolution that = (Resolution) o;
            return width == that.width && height == that.height && Objects.equals(aspectRatio, that.aspectRatio);
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height, aspectRatio);
        }

        @Override
        public int compareTo(Resolution res) {
            return Comparator
                    .comparingInt(Resolution::getWidth)
                    .thenComparingInt(Resolution::getHeight)
                    .compare(this, res);
        }

        @Override
        public String toString() {
            return width + " x " + height + " (" + aspectRatio + ")";
        }
    }
}