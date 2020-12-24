package com.home.transaction.manager

import de.flapdoodle.embed.mongo.MongoImportStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongoImportConfigBuilder

class DataLoader {
    private IMongodConfig mongodConfig
    private String dbName

    DataLoader(IMongodConfig mongodConfig, String dbName) {
        this.mongodConfig = mongodConfig
        this.dbName = dbName
    }

    def load(String fileName, String collectionName) {
            String jsonFile=Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
            jsonFile=jsonFile.replaceFirst("file:","");
            def mongoImportStarter = MongoImportStarter.getDefaultInstance()
            def mongoImporter = mongoImportStarter.prepare(new MongoImportConfigBuilder()
                    .version(mongodConfig.version())
                    .db(dbName)
                    .net(mongodConfig.net())
                    .collection(collectionName)
                    .importFile(jsonFile)
                    .jsonArray(true)
                    .upsert(true)
                    .dropCollection(true)
                    .build())
            mongoImporter.start()
    }
}
