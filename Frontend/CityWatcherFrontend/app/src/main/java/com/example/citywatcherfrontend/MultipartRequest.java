package com.example.citywatcherfrontend;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class MultipartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final Map<String, String> mStringParts;

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";

    public MultipartRequest(
            String url,
            Response.ErrorListener errorListener,
            Response.Listener<String> listener,
            File file,
            Map<String, String> stringParts) {
        super(Method.POST, url, errorListener);

        this.mListener = listener;
        this.mFilePart = file;
        this.mStringParts = stringParts != null ? stringParts : new HashMap<>();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add JSON string part (user details)
            String userJson = new JSONObject(mStringParts).toString();
            addJsonPart(dos, "user", userJson);

            // Add file part (profile image)
            if (mFilePart != null) {
                addFilePart(dos, "file", mFilePart);
            }

            // Final boundary
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private void addJsonPart(DataOutputStream dos, String key, String json) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
        dos.writeBytes("Content-Type: application/json" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(json + lineEnd);
    }


    private void addFilePart(DataOutputStream dos, String key, File file) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
        dos.writeBytes("Content-Type: " + java.net.URLConnection.guessContentTypeFromName(file.getName()) + lineEnd);
        dos.writeBytes(lineEnd);

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, length);
        }
        fis.close();

        dos.writeBytes(lineEnd);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseBody = new String(response.data);
        return Response.success(responseBody, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    protected abstract Map<String, DataPart> getByteData();

    public class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }

}
