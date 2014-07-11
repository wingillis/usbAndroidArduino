package com.example.wgillis.arduinoomm;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {

    private UsbManager usbManager;
    private TextView textView;
    private Button button;
    UsbSerialPort port;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        textView = (TextView) findViewById(R.id.text_wow);
        button = (Button) findViewById(R.id.open_port);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

// Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }

// Read some data! Most have just one port (port 0).
        port = driver.getPorts().get(0);
        try {
            port.open(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            byte[] buffer = new byte[16];
            int numBytesRead = port.read(buffer, 1000);
            Log.d("TAG:", "Read " + numBytesRead + " bytes.");
            textView.setText(new String(buffer, "UTF-8"));
        } catch (IOException e) {
            // Deal with error.
            Log.e("TSG", "MSG");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    byte[] buffer = new byte[16];
                    port.read(buffer, 1000);
                    textView.setText(new String(buffer, "UTF-8"));
                } catch (IOException e) {

                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
