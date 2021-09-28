package top.ordinaryroad.constant;

/**
 * RocketMQ参数
 *
 * @author mjz
 * @date 2021/9/28
 */
public class RocketMQConstants {
    /**
     * TODO 修改RocketMQ Dashboard地址
     */
    private static final String ROCKER_MQ_DASHBOARD_DOMAIN = "";
    /**
     * TODO 修改RocketMQ Dashboard登录用户名
     */
    private static final String ROCKER_MQ_DASHBOARD_USERNAME = "";
    /**
     * TODO 修改RocketMQ Dashboard登录密码
     */
    private static final String ROCKER_MQ_DASHBOARD_PASSWORD = "";
    public static final String API_LOGIN_URL = "https://" + ROCKER_MQ_DASHBOARD_DOMAIN + "/login/login.do?password=" + ROCKER_MQ_DASHBOARD_PASSWORD + "&username=" + ROCKER_MQ_DASHBOARD_USERNAME;
    public static final String API_LIST_TOPIC_URL = "https://" + ROCKER_MQ_DASHBOARD_DOMAIN + "/topic/list.query";
    public static final String API_QUERY_CONSUMER_URL = "https://" + ROCKER_MQ_DASHBOARD_DOMAIN + "/topic/queryConsumerByTopic.query?topic=";
    /**
     * TODO 修改文件保存路径
     */
    public static final String EXCEL_FILE_PATH = "G:\\xxx\\Topic列表-rocketmq.xlsx";
}
