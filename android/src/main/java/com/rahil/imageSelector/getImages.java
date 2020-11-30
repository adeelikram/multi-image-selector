package com.rahil.imageSelector;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;


import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


@NativePlugin(
        requestCodes = {getImages.REQUEST_IMAGE_PICK}
)
public class getImages extends Plugin {

    protected static final int REQUEST_IMAGE_PICK =12345 ;

    @PluginMethod
    public  void echo(PluginCall call){
        JSObject obj=new JSObject();
        obj.put("value","Hello");
        call.success(obj);
    }

    @PluginMethod
    public void getImagesFromGallery(PluginCall call) {
        saveCall(call);
        Intent intet = new Intent(Intent.ACTION_PICK);
        intet.setType("image/*");
        intet.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(call, intet, REQUEST_IMAGE_PICK);
    }


    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        String chuncks;

        PluginCall call=getSavedCall();
        Integer quality=call.getInt("quality");
        quality=(quality==null)?100:quality;
        if (requestCode == REQUEST_IMAGE_PICK && data!=null) {
            int cout = data.getClipData().getItemCount();
            chuncks = "";
            ClipData mClipData = data.getClipData();

            ImageDecoder.Source source;
            Bitmap bitmap = null;
            for (int i = 0; i < cout; i++) {
                Uri u=mClipData.getItemAt(i).getUri();
                ContentResolver resolver = getContext().getContentResolver();
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        source = ImageDecoder.createSource(resolver,u);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    } else {
                        // Use older version
                        bitmap = MediaStore.Images.Media.getBitmap(resolver, u);
                    }
                } catch (IOException e) {
                    call.error(e.getMessage());
                }
                String fileExt=resolver.getType(u);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                switch(fileExt){
                    case "image/jpeg":
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                        break;

                    case "image/png":
                        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                        break;
                    case "image/webp":
                        bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream);
                        break;
                    default:
                        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                }
                byte[] byteArray = outputStream.toByteArray();

                //Use your Base64 String as you wish
                String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                if(!(cout<=i+1)){
                    chuncks+="data:"+fileExt+";base64,"+encodedString+"\n\n";
                }
                else{
                    chuncks+="data:"+fileExt+";base64,"+encodedString;
                }
            }
            JSObject object=new JSObject();
            object.put("value",chuncks);
            call.success(object);
        }
    }
}