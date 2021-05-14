package com.app.tributum.retrofit;

import com.app.tributum.model.ContractModel;
import com.app.tributum.model.EmailBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InterfaceAPI {

    @POST("generatePDF")
    Call<Object> sendContract(@Body ContractModel contractModel);

    @POST("notifyUser")
    Call<Object> sendEmail(@Body EmailBody emailBody);
}