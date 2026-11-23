package com.mosca.pneus.repository;

import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusPneu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PneuRepository extends JpaRepository<Pneu, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByEquipamento_Id(Long equipamentoId);

    Optional<Pneu> findByEquipamento_IdAndPosicao(Long equipamentoId, Integer posicao);

    List<Pneu> findByStatusAndEquipamentoIsNullOrderByCodigoAsc(StatusPneu status);
}
