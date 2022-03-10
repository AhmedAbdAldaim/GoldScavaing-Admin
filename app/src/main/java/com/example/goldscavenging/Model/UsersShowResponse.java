package com.example.goldscavenging.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsersShowResponse {
    @SerializedName("users")
    private List<UsersShowModel> usersShowModel;

    public UsersShowResponse(List<UsersShowModel> usersShowModel) {
        this.usersShowModel = usersShowModel;
    }

    public List<UsersShowModel> getUsersShowModel() {
        return usersShowModel;
    }

    public void setUsersShowModel(List<UsersShowModel> usersShowModel) {
        this.usersShowModel = usersShowModel;
    }
}
