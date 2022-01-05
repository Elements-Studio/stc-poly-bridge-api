package org.starcoin.polybridge.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class VoteRewardProcessTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessTaskService.class);


    @Scheduled(fixedDelayString = "${poly-bridge.vote-reward-process-task-service.fixed-delay}")
    public void task() {


    }

}
