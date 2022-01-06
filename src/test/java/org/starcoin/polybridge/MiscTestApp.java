package org.starcoin.polybridge;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static org.starcoin.jsonrpc.client.JSONRPC2Session.JSON_MEDIA_TYPE;

public class MiscTestApp {

    public static void main(String[] args) {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        String reqBodyData = "{\"SrcChainId\":2,\"SwapTokenHash\":\"dac17f958d2ee523a2206206994597c13d831ec7\",\"Hash\":\"0000000000000000000000000000000000000000\",\"DstChainId\":3}";
        String url = "https://bridge.poly.network/v1/getfee";
        postRequestAndPrintResponse(okHttpClient, reqBodyData, url);

        reqBodyData = "{}";
        url = "https://bridge.poly.network/v1/tokenbasics";
        postRequestAndPrintResponse(okHttpClient, reqBodyData, url);

        reqBodyData = "{\n" +
                "    \"ChainId\": 2,\n" +
                "    \"Hash\": \"dac17f958d2ee523a2206206994597c13d831ec7\"\n" +
                "}";
        url = "https://bridge.poly.network/v1/tokenmap";
        postRequestAndPrintResponse(okHttpClient, reqBodyData, url);

        reqBodyData = "{\n" +
                "  \"SrcChainId\": 3,\n" +
                "  \"DstChainId\": 2\n" +
                "}";
        url = "https://bridge.poly.network/v1/expecttime";
        postRequestAndPrintResponse(okHttpClient, reqBodyData, url);
    }

    private static void postRequestAndPrintResponse(OkHttpClient okHttpClient, String reqBodyData, String url) {
        System.out.println("---------------- " + url + " -------------------");
        System.out.println("Request:");
        System.out.println(reqBodyData);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, reqBodyData);
        Request okRequest = new Request.Builder().post(body).url(url).build();
        try {
            Response response = okHttpClient.newCall(okRequest).execute();
            System.out.println("Response:");
            System.out.println(response.body().string());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
