package org.starcoin.polybridge.api.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Starcoin Poly Bridge RESTful API"})
@RestController
@RequestMapping("v1")
public class PolyBridgeController {
    private static final Logger LOG = LoggerFactory.getLogger(PolyBridgeController.class);


//    @GetMapping("exportRewardCsv")
//    public void exportRewardCsv(HttpServletResponse response,
//                                @RequestParam("processId") Long processId
//    ) throws IOException {
//        // set file name and content type
//        String filename = "rewards" + processId + ".csv";
//        response.setContentType("text/csv");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + filename + "\"");
//        Writer writer = response.getWriter();
//        // create a csv writer
//        // header record
//        String[] headerRecord = {"voter", "reward_amount"};
//        // create a csv writer
//        CSVWriter csvWriter = new CSVWriter(writer);
////                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
////                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
////                .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
////                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
////                .build();
//        List<Map<String, Object>> rewards = voteRewardService.sumRewardAmountGroupByVoter(proposalId);
//        rewards.stream().map(m -> {
//            List<String> cells = new ArrayList<>();
//            for (String h : headerRecord) {
//                cells.add(m.get(h).toString());
//            }
//            return cells.toArray(new String[0]);
//        }).forEach(r -> csvWriter.writeNext(r));
//        csvWriter.close();
//        writer.close();
//    }

}
