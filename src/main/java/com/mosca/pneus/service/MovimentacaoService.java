package com.mosca.pneus.service;

import com.mosca.pneus.domain.Movimentacao;
import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.repository.MovimentacaoRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Movimentacao> listar() {
        return movimentacaoRepository.findAllByOrderByDataHoraDesc().stream()
                .map(this::semProxy)
                .toList();
    }

    public void registrar(Pneu pneu, String origem, String destino) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setPneu(pneu);
        movimentacao.setOrigem(origem);
        movimentacao.setDestino(destino);
        movimentacaoRepository.save(movimentacao);
    }

    private Movimentacao semProxy(Movimentacao movimentacao) {
        if (movimentacao.getPneu() != null) {
            movimentacao.setPneu(Hibernate.unproxy(movimentacao.getPneu(), Pneu.class));
        }
        return movimentacao;
    }
}
