package xiaof3i.stupidham;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by f3i on 10/28/15.
 */



public  class Location {
    public static List<double[]> location = new ArrayList();
    static int counter = 0;
    public static int isInit = 0;

    public static void  initLocation(){
        if (isInit!=1) {
            location.add(new double[]{114.367226,30.533552});
            location.add(new double[]{114.367226, 30.533552});

            XposedBridge.log("--------------location dict inited!----------------");
            Log.e("location dict inited", String.valueOf(isInit));
            isInit = 1;

        }
    }


    public static double[] updateLocation(){

        try {
            Thread.sleep(2100);

        } catch (java.lang.InterruptedException ignore) {

            XposedBridge.log("--------------java.lang.InterruptedException----------------");
        }

        double[] currentLocation = {114.367972,30.533575};
        try {
            currentLocation =  location.get(counter);
        }catch (java.lang.NullPointerException ignore){
            XposedBridge.log("--------------updatelocation out of bound----------------");

        }
        counter =(counter+1)%54;

        return currentLocation;




    }
}
