//package com.example.tributum.fragment.invoices;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.icu.text.SimpleDateFormat;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.tributum.R;
//import com.example.tributum.application.AppKeysValues;
//import com.example.tributum.application.TributumAppHelper;
//import com.example.tributum.fragment.invoices.adapter.InvoicesAdapter;
//import com.example.tributum.fragment.invoices.listener.InvoiceItemClickListener;
//import com.example.tributum.fragment.invoices.listener.PdfListener;
//import com.example.tributum.fragment.invoices.model.InvoiceModel;
//import com.example.tributum.listener.InvoicesDeleteListener;
//import com.example.tributum.utils.BitmapUtils;
//import com.example.tributum.utils.ConstantsUtils;
//import com.example.tributum.utils.ui.LoadingScreen;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class InvoicesFragment1 extends Fragment implements InvoiceItemClickListener, InvoicesDeleteListener, PdfListener {
//
//    private InvoicesAdapter adapter;
//
//    private View closeCamera;
//
//    private ImageView previewImage;
//
//    private View previewLayout;
//
//    private ImageView centerButton;
//
//    private int photoClicked;
//
//    public int PICTURE_NUMBER = 1;
//
//    private String pictureImagePath = "";
//
//    private EditText payerName;
//
//    private EditText payerEmail;
//
//    private LoadingScreen loadingScreen;
//
//    public InvoicesFragment1() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.invoices_fragment, container, false);
//        setupViews(view);
//        return view;
//    }
//
//    private void setupViews(View view) {
//        centerButton = view.findViewById(R.id.take_photo_button_id);
//        closeCamera = view.findViewById(R.id.close_camera_id);
//        previewImage = view.findViewById(R.id.image_preview_id);
//        previewLayout = view.findViewById(R.id.preview_layout_id);
//        payerName = view.findViewById(R.id.payer_edit_text);
//        payerEmail = view.findViewById(R.id.payer_email_edit_text);
//
//        loadingScreen = new LoadingScreen(getActivity(), getActivity().findViewById(android.R.id.content));
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//
//        RecyclerView recyclerView = view.findViewById(R.id.invoices_recycler_id);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(gridLayoutManager);
//
//        List<InvoiceModel> list = new ArrayList<>();
//        list.add(new InvoiceModel(null, ""));
//        adapter = new InvoicesAdapter(getActivity(), list, this, this);
//        recyclerView.setAdapter(adapter);
//
//        view.findViewById(R.id.take_photo_button_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapter.remove(photoClicked);
//                clearPreview();
//                centerButton.setVisibility(View.GONE);
//                closeCamera.setVisibility(View.GONE);
//            }
//        });
//        closeCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearPreview();
//                centerButton.setVisibility(View.GONE);
//                closeCamera.setVisibility(View.GONE);
//            }
//        });
//
//        view.findViewById(R.id.invoices_send_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (payerName.getText().toString().equals("")
//                        || payerEmail.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), getString(R.string.add_all_info), Toast.LENGTH_SHORT).show();
//                } else if (PICTURE_NUMBER > 1) {
//                    loadingScreen.show();
//                    PdfAsyncTask asyncTask = new PdfAsyncTask(InvoicesFragment1.this, getActivity(), list, getView().findViewById(R.id.payer_edit_text).toString());
//                    asyncTask.execute();
//                } else {
//                    Toast.makeText(getActivity(), getString(R.string.no_photo_taken), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == ConstantsUtils.CAMERA_REQUEST_INVOICES_ID && resultCode == Activity.RESULT_OK) {
//            File imgFile = new File(pictureImagePath);
//            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                adapter.addItemToList(new InvoiceModel(myBitmap, pictureImagePath));
//            }
//
//            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
//                onTakePhotoClick();
//                PICTURE_NUMBER++;
//
//                if (!TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
//                    TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.TRUE);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onTakePhotoClick() {
//        if (checkPermissions()) {
//            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String imageFileName = timeStamp + ".jpg";
//            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
//            File file = new File(pictureImagePath);
//            Uri outputFileUri = Uri.fromFile(file);
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            startActivityForResult(cameraIntent, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
//        } else {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            onTakePhotoClick();
//    }
//
//    @Override
//    public void onPreviewPhotoClick(String filePath, int photoIndex) {
//        photoClicked = photoIndex;
//        previewImage.setImageBitmap(BitmapFactory.decodeFile(BitmapUtils.compressBitmap(getActivity(), filePath, true)));
//        previewLayout.setVisibility(View.VISIBLE);
//        centerButton.setVisibility(View.VISIBLE);
//        closeCamera.setVisibility(View.VISIBLE);
//    }
//
//    private void clearPreview() {
//        previewLayout.setVisibility(View.GONE);
//        previewImage.setImageResource(0);
//    }
//
//    @Override
//    public void onDestroy() {
//        clearFormStarted();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onDestroyView() {
//        clearFormStarted();
//        super.onDestroyView();
//    }
//
//    private void clearFormStarted() {
//        PICTURE_NUMBER = 1;
//        if (TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
//            TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.FALSE);
//        }
//    }
//
//    @Override
//    public void clearList() {
//        clearFormStarted();
//    }
//
//    private boolean checkPermissions() {
//        if (getActivity() == null)
//            return false;
//        int result;
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        for (String permission : ConstantsUtils.PERMISSIONS) {
//            result = ContextCompat.checkSelfPermission(getActivity(), permission);
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(permission);
//            }
//        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ConstantsUtils.MULTIPLE_PERMISSIONS_PPS);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onPdfCreated() {
//        loadingScreen.hide();
//    }
//}