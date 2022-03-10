package com.example.goldscavenging.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsersSatausResponse {
    @SerializedName("user")
    private UsersShowModel usersShowModel;

    public UsersSatausResponse(UsersShowModel usersShowModel) {
        this.usersShowModel = usersShowModel;
    }

    public UsersShowModel getUsersShowModel() {
        return usersShowModel;
    }

    public void setUsersShowModel(UsersShowModel usersShowModel) {
        this.usersShowModel = usersShowModel;
    }
}
