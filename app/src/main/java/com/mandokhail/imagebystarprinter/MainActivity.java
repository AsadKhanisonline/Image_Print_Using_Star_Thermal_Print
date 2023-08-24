package com.mandokhail.imagebystarprinter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mandokhail.imagebystarprinter.Printer.PrintContentStar;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView ivDisplay;
    Button btnPrint;
    Bitmap bitmapCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivDisplay = findViewById(R.id.imageView);
        btnPrint = findViewById(R.id.button);

        CreateBitmap();
        CheckPermissions();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintSlip();
            }
        });

    }

    private void CreateBitmap() {
        try {
            int pageWith = 620;
            int pageHight = 400;

            //Bitmap width height
            bitmapCanvas = Bitmap.createBitmap(pageWith, pageHight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCanvas);
            canvas.drawColor(Color.WHITE);

            try {
                //Image
                Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tech);
                Bitmap myBitmapTrim = getResizedBitmap(myBitmap, 120, 120);
                int startX = (canvas.getWidth() - myBitmapTrim.getWidth()) / 2;//for horizontal position
                canvas.drawBitmap(myBitmapTrim, startX, 120, null);
            } catch (Exception e) {
            }
            int hightFromTop = 280;
            Paint paintTitle = new Paint();
            paintTitle.setColor(Color.BLACK);
            paintTitle.setTextSize(32);
            paintTitle.setTextAlign(Paint.Align.CENTER);
            paintTitle.setTypeface(Typeface.SANS_SERIF);
            paintTitle.setFakeBoldText(true);

            //Title
            // 310 is half of page width
            canvas.drawText("Hi! Good Day.", 310, hightFromTop, paintTitle);

            ivDisplay.setImageBitmap(bitmapCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void CheckPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 2);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 4);
        }
    }

    private void PrintSlip() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 2);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 3);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 4);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 5);
        } else {
            if (bitmapCanvas != null) {
                print(bitmapCanvas);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void print(Bitmap bitmap) {
        try {
            String portName = null;
            if (bitmap != null) {
                try {

                    List<PortInfo> portInfos = StarIOPort.searchPrinter("BT:", this);

                    for (int i = 0; i < portInfos.size(); i++) {

                  /*  Map<String, String> map = new HashMap<>();
                    map.put(INTERFACE_TYPE_KEY, portInfos.get(i).getPortName());
                    map.put(IDENTIFIER_KEY, portInfos.get(i).getMacAddress());
                    map.put(DEVICE_NAME_KEY, portInfos.get(i).getModelName());*/


                        portName = portInfos.get(i).getPortName();
//                        macaddress = portInfos.get(i).getMacAddress();

                    }
//            mAdapter.notifyDataSetChanged();
                } catch (StarIOPortException e) {
                    e.printStackTrace();
                }
                StarIOPort port = null;
                try {
                    // Port open

                    port = StarIOPort.getPort(portName, "", 10000, this);


                    // Print end monitoring -Start

                    StarPrinterStatus status = port.beginCheckedBlock();

                  /*  byte[] command7 = PrintContentStar.getTestPage();
                    // Send print data
                    port.writePort(command7, 0, command7.length);*/

                    byte[] command8 = PrintContentStar.printBitmap(PrintContentStar.PAPER_SIZE_THREE_INCH, true, bitmap);

                    // Send print data
                    port.writePort(command8, 0, command8.length);


//                port.writePort(command22, 0, command.length);

                    // Print end monitoring -End
                    status = port.endCheckedBlock();

                    // Status judgment during printing completion monitoring
                    if (status.offline == false) {
                        // Print successful end (Printer Online)
                    } else {
                        // Printing is abnormal termination (no paper, printer cover open etc)
                        // Notify user
                    }
                } catch (StarIOPortException e) {
                    // Error

                } finally {
                    try {
                        // Port close
                        StarIOPort.releasePort(port);
                    } catch (StarIOPortException e) {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}