package com.mosca.pneus.config;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer vazioViraNulo() {
        return builder -> builder.postConfigurer(mapper -> {
            for (LogicalType tipo : new LogicalType[] {
                    LogicalType.Enum,
                    LogicalType.Integer,
                    LogicalType.Float,
                    LogicalType.Boolean,
                    LogicalType.DateTime
            }) {
                mapper.coercionConfigFor(tipo)
                        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
            }
        });
    }
}
