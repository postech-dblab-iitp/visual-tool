package org.jkiss.dbeaver.ext.turbographpp.graph.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class SendHttp {

    public static boolean sendPost(
            String requestUrl,
            HashMap<String, Vertex<CypherNode>> requestNode,
            HashMap<String, FxEdge<CypherEdge, CypherNode>> requestEdge) {
        try {
            X509TrustManager trustManager =
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] xcs, String string)
                                throws CertificateException {}

                        public void checkServerTrusted(X509Certificate[] xcs, String string)
                                throws CertificateException {}

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    };
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] {trustManager}, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

            URL url = new URL(requestUrl);
            HttpsURLConnection httpConn = null;
            httpConn = (HttpsURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setDoOutput(true);

            OutputStreamWriter wr = null;
            wr = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");

            wr.write(makeJsonData(requestNode, requestEdge));
            wr.flush();
            wr.close();

            InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            isr.close();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean sendPostHttp(
            String requestUrl,
            HashMap<String, Vertex<CypherNode>> requestNode,
            HashMap<String, FxEdge<CypherEdge, CypherNode>> requestEdge) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpConn = null;
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setDoOutput(true);

            OutputStreamWriter wr = null;
            wr = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");

            wr.write(makeJsonData(requestNode, requestEdge));
            wr.flush();
            wr.close();

            InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            isr.close();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static String makeJsonData(
            HashMap<String, Vertex<CypherNode>> requestNode,
            HashMap<String, FxEdge<CypherEdge, CypherNode>> requestEdge) {
        long beforeTime = System.currentTimeMillis();

        Gson gson = new Gson();
        String retString = "";
        int count = 0;

        retString += "{\"nodes\":[";
        for (String nodeKey : requestNode.keySet()) {
            JsonObject jsonData = new JsonObject();
            count++;
            jsonData.addProperty("id", requestNode.get(nodeKey).element().getID());
            jsonData.addProperty("label", requestNode.get(nodeKey).element().getLabelsString());
            jsonData.addProperty("display", requestNode.get(nodeKey).element().getDisplay());
            // jsonData.addProperty("label", 1);
            // retString += "{";
            retString += gson.toJson(jsonData);
            // retString += "},";
            if (requestNode.size() != count) {
                retString += ",";
            }
        }
        retString += "],";

        retString += "\"links\":[";
        count = 0;
        for (String edgeKey : requestEdge.keySet()) {
            JsonObject jsonData = new JsonObject();
            count++;
            jsonData.addProperty("id", requestEdge.get(edgeKey).element().getID());
            jsonData.addProperty("type", requestEdge.get(edgeKey).element().getTypes().toString());
            jsonData.addProperty("source", requestEdge.get(edgeKey).element().getStartNodeID());
            jsonData.addProperty("target", requestEdge.get(edgeKey).element().getEndNodeID());
            retString += gson.toJson(jsonData);
            if (requestEdge.size() != count) {
                retString += ",";
            }
        }
        retString += "]}";

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);
        // System.out.println("(m) : " + secDiffTime);

        return retString;
    }
}
