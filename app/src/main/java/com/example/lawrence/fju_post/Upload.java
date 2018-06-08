package com.example.lawrence.fju_post;

public class Upload { private String mImageUrl;

    public Upload(){
        //empty constructor needed
    }

    public Upload(String imageUrl){
        mImageUrl=imageUrl;
    }
    public String getmImageUrl(){
        return mImageUrl;
    }
    public void setmImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }
}

