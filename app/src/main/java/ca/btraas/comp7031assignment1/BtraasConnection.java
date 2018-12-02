package ca.btraas.comp7031assignment1;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ProtocolException;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


class BtraasConnection implements Closeable {


    private static String LINE_FEED = "\r\n";
    private static String TAG = BtraasConnection.class.getSimpleName();


    private String boundary = "===" + System.currentTimeMillis() + "===";

    private OutputStream outputStream;
    private PrintWriter writer;

    Boolean closed = false;
    private HttpsURLConnection con;

    String charset = "UTF-8";


    int getResponseCode() throws IOException {
        return con.getResponseCode();
    }

//    val responseCode: Int
//        get() = con.responseCode


//    val headerFields: MutableMap<String, MutableList<String>>?
//     BtraasConnection() = con.headerFields


    public static abstract class OnPercentUploadedHandler {
        abstract void onPercentUploaded(int percent);
    }


   public BtraasConnection(HttpsURLConnection connection) throws IOException {
        this.con = connection;
        con.setUseCaches(false);
        con.setInstanceFollowRedirects(false);
        con.setDoOutput(true);
        con.setDoInput(true);

        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);


        outputStream = con.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), false);

    }

    @RequiresApi(21) // no idea why, but it crashes on API 19
    void setMethod(String method) throws ProtocolException {
        con.setRequestMethod(method);
    }

    void setHeaders(Map<String,String> headers) {


        for(String key : headers.keySet()) {

            String value = headers.get(key);

            writer.append(key).append(": ").append(value).append(LINE_FEED);
            writer.flush();
//            con.setRequestProperty(header.key, header.value)
        }
    }

    // This is where the fun begins

    void addFormField(String name, String value) {
        writer.append( "--" + boundary + LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\""+name+"\"" + LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset + LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value + LINE_FEED);
        writer.flush();

    }

    void addFile(String fieldName, File file, OnPercentUploadedHandler percentUploaded) throws IOException {
        String name = file.getName();


        writer.append("--" + boundary + LINE_FEED);
        writer.append("Content-Disposition: file; name=\""+fieldName+"\";filename=\""+name+"\"" + LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(name) + LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary" + LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        long fileBytes = file.length();
        int percentComplete = 0;
        int totalBytesUploaded = 0;

        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while(true) {
            bytesRead = inputStream.read(buffer);
            if(bytesRead == -1) break;


            outputStream.write(buffer, 0, bytesRead);

            totalBytesUploaded += bytesRead;

            int newPercentComplete = (int)(totalBytesUploaded / fileBytes);
            if( newPercentComplete != percentComplete ) {
                percentComplete = newPercentComplete;
                percentUploaded.onPercentUploaded(percentComplete);
            }

        }

        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();

    }


    @Override
    public void close() {

        closed = true;

//        writer + LINE_FEED
        writer.flush();
        writer.append("--" + boundary + "--" + LINE_FEED);
        writer.close();
    }


    private Long copyTo(Reader reader, Writer out) throws IOException {
        int bufferSize = 8 * 1024;
        Long charsCopied = 0L;
        char[] buffer = new char[bufferSize];
        int chars = reader.read(buffer);
        while (chars >= 0) {
            out.write(buffer, 0, chars);
            charsCopied += chars;
            chars = reader.read(buffer);
        }
        return charsCopied;
    }


    private String readText(Reader reader) throws IOException {
        StringWriter buffer = new StringWriter();
        copyTo(reader, buffer);
        return buffer.toString();
    }

    public String output() throws IOException {

        if(!closed) close();

        InputStream iStream = ((getResponseCode() == 200)  ? con.getInputStream() : con.getErrorStream());

        String rawData = "";

        try {

            BufferedReader bf = new BufferedReader(new InputStreamReader(iStream)); //.bufferedReader()
            rawData = readText(bf);

        }
        catch (InterruptedIOException e) {
            Log.e(TAG, "InputStream IO interrupted");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(TAG, "Error reading InputStream");
            e.printStackTrace();

        }
        finally {


            if (!rawData.equals("")) {
                try {
                    iStream.close();
                }
                catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }

            }
            con.disconnect();
        }
        return rawData;
    }



}
//
//operator fun PrintWriter.plus(string: String): PrintWriter {
//    return append(string)
//}
//
//operator fun PrintWriter.plus(other: Any): PrintWriter {
//    return append(other.toString())
//}

//operator fun String.plus(printWriter: PrintWriter) {
//    printWriter.append(this)
//}

