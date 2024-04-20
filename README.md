# Java-WindowsDisplayInfo
Self-contained Java Class to retrieve Windows Display Resolution(s), Aspect ratios and Scale factor

## Setup 
- Add class to src: [**WindowsDisplayInfo.java**](/src/WindowsDisplayInfo.java)
- Add library: ****net.java.dev.jna.platform:Release**** *(through Maven or similar)*


## Usage
    System.out.println("Scale (double)  : " + retrieveScaleFactor());
    System.out.println("Scale ( int% )  : " + retrieveScaleFactorPercentage());
    System.out.println("Native          : " + retrieveNativeResolution());
    System.out.println("Adjusted        : " + retrieveAdjustedResolution());
    System.out.println("Maximum         : " + retrieveMaximumResolution());
    System.out.println("All (Ascending) : " + retrieveAllScreenResolutions(true)); 
    System.out.println("All (Descending): " + retrieveAllScreenResolutions(false));

## Output
### Example: 1920 x 1080 and 100% scaling 
    Scale (double) 	: 1.0
    Scale ( int% )  : 100
    Native          : 1920 x 1080 (16:9)
    Adjusted        : 1920 x 1080 (16:9)
    Maximum         : 3840 x 2160 (16:9)
    All (Ascending) : [800 x 600 (4:3), ..., 3840 x 2160 (16:9)]
    All (Descending): [3840 x 2160 (16:9), ..., 800 x 600 (4:3)]

### Example: 2560 x 1440 and 150% scaling 
    Scale (double)  : 1.5
    Scale ( int% )  : 150
    Native          : 2560 x 1440 (16:9)
    Adjusted        : 1920 x 1080 (16:9)
    Maximum         : 3840 x 2160 (16:9)
    All (Ascending) : [800 x 600 (4:3), ..., 3840 x 2160 (16:9)]
    All (Descending): [3840 x 2160 (16:9), ..., 800 x 600 (4:3)]

### Example: 3840 x 2160 and 200% scaling 
    Scale (double)  : 2.0
    Scale ( int% )  : 200
    Native          : 3840 x 2160 (16:9)
    Adjusted        : 1920 x 1080 (16:9)
    Maximum         : 3840 x 2160 (16:9)
    All (Ascending) : [800 x 600 (4:3), ..., 3840 x 2160 (16:9)]
    All (Descending): [3840 x 2160 (16:9), ..., 800 x 600 (4:3)]
