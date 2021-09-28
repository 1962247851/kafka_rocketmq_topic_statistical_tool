package top.ordinaryroad;

import okhttp3.Cookie;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import top.ordinaryroad.constant.KafkaConstants;
import top.ordinaryroad.util.OkHttpUtil;
import top.ordinaryroad.util.PoiUtil;

import java.io.IOException;
import java.util.*;

/**
 * @author 苗锦洲
 * @date 2021/7/26
 */
public class Kafka {

    public static void main(String[] args) {
        // topic, [consumers]
        Map<String, List<String>> resultMap = new TreeMap<>();

        // 设置cookie的token信息
        ArrayList<Cookie> cookieList = new ArrayList<>();
        cookieList.add(
                new Cookie.Builder()
                        .name("play-basic-authentication")
                        .value(KafkaConstants.AUTHENTICATION_VALUE)
                        .domain(KafkaConstants.AUTHENTICATION_DOMAIN)
                        .path("/")
                        .httpOnly()
                        .build()
        );
        OkHttpUtil.CookieJarImpl.COOKIE_STORE.put(KafkaConstants.AUTHENTICATION_DOMAIN, cookieList);
        try {
            Response get = OkHttpUtil.get(KafkaConstants.API_URL);
            ResponseBody body = get.body();
            if (get.code() == 200) {
                if (body != null) {
                    Document document = Jsoup.parse(body.string());
                    Element topicsTable = document.getElementById("topics-table");
                    assert topicsTable != null;
                    Elements tbody = topicsTable.getElementsByTag("tbody");
                    for (Element tbodyElement : tbody) {
                        for (Element trElement : tbodyElement.getElementsByTag("tr")) {
                            Element tdTopic = trElement.getElementsByTag("td").get(0);
                            String topic = tdTopic.text();
                            List<String> consumersList = new ArrayList<>();
                            System.out.println("Topic：" + topic);
                            Response getResponse = OkHttpUtil.get(KafkaConstants.API_URL + "/" + topic);
                            if (getResponse.code() == 200) {
                                // Consumers
                                Document consumersDocument = Jsoup.parse(Objects.requireNonNull(getResponse.body()).string());
                                // Consumers consuming from this topic
                                Element card = consumersDocument.getElementsByClass("card").get(5);
                                // System.out.println(card.html());
                                Element consumersTable = card.getElementsByClass("card-body").get(0).getElementsByClass("table").get(0);
                                Element consumersTableBody = consumersTable.getElementsByTag("tbody").get(0);
                                Elements tr = consumersTableBody.getElementsByTag("tr");
                                if (tr.isEmpty()) {
                                    System.out.println("无消费者");
                                } else {
                                    System.out.println("消费者组Id：");
                                    for (Element element : tr) {
                                        // 过滤规则
                                        String groupId = element.getElementsByTag("td").get(0).text();
                                        if (groupId.contains(":")) {
                                            System.out.print("跳过：" + groupId);
                                        } else {
                                            consumersList.add(groupId);
                                            System.out.println(groupId);
                                        }
                                    }
                                }
                                consumersList.sort(String::compareTo);
                                resultMap.put(topic, consumersList);
                                System.out.println();
                            } else {
                                System.out.println(getResponse.message());
                            }
                        }
                    }


                    // 处理map，分成多个环境
                    Map<String, Map<String, List<String>>> map = new TreeMap<>();
                    resultMap.forEach((topic, list) -> {
                        // 判断Topic是什么环境
                        String env = getEnvironmentCode(topic);
                        System.out.println("环境：" + env + "，Topic：" + topic);

                        Map<String, List<String>> stringListMap;
                        if (map.containsKey(env)) {
                            stringListMap = map.get(env);
                        } else {
                            stringListMap = new TreeMap<>();
                            map.put(env, stringListMap);
                        }
                        stringListMap.put(topic, list);
                    });

                    // 写入单个Excel文件
                    PoiUtil.writeTopicsToExcel(KafkaConstants.EXCEL_FILE_PATH, map);
                    // 写入多个Excel文件
                    PoiUtil.writeTopicsToMultiExcel(KafkaConstants.EXCEL_FILE_PATH, map);
                }
            } else {
                System.out.println(get.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 解析KafkaTopic
     *
     * @param kafkaTopic kafkaTopic
     * @return 哪个环境
     */
    private static String getEnvironmentCode(String kafkaTopic) {
        String env = "unknown";
        if (kafkaTopic.startsWith("CANARY-") || kafkaTopic.startsWith("canary-")) {
            return "canary";
        }
        if (kafkaTopic.startsWith("PROD-")) {
            return "prod";
        }
        return env;
    }

}
