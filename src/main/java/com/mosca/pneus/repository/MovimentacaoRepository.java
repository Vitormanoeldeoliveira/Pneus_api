package com.mosca.pneus.repository;

import com.mosca.pneus.domain.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByOrderByDataHoraDesc();

    long countByPneu_Id(Long pneuId);
}
