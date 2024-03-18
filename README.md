# POC Springboot Kafka Stream | Managing Application Exceptions

## Description:
This POC proposes a solution for managing application exceptions in the context of a Kafka stream.

# How to launch:
**Install kafka with 4 defaults topics :** 
- inputTopic
- outputTopic
- retryTopic
- genericDlqTopic

**Next :**  
`mvn clean install`  
`mvn spring-boot:run`

**Test it:**  
Publish into inputTopic strings  
  
If string = 'error' => retryTopic  
If string = 'error_bis' => genericDlqTopic
