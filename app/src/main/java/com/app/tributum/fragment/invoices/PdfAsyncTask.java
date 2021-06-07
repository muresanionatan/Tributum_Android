package com.app.tributum.fragment.invoices;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;

import com.app.tributum.fragment.invoices.listener.AsyncListener;
import com.app.tributum.fragment.invoices.model.InvoiceModel;
import com.app.tributum.utils.BitmapUtils;
import com.app.tributum.utils.DropboxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfAsyncTask extends AsyncTask<Void, Void, Void> {

    private AsyncListener listener;

    private Activity activity;

    private List<InvoiceModel> list;

    private String username;

    private static String imageFileName;

    private String months;

    public PdfAsyncTask(AsyncListener listener, Activity activity, List<InvoiceModel> list, String username, String months) {
        this.listener = listener;
        this.activity = activity;
        this.list = list;
        this.username = username;
        this.months = months;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File file = getOutputFile(activity);
        if (file != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (int i = 0; i < list.size() - 1; i++) {
                    InvoiceModel model = list.get(i);
                    Bitmap myBitmap = BitmapFactory.decodeFile(BitmapUtils.compressBitmap(activity, model.getFilePath(), true));
                    if (myBitmap == null)
                        continue;
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(myBitmap.getWidth(), myBitmap.getHeight(), (i + 1)).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    canvas.drawBitmap(myBitmap, 0f, 0f, null);
                    pdfDocument.finishPage(page);
                    myBitmap.recycle();
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                DropboxUtils.uploadPdfOnDropbox(username, months, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (listener != null)
            listener.onTaskCompleted();
        super.onPostExecute(aVoid);
    }

    private File getOutputFile(Activity activity) {
        File root = new File(activity.getExternalFilesDir(null), "PDFs");

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