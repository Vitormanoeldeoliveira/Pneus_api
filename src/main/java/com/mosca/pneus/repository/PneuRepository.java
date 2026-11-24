package com.mosca.pneus.repository;

import com.mosca.pneus.domain.Pneu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PneuRepository extends JpaRepository<Pneu, Long>, JpaSpecificationExecutor<Pneu> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByEquipamento_Id(Long equipamentoId);

    Optional<Pneu> findByEquipamento_IdAndPosicao(Long equipamentoId, Integer posicao);
}
