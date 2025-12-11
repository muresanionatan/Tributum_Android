package com.app.tributum.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;

import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.CombinePdfListener;
import com.app.tributum.utils.BitmapUtils;
import com.app.tributum.utils.DropboxUtils;
import com.app.tributum.utils.ui.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CombinePhotosInPdfTask extends AsyncTask<Void, Void, Void> {
    private CombinePdfListener listener;
    private List<VatModel> photosList;
    private String username;
    private String imageFileName;
    private String fileName;
    private List<File> pdfFile;

    private String path;

    public CombinePhotosInPdfTask(CombinePdfListener listener, List<VatModel> photosList, String username, String fileName, List<File> pdfFile) {
        this.listener = listener;
        this.photosList = photosList;
        this.username = username;
        this.fileName = fileName;
        this.pdfFile = pdfFile;
    }

    public CombinePhotosInPdfTask(CombinePdfListener listener, List<VatModel> photosList,
                                  String username, String fileName, List<File> pdfFile, String path) {
        this.listener = listener;
        this.photosList = photosList;
        this.username = username;
        this.fileName = fileName;
        this.pdfFile = pdfFile;
        this.path = path;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File file = getOutputFile();
        if (file != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                // Add photos first
                int pageCount = addPhotosToPdf(pdfDocument, photosList);

                // Add PDF pages after photos (if pdfFile exists)
                if (pdfFile != null) {
                    FileUtils.addPdfPagesToPdf(pdfDocument, pdfFile, pageCount);
                }

                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (path.equals("vat"))
                    DropboxUtils.uploadVatOnDropbox(username, fileName, file);
                else
                    DropboxUtils.uploadToFormDropbox(username, fileName, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int addPhotosToPdf(PdfDocument pdfDocument, List<VatModel> photoList) {
        int pageNumber = 0;
        for (int i = 0; i < photoList.size() - 1; i++) {
            VatModel model = photoList.get(i);
            Bitmap myBitmap = BitmapFactory.decodeFile(BitmapUtils.compressBitmap(model.getFilePath(), true));
            if (myBitmap == null)
                continue;
            pageNumber++;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(myBitmap.getWidth(), myBitmap.getHeight(), pageNumber).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(myBitmap, 0f, 0f, null);
            pdfDocument.finishPage(page);
            myBitmap.recycle();
        }
        return pageNumber;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (listener != null)
            listener.onPdfCompleted(fileName);
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