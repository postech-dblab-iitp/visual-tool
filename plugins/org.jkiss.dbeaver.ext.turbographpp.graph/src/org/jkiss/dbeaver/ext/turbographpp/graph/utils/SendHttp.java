package org.jkiss.dbeaver.ext.turbographpp.graph.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SendHttp {

    public static boolean sendPost(
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

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            //System.out.println(in.readLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean sendGet(String requestUrl) {

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpConn = null;
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("Get");
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setDoOutput(true);

            OutputStreamWriter wr = null;
            wr = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");

            //            String jsonString = "{name : kim, id : id123}";
            //            JsonObject jsonData = new JsonObject();
            //            jsonData.addProperty("id", "1");

            // wr.write(new Gson().toJson(makeJsonData(requestNode, requestEdge)));
            // makeJsonData(requestNode, requestEdge);
            wr.write("update");
            wr.flush();

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            //System.out.println(in.readLine());

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
            //jsonData.addProperty("label", 1);
            //retString += "{";
            retString += gson.toJson(jsonData);
            //retString += "},";
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
        //System.out.println("(m) : " + secDiffTime);

        return retString;
    }
}
