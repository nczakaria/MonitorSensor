package nczakaria.github.com.monitorsensor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;


public class MyActivity extends Activity{

    private RemoteSensorManager remoteSensorManager;
    private TextView mainText;
    private TextView saveText;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        remoteSensorManager = RemoteSensorManager.getInstance(this);

        mainText = (TextView) findViewById(R.id.mainText);
        saveText = (TextView) findViewById(R.id.saveText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mainText.setText(remoteSensorManager.getResult());

            counter ++;
            String filename = "/sdcard/dataCollection/" + counter + "_raw.csv";

            // write on SD card file data in the text box
            try {
                File myFile = new File(filename);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(remoteSensorManager.getResult());
                myOutWriter.close();
                fOut.close();
                String message =  "Success: " + filename;
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

                remoteSensorManager.resetResult("");
                saveText.setText("Save counter: " + counter);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteSensorManager.startMeasurement();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteSensorManager.stopMeasurement();
    }
}
