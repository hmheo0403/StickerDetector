/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cfd.map.mohit.locationalarm.locationalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraFocus;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Keval on 11-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class DemoCamService extends HiddenCameraService {
    int index = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "HAHA DemoCamService onStartCommand()", Toast.LENGTH_LONG).show();
        index = intent.getIntExtra("INDEX",0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,
                    "Capturing imagesss.", Toast.LENGTH_LONG).show();
            if (HiddenCameraUtils.canOverDrawOtherApps(this))
            {
                CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .setCameraFocus(CameraFocus.AUTO)
                        .build();

                startCamera(cameraConfig);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoCamService.this,
                                "Capturing image.", Toast.LENGTH_SHORT).show();
//////////
                        takePicture();
                    }
                }, 2000L);
            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        }
        else
            {

            //TODO Ask your parent activity for providing runtime permission
                Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
            }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {

        // Convert file to bitmap.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        // Do something.
        Toast.makeText(this, "HAHA DemoCamActivity onImageCapture()", Toast.LENGTH_LONG).show();

        if(!isDark(imageFile,bitmap))
        {
            Toast.makeText(this, "HAHA DemoCamActivity onImageCapture() is Dark?: NO Play Alarm", Toast.LENGTH_LONG).show();
            playAlarm(index);
        }
        else
        {
            Toast.makeText(this, "HAHA DemoCamActivity onImageCapture() is Dark?: Yes Sticker nicely applied! ", Toast.LENGTH_LONG).show();

        }
        //Display the image to the image view ee

        stopSelf();
    }
    public void playAlarm(int pos) {

        AlarmDatabase alarmDatabase = new AlarmDatabase(getApplicationContext());
        ArrayList<GeoAlarm> geoAlarms;
        geoAlarms = alarmDatabase.getAllData();
        Intent intent1 = new Intent(getApplicationContext(),AlarmScreenActivity.class);
        intent1.putExtra("geoAlarm",geoAlarms.get(pos));
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
    }

    private boolean isDark(@NonNull File imageFile, Bitmap bitmap)
    {


        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                // does alpha matter?
            }
        }

//        int averageColor = Color.rgb(redBucket / pixelCount,
//                greenBucket / pixelCount,
//                blueBucket / pixelCount);
        int avgRed = redBucket/pixelCount;
        int avgGreen = greenBucket/pixelCount;
        int avgBlue = blueBucket/pixelCount;
        int intBrightness = calcBrightness(avgRed, avgGreen, avgBlue);

        if(intBrightness <= 40)
            return true;
        return  false;
    }

    private int calcBrightness(int red, int green, int blue)
    {
        int brightness = (int) Math.sqrt(
                red * red * .241 +
                        green * green * .691 +
                        blue * blue * .068);
        Toast.makeText(this, "HAHA DemoCamActivity calcBrightness(): "+brightness, Toast.LENGTH_LONG).show();
        return brightness;
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
//                Toast.makeText(this, R.string.error_cannot_open, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
//                Toast.makeText(this, R.string.error_cannot_write, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
//                Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
//                Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
                break;
        }

        stopSelf();
    }
}
