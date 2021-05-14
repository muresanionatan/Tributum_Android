package com.example.tributum.fragment.invoices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tributum.R;
import com.example.tributum.activity.MainActivity;
import com.example.tributum.application.AppKeysValues;
import com.example.tributum.application.TributumAppHelper;
import com.example.tributum.fragment.invoices.adapter.InvoicesAdapter;
import com.example.tributum.fragment.invoices.listener.InvoiceItemClickListener;
import com.example.tributum.fragment.invoices.listener.AsyncListener;
import com.example.tributum.fragment.invoices.model.InvoiceModel;
import com.example.tributum.listener.InvoicesDeleteListener;
import com.example.tributum.listener.KeyboardListener;
import com.example.tributum.model.EmailBody;
import com.example.tributum.retrofit.InterfaceAPI;
import com.example.tributum.retrofit.RetrofitClientInstance;
import com.example.tributum.utils.ConstantsUtils;
import com.example.tributum.utils.UtilsGeneral;
import com.example.tributum.utils.animation.AnimUtils;
import com.example.tributum.utils.ui.FileUtils;
import com.example.tributum.utils.ui.KeyboardVisibility;
import com.example.tributum.utils.ui.LoadingScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InvoicesFragment extends Fragment implements InvoiceItemClickListener, InvoicesDeleteListener, AsyncListener, KeyboardListener {

    private InvoicesAdapter adapter;

    private ImageView previewImage;

    private View previewLayout;

    private int photoClicked;

    public int PICTURE_NUMBER = 1;

    private String pictureImagePath = "";

    private EditText name;

    private EditText payerEmail;

    private LoadingScreen loadingScreen;

    private TextView sendButton;

    private boolean isPreview;

    private EditText startingMonth;

    private EditText endingMonth;

    private TextView addFromGallery;

    public InvoicesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invoices_fragment, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        previewImage = view.findViewById(R.id.image_preview_id);
        previewLayout = view.findViewById(R.id.preview_layout_id);
        name = view.findViewById(R.id.payer_edit_text);
        payerEmail = view.findViewById(R.id.payer_email_edit_text);
        sendButton = view.findViewById(R.id.invoices_send_id);
        startingMonth = view.findViewById(R.id.start_month_edit_text);
        endingMonth = view.findViewById(R.id.end_month_edit_text);
        addFromGallery = view.findViewById(R.id.add_from_gallery_invoices_id);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        startingMonth.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        endingMonth.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loadingScreen = new LoadingScreen(getActivity(), getActivity().findViewById(android.R.id.content));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        RecyclerView recyclerView = view.findViewById(R.id.invoices_recycler_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        List<InvoiceModel> list = new ArrayList<>();
        list.add(new InvoiceModel(""));
        adapter = new InvoicesAdapter(getActivity(), list, this, this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.delete_photo_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.remove(photoClicked);
                clearPreview();
            }
        });
        view.findViewById(R.id.close_camera_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreview();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")
                        || payerEmail.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.add_all_info), Toast.LENGTH_SHORT).show();
                } else if (PICTURE_NUMBER > 1) {
                    UtilsGeneral.hideSoftKeyboard(getActivity());
                    loadingScreen.show();
                    PdfAsyncTask asyncTask = new PdfAsyncTask(InvoicesFragment.this, getActivity(), list, name.getText().toString(),
                            startingMonth.getText().toString()
                                    + "_" + endingMonth.getText().toString());
                    asyncTask.execute();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_photo_taken), Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.invoices_recycler_id).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(getActivity());
                }
                return false;
            }
        });

        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ConstantsUtils.SELECT_PICTURES);
            }
        });

        name.setText(TributumAppHelper.getStringSetting(AppKeysValues.INVOICE_NAME));
        payerEmail.setText(TributumAppHelper.getStringSetting(AppKeysValues.INVOICE_EMAIL));

        KeyboardVisibility keyboardVisibility = new KeyboardVisibility(this);
        keyboardVisibility.handleKeyboardVisibility(view.findViewById(R.id.invoices_main_layout_id));

        AnimUtils.getTranslationYAnimator(sendButton, 0).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.CAMERA_REQUEST_INVOICES_ID && resultCode == Activity.RESULT_OK) {
            adapter.addItemToList(new InvoiceModel(pictureImagePath));

            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                onTakePhotoClick();
                PICTURE_NUMBER++;

                if (!TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
                    TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.TRUE);
                }
            }
        } else if (requestCode == ConstantsUtils.SELECT_PICTURES && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                PICTURE_NUMBER = count;
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    adapter.addItemToList(new InvoiceModel(FileUtils.getPath(getActivity(), imageUri)));
                }
            } else if (data.getData() != null) {
                PICTURE_NUMBER++;
                String imagePath = data.getData().getPath();
                adapter.addItemToList(new InvoiceModel(imagePath));
            }
        }
    }

    @Override
    public void onTakePhotoClick() {
        if (checkPermissions()) {
            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + ".jpg";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
            File file = new File(pictureImagePath);
            Uri outputFileUri = FileProvider.getUriForFile(getActivity(),
                    "com.example.tributum.fragment.invoices.provider", file);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onTakePhotoClick();
    }

    @Override
    public void onPreviewPhotoClick(String filePath, int photoIndex) {
        isPreview = true;
        photoClicked = photoIndex;
        Glide.with(getActivity()).load("file://" + filePath).into(previewImage);
        previewLayout.setVisibility(View.VISIBLE);
        addFromGallery.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        ((MainActivity) getActivity()).getBottomNavigation().setVisibility(View.GONE);
    }

    public void clearPreview() {
        isPreview = false;
        previewLayout.setVisibility(View.GONE);
        previewImage.setImageResource(0);
        addFromGallery.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getBottomNavigation().setVisibility(View.VISIBLE);
    }

    public boolean isPreview() {
        return isPreview;
    }

    @Override
    public void onDestroy() {
        clearFormStarted();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        clearFormStarted();
        saveListToPreferences();
        super.onDestroyView();
    }

    private void saveListToPreferences() {
        if (!name.getText().toString().equals(""))
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_NAME, name.getText().toString());
        if (!payerEmail.getText().toString().equals(""))
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_EMAIL, payerEmail.getText().toString());
    }

    private void clearFormStarted() {
        PICTURE_NUMBER = 1;
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
            TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.FALSE);
        }
    }

    @Override
    public void clearList() {
        clearFormStarted();
    }

    private boolean checkPermissions() {
        if (getActivity() == null)
            return false;
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : ConstantsUtils.PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT);
            return false;
        }
        return true;
    }

    private void sendInternalEmail() {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful())
                    sendClientEmail();
                else
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                loadingScreen.hide();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                loadingScreen.hide();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendClientEmail() {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(payerEmail.getText().toString(), generateClientEmailMessage()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful())
                    Toast.makeText(getActivity(), getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                loadingScreen.hide();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                loadingScreen.hide();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateInternalEmailMessage() {
        String formattedString = name.getText().toString().toUpperCase();
        if (formattedString.contains(" "))
            formattedString = formattedString.replace(" ", "%20");
        return getString(R.string.invoices_message_email) + name.getText().toString()
                + getString(R.string.invoices_message_email_part2) + startingMonth.getText().toString()
                + " - " + endingMonth.getText().toString()
                + "\n\n" + "Click on below link to access the pdf\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/VATS/" + formattedString;
    }

    private String generateClientEmailMessage() {
        return "The receipts"
                + getString(R.string.invoices_message_email_part2) + startingMonth.getText().toString()
                + " - " + endingMonth.getText().toString()
                + "were sent and we'll be processed.";
    }

    @Override
    public void onTaskCompleted() {
        loadingScreen.hide();
        sendInternalEmail();
        Toast.makeText(getActivity(), R.string.pdf_sent, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyboardStateChanged(boolean opened) {
        if (opened) {
            AnimUtils.getTranslationYAnimator(sendButton, 250).start();
            AnimUtils.getFadeOutAnimator(((MainActivity) getActivity()).getBottomNavigation()).start();
        } else {
            AnimUtils.getTranslationYAnimator(sendButton, 0).start();
            AnimUtils.getFadeInAnimator(((MainActivity) getActivity()).getBottomNavigation(), AnimUtils.DURATION_50, AnimUtils.DURATION_50, null, null).start();
        }
    }
}