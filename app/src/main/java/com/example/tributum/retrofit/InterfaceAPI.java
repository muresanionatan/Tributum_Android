package com.example.tributum.retrofit;

import com.example.tributum.model.ContractModel;
import com.example.tributum.model.EmailBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InterfaceAPI {

    @POST("generatePDF")
    Call<Object> sendContract(@Body ContractModel contractModel);

    @POST("notifyUser")
    Call<Object> sendEmail(@Body EmailBody emailBody);
}