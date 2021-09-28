package top.ordinaryroad.util;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ordinaryroad.constant.OkHttpConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author 苗锦洲
 * @date 2021/7/26
 */
public class OkHttpUtil {
    private static final OkHttpClient OK_HTTP_CLIENT;

    public static Response get(@NotNull String url) throws IOException {
        return get(url, null);
    }

    public static Response get(@NotNull String url, @Nullable Map<String, Object> params) throws IOException {
        Request request = new Request.Builder()
                .url(url + getUrlParamsByMap(params))
                .method("GET", null)
                .build();
        return OK_HTTP_CLIENT.newCall(request).execute();
    }

    public static Response post(@NotNull String url) throws IOException {
        return post(url, null);
    }

    public static Response post(@NotNull String url, @Nullable Map<String, String> params) throws IOException {
        FormBody.Builder body = new FormBody.Builder();
        if (params != null) {
            params.forEach(body::add);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();
        return OK_HTTP_CLIENT.newCall(request).execute();
    }

    static {
        OK_HTTP_CLIENT = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(
                        new Cache(
                                new File(OkHttpConstants.CACHE_FILE_PATHNAME),
                                OkHttpConstants.CACHE_SIZE
                        )
                )
                .cookieJar(new CookieJarImpl())
                .build();
    }

    private static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder params = new StringBuilder("?");
        map.forEach((key, value) -> {
            if (value instanceof List) {
                ((List<?>) value).forEach((Consumer<Object>) o -> params.append(key).append("=").append(o).append("&"));
            } else {
                params.append(key).append("=").append(value).append("&");
            }
        });
        String str = params.toString();
        return str.substring(0, str.length() - 1);
    }

    public static class CookieJarImpl implements CookieJar {

        public static final Map<String, List<Cookie>> COOKIE_STORE = new HashMap<>();

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            return COOKIE_STORE.getOrDefault(httpUrl.host(), new ArrayList<>());
        }

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            COOKIE_STORE.put(httpUrl.host(), list);
        }
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("a", 1);
        List<String> arrayList = new ArrayList<>();
        arrayList.add("2");
        arrayList.add("3");
        params.put("b", arrayList);
        System.out.println(getUrlParamsByMap(params));
    }

}
