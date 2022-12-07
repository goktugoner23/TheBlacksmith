package com.goktugoner;

import java.io.File;

public class ReturnType {
    private String message; //message to post if success/unsuccess
    private File file; //file
    private boolean success; //file upload status

    public ReturnType(String message, File file, boolean success){
        this.message = message;
        this.file = file;
        this.success = success;
    }

    public String getMessage(){
        return message;
    }

    public File getFile(){
        return file;
    }

    public boolean isSuccess(){
        return success;
    }
}
