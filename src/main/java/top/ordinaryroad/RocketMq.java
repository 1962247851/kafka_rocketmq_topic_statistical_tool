package top.ordinaryroad;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.ResponseBody;
import top.ordinaryroad.constant.RocketMQConstants;
import top.ordinaryroad.util.OkHttpUtil;
import top.ordinaryroad.util.PoiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 苗锦洲
 * @date 2021/7/26
 */
public class RocketMq {

    public static void main(String[] args) {
        // topic, [consumers]
        Map<String, List<String>> resultMap = new TreeMap<>();
        try {
            Response post = OkHttpUtil.post(RocketMQConstants.API_LOGIN_URL);
            ResponseBody body = post.body();
            if (body != null) {
                JSONObject jsonObject = JSON.parseObject(body.string());
                if (jsonObject.getInteger("status") == 0) {
                    Response response = OkHttpUtil.get(RocketMQConstants.API_LIST_TOPIC_URL);
                    ResponseBody body1 = response.body();
                    if (body1 != null) {
                        JSONObject jsonObject1 = JSON.parseObject(body1.string());
                        if (jsonObject1.getInteger("status") == 0) {
                            List<String> topicList = jsonObject1.getJSONObject("data")
                                    .getJSONArray("topicList")
                                    .toJavaList(String.class);
                            for (String topic : topicList) {
                                String pattern = "%(.+)%";
                                Pattern r = Pattern.compile(pattern);
                                Matcher m = r.matcher(topic);
                                if (m.find()) {
//                                    System.out.println(m.group(0));
                                } else {
                                    List<String> consumersList = new ArrayList<>();
                                    System.out.println("Topic：" + topic);
                                    // 查询消费者信息
                                    Response consumerResponse = OkHttpUtil.get(RocketMQConstants.API_QUERY_CONSUMER_URL + topic);
                                    ResponseBody body2 = consumerResponse.body();
                                    if (body2 != null) {
                                        JSONObject consumerObject = JSON.parseObject(body2.string());
                                        if (consumerObject.getInteger("status") == 0) {
                                            JSONObject data = consumerObject.getJSONObject("data");
                                            if (data.size() == 0) {
                                                System.out.println("无消费者");
                                            } else {
                                                System.out.println("消费者：");
                                                data.forEach((groupId, o) -> {
                                                    // 过滤规则
                                                    if (groupId.contains(":")) {
                                                        System.out.print("跳过：" + groupId);
                                                    } else {
                                                        consumersList.add(groupId);
                                                        System.out.println(groupId);
                                                    }
                                                });
                                            }
                                            consumersList.sort(String::compareTo);
                                            resultMap.put(topic, consumersList);
                                            System.out.println();
                                        }
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
                            PoiUtil.writeTopicsToExcel(RocketMQConstants.EXCEL_FILE_PATH, map);
                            // 写入多个Excel文件
                            PoiUtil.writeTopicsToMultiExcel(RocketMQConstants.EXCEL_FILE_PATH, map);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 解析RocketMQTopic
     *
     * @param rocketMqTopic rocketMqTopic
     * @return 哪个环境
     */
    private static String getEnvironmentCode(String rocketMqTopic) {
        String env = "unknown";
        if (rocketMqTopic.endsWith("_prod")) {
            return "prod";
        }
        if (rocketMqTopic.endsWith("_canary") || rocketMqTopic.endsWith("_canay")) {
            return "canary";
        }
        return env;
    }

}
