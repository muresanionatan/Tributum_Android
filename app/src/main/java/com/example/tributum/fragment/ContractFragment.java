//package com.example.tributum.fragment;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.icu.util.Calendar;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.CheckBox;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.tributum.R;
//import com.example.tributum.application.AppKeysValues;
//import com.example.tributum.application.TributumAppHelper;
//import com.example.tributum.helper.DrawingView;
//import com.example.tributum.listener.SignatureListener;
//import com.example.tributum.model.ContractModel;
//import com.example.tributum.retrofit.InterfaceAPI;
//import com.example.tributum.retrofit.RetrofitClientInstance;
//import com.example.tributum.utils.ConstantsUtils;
//import com.example.tributum.utils.MailUtils;
//import com.example.tributum.utils.NetworkUtils;
//import com.example.tributum.utils.UtilsGeneral;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
//public class ContractFragment extends Fragment implements SignatureListener {
//
//    private Bitmap signatureFile;
//
//    private Bitmap ppsFile;
//
//    private Bitmap idFile;
//
//    private EditText nameEditText;
//
//    private EditText addressEditText;
//
//    private EditText ppsNumberEditText;
//
//    private EditText emailEditText;
//
//    private EditText occupationEditText;
//
//    private ImageView ppsRemove;
//
//    private ImageView idRemove;
//
//    private RelativeLayout parentView;
//
//    private CheckBox singleCheck;
//
//    private CheckBox marriedCheck;
//
//    private CheckBox divorcedCheck;
//
//    private CheckBox cohabitingCheck;
//
//    private TextView contractDate;
//
//    private TextView birthday;
//
//    private CheckBox otherCheck;
//
//    private boolean isSignatureSet;
//
//    public ContractFragment() {
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.contract_activity, container, false);
//        setupViews(view);
//        return view;
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setupViews(final View view) {
//        if (getActivity() == null)
//            return;
//
//        view.findViewById(R.id.take_picture_pps).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera(ConstantsUtils.CAMERA_REQUEST_PPS);
//            }
//        });
//        view.findViewById(R.id.take_picture_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera(ConstantsUtils.CAMERA_REQUEST_ID);
//            }
//        });
//        view.findViewById(R.id.upload_pps).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS);
//            }
//        });
//        view.findViewById(R.id.upload_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID);
//            }
//        });
//        view.findViewById(R.id.open_date_picker).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getActivity() == null)
//                    return;
//
//                final Calendar calendar = Calendar.getInstance();
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                int month = calendar.get(Calendar.MONTH);
//                int year = calendar.get(Calendar.YEAR);
//
//                DatePickerDialog picker = new DatePickerDialog(getActivity(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                                contractDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                            }
//                        }, year, month, day);
//                picker.show();
//            }
//        });
//        view.findViewById(R.id.edittext_birthday_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getActivity() == null)
//                    return;
//
//                final Calendar calendar = Calendar.getInstance();
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                int month = calendar.get(Calendar.MONTH);
//                int year = calendar.get(Calendar.YEAR);
//
//                DatePickerDialog picker = new DatePickerDialog(getActivity(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                                birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                            }
//                        }, year, month, day);
//                picker.show();
//            }
//        });
//
//        TextView sendButton = view.findViewById(R.id.send_button);
//        ImageView clearImage = view.findViewById(R.id.clear_image);
//        ppsRemove = view.findViewById(R.id.pps_remove_image);
//        idRemove = view.findViewById(R.id.id_remove_image);
//
//        nameEditText = view.findViewById(R.id.name_edit_text);
//        addressEditText = view.findViewById(R.id.address_edit_text);
//        ppsNumberEditText = view.findViewById(R.id.pps_number_edit_text);
//        emailEditText = view.findViewById(R.id.email_edit_text);
//        occupationEditText = view.findViewById(R.id.occupation_edit_text);
//
//        nameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        nameEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!nameEditText.getText().toString().equals("")
//                        && !TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
//                    TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.TRUE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        addressEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        ppsNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        UtilsGeneral.setMaxLengthAndAllCapsToEditText(ppsNumberEditText, 9, true);
//
//        emailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        occupationEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//        singleCheck = view.findViewById(R.id.single_checkbox);
//        marriedCheck = view.findViewById(R.id.married_checkbox);
//        divorcedCheck = view.findViewById(R.id.divorced_checkbox);
//        cohabitingCheck = view.findViewById(R.id.cohabiting_checkbox);
//
//        contractDate = view.findViewById(R.id.starting_date);
////        birthday = view.findViewById(R.id.date_of_birth);
//
//        EditText otherEditText = (EditText) view.findViewById(R.id.other_edit_id);
//        UtilsGeneral.setMaxLengthAndAllCapsToEditText(otherEditText, 60, false);
//        otherCheck = view.findViewById(R.id.ninth);
//        otherCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UtilsGeneral.setFocusOnInput(getActivity(), otherEditText);
//            }
//        });
//        otherEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                otherCheck.setChecked(true);
//            }
//        });
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleSendButtonClick(view);
//            }
//        });
//
//        occupationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    UtilsGeneral.removeFocusFromInput(getActivity(), occupationEditText);
//                    return true;
//                }
//                return false;
//            }
//        });
//        occupationEditText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                UtilsGeneral.setFocusOnInput(getActivity(), occupationEditText);
//                return false;
//            }
//        });
//
//        clearImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleClearImageClick();
//            }
//        });
//
//        ppsRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ppsRemove.setVisibility(View.GONE);
//                ppsFile = null;
//            }
//        });
//
//        idRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                idRemove.setVisibility(View.GONE);
//                idFile = null;
//            }
//        });
//
//        parentView = view.findViewById(R.id.signature_drawing_view);
//        final DrawingView myDrawView = new DrawingView(getActivity(), this);
//        parentView.addView(myDrawView);
//
//        view.findViewById(R.id.make_signature).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                view.findViewById(R.id.signature_layout).setVisibility(View.VISIBLE);
//            }
//        });
//
//        view.findViewById(R.id.save_signature_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveSignatureImage();
//                view.findViewById(R.id.signature_layout).setVisibility(View.GONE);
//            }
//        });
//
//        view.findViewById(R.id.delete_signature_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDrawView.clear();
//                isSignatureSet = false;
//                view.findViewById(R.id.signature_layout).setVisibility(View.GONE);
//            }
//        });
//
//        singleCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                singleCheck.setChecked(true);
//                marriedCheck.setChecked(false);
//                divorcedCheck.setChecked(false);
//                cohabitingCheck.setChecked(false);
//            }
//        });
//
//        marriedCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                singleCheck.setChecked(false);
//                marriedCheck.setChecked(true);
//                divorcedCheck.setChecked(false);
//                cohabitingCheck.setChecked(false);
//            }
//        });
//
//        divorcedCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                singleCheck.setChecked(false);
//                marriedCheck.setChecked(false);
//                divorcedCheck.setChecked(true);
//                cohabitingCheck.setChecked(false);
//            }
//        });
//
//        cohabitingCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                singleCheck.setChecked(false);
//                marriedCheck.setChecked(false);
//                divorcedCheck.setChecked(false);
//                cohabitingCheck.setChecked(true);
//            }
//        });
//
//        final View taxesView = view.findViewById(R.id.taxes_id);
//        final CheckBox selfEmployed = view.findViewById(R.id.self_employed_id);
//        final CheckBox employee = view.findViewById(R.id.employee_id);
//        selfEmployed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selfEmployed.isChecked())
//                    taxesView.setVisibility(View.VISIBLE);
//                else
//                    taxesView.setVisibility(View.GONE);
//
//                if (!employee.isChecked())
//                    employee.setChecked(true);
//            }
//        });
//
//        employee.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!employee.isChecked() && !selfEmployed.isChecked()) {
//                    selfEmployed.setChecked(true);
//                    taxesView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//    }
//
//    private void saveSignatureImage() {
//        if (getActivity() == null)
//            return;
//
//        parentView.setDrawingCacheEnabled(true);
//        signatureFile = parentView.getDrawingCache();
//        System.out.println("ContractFragment.saveSignatureImage " + parentView.getDrawingCache());
//        File signature = new File(getActivity().getFilesDir(), "/signature.png");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(signature);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        signatureFile.compress(Bitmap.CompressFormat.PNG, 95, fos);
//    }
//
//    private void handleSendButtonClick(View view) {
//        if (getActivity() == null)
//            return;
//        if (NetworkUtils.isNetworkConnected(getActivity())) {
//            if (nameEditText.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
//            } else if (addressEditText.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_address), Toast.LENGTH_SHORT).show();
//            } else if (ppsNumberEditText.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_pps), Toast.LENGTH_SHORT).show();
//            } else if (!MailUtils.isEmailValid(emailEditText.getText().toString())) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_correct_email), Toast.LENGTH_SHORT).show();
//            } else if (occupationEditText.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_occupation), Toast.LENGTH_SHORT).show();
//            } else if (birthday.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_birthday), Toast.LENGTH_SHORT).show();
//            } else if (contractDate.getText().toString().equals("")) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_contract_date), Toast.LENGTH_SHORT).show();
//            } else if (((CheckBox) view.findViewById(R.id.self_employed_id)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.first)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.second)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.third)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.fourth)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.fifth)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.sixth)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.seventh)).isChecked()
//                    && !((CheckBox) view.findViewById(R.id.eight)).isChecked()
//                    && !otherCheck.isChecked()) {
//                Toast.makeText(getActivity(), getString(R.string.please_enter_applying_for), Toast.LENGTH_SHORT).show();
//            } else if (ppsFile == null) {
//                Toast.makeText(getActivity(), getString(R.string.please_add_pps), Toast.LENGTH_SHORT).show();
//            } else if (idFile == null) {
//                Toast.makeText(getActivity(), getString(R.string.please_add_id), Toast.LENGTH_SHORT).show();
//            } else if (signatureFile == null || !isSignatureSet) {
//                Toast.makeText(getActivity(), getString(R.string.please_add_signature), Toast.LENGTH_SHORT).show();
//            } else {
//                sendInfo(view);
//            }
//        } else {
//            Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void sendInfo(View view) {
//        ContractModel contractModel = new ContractModel(
//                nameEditText.getText().toString(),
//                addressEditText.getText().toString(),
//                ppsNumberEditText.getText().toString(),
//                emailEditText.getText().toString(),
//                contractDate.getText().toString(),
//                birthday.getText().toString());
//
//        if (singleCheck.isChecked())
//            contractModel.setMaritalStatus(getString(R.string.single_label));
//        else if (marriedCheck.isChecked())
//            contractModel.setMaritalStatus(getString(R.string.married_label));
//        else if (divorcedCheck.isChecked())
//            contractModel.setMaritalStatus(getString(R.string.divorced_label));
//        else if (cohabitingCheck.isChecked())
//            contractModel.setMaritalStatus(getString(R.string.cohabiting_label));
//
//        List<String> employmentList = new ArrayList<>();
//        if (((CheckBox) view.findViewById(R.id.self_employed_id)).isChecked())
//            employmentList.add(getString(R.string.self_employed_label));
//        if (((CheckBox) view.findViewById(R.id.employee_id)).isChecked())
//            employmentList.add(getString(R.string.employee_label));
//        contractModel.setEmployment(employmentList);
//
//        List<String> taxes = new ArrayList<>();
//        if (((CheckBox) view.findViewById(R.id.first)).isChecked())
//            taxes.add(getString(R.string.income_tax_label));
//        if (((CheckBox) view.findViewById(R.id.second)).isChecked())
//            taxes.add(getString(R.string.corporation_tax_label));
//        if (((CheckBox) view.findViewById(R.id.third)).isChecked())
//            taxes.add(getString(R.string.value_added_tax_label));
//        if (((CheckBox) view.findViewById(R.id.fourth)).isChecked())
//            taxes.add(getString(R.string.employer_paye_label));
//        if (((CheckBox) view.findViewById(R.id.fifth)).isChecked())
//            taxes.add(getString(R.string.capital_gains_label));
//        if (((CheckBox) view.findViewById(R.id.sixth)).isChecked())
//            taxes.add(getString(R.string.relevant_contract_label));
//        if (((CheckBox) view.findViewById(R.id.seventh)).isChecked())
//            taxes.add(getString(R.string.environment_levy_label));
//        if (((CheckBox) view.findViewById(R.id.eight)).isChecked())
//            taxes.add(getString(R.string.divided_withholding_label));
//        if (otherCheck.isChecked())
//            taxes.add(getString(R.string.other_label));
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        signatureFile.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
////        signatureFile.recycle();
//
//        contractModel.setOther(((EditText) view.findViewById(R.id.other_edit_id)).getText().toString());
//        contractModel.setTaxes(taxes);
//        contractModel.setSignature(byteArray);
//
//        Retrofit retrofit = RetrofitClientInstance.getInstance();
//        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);
//
//        Call<String> call = api.sendContract(contractModel);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                System.out.println("ContractFragment.onResponse " + response.body());
//                if (response.isSuccessful())
//                    Toast.makeText(getActivity(), getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private void pickPictureFromGallery(int requestId) {
//        if (getActivity() == null)
//            return;
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                openFilePicker(requestId);
//            } else {
//                if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS);
//                } else {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID);
//                }
//            }
//        }
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private void openCamera(int requestId) {
//        if (Build.VERSION.SDK_INT >= 23 && checkPermissions())
//            takePicture(requestId);
//    }
//
//    private void takePicture(int requestId) {
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, requestId);
//    }
//
//    private void openFilePicker(int requestId) {
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, requestId);
//    }
//
//    private String getFilePath(Intent data) {
//        Uri uri = data.getData();
//        String[] projection = {MediaStore.Images.Media.DATA};
//
//        if (uri == null)
//            return "";
//        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, projection, null, null, null);
//
//        if (cursor == null)
//            return "";
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(projection[0]);
//        String filepath = cursor.getString(columnIndex);
//        cursor.close();
//
//        return filepath;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data == null)
//            return;
//
//        switch (requestCode) {
//            case ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS:
//                if (resultCode == Activity.RESULT_OK) {
//                    ppsFile = BitmapFactory.decodeFile(getFilePath(data));
//                    ppsRemove.setVisibility(View.VISIBLE);
//                }
//                break;
//            case ConstantsUtils.SELECTED_PICTURE_REQUEST_ID:
//                if (resultCode == Activity.RESULT_OK) {
//                    idFile = BitmapFactory.decodeFile(getFilePath(data));
//                    idRemove.setVisibility(View.VISIBLE);
//                }
//                break;
//            case ConstantsUtils.CAMERA_REQUEST_PPS:
//                if (resultCode == Activity.RESULT_OK) {
//                    if (data.getExtras() == null)
//                        return;
//
//                    ppsFile = (Bitmap) data.getExtras().get("data");
//                    ppsRemove.setVisibility(View.VISIBLE);
//                }
//                break;
//            case ConstantsUtils.CAMERA_REQUEST_ID:
//                if (resultCode == Activity.RESULT_OK) {
//                    if (data.getExtras() == null)
//                        return;
//
//                    idFile = (Bitmap) data.getExtras().get("data");
//                    idRemove.setVisibility(View.VISIBLE);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS);
//                break;
//            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID);
//                break;
//            case ConstantsUtils.MULTIPLE_PERMISSIONS_PPS:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    takePicture(ConstantsUtils.CAMERA_REQUEST_PPS);
//                break;
//            case ConstantsUtils.MULTIPLE_PERMISSIONS_ID:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    takePicture(ConstantsUtils.CAMERA_REQUEST_ID);
//                break;
//        }
//    }
//
//    @Override
//    public void onDrawingStarted() {
//        isSignatureSet = true;
//    }
//
//    private void handleClearImageClick() {
//        if (getActivity() == null)
//            return;
//        clearFieldsInPps();
//        ppsRemove.setVisibility(View.GONE);
//        idRemove.setVisibility(View.GONE);
//        ppsFile = null;
//        idFile = null;
//        signatureFile = null;
//    }
//
//    private void clearFieldsInPps() {
//        nameEditText.setText("");
//        addressEditText.setText("");
//        ppsNumberEditText.setText("");
//        emailEditText.setText("");
//        occupationEditText.setText("");
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
//        if (TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
//            TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.FALSE);
//        }
//    }
//}