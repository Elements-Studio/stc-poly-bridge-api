package org.starcoin.polybridge.api.controller;

import com.opencsv.CSVWriter;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.starcoin.polybridge.data.model.VoteRewardProcess;
import org.starcoin.polybridge.service.VoteRewardProcessService;
import org.starcoin.polybridge.service.VoteRewardService;
import org.starcoin.polybridge.vo.VoteRewardProcessVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = {"PolyBridge Java RESTful API"})
@RestController
@RequestMapping("v1/poly-bridge")
public class PolyBridgeController {
    private static final Logger LOG = LoggerFactory.getLogger(PolyBridgeController.class);

    @Resource
    private VoteRewardService voteRewardService;

    @Resource
    private VoteRewardProcessService voteRewardProcessService;


    @GetMapping("voteRewardProcesses/{processId}")
    public VoteRewardProcess getVoteRewardProcess(@PathVariable("processId") Long processId) {
        return voteRewardProcessService.getVoteRewardProcess(processId);
    }


    @PostMapping("createVoteRewardProcess")
    public VoteRewardProcess createVoteRewardProcess(@RequestParam("proposalId") String proposalId,
                                                     @RequestParam(value = "onChainDisabled", required = false) Boolean onChainDisabled) {
        return voteRewardProcessService.createVoteRewardProcessByProposalId(proposalId, onChainDisabled == null || onChainDisabled);
    }

    @PostMapping("voteRewardProcesses")
    public VoteRewardProcess postVoteRewardProcess(@RequestBody VoteRewardProcessVO voteRewardProcess) {
        if (voteRewardProcess.getOnChainDisabled() == null) {
            voteRewardProcess.setOnChainDisabled(true);// default DISABLE on-chain operations!!!
        }
        long proposalProcessSeqNumber = voteRewardProcess.getOnChainDisabled()
                ? System.currentTimeMillis()
                : VoteRewardProcess.DEFAULT_PROPOSAL_PROCESS_SEQ_NUMBER;
        voteRewardProcess.setProposalProcessSeqNumber(proposalProcessSeqNumber);
        return voteRewardProcessService.createVoteRewardProcess(voteRewardProcess);
    }


    @PostMapping("resetVoteRewardProcess")
    public void resetVoteRewardProcess(@RequestParam("processId") Long processId) {
        voteRewardProcessService.resetVoteRewardProcess(processId);
    }

    @GetMapping("exportPolyBridgeJson")
    public void exportPolyBridgeJson(HttpServletResponse response,
                                  @RequestParam("processId") Long processId
    ) throws IOException {
        VoteRewardProcess voteRewardProcess = voteRewardProcessService.findByIdOrElseThrow(processId);
        if (voteRewardProcess.isProcessing()) {
            //response.sendError(HttpServletResponse.SC_NOT_FOUND);
            //return;
            LOG.info("Process is processing: " + processId);
        }
        Long proposalId = voteRewardProcess.getProposalId();

        // set file name and content type
        String filename = "airdrop" + processId + ".json";
        response.setContentType("text/json");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");
        Writer writer = response.getWriter();
        writer.write(voteRewardProcess.getPolyBridgeJson());
        writer.close();
    }

    @GetMapping("exportRewardCsv")
    public void exportRewardCsv(HttpServletResponse response,
                                @RequestParam("processId") Long processId
    ) throws IOException {
        VoteRewardProcess voteRewardProcess = voteRewardProcessService.findByIdOrElseThrow(processId);
        if (voteRewardProcess.isProcessing()) {
            //response.sendError(HttpServletResponse.SC_NOT_FOUND);
            //return;
            LOG.info("Process is processing: " + processId);
        }
        Long proposalId = voteRewardProcess.getProposalId();

        // set file name and content type
        String filename = "rewards" + processId + ".csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");
        Writer writer = response.getWriter();
        // create a csv writer
        // header record
        String[] headerRecord = {"voter", "reward_amount"};
        // create a csv writer
        CSVWriter csvWriter = new CSVWriter(writer);
//                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
//                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
//                .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
//                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
//                .build();
        List<Map<String, Object>> rewards = voteRewardService.sumRewardAmountGroupByVoter(proposalId);
        rewards.stream().map(m -> {
            List<String> cells = new ArrayList<>();
            for (String h : headerRecord) {
                cells.add(m.get(h).toString());
            }
            return cells.toArray(new String[0]);
        }).forEach(r -> csvWriter.writeNext(r));
        csvWriter.close();
        writer.close();
    }

}
