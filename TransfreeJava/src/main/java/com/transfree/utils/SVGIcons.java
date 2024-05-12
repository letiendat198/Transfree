package com.transfree.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class SVGIcons {
    public SVGPath appleIcon = new SVGPath();
    public SVGPath linuxIcon = new SVGPath();

    public static SVGPath getWindowsIcon(){
        SVGPath windowsIcon = new SVGPath();
        windowsIcon.setContent("M0 12.402l35.687-4.86.016 34.423-35.67.203zm35.67 33.529l.028 34.453L.028 75.48.026 45.7zm4.326-39.025L87.314 0v41.527l-47.318.376zm47.329 39.349l-.011 41.34-47.318-6.678-.066-34.739z");
        windowsIcon.setFill(Color.web("#00adef"));
        return windowsIcon;
    }
    public static SVGPath getAndroidIcon(){
        SVGPath androidIcon = new SVGPath();
        androidIcon.setContent("M930.77,536.42a53.07,53.07,0,1,1,53.06-53.08,53.14,53.14,0,0,1-53.06,53.08m-586.54,0a53.07,53.07,0,1,1,53.06-53.08,53.13,53.13,0,0,1-53.06,53.08M949.8,216.77,1055.85,33.09A22.06,22.06,0,1,0,1017.64,11L910.25,197c-82.12-37.48-174.35-58.35-272.76-58.35S446.86,159.55,364.74,197L257.36,11a22.06,22.06,0,1,0-38.22,22.06L325.2,216.77C143.09,315.82,18.53,500.18.31,718H1274.69c-18.24-217.82-142.79-402.18-324.89-501.23");
        androidIcon.setFill(Color.web("#3DDC84"));
        return androidIcon;
    }
    public static SVGPath getAppleIcon(){
        SVGPath appleIcon = new SVGPath();
        appleIcon.setContent("M788.1 340.9c-5.8 4.5-108.2 62.2-108.2 190.5 0 148.4 130.3 200.9 134.2 202.2-.6 3.2-20.7 71.9-68.7 141.9-42.8 61.6-87.5 123.1-155.5 123.1s-85.5-39.5-164-39.5c-76.5 0-103.7 40.8-165.9 40.8s-105.6-57-155.5-127C46.7 790.7 0 663 0 541.8c0-194.4 126.4-297.5 250.8-297.5 66.1 0 121.2 43.4 162.7 43.4 39.5 0 101.1-46 176.3-46 28.5 0 130.9 2.6 198.3 99.2zm-234-181.5c31.1-36.9 53.1-88.1 53.1-139.3 0-7.1-.6-14.3-1.9-20.1-50.6 1.9-110.8 33.7-147.1 75.8-28.5 32.4-55.1 83.6-55.1 135.5 0 7.8 1.3 15.6 1.9 18.1 3.2.6 8.4 1.3 13.6 1.3 45.4 0 102.5-30.4 135.5-71.3z");
        appleIcon.setFill(Color.BLACK);
        return appleIcon;
    }
    public static SVGPath getLinuxIcon(){
        SVGPath linuxIcon = new SVGPath();
        linuxIcon.setContent("M0 12.402l35.687-4.86.016 34.423-35.67.203zm35.67 33.529l.028 34.453L.028 75.48.026 45.7zm4.326-39.025L87.314 0v41.527l-47.318.376zm47.329 39.349l-.011 41.34-47.318-6.678-.066-34.739z");
        linuxIcon.setFill(Color.web("#00adef"));
        return linuxIcon;
    }
}
