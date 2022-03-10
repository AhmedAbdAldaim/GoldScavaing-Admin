package com.example.goldscavenging.Model;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {
    @SerializedName("user")
    private DeleteModel deleteModel;

    @SerializedName("massage_ar")
    private String massage_ar;

    @SerializedName("message_en")
    private String message_en;

    @SerializedName("error")
    private boolean error;

    public DeleteResponse(DeleteModel deleteModel, String massage_ar, String message_en) {
        this.deleteModel = deleteModel;
        this.massage_ar = massage_ar;
        this.message_en = message_en;
    }

    public DeleteModel getDeleteModel() {
        return deleteModel;
    }

    public void setDeleteModel(DeleteModel deleteModel) {
        this.deleteModel = deleteModel;
    }

    public String getMassage_ar() {
        return massage_ar;
    }

    public void setMassage_ar(String massage_ar) {
        this.massage_ar = massage_ar;
    }

    public String getMessage_en() {
        return message_en;
    }

    public void setMessage_en(String message_en) {
        this.message_en = message_en;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
