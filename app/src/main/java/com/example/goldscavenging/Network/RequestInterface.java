package com.example.goldscavenging.Network;




import com.example.goldscavenging.Model.DeleteResponse;
import com.example.goldscavenging.Model.LoginResponse;
import com.example.goldscavenging.Model.EditProfileResponse;
import com.example.goldscavenging.Model.RestpasswordResponse;
import com.example.goldscavenging.Model.UsersAddedResponse;
import com.example.goldscavenging.Model.UsersSatausResponse;
import com.example.goldscavenging.Model.UsersShowResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface RequestInterface {


    //LOGIN
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> Login(@Field("phone") String phone,
                              @Field("password") String password);


    //Reset Password
    @FormUrlEncoded
    @POST("resetpasswordadmin/{phone}")
    Call<RestpasswordResponse> RestPassword(
            @Path("phone") String phone,
            @Field("password") String password);


    //ADDED USERS
    @FormUrlEncoded
    @POST("register")
    Call<UsersAddedResponse> NewRegisteration(@Field("name") String name,
                                      @Field("phone") String phone,
                                      @Field("shop") String shop,
                                      @Field("password") String password,
                                      @Header("Authorization") String authorization);

    //GET USERS
    @GET("users")
    Call<UsersShowResponse> GETUsers(
            @Header("Authorization") String authorization);


    //CHECK STATUS
    @FormUrlEncoded
    @PUT("status/{id}")
    Call<UsersSatausResponse> status(
            @Path("id") String id,
            @Field("status") String status,
            @Header("Authorization") String authorization
    );

    //Users DELETE
//    @DELETE("destroy/{id}")
//    Call<DeleteResponse> delete(
//            @Path("id") String id,
//            @Header("Authorization") String authorization
//    );

    //Edit Profile
    @FormUrlEncoded
    @PUT("user")
    Call<EditProfileResponse> EditProfile(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("password") String password,
            @Header("Authorization") String authorization
    );


    //Delete User
    @FormUrlEncoded
    @POST("userdelete/{id}")
    Call<DeleteResponse> DeleteUser(
            @Path("id") String id,
            @Field("deleted") String deleted,
            @Header("Authorization") String authorization
    );

    //delted list
    @GET("usersdeleted")
    Call<UsersShowResponse> GetAllDelteted(@Header("Authorization") String authorization);


    //delete Mac
    @POST("deletemac/{mac_address}")
    Call<DeleteResponse> DeleteMAC(@Path("mac_address")String mac_address,
                                      @Header("Authorization") String authorization);

}

