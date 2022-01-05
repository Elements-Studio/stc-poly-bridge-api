package org.starcoin.polybridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.polybridge.data.repo.StarcoinEventRepository;
import org.starcoin.polybridge.service.ElasticSearchService;
import org.starcoin.polybridge.service.StarcoinVoteChangedEventService;

@SpringBootTest
class PolyBridgeApplicationTests {


    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    StarcoinVoteChangedEventService starcoinVoteChangedEventService;

    @Autowired
    StarcoinEventRepository starcoinEventRepository;


    @Test
    void contextLoads() {


        starcoinVoteChangedEventService.findESEventsAndSave(0L,
                "0xb2aa52f94db4516c5beecef363af850a",
                0, Long.MAX_VALUE);

    }

}
