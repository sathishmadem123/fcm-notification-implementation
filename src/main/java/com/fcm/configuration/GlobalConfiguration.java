package com.fcm.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.entity.FcmRecipient;
import com.fcm.entity.FcmTopic;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class GlobalConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        Converter<Set<FcmTopic>, Set<String>> topicConverter =
                ctx -> ctx.getSource() == null ? Set.of() :
                        ctx.getSource().stream()
                                .map(FcmTopic::getName)
                                .collect(Collectors.toSet());

        modelMapper.typeMap(FcmRecipient.class, FcmRecipientDTO.class)
                .addMappings(mapper ->
                        mapper.using(topicConverter).map(FcmRecipient::getTopics, FcmRecipientDTO::setTopics));

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
