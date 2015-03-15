package nczakaria.github.com.monitorsensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyActivity extends Activity implements SensorEventListener{

    private static final String TAG = MyActivity.class.getName();

    private TextView rate;
    private TextView text_acc;
    private TextView text_mag;
    private TextView text_gyr;
    private TextView text_hrt;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;

    private Sensor mHeartRateSensor;
    private Sensor mAccelerator;
    private Sensor mGyroscope;
    private Sensor mMagneticField;

    private SensorManager mSensorManager;
    private CountDownLatch latch;

    private boolean bool_acc;
    private boolean bool_mag;
    private boolean bool_gyr;
    private boolean bool_hrt;

    private DeviceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        client = DeviceClient.getInstance(this);

        latch = new CountDownLatch(1);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                rate = (TextView) stub.findViewById(R.id.rate);
                rate.setText("Reading...");

                text_acc = (TextView) stub.findViewById(R.id.text_accelerometer);
                text_mag = (TextView) stub.findViewById(R.id.text_magneticField);
                text_gyr = (TextView) stub.findViewById(R.id.text_gyroscope);
                text_hrt = (TextView) stub.findViewById(R.id.text_heartrate);

                latch.countDown();
            }
        });

        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE); // using Sensor Lib2 (Samsung Gear Live)
        mAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // using Sensor Lib2 (Samsung Gear Live)
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // using Sensor Lib2 (Samsung Gear Live)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // using Sensor Lib2 (Samsung Gear Live)

        bool_acc = false;
        bool_gyr = false;
        bool_hrt = false;
        bool_mag = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSensorManager.registerListener(this, this.mHeartRateSensor, 1000000);
        mSensorManager.registerListener(this, this.mAccelerator, 1000000);
        mSensorManager.registerListener(this, this.mMagneticField, 1000000);
        mSensorManager.registerListener(this, this.mGyroscope, 1000000);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            latch.await();

            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                if(!bool_acc){ bool_acc = true; }
                text_acc.setText("ACC, " + System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2]);
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                if(!bool_mag){ bool_mag = true; }
                text_mag.setText("MAG, " + System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2]);
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                if(!bool_gyr){ bool_gyr = true; }
                text_gyr.setText("GYR, " + System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2]);
            }

            if(sensorEvent.sensor.getType() == SENSOR_TYPE_HEARTRATE)
            {
                if(!bool_hrt){ bool_hrt = true; }
                text_hrt.setText("HRT, " + System.currentTimeMillis() + "," + sensorEvent.values[0]);
            }

            if(bool_acc && bool_mag && bool_gyr && bool_hrt)
            {
                rate.setText("Sensing All");
                client.sendSensorData(sensorEvent.sensor.getName(), System.currentTimeMillis(), sensorEvent.values);
            }


        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Log.d(TAG, "accuracy changed: " + i);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }
}
