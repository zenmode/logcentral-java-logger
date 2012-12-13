package org.zenmode.logcentral;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Logger {

    private String newMessageUrl;

    public Logger(String instanceId, String server) {
        this.newMessageUrl = newMessageUrl(server, instanceId);
    }

    public Logger(String instanceId) {
        this(instanceId, "http://logcentral.zenmode.org");
    }

    protected String newMessageUrl(String server, String instanceId) {
        return server + "/api/v1/instances/" + instanceId + "/messages";
    }

    protected HttpPost newMessageRequest(String severity, String text, Long generated) {
        HttpPost post = new HttpPost(newMessageUrl);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("severity", severity));
        nvps.add(new BasicNameValuePair("text", text));

        nvps.add(new BasicNameValuePair("generated", String.valueOf(generated)));

        post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        return post;
    }

    protected void sendNewMessage(final String severity, final String text, final Long generated) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DefaultHttpClient().execute(newMessageRequest(severity, text, generated));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void log(String severity, String text) {
        sendNewMessage(severity, text, System.currentTimeMillis());
    }

    public void trace(String text) {
        log(Severity.TRACE, text);
    }

    public void info(String text) {
        log(Severity.INFO, text);
    }

    public void debug(String text) {
        log(Severity.DEBUG, text);
    }

    public void warning(String text) {
        log(Severity.WARNING, text);
    }

    public void error(String text) {
        log(Severity.ERROR, text);
    }

    public void fatal(String text) {
        log(Severity.FATAL, text);
    }
}
