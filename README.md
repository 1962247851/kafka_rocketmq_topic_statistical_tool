# kafka_rocketmq_topic_statistical_tool
简单的爬虫项目，抓取RocketmqMQ Dashboard和Kafka CMAK，根据TOPIC名称进行分类，最后生成单个Excel文件多个Sheet 或者 根据分类结果生成对应的单个Excel文件

# 1. 修改需要的地方（TODO），粗体为必选
## 1.1 **接口**
- top.ordinaryroad.constant.KafkaConstants
- top.ordinaryroad.constant.RocketMQConstants

## 1.2 **解析规则**
- top.ordinaryroad.Kafka.getEnvironmentCode
- top.ordinaryroad.RocketMq.getEnvironmentCode

## 1.3 Excel样式
- top.ordinaryroad.util.PoiUtil

# 2. 运行
运行 ` top.ordinaryroad.Main.main `