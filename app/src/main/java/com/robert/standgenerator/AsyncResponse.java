package com.robert.standgenerator;
/**
 * Created by Robert on 7/29/2014.
 */
public interface AsyncResponse {
    void processFinish(String[] output);
    void setUrl(String url);
}


