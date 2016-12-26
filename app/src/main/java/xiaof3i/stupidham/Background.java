package xiaof3i.stupidham;

/**
 * Created by f3i on 10/25/15.
 */
import android.location.GpsSatellite;

import java.lang.reflect.Method;
import android.location.GpsStatus;
import android.util.Log;
import de.robv.android.xposed.*;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.lang.reflect.Modifier;
import java.util.*;




public  class Background implements IXposedHookLoadPackage {


    private static double[] currePoint ={30.534558,114.367200};  //start point & real time point






    private  static int  arrivePoint = 0 ;


    public boolean isArrive(double[] curreP,double[] targetP){
        Log.e("detaX",String.valueOf(curreP[0]*1000000)+"**"+String.valueOf(targetP[0]*1000000)+"*------*"+String.valueOf(curreP[1])+"**"+String.valueOf(targetP[1]));
        //Log.e("detaY",String.valueOf(curreP[1]*1000000-targetP[1]*1000000));

        double deltaX = Math.abs(curreP[0]*1000000 - targetP[0]*1000000);
        double deltaY = Math.abs(curreP[1]*1000000 - targetP[1]*1000000);
        double distance = deltaX+deltaY;
        Log.e("current Distance",String.valueOf(distance));
        if(distance<13){
            return true;
        }else{
            return  false;
        }
    }

    public double[] updateLocation(){
        double[][] locatioPonit ={{30.534558,114.367200}, //0
                {30.533465,114.367200},  //1
                {30.533465,114.367989},  //2
                {30.534558,114.367989}}; //3
        Log.e("*--*",String.valueOf(locatioPonit[0][1]));
        double offset = Math.random()/83555;//84000

        switch (arrivePoint){
            case 0:
                currePoint[0] -= offset;
                Log.e("1~*~2",String.valueOf(currePoint[0])+"*"+String.valueOf(currePoint[1]));
                if(isArrive(currePoint,locatioPonit[1])) {
                    Log.e("i arrive point 2 ah!","haha");
                    arrivePoint = 1;
                    currePoint = locatioPonit[1];
                }
                break;
            case 1:
                currePoint[1] += offset;

                if(isArrive(currePoint,locatioPonit[2])) {
                    arrivePoint = 2;
                    currePoint = locatioPonit[2];
                }
                break;
            case 2:
                currePoint[0] += offset;
                if(isArrive(currePoint,locatioPonit[3])) {
                    arrivePoint = 3;
                    currePoint = locatioPonit[3];
                }
                break;
            case 3:
                currePoint[1] -= offset;

                if(isArrive(currePoint,locatioPonit[0])) {
                    Log.e("3~*~0before",String.valueOf(currePoint[0])+"*"+String.valueOf(currePoint[1]));
                    arrivePoint = 0;
                    currePoint = locatioPonit[0];
                    Log.e("3~*~0after",String.valueOf(currePoint[0])+"*"+String.valueOf(currePoint[1]));

                }
                break;

        }
        return currePoint;




    }



    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {


        if (!lpparam.packageName.equals("com.aipao.hanmoveschool"))
            return;


        //XposedBridge.log("true Loaded app: " + lpparam.packageName);



        XC_MethodHook hookGetLocType = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                super.beforeHookedMethod(param);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                param.setResult(61);

                super.afterHookedMethod(param);

            }
        };

        XC_MethodHook hookGps = new XC_MethodHook() {

            /**
             * android.location.LocationManager类的getGpsStatus方法
             * 其参数只有1个：GpsStatus status
             * Retrieves information about the current status of the GPS engine.
             * This should only be called from the {@link GpsStatus.Listener#onGpsStatusChanged}
             * callback to ensure that the data is copied atomically.
             */
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                GpsStatus gss = (GpsStatus) param.getResult();
                if (gss == null)
                    return;
                Log.e("gps---","hooksuccess");
                Class<?> clazz = GpsStatus.class;
                Method m = null;
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals("setStatus")) {
                        if (method.getParameterTypes().length > 1) {
                            m = method;
                            break;
                        }
                    }
                }

                //access the private setStatus function of GpsStatus
                m.setAccessible(true);

                //make the apps belive GPS works fine now
                int svCount = 5;
                int[] prns = {1, 2, 3, 4, 5};
                float[] snrs = {0, 0, 0, 0, 0};
                float[] elevations = {0, 0, 0, 0, 0};
                float[] azimuths = {0, 0, 0, 0, 0};
                int ephemerisMask = 0x1f;
                int almanacMask = 0x1f;

                //5 satellites are fixed
                int usedInFixMask = 0x1f;

                try {
                    if (m != null) {
                        m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        Log.e("--*--gps", gss.toString());
                        param.setResult(gss);
                    }
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            }
        };




        XC_MethodHook hookLatLng = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                //XposedBridge.log("----latitude  ----:" + param.args[0]);
                //XposedBridge.log("----longitude  ----:" + param.args[1]);
                //Log.e("---LatLngParam---",param.args[0]+"---"+param.args[1]);

                double[] currentlocation = updateLocation();
                param.args[0] = currentlocation[0];
                param.args[1] = currentlocation[1];

                //XposedBridge.log("----latitude  ----:" + param.args[0]);
                //XposedBridge.log("----longitude  ----:" + param.args[1]);
                // Log.e("---LatLngParam---",i+"---"+param.args[i]);
                //Log.e("---LatLngParam---",param.args[0]+"---"+param.args[1]);
                super.beforeHookedMethod(param);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                super.afterHookedMethod(param);

            }
        };


        XposedHelpers.findAndHookMethod("com.baidu.location.BDLocation", lpparam.classLoader, "getLocType",hookGetLocType);
       // XposedHelpers.findAndHookMethod("android.location.LocationManager", lpparam.classLoader, "getGpsStatus",hookGps);
        XposedHelpers.findAndHookConstructor(XposedHelpers.findClass("com.baidu.mapapi.model.LatLng", lpparam.classLoader), double.class ,double.class ,hookLatLng);


    }


}




