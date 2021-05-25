package clp.core.helpers;

import clp.core.exception.CustomException;
import clp.core.messages.HttpMessage;
import clp.core.messages.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtils {
    public static HttpMessage sendHttp(Message message, String endpoint) throws CustomException {
        try {

            URL url = new URL(endpoint);
            URLConnection connection = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            for (String s : message.getHeaders().keySet()) {
                http.setRequestProperty(s, message.getHeaderValue(s));
            }
            http.setDoOutput(true);

            if (message.getBody().equalsIgnoreCase("")) {
                http.setRequestMethod("GET");
            } else {
                http.setRequestMethod("POST");
                http.getOutputStream().write(message.getBody().getBytes());
            }


            HttpMessage responseMessage = new HttpMessage(org.apache.commons.io.IOUtils.toString(http.getInputStream()));
            responseMessage.setStatusCode(http.getResponseCode());
            for (String s : http.getHeaderFields().keySet()) {
                responseMessage.setHeader(s, http.getHeaderField(s));
            }

            http.disconnect();
            return responseMessage;

        } catch (IOException e) {
            throw new CustomException(e.getMessage());
        }

    }
}
