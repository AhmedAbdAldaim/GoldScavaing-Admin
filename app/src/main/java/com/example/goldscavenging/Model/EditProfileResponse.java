package com.example.goldscavenging.Model;

import com.google.gson.annotations.SerializedName;

public class EditProfileResponse {
    @SerializedName("user")
    private EditProfileModel editProfileModel;

    @SerializedName("message")
    private String message;

    public EditProfileResponse(EditProfileModel editProfileModel, String message) {
        this.editProfileModel = editProfileModel;
        this.message = message;
    }

    public EditProfileModel getEditProfileModel() {
        return editProfileModel;
    }

    public void setEditProfileModel(EditProfileModel editProfileModel) {
        this.editProfileModel = editProfileModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
