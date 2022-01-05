package org.starcoin.polybridge.vo;


import java.time.ZonedDateTime;

public class VoteRewardProcessVO {

    private ZonedDateTime voteStartDateTime;

    private ZonedDateTime voteEndDateTime;


    public ZonedDateTime getVoteStartDateTime() {
        return voteStartDateTime;
    }

    public void setVoteStartDateTime(ZonedDateTime voteStartDateTime) {
        this.voteStartDateTime = voteStartDateTime;
    }

    public ZonedDateTime getVoteEndDateTime() {
        return voteEndDateTime;
    }

    public void setVoteEndDateTime(ZonedDateTime voteEndDateTime) {
        this.voteEndDateTime = voteEndDateTime;
    }
}
