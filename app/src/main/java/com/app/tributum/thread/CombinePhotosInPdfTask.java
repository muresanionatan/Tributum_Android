package com.app.tributum.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.CombinePdfListener;
import com.app.tributum.utils.BitmapUtils;
import com.app.tributum.utils.DropboxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CombinePhotosInPdfTask extends AsyncTask<Void, Void, Void> {

    private CombinePdfListener listener;

    private List<VatModel> photosList;

    private String username;

    private static String imageFileName;
    private static String fileName;

    private List<File> pdfFile;

    public CombinePhotosInPdfTask(CombinePdfListener listener, List<VatModel> photosList, String username, String fileName, List<File> pdfFile) {
        this.listener = listener;
        this.photosList = photosList;
        this.username = username;
        this.fileName = fileName;
        this.pdfFile = pdfFile;
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
                    addPdfPagesToPdf(pdfDocument, pdfFile, pageCount);
                }

                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
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

    private void addPdfPagesToPdf(PdfDocument pdfDocument, List<File> pdfFiles, int startPageNumber) {
        if (pdfFiles == null || pdfFiles.isEmpty()) {
            return;
        }

        int currentPageNumber = startPageNumber;

        // Iterate through each PDF file in the list
        for (File pdfFile : pdfFiles) {
            if (pdfFile == null || !pdfFile.exists()) {
                continue;
            }

            ParcelFileDescriptor fileDescriptor = null;
            PdfRenderer pdfRenderer = null;

            try {
                fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);

                int pageCount = pdfRenderer.getPageCount();

                // Add all pages from this PDF file
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = pdfRenderer.openPage(i);

                    // Create a bitmap for the page
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

                    // Render the page to the bitmap
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    // Add the bitmap as a new page in the output PDF
                    currentPageNumber++;
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), currentPageNumber).create();
                    PdfDocument.Page pdfPage = pdfDocument.startPage(pageInfo);
                    Canvas canvas = pdfPage.getCanvas();
                    canvas.drawBitmap(bitmap, 0f, 0f, null);
                    pdfDocument.finishPage(pdfPage);

                    // Clean up
                    bitmap.recycle();
                    page.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close resources for this PDF file
                if (pdfRenderer != null) {
                    pdfRenderer.close();
                }
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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