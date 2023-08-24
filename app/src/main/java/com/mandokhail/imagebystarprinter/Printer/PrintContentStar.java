package com.mandokhail.imagebystarprinter.Printer;


import static com.starmicronics.starioextension.ICommandBuilder.CutPaperAction;
import static com.starmicronics.starioextension.StarIoExt.Emulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;


public class PrintContentStar {

    public static final String TAG = "StarPrinterHelper";

    public static final int PAPER_SIZE_TWO_INCH = 384;
    public static final int PAPER_SIZE_THREE_INCH = 576;
    public static final int PAPER_SIZE_FOUR_INCH = 832;

    public static final Emulation emulation = Emulation.StarGraphic;

    static public Bitmap createBitmapFromText(String printText, int textSize, int printWidth, Typeface typeface) {
        Paint paint = new Paint();
        Bitmap bitmap;
        Canvas canvas;

        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        paint.getTextBounds(printText, 0, printText.length(), new Rect());

        TextPaint textPaint = new TextPaint(paint);
        StaticLayout staticLayout = new StaticLayout(printText, textPaint, printWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        // Create bitmap
        bitmap = Bitmap.createBitmap(staticLayout.getWidth(), staticLayout.getHeight(), Bitmap.Config.ARGB_8888);

        // Create canvas
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.translate(0, 0);
        staticLayout.draw(canvas);

        return bitmap;
    }

    public static Bitmap create3inchRasterReceiptImage() {
        String textToPrint =
                "         OLU THe Boutique\n" +
                        "             123 Elo Road\n" +
                        "           City, State 12345\n" +
                        "\n" +
                        "Date:MM/DD/YYYY          Time:HH:MM PM\n" +
                        "--------------------------------------\n" +
                        "SALE\n" +
                        "SKU            Description       Total\n" +
                        "300678566      PLAIN T-SHIRT     10.99\n" +
                        "300692003      BLACK DENIM       29.99\n" +
                        "300651148      BLUE DENIM        29.99\n" +
                        "300642980      STRIPED DRESS     49.99\n" +
                        "30063847       BLACK BOOTS       35.99\n" +
                        "\n" +
                        "Subtotal                        156.95\n" +
                        "Tax                               0.00\n" +
                        "--------------------------------------\n" +
                        "Total                          $156.95\n" +
                        "--------------------------------------\n" +
                        "\n" +
                        "Charge\n" +
                        "156.95\n" +
                        "Visa XXXX-XXXX-XXXX-0123\n" +
                        "Refunds and Exchanges\n" +
                        "Within 30 days with receipt\n" +
                        "And tags attached\n";

        int      textSize = 25;
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);

        return createBitmapFromText(textToPrint, textSize, PAPER_SIZE_THREE_INCH, typeface);
    }


    public static byte[] createScaleRasterReceiptData(int width, boolean bothScale) {
        byte[] data;

        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        // add text info
        Bitmap image = create3inchRasterReceiptImage();
        builder.appendBitmap(image, false, width, bothScale);

        // add Barcode

        String barcode = "1234567890";
        data = barcode.getBytes();
        builder.appendBarcode(data, ICommandBuilder.BarcodeSymbology.Code93,ICommandBuilder.BarcodeWidth.Mode1, 100, false);

        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        builder.appendBitmap(createBitmapFromText(barcode, 25, PAPER_SIZE_THREE_INCH, typeface), false, width, bothScale);

        builder.appendCutPaper(CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        return builder.getCommands();
    }

    public static byte[] createTextReceiptData(String textToPrint) {
        int      textSize = 25;

        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        Bitmap image = createBitmapFromText(textToPrint, textSize, PAPER_SIZE_THREE_INCH, typeface);
        builder.appendBitmap(image, false, PAPER_SIZE_THREE_INCH, true);
        builder.endDocument();

        return builder.getCommands();
    }

    public static byte[] getTestPage()
    {
        return createScaleRasterReceiptData(PAPER_SIZE_THREE_INCH, true);
    }


    public static byte[] printBitmap(int width, boolean bothScale ,Bitmap bitmap) {
        byte[] data;

        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        // add text info
        Bitmap image = bitmap;
        builder.appendBitmap(image, false, width, bothScale);

        // add Barcode

        String barcode = "1234567890";
        data = barcode.getBytes();
//        builder.appendBarcode(data, ICommandBuilder.BarcodeSymbology.Code93,ICommandBuilder.BarcodeWidth.Mode1, 100, false);

        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
//        builder.appendBitmap(createBitmapFromText(barcode, 25, PAPER_SIZE_THREE_INCH, typeface), false, width, bothScale);

        builder.appendCutPaper(CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        return builder.getCommands();
    }
}