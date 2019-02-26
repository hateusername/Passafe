package com.sitp.passafe_httpspart;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpsManager {

    public static HttpsManager instance;

    private HttpsManager(){}

    public static HttpsManager getInstance(){
        if(instance == null){
            instance = new HttpsManager();
        }
        return instance;
    }

    public void sendKeystring(Context context, String UID, String keystring) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        URL url = new URL("https://www.ethan-cloud.cn:8443/NewServlet/Demo");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");

        OutputStream outputStream = urlConnection.getOutputStream();
        String data = "uid="+URLEncoder.encode(UID,"utf-8")+"&keystring="+URLEncoder.encode(UID,"utf-8")+"&method=add";
        outputStream.write(data.getBytes("UTF-8"));
        outputStream.close();

        urlConnection.connect();
        System.out.print(urlConnection.getResponseCode());
        /*try{
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance("BKS");
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance("BKS");

            //读取证书
            InputStream ksIn = context.getResources().getAssets().open("client.bks");
            InputStream tsIn = context.getResources().getAssets().open("truststore.bks");

            //加载证书
            keyStore.load(ksIn,"passafe@sitp".toCharArray());
            trustStore.load(tsIn,"passafe@sitp".toCharArray());
            ksIn.close();
            tsIn.close();

            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(keyStore, "passafe@sitp".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            //通过HttpsURLConnection设置链接
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);

            String url = "https://10.0.2.2:8443/NewServlet/Demo";
            URL connectUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) connectUrl.openConnection();
            //设置ip授权认证：如果已经安装该证书，可以不设置，否则需要设置
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            conn.setRequestMethod("POST");

            OutputStream outputStream = conn.getOutputStream();
            String data = "uid="+UID+"&keystring="+keystring+"&method=add";
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.close();

            conn.connect();
            System.out.print(conn.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public String queryKeystring(Context context, String UID) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        URL url = new URL("https://www.ethan-cloud.cn:8443/NewServlet/Demo");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");

        OutputStream outputStream = urlConnection.getOutputStream();
        String data = "uid="+URLEncoder.encode(UID,"utf-8")+"&method=query";
        outputStream.write(data.getBytes("UTF-8"));
        outputStream.close();

        urlConnection.connect();
        System.out.print("!!!code"+urlConnection.getResponseCode());

        InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
        BufferedReader reader = new BufferedReader(in);
        String keystring = reader.readLine();

        return keystring;
        /*try{
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance("BKS");
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance("BKS");

            //读取证书
            InputStream ksIn = context.getResources().getAssets().open("client.bks");
            InputStream tsIn = context.getResources().getAssets().open("truststore.bks");

            //加载证书
            keyStore.load(ksIn,"passafe@sitp".toCharArray());
            trustStore.load(tsIn,"passafe@sitp".toCharArray());
            ksIn.close();
            tsIn.close();

            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(keyStore, "passafe@sitp".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            //通过HttpsURLConnection设置链接
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);

            String url = "https://10.0.2.2:8443/NewServlet/Demo";
            URL connectUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) connectUrl.openConnection();
            //设置ip授权认证：如果已经安装该证书，可以不设置，否则需要设置
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            conn.setRequestMethod("POST");

            OutputStream outputStream = conn.getOutputStream();
            String data = "uid="+UID+"&method=query";
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.close();

            conn.connect();
            System.out.print(conn.getResponseCode());

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            String keystring = reader.readLine();

            return keystring;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }*/
    }

}
