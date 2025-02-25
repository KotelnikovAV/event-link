package ru.eventlink.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

//    @Bean
//    public MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String uri) {
//        return MongoClients.create(uri);
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate(MongoDatabaseFactory dbFactory) {
//        MongoTemplate mongoTemplate = new MongoTemplate(dbFactory);
//        mongoTemplate.setReadPreference(ReadPreference.secondary());
//        return mongoTemplate;
//    }
//
//    @Bean
//    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
//        return new SimpleMongoClientDatabaseFactory(mongoClient, "comments");
//    }
//
//    @Bean
//    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
}
