package com.example.emailauthentication;

public final class SharedData {
    private static SharedData instance = new SharedData();

    private SharedData() {
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public static void setInstance(SharedData instance) {
        SharedData.instance = instance;
    }

    public void setAllClean() {
        instance = null;
    }

    public String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String userPass;

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
