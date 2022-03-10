package com.example.goldscavenging.Model;

import com.google.gson.annotations.SerializedName;

public class UsersAddedResponse {

    @SerializedName("newUser")
    private UsersAddedModel registerLoginModel;

    public UsersAddedResponse(UsersAddedModel registerLoginModel ) {
        this.registerLoginModel = registerLoginModel;
    }

    public UsersAddedModel getRegisterLoginModel() {
        return registerLoginModel;
    }

    public void setRegisterLoginModel(UsersAddedModel registerLoginModel) {
        this.registerLoginModel = registerLoginModel;
    }
}
