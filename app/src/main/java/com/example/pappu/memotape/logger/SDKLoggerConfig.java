/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.pappu.memotape.logger;

import android.util.Log;

public class SDKLoggerConfig {
    private static final boolean ON = false;
    public static void logD(String tag, String message){
        if(ON)
        Log.d(tag,message);
    }
}
