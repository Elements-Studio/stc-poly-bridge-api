package org.starcoin.polybridge.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.polybridge.data.model.VoteRewardProcess;
import org.starcoin.polybridge.data.repo.VoteRewardProcessRepository;
import org.starcoin.polybridge.service.VoteRewardProcessService;

import java.util.List;

@Service
public class VoteRewardProcessTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessTaskService.class);

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private VoteRewardProcessService voteRewardProcessService;

    @Scheduled(fixedDelayString = "${airdrop.vote-reward-process-task-service.fixed-delay}")
    public void task() {



    }

}
