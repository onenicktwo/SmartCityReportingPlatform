package com.example.citywatcherfrontend;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivityMulti extends MultipartRequest{
    private File mImageFile;
    private Map<String, String> mParams;

    public RegisterActivityMulti(String url,
                                    Response.ErrorListener errorListener,
                                    Response.Listener<String> listener,
                                    File imageFile,
                                    Map<String, String> params) {
        super(url, errorListener, listener, imageFile, params);
        this.mImageFile = imageFile;
        this.mParams = params;
    }

    @Override
    protected Map<String, DataPart> getByteData() {
        // Convert image file to DataPart
        Map<String, DataPart> byteData = new HashMap<>();
        if (mImageFile != null) {
            byte[] fileData = getFileData(mImageFile); // Convert file to byte array
            byteData.put("image", new DataPart(mImageFile.getName(), fileData, "image/jpeg"));
        }
        return byteData;
    }

    /**
     * Converts a File into a byte array.
     */
    private byte[] getFileData(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileData = new byte[(int) file.length()];
            fileInputStream.read(fileData);
            return fileData;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0]; // Return an empty array in case of error
        }
    }


}

