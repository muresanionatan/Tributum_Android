//package com.example.tributum.fragment.invoices;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.pdf.PdfDocument;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.dropbox.core.DbxException;
//import com.dropbox.core.DbxRequestConfig;
//import com.dropbox.core.v2.DbxClientV2;
//import com.dropbox.core.v2.files.WriteMode;
//import com.example.tributum.fragment.invoices.listener.PdfListener;
//import com.example.tributum.fragment.invoices.model.InvoiceModel;
//import com.example.tributum.utils.BitmapUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//public class PdfAsyncTask1 extends AsyncTask<Void, Void, Void> {
//
//    private PdfListener listener;
//
//    private Activity activity;
//
//    private List<InvoiceModel> list;
//
//    private String username;
//
//    private static String imageFileName;
//
//    private File uploadFile;
//
//    private static final String ACCESS_TOKEN = "GH2DbopZybMAAAAAAAAAAQACWCPsCEpxctSWk2ZaU1hRQ4FmylxO8kVjat9SkQd4";
//
//    PdfAsyncTask1(PdfListener listener, Activity activity, List<InvoiceModel> list, String username) {
//        this.listener = listener;
//        this.activity = activity;
//        this.list = list;
//        this.username = username;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        File file = getOutputFile(activity);
//        uploadFile = file;
//        if (file != null) {
//            try {
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                PdfDocument pdfDocument = new PdfDocument();
//
//                for (int i = 0; i < list.size(); i++) {
//                    InvoiceModel model = list.get(i);
//                    if (model.getBitmap() != null) {
//                        Bitmap myBitmap = BitmapFactory.decodeFile(BitmapUtils.compressBitmap(activity, model.getFilePath(), true));
//                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(myBitmap.getWidth(), myBitmap.getHeight(), (i + 1)).create();
//                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
//                        Canvas canvas = page.getCanvas();
//                        canvas.drawBitmap(myBitmap, 0f, 0f, null);
//                        pdfDocument.finishPage(page);
//                        myBitmap.recycle();
//                    }
//                }
//                pdfDocument.writeTo(fileOutputStream);
//                pdfDocument.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                uploadFileToDropbox();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
////            Uri imageUri = FileProvider.getUriForFile(
////                    activity, "com.example.tributum.fragment.invoices.provider", file);
//////
////            Intent shareIntent = new Intent(Intent.ACTION_SEND);
////            shareIntent.setType("application/pdf");
////            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tributum@yahoo.com"});
////            shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.invoice_mail_subject));
////            shareIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.please_register_vat));
////            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
////            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            activity.startActivity(shareIntent);
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        if (listener != null)
//            listener.onPdfCreated();
//        super.onPostExecute(aVoid);
//    }
//
//    private File getOutputFile(Activity activity) {
//        File root = new File(activity.getExternalFilesDir(null), "PDFs");
//
//        boolean isFolderCreated = true;
//
//        if (!root.exists()) {
//            isFolderCreated = root.mkdir();
//        }
//
//        if (isFolderCreated) {
//            imageFileName = "PDF_" + username;
//            return new File(root, imageFileName + ".pdf");
//        } else {
//            return null;
//        }
//    }
//
//    private void uploadFileToDropbox() throws FileNotFoundException {
//        DbxRequestConfig config = DbxRequestConfig.newBuilder("tributum").build();
//        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
//
//        try {
//            InputStream inputStream = new FileInputStream(uploadFile);
//            client.files().uploadBuilder("/VATS/" + username + ".pdf")
//                    .withMode(WriteMode.OVERWRITE)
//                    .uploadAndFinish(inputStream);
//            Log.d("Upload Status", "Success");
//        } catch (DbxException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}