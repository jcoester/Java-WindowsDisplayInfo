# Java-WindowsDisplayInfo
Standalone Java Class to retrieve Windows resolution(s), aspect ratios and scale factor

## Setup 
Add library: ****net.java.dev.jna.platform:Release**** *(through Maven or similar)*
Add file to src: [**WindowsDisplayInfo.java**](/src/WindowsDisplay.java)

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
    Scale ( int% )  	: 100.0
    Native          	: 1920 x 1080 (16:9)
    Adjusted        	: 1920 x 1080 (16:9)
    Maximum         	: 3840 x 2160 (16:9)
    All (Ascending) 	: [800 x 600 (4:3), ..., 3840 x 2160 (16:9)]
    All (Descending)	: [3840 x 2160 (16:9), ..., 800 x 600 (4:3)]

### Example: 3840 x 2160 and 200% scaling 

    Scale (double)  	: 2.0
    Scale ( int% )  	: 200.0
    Native          	: 3840 x 2160 (16:9)
    Adjusted        	: 1920 x 1080 (16:9)
    Maximum         	: 3840 x 2160 (16:9)
    All (Ascending) 	: [800 x 600 (4:3), ..., 3840 x 2160 (16:9)]
    All (Descending)	: [3840 x 2160 (16:9), ..., 800 x 600 (4:3)]

