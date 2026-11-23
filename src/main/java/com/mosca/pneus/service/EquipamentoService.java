package com.mosca.pneus.service;

import com.mosca.pneus.domain.Equipamento;
import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusPneu;
import com.mosca.pneus.exception.EquipamentoException;
import com.mosca.pneus.repository.EquipamentoRepository;
import com.mosca.pneus.repository.PneuRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final PneuRepository pneuRepository;
    private final MovimentacaoService movimentacaoService;

    public EquipamentoService(
            EquipamentoRepository equipamentoRepository,
            PneuRepository pneuRepository,
            MovimentacaoService movimentacaoService
    ) {
        this.equipamentoRepository = equipamentoRepository;
        this.pneuRepository = pneuRepository;
        this.movimentacaoService = movimentacaoService;
    }

    public List<Equipamento> listar() {
        return equipamentoRepository.findAll().stream()
                .sorted(Comparator.comparing(Equipamento::getCodigo))
                .map(this::semProxy)
                .toList();
    }

    public Equipamento buscar(Long id) {
        return equipamentoRepository.findById(id).map(this::semProxy).orElse(null);
    }

    @Transactional
    public String criar(Equipamento dados) {
        EquipamentoException.validar(dados);

        String codigo = Textos.upper(dados.getCodigo());

        if (equipamentoRepository.existsByCodigo(codigo)) {
            return "Já existe um equipamento com o código " + codigo;
        }

        dados.setCodigo(codigo);
        dados.setNome(dados.getNome().trim());
        dados.setPlaca(Textos.upper(Textos.trimToNull(dados.getPlaca())));
        dados.setObservacao(Textos.trimToNull(dados.getObservacao()));
        dados.setPneus(new ArrayList<>());

        equipamentoRepository.save(dados);
        return null;
    }

    @Transactional
    public String atualizar(Long id, Equipamento dados) {
        Equipamento equipamento = buscar(id);

        if (equipamento == null) {
            return "Equipamento não encontrado";
        }

        EquipamentoException.validar(dados);

        String codigo = Textos.upper(dados.getCodigo());

        if (equipamentoRepository.existsByCodigoAndIdNot(codigo, id)) {
            return "Já existe um equipamento com o código " + codigo;
        }

        Integer ocupada = equipamento.getPneus().stream()
                .map(Pneu::getPosicao)
                .filter(posicao -> posicao != null)
                .max(Integer::compareTo)
                .orElse(null);

        if (ocupada != null && dados.getQuantidadePosicoes() < ocupada) {
            return "A posição " + ocupada + " está ocupada. A capacidade não pode ser menor";
        }

        equipamento.setCodigo(codigo);
        equipamento.setNome(dados.getNome().trim());
        equipamento.setTipo(dados.getTipo());
        equipamento.setPlaca(Textos.upper(Textos.trimToNull(dados.getPlaca())));
        equipamento.setQuantidadePosicoes(dados.getQuantidadePosicoes());
        equipamento.setStatus(dados.getStatus());
        equipamento.setObservacao(Textos.trimToNull(dados.getObservacao()));

        return null;
    }

    @Transactional
    public String excluir(Long id) {
        Equipamento equipamento = buscar(id);

        if (equipamento == null) {
            return "Equipamento não encontrado";
        }

        if (pneuRepository.existsByEquipamento_Id(id)) {
            return "Equipamento tem pneus vinculados. Esvazie as posições antes de excluir";
        }

        equipamentoRepository.delete(equipamento);
        return null;
    }

    @Transactional
    public String vincular(Long equipamentoId, int posicao, Long pneuId) {
        Equipamento equipamento = buscar(equipamentoId);

        if (equipamento == null) {
            return "Equipamento não encontrado";
        }

        String erro = posicaoValida(equipamento, posicao);

        if (erro != null) {
            return erro;
        }

        if (pneuRepository.findByEquipamento_IdAndPosicao(equipamentoId, posicao).isPresent()) {
            return "A posição " + posicao + " já está ocupada";
        }

        if (pneuId == null) {
            return "Informe o pneu";
        }

        Pneu pneu = pneuRepository.findById(pneuId).orElse(null);

        if (pneu == null) {
            return "Pneu não encontrado";
        }

        if (pneu.getStatus() != StatusPneu.ESTOQUE || pneu.getEquipamento() != null) {
            return "Só um pneu em estoque pode ser montado";
        }

        movimentacaoService.registrar(pneu, "estoque", lugar(equipamento, posicao));
        pneu.montar(equipamento, posicao);
        return null;
    }

    @Transactional
    public String retirar(Long equipamentoId, int posicao) {
        Equipamento equipamento = buscar(equipamentoId);

        if (equipamento == null) {
            return "Equipamento não encontrado";
        }

        String erro = posicaoValida(equipamento, posicao);

        if (erro != null) {
            return erro;
        }

        Pneu pneu = pneuRepository.findByEquipamento_IdAndPosicao(equipamentoId, posicao).orElse(null);

        if (pneu == null) {
            return "Não há pneu nesta posição";
        }

        movimentacaoService.registrar(pneu, lugar(equipamento, posicao), "estoque");
        pneu.desmontar();
        return null;
    }

    @Transactional
    public String trocar(Long equipamentoId, Integer origem, Integer destino) {
        if (origem == null || destino == null) {
            return "Informe as duas posições";
        }

        Equipamento equipamento = buscar(equipamentoId);

        if (equipamento == null) {
            return "Equipamento não encontrado";
        }

        String erro = posicaoValida(equipamento, origem);

        if (erro == null) {
            erro = posicaoValida(equipamento, destino);
        }

        if (erro != null) {
            return erro;
        }

        if (origem.equals(destino)) {
            return null;
        }

        Pneu pneuOrigem = pneuRepository.findByEquipamento_IdAndPosicao(equipamentoId, origem).orElse(null);

        if (pneuOrigem == null) {
            return "Não há pneu na posição " + origem;
        }

        Pneu pneuDestino = pneuRepository.findByEquipamento_IdAndPosicao(equipamentoId, destino).orElse(null);

        movimentacaoService.registrar(pneuOrigem, lugar(equipamento, origem), lugar(equipamento, destino));

        if (pneuDestino != null) {
            movimentacaoService.registrar(pneuDestino, lugar(equipamento, destino), lugar(equipamento, origem));
        }

        pneuOrigem.setPosicao(null);
        pneuRepository.flush();

        if (pneuDestino != null) {
            pneuDestino.setPosicao(origem);
            pneuRepository.flush();
        }

        pneuOrigem.setPosicao(destino);
        return null;
    }

    private String lugar(Equipamento equipamento, int posicao) {
        return equipamento.getCodigo() + ", posição " + posicao;
    }

    private String posicaoValida(Equipamento equipamento, int posicao) {
        if (posicao < 1 || posicao > equipamento.getQuantidadePosicoes()) {
            return "Posição " + posicao + " inválida. Este equipamento vai de 1 a " + equipamento.getQuantidadePosicoes();
        }
        return null;
    }

    private Equipamento semProxy(Equipamento equipamento) {
        equipamento.getPneus().forEach(pneu -> Hibernate.unproxy(pneu));
        return equipamento;
    }

}
