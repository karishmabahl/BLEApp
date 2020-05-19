package com.example.karis.projproj;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_ENABLE_CODE = 1;
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private ToggleButton btOnOff, locOnOff;
    private Button scanButton;
    private BluetoothLeScanner bluetoothLeScanner;
    ScanCallback scanCallback;
    ScanSettings scanSettings;
    List<ScanFilter> scanFilter = null;
    String GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb"; //uuid of 'generic access' service, this service contains device name (UUID- 00002a00-0000-1000-8000-00805f9b34fb, Property- READ, Value- MAP, Hex- 0x4D4150)
    String bleAddressIT = "5C:31:3E:86:28:F9";
    String bleAddressCOM = "3C:A3:08:0D:2F:45";
    String[] bleNames = new String[]{"IT","COM"};
    String[] bleAddr = new String[]{"5C:31:3E:86:28:F9","3C:A3:08:0D:2F:45"};
    //String[] bleNamesFound;
    //String[] bleAddrFound;
    int imageForList = R.drawable.bt96;
    ListView listView;

    int t = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Check if device supports BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter i.e the device's own Bluetooth radio.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //Toggle Button to enable or disable Bluetooth
        btOnOff = (ToggleButton) findViewById(R.id.toggleButton_scanActivity);
        if(bluetoothAdapter.isEnabled())
        {
            btOnOff.setChecked(true);
        }
        btOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!bluetoothAdapter.enable()) {
                        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                    }
                }
                else{
                    bluetoothAdapter.disable();
                }
            }
        });

        //turn on location services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_ENABLE_CODE);
            }

        //instantiate a BluetoothLeScanner object to use the Low energy scan operations
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        //Create reference to Scan Button
        scanButton = (Button) findViewById(R.id.button_scanActivity);
        //Call the Scan Function
        clickOnScan();
        //create reference to ListView and assign the adapter
        listView = (ListView) findViewById(R.id.listView_scanActivity);
        ScanListAdapter scanListAdapter = new ScanListAdapter(ScanActivity.this, bleNames, bleAddr, imageForList);
        listView.setAdapter(scanListAdapter);


    }

    private void clickListView(final String devName){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(bleNames[position].equals(devName)){

                   Intent goToDetail = new Intent(ScanActivity.this, SelectedDeviceActivity.class);
                   String msg = bleNames[position];
                   goToDetail.putExtra("device", msg);
                   startActivity(goToDetail);

                }
                else {
                    Log.e("Bubba", bleNames[position] + " and " + devName);
                    Toast toast = Toast.makeText(getApplicationContext(), bleNames[position] + " not in proximity.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void clickOnScan(){
        //<<<ADDITIONAL NOTE- I may not have to connect to the GATT server since I can only rely on the device names such as Xblock and Yblock,
        // the only constraint will be that the two sensors aren't scanned at once.>>>
        //creation of settings on the basis of which a ble d evice should be detected
        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();
        //creation of settings on the basis of which a ble device should be detected
        if(bleNames != null){
            scanFilter = new ArrayList<>();
            for(String name : bleNames){
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceName(name)
                        .build();
                scanFilter.add(filter);
            }
        }


        //creation of scanCallback object, this will get me the result of my scan
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                //Log.e("Bubba",device.toString());
                //String addr = device.toString();
                //if (addr.equals(bleAddressCOM)) {pos = 2;} else if (addr.equals(bleAddressIT)) { pos = 1;}
                String name = device.getName();
                while(t<1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "The device " + name + " is in your range. Click on " +
                            name + " to continue.", Toast.LENGTH_LONG);
                    toast.show();
                    t++;
                }
                clickListView(name);


                Log.i(" Bubba on Scan Result ", "On scan result " + device.getName());



            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.i(" Bubba BatchResult: ", "Batch Result");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.i(" Bubba ScanFailed: ", "Scan Failed error code: " + errorCode);

            }
        };

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled()) {
                    bluetoothLeScanner.stopScan(scanCallback);
                    bluetoothLeScanner.flushPendingScanResults(scanCallback);
                    bluetoothLeScanner.startScan(scanFilter, scanSettings, scanCallback);
                }else {Toast.makeText(getApplicationContext(),"Turn on Bluetooth before scanning.",Toast.LENGTH_LONG).show();}
            }
        });

    }
}
