package com.example.kstream.application.kafka.streams;

import com.example.kstream.application.aop.TopicDlqMetadata;
import com.example.kstream.application.kafka.streams.transformer.ExceptionTransformerSupplier;
import com.example.kstream.application.proxy.FirstServiceProxy;
import com.example.kstream.common.constantes.HeaderConstantes;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private final String topicInput;
    private final String topicOutput;
    private final FirstServiceProxy firstService;

    // TopicNameExtractor topic dlq name from header
    private final TopicNameExtractor <String, ?> topicNameExtractorByHeader = (key, value, recordContext) -> {
        RecordHeader headerTopicName = (RecordHeader) Arrays.stream(recordContext.headers().toArray())
                .filter(header -> HeaderConstantes.TOPIC_OUTPUT.equals(header.key())).collect(Collectors.toList()).get(0);
        return new String(headerTopicName.value(), StandardCharsets.UTF_8);
    };

    public KafkaStreamsConfig(@Value("${custom.kafka.streams.topic.input.name}") String topicInput,
                              @Value("${custom.kafka.streams.topic.output.name}") String topicOutput,
                              final FirstServiceProxy firstService) {
        this.topicInput = topicInput;
        this.topicOutput = topicOutput;
        this.firstService = firstService;
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {

        final KStream<String, String> stream = streamsBuilder.stream(topicInput);

        // DeepClone to duplicate object into the Tuple Pair
        final KStream<String, Pair<String, ?>> streamObject = stream.mapValues(
                demandeDepot -> new Pair<>(demandeDepot,
                        SerializationUtils.clone(demandeDepot)),
                Named.as("deep_clone_message_to_pair")
        );

        // Call to FirstService Service 1
        final KStream<String, Pair<String, ?>> successService1 = applyServiceWithExceptionPolicy(streamObject, firstService::service1, "FirstService_service1");

        // Call to FirstService Service 2
        final KStream<String, Pair<String, ?>> successService2 = applyServiceWithExceptionPolicy(successService1, firstService::service2, "FirstService_service2");

        // mapvalues to get Value1 from Pair (just before publish)
        final KStream<String, String> successMapToString = successService2.mapValues(value -> (String) value.getValue1());

        // Publish to topicOutput for success case
        successMapToString.to(topicOutput);

        return stream;
    }


    /**
     * Generic method for triggering calls to business services
     * This method mutualizes error management and publications in reject/retry topics, based on
     * exceptions returned by the business services
     * @param streamInit: stream "starting point" before launching the service
     * @param service: description of the function to be triggered
     * @return the stream of the success case
     */
    private KStream<String, Pair<String, ?>> applyServiceWithExceptionPolicy(
            KStream<String, Pair<String, ?>> streamInit,
            UnaryOperator<Pair<String, ?>> service,
            String serviceName) {

        ////////////////////////
        //    Service call    //
        ////////////////////////

        // Returns in element 2 of the Pair
        // EITHER the altered object
        // EITHER a TopicsExceptionMetadata object => triggers a publication to a reject or replay topic
        final KStream<String, Pair<String, ?>> streamAppelService = streamInit
                // Triggers the function given in parameter
                // In our case, corresponds to a call for services
                .mapValues(service::apply, Named.as(serviceName));

        // We split into several branches (between each service call) to dispatch to the topics
        final KStream<String, Pair<String, ?>>[] streamsAppelService1 = streamAppelService
            .branch(
                (k,v) -> v.getValue1() instanceof TopicDlqMetadata,
                (k,v) -> v != null
            );

        streamsAppelService1[0]
                .transform(new ExceptionTransformerSupplier(), Named.as("add_header_after_" + serviceName + "_to_topics_exceptions"))
                .to((TopicNameExtractor<String, String>) topicNameExtractorByHeader);

        return streamsAppelService1[1];
    }
}

