package com.example.howtostoreimageandretreveimageformfirebase;

public class Upload {
    String Image_name;
    String Uri_name;

    public Upload() {

    }

    public Upload(String image_name, String uri_name) {
        Image_name = image_name;
        Uri_name = uri_name;
    }

    public String getImage_name() {
        return Image_name;
    }

    public void setImage_name(String image_name) {
        Image_name = image_name;
    }

    public String getUri_name() {
        return Uri_name;
    }

    public void setUri_name(String uri_name) {
        Uri_name = uri_name;
    }
}
