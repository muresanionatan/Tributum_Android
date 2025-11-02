package com.app.tributum.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;

import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.utils.BitmapUtils;
import com.app.tributum.utils.DropboxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfAsyncTask extends AsyncTask<Void, Void, Void> {

    private AsyncListener listener;

    private List<VatModel> invoicesList;

    private List<VatModel> privatesList;

    private String username;

    private static String imageFileName;

    private String months;

    public PdfAsyncTask(AsyncListener listener, List<VatModel> invoicesList, List<VatModel> privatesList, String username, String months) {
        this.listener = listener;
        this.invoicesList = invoicesList;
        this.privatesList = privatesList;
        this.username = username;
        months = months.replaceAll(" ", "_");
        this.months = months;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File file = getOutputFile();
        if (file != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                pdfDocument.finishPage(addPageWithText(pdfDocument, username, months));
                addPhotosToPdf(pdfDocument, invoicesList);

                if (privatesList != null && privatesList.size() > 1) {
                    pdfDocument.finishPage(addPageWithText(pdfDocument, "Private Jobs", ""));
                    addPhotosToPdf(pdfDocument, privatesList);
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                DropboxUtils.uploadVatOnDropbox(username, months, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void addPhotosToPdf(PdfDocument pdfDocument, List<VatModel> photoList) {
        for (int i = 0; i < photoList.size() - 1; i++) {
            VatModel model = photoList.get(i);
            Bitmap myBitmap = BitmapFactory.decodeFile(BitmapUtils.compressBitmap(model.getFilePath(), true));
            if (myBitmap == null)
                continue;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(myBitmap.getWidth(), myBitmap.getHeight(), (i + 1)).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(myBitmap, 0f, 0f, null);
            pdfDocument.finishPage(page);
            myBitmap.recycle();
        }
    }

    private PdfDocument.Page addPageWithText(PdfDocument pdfDocument, String name, String months) {
        PdfDocument.PageInfo pageInfo = new
                PdfDocument.PageInfo.Builder(1000, 1000, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(40);

        canvas.drawText(name, 200, 500, paint);
        canvas.drawText(months, 200, 550, paint);

        return page;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (listener != null)
            listener.onTaskCompleted(null);
        super.onPostExecute(aVoid);
    }

    private File getOutputFile() {
        File root = new File(TributumApplication.getInstance().getExternalFilesDir(null), "PDFs");

        boolean isFolderCreated = true;

        if (!root.exists()) {
            isFolderCreated = root.mkdir();
        }

        if (isFolderCreated) {
            imageFileName = "PDF_" + username;
            return new File(root, imageFileName + ".pdf");
        } else {
            return null;
        }
    }
}