package com.example.kstream.application.aop;

import com.example.kstream.common.exception.ApplicativeException;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * AOP to intercept ApplicativeExceptions to dispatch to dlq Topics
 */
@Aspect
@Component
class ExceptionWrapperInterceptor {

    private static Logger logger = Logger.getLogger(ExceptionWrapperInterceptor.class);

    @Value("${custom.kafka.streams.topic.dlq.retry.listApplicativeErrors}")
    private List<String> retryErrors;

    @Value("${custom.kafka.streams.topic.dlq.retry.name}")
    private String topicDlqRetry;

    @Value("${custom.kafka.streams.topic.dlq.main.name}")
    private String topicDlqMain;

    @Pointcut("within(com.example.kstream.application.proxy.impl.*) && execution(public * *(..))")
    public void onlyPublicServiceClasses() {
    }

    @Around("onlyPublicServiceClasses()")
    public Object intercept(ProceedingJoinPoint thisJoinPoint) throws Throwable {

        TopicDlqMetadata topicDlqMetadata;

        try {
            return thisJoinPoint.proceed();
        } catch (ApplicativeException ex) {
            if (retryErrors.contains(ex.getApplicativeError().toString())) {
                topicDlqMetadata = new TopicDlqMetadata(topicDlqRetry, ex.getApplicativeError());
                logger.debug("Catch to " + topicDlqRetry);
            } else {
                topicDlqMetadata = new TopicDlqMetadata(topicDlqMain, ex.getApplicativeError());
                logger.debug("Catch to " + topicDlqMain);
            }
            return ((Pair<String, ?>) thisJoinPoint.getArgs()[0]).setAt1(topicDlqMetadata);
        }
    }
}
