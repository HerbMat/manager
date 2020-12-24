package com.home.transaction.manager

import com.mongodb.reactivestreams.client.MongoClient
import de.flapdoodle.embed.mongo.config.IMongodConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MongoTestConfig {

    @Autowired
    private IMongodConfig mongodConfig

    @Autowired
    private MongoClient mongoClient

    @Bean
    DataLoader dataLoader() {
        return new DataLoader(mongodConfig, "test")
    }
}
