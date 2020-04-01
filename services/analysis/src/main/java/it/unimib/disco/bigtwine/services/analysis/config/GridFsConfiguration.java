package it.unimib.disco.bigtwine.services.analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class GridFsConfiguration {
    @Bean
    public GridFsTemplate gridFsTemplate(MongoDbFactory mongoDbFactory, MappingMongoConverter mappingMongoConverter) throws Exception {
        return new GridFsTemplate(mongoDbFactory, mappingMongoConverter);
    }
}
