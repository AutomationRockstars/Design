package com.automationrockstars.gunter;

import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.automationrockstars.base.ConfigLoader.config;

public class ElasticSender {

    public static final String ES_URI_PARAM = "gunter.elastic.uri";
    public static final String ES_INDEX_PARAM = "gunter.elastic.index";
    public static final String ES_TYPE_PARAM = "gunter.elastic.type";
    public static final String ES_REST_CONNECTOR_PARAM = "gunter.elastic.rest";

    private static final String DEFAULT_INDEX = "ci";
    private static final String DEFAULT_TYPE = "jobs";

    private static final AtomicBoolean rest = new AtomicBoolean();

    private static boolean succesfull(String result){
        Preconditions.checkNotNull(result,"ElasticSearch result is null");
        return result.contains("\"result\":\"created\"");
    }
    public static void send(Event event){
        String result = null;
        if (isRest()){
            result = restSend(event);
        } else {
            result = dataSend(event);
        }
        Preconditions.checkState(succesfull(result),"Sending event %s failed due to %s",event,result);
    }

    public static String restSend(Event event){
        try {
            return Request.Post(jobsUrl()).bodyString(EventFactory.toJson(event), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        } catch (IOException e) {
            Throwables.throwIfUnchecked(e);
            return null;
        }
    }


    public static String jobsUrl(){
        return String.format("http://%s/%s/%s",
                config().getString(ES_URI_PARAM),
                config().getString(ES_INDEX_PARAM,DEFAULT_INDEX),
                config().getString(ES_TYPE_PARAM,DEFAULT_TYPE));
    }



    @VisibleForTesting
    protected static boolean canConnectViaRest(){
        try {
            if (Request.Get(jobsUrl()).execute().returnResponse().getStatusLine() != null){
                return true;
            } else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @VisibleForTesting
    protected static boolean canConnectViaData(){
        try {
                Request.Get(jobsUrl()).execute();
                return false;
        } catch (ClientProtocolException e){
            return true;
        } catch (IOException e){
            return false;
        }
    }

    private static TransportClient clientInstance;
    public static String dataSend(Event event){
        IndexRequest index = new IndexRequest(
                config().getString(ES_INDEX_PARAM,DEFAULT_INDEX),
                config().getString(ES_TYPE_PARAM,DEFAULT_TYPE),
                event.getId()).source(EventFactory.toJson(event), XContentType.JSON);
        try {
            return client().index(index).get().toString();
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            return null;
        }
    }
    @VisibleForTesting
    protected static TransportClient client(){
        if (clientInstance == null){
            try {
                URI uri = new URI("transport://"+ config().getString(ES_URI_PARAM));
                clientInstance = new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new TransportAddress(InetAddress.getByName(uri.getHost()),uri.getPort()));
            } catch (URISyntaxException | UnknownHostException e) {
                Throwables.throwIfUnchecked(e);
            }
        }
        return clientInstance;
    }
    public static boolean isRest(){
        if (config().getBoolean(ES_REST_CONNECTOR_PARAM,false) ){
            return canConnectViaRest();
        }
            if (canConnectViaData()){
                return false;
            } else if (canConnectViaRest()){
                return true;
            } else throw new RuntimeException("Cannot connect to elasticsearch");

    }

    public static void close() {
        if (clientInstance != null) {
            clientInstance.close();
            clientInstance = null;
        }
    }
}
