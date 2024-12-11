package com.example.citywatcherfrontend;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public abstract class MultipartRequest extends Request<String> {

    private final Response.Listener<NetworkResponse> listener;
    private final Map<String, String> params;
    private final Map<String, byte[]> fileUploads;

    private final String boundary = "boundary-" + System.currentTimeMillis();
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";

    public MultipartRequest(String url, Response.Listener<NetworkResponse> listener,
                            Response.ErrorListener errorListener,
                            Map<String, String> params, Map<String, byte[]> fileUploads) {
        super(Method.POST, url, errorListener);
        this.listener = listener;
        this.params = params;
        this.fileUploads = fileUploads;
    }


    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add text parameters
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(entry.getValue() + lineEnd);
                }
            }

            // Add file uploads
            if (fileUploads != null) {
                for (Map.Entry<String, byte[]> entry : fileUploads.entrySet()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + entry.getKey() + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(entry.getValue());
                    dos.writeBytes(lineEnd);
                }
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

}




