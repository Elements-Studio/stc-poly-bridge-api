package org.starcoin.polybridge.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.polybridge.data.model.StarcoinEvent;
import org.starcoin.polybridge.data.model.StarcoinVoteChangedEvent;

import javax.transaction.Transactional;
import java.util.List;

public interface StarcoinEventRepository extends JpaRepository<StarcoinEvent, String> {

    List<StarcoinVoteChangedEvent> findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(Long proposalId);

    @Modifying
    @Transactional
    @Query(value = "update starcoin_event " +
            "set status = 'DEACTIVED' " +
            "where proposal_id = :proposalId", nativeQuery = true)
    void deactiveEventsByProposalId(@Param("proposalId") Long proposalId);
}
