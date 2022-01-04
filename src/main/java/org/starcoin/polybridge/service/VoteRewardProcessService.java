package org.starcoin.polybridge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.polybridge.data.model.StarcoinVoteChangedEvent;
import org.starcoin.polybridge.data.model.VoteRewardProcess;
import org.starcoin.polybridge.data.repo.StarcoinEventRepository;
import org.starcoin.polybridge.data.repo.VoteRewardProcessRepository;
import org.starcoin.polybridge.data.repo.VoteRewardRepository;
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleTree;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.starcoin.polybridge.data.model.VoteRewardProcess.MAX_NAME_LENGTH;

@Service
public class VoteRewardProcessService {
    public static final Long NO_AIRDROP_ID = -1L;
    public static final long CLAIM_REWARD_TIME_LIMIT_MILLISECONDS = 14 * 24L * 60 * 60 * 1000;

    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessService.class);

    //todo config???
    private static final BigInteger TOTAL_REWARD_AMOUNT_LIMIT = BigInteger.valueOf(20000L).multiply(BigInteger.TEN.pow(9));

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private StarcoinVoteChangedEventService starcoinVoteChangedEventService;

    @Autowired
    private VoteRewardService voteRewardService;

    @Autowired
    private StarcoinEventRepository starcoinEventRepository;

    @Autowired
    private VoteRewardRepository voteRewardRepository;


    @Autowired
    private StarcoinProposalService starcoinProposalService;

    public VoteRewardProcess getVoteRewardProcess(Long processId) {
        return voteRewardProcessRepository.findById(processId).orElse(null);
    }

    public VoteRewardProcess createVoteRewardProcess(VoteRewardProcess src) {
        if (src.getName() == null || src.getName().isEmpty())
            throw new IllegalArgumentException("Process name is null.");
        if (src.getChainId() == null) throw new IllegalArgumentException("Chain Id is null.");
        if (src.getVoteStartTimestamp() == null)
            throw new IllegalArgumentException("Start time is null.");
        if (src.getVoteEndTimestamp() == null)
            throw new IllegalArgumentException("End time is null.");
        if (src.getName().length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Name is too long.");
        if (src.getProposalProcessSeqNumber() == null) {
            throw new IllegalArgumentException("Proposal Process Sequence Number is null.");
        }
        VoteRewardProcess v = new VoteRewardProcess();
        BeanUtils.copyProperties(src, v);
        v.setProcessId(null);
        v.setVersion(null);
        v.setCreatedAt(System.currentTimeMillis());
        v.setCreatedBy("admin");
        v.setUpdatedAt(v.getCreatedAt());
        v.setUpdatedBy(v.getCreatedBy());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
        return v;
    }

    public VoteRewardProcess findByIdOrElseThrow(Long processId) {
        return voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
    }


    private void processVoteRewards(VoteRewardProcess v, List<StarcoinVoteChangedEvent> events) {
        voteRewardService.addOrUpdateVoteRewards(v.getProposalId(), events);
        voteRewardService.calculateRewords(v.getProposalId(), v.getVoteEndTimestamp());
        BigInteger totalRewardAmount = voteRewardRepository.sumTotalRewardAmountByProposalId(v.getProposalId());
        if (totalRewardAmount.compareTo(TOTAL_REWARD_AMOUNT_LIMIT) > 0) {
            LOG.info("Calculated total reward amount exceed limit. " + totalRewardAmount + " > " + TOTAL_REWARD_AMOUNT_LIMIT);
            voteRewardService.adjustRewardsUnderLimit(v.getProposalId(), totalRewardAmount, TOTAL_REWARD_AMOUNT_LIMIT);
            LOG.info("Adjusted rewards under total amount limit: " + TOTAL_REWARD_AMOUNT_LIMIT);
        }
    }

    public void updateVoteRewardProcessStatusError(Long processId, String message) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
        v.setStatusError(message);
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    private void updateVoteRewardProcessingMessage(Long processId, String message) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
        v.setMessage(message);
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    private void updateVoteRewardProcessStatusProcessed(Long processId) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
        v.processed();
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    private void updateStatusProcessing(VoteRewardProcess v) {
        v.processing();
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    public VoteRewardProcess createVoteRewardProcessByProposalId(String proposalId, boolean onChainDisabled) {
        if (!onChainDisabled) {
            BigInteger maxOnChainProposalId = voteRewardProcessRepository.getMaxOnChainProposalId();
            //System.out.println(maxOnChainProposalId);
            if (maxOnChainProposalId != null && maxOnChainProposalId.compareTo(new BigInteger(proposalId)) >= 0) {
                String msg = "Proposal #" + proposalId + " already has process.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
        }
        StarcoinProposalService.Proposal proposal = starcoinProposalService.getProposalByIdOnChain(proposalId);
        VoteRewardProcess voteRewardProcess = starcoinProposalService.newVoteRewardProcess(proposal, onChainDisabled);
        //System.out.println(voteRewardProcess);
        return createVoteRewardProcess(voteRewardProcess);
    }

    /**
     * Reset process status to 'CREATED', so it can be reprocessed.
     */
    public void resetVoteRewardProcess(Long processId) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
        v.reset();
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
    }
}
