package top.ordinaryroad.constant;

/**
 * Kafka参数
 *
 * @author mjz
 * @date 2021/9/28
 */
public class KafkaConstants {
    /**
     * TODO 修改Kafka Manager CMAK地址
     */
    private static final String CMAK_DOMAIN = "";
    /**
     * TODO 修改集群名称
     */
    private static final String CLUSTER_NAME = "";
    public static final String API_URL = "http://" + CMAK_DOMAIN + "/clusters/" + CLUSTER_NAME + "/topics";
    /**
     * TODO 浏览器登录，查cookie
     */
    public static final String AUTHENTICATION_VALUE = "YWRtaW5hZG1pbg==";
    public static final String AUTHENTICATION_DOMAIN = CMAK_DOMAIN;
    /**
     * TODO 修改文件保存路径
     */
    public static final String EXCEL_FILE_PATH = "G:\\xxx\\Topic列表-kafka.xlsx";
}
