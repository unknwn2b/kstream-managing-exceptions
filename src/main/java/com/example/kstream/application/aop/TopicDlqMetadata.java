package com.example.kstream.application.aop;

import com.example.kstream.common.exception.ApplicativeError;

public class TopicDlqMetadata {

    private ApplicativeError applicativeError;

    private String topicName;

    public TopicDlqMetadata(String topicName, ApplicativeError applicativeError) {
        this.applicativeError = applicativeError;
        this.topicName = topicName;
    }

    public ApplicativeError getApplicativeError() {
        return applicativeError;
    }

    public String getTopicName() {
        return topicName;
    }
}
