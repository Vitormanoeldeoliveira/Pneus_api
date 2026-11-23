package com.mosca.pneus.service;

import com.mosca.pneus.domain.Equipamento;
import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusPneu;
import com.mosca.pneus.exception.PneuException;
import com.mosca.pneus.repository.MovimentacaoRepository;
import com.mosca.pneus.repository.PneuRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PneuService {

    private final PneuRepository pneuRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public PneuService(PneuRepository pneuRepository, MovimentacaoRepository movimentacaoRepository) {
        this.pneuRepository = pneuRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Pneu> listar() {
        return pneuRepository.findAll().stream()
                .sorted(Comparator.comparing(Pneu::getCodigo))
                .map(this::semProxy)
                .toList();
    }

    public List<Pneu> estoque() {
        return pneuRepository.findByStatusAndEquipamentoIsNullOrderByCodigoAsc(StatusPneu.ESTOQUE).stream()
                .map(this::semProxy)
                .toList();
    }

    public Pneu buscar(Long id) {
        return pneuRepository.findById(id).map(this::semProxy).orElse(null);
    }

    @Transactional
    public String criar(Pneu pneu) {
        PneuException.validar(pneu);

        if (pneu.getStatus() == StatusPneu.EM_USO) {
            return "Pneu entra em uso quando é montado no equipamento";
        }

        String codigo = Textos.upper(pneu.getCodigo());

        if (pneuRepository.existsByCodigo(codigo)) {
            return "Já existe um pneu com o código " + codigo;
        }

        pneu.setCodigo(codigo);
        pneu.setEquipamento(null);
        pneu.setPosicao(null);

        if (pneu.getStatus() == null) {
            pneu.setStatus(StatusPneu.ESTOQUE);
        }

        pneu.setObservacao(Textos.trimToNull(pneu.getObservacao()));
        pneuRepository.save(pneu);
        return null;
    }

    @Transactional
    public String atualizar(Long id, Pneu dados) {
        Pneu pneu = buscar(id);

        if (pneu == null) {
            return "Pneu não encontrado";
        }

        PneuException.validar(dados);

        String codigo = Textos.upper(dados.getCodigo());

        if (pneuRepository.existsByCodigoAndIdNot(codigo, id)) {
            return "Já existe um pneu com o código " + codigo;
        }

        if (pneu.getEquipamento() == null && dados.getStatus() == StatusPneu.EM_USO) {
            return "Para colocar em uso, monte o pneu no equipamento";
        }

        pneu.setCodigo(codigo);
        pneu.setMarca(dados.getMarca().trim());
        pneu.setModelo(dados.getModelo().trim());
        pneu.setMedida(dados.getMedida().trim());
        pneu.setSulcoMm(dados.getSulcoMm());
        pneu.setKm(dados.getKm());
        pneu.setDataAquisicao(dados.getDataAquisicao());
        pneu.setObservacao(Textos.trimToNull(dados.getObservacao()));

        if (pneu.getEquipamento() == null) {
            pneu.setStatus(dados.getStatus());
        }

        return null;
    }

    @Transactional
    public String excluir(Long id) {
        Pneu pneu = buscar(id);

        if (pneu == null) {
            return "Pneu não encontrado";
        }

        if (pneu.getEquipamento() != null) {
            return "Pneu montado em " + pneu.getEquipamento().getCodigo() + ". Retire do equipamento antes de excluir";
        }

        if (movimentacaoRepository.countByPneu_Id(id) > 0) {
            return "Pneu já tem movimentação e não pode ser excluído";
        }

        pneuRepository.delete(pneu);
        return null;
    }

    private Pneu semProxy(Pneu pneu) {
        if (pneu.getEquipamento() != null) {
            pneu.setEquipamento(Hibernate.unproxy(pneu.getEquipamento(), Equipamento.class));
        }
        return pneu;
    }

}
