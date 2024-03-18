package com.example.kstream.application.kafka.streams.transformer;

import com.example.kstream.application.aop.TopicDlqMetadata;
import com.example.kstream.common.constantes.HeaderConstantes;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.javatuples.Pair;

import java.nio.charset.StandardCharsets;

public class ExceptionTransformerSupplier implements TransformerSupplier<String, Pair<String, ?>, KeyValue<String, String>> {

    @Override
    public Transformer<String, Pair<String, ?>, KeyValue<String, String>> get() {
        return new ExceptionTransformer();
    }

    private static class ExceptionTransformer implements Transformer<String, Pair<String, ?>, KeyValue<String, String>> {

        ProcessorContext context;

        @Override
        public void init(ProcessorContext context) {
            this.context = context;
        }

        @Override
        public KeyValue<String, String> transform(String key, Pair<String, ?> value) {
            Headers headers = context.headers();
            headers.add(HeaderConstantes.TOPIC_OUTPUT, ((TopicDlqMetadata) value.getValue1()).getTopicName().getBytes(StandardCharsets.UTF_8));
            headers.add(HeaderConstantes.EXCEPTION, ((TopicDlqMetadata) value.getValue1()).getApplicativeError().toString().getBytes(StandardCharsets.UTF_8));
            return KeyValue.pair(key, value.getValue0());
        }

        @Override
        public void close() {

        }
    }
}
