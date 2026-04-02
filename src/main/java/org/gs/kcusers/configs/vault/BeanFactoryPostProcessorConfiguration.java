package org.gs.kcusers.configs.vault;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class BeanFactoryPostProcessorConfiguration {
    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return bf -> {
            Arrays.stream(bf.getBeanNamesForType(ServerProperties.class))
                    .map(bf::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("VaultDataImport"));
        };
    }
}
