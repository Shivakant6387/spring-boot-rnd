package org.example.springbootrndproject.repository;

import org.example.springbootrndproject.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
