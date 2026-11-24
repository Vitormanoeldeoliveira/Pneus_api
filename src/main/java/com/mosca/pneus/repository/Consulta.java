package com.mosca.pneus.repository;

import com.mosca.pneus.domain.Equipamento;
import com.mosca.pneus.domain.Movimentacao;
import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusEquipamento;
import com.mosca.pneus.domain.StatusPneu;
import com.mosca.pneus.domain.TipoEquipamento;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class Consulta {

    private Consulta() {
    }

    public static Specification<Pneu> pneus(
            String codigo,
            String marca,
            String modelo,
            String medida,
            StatusPneu status,
            String equipamento,
            boolean soEstoque
    ) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (soEstoque) {
                filtros.add(cb.equal(root.get("status"), StatusPneu.ESTOQUE));
                filtros.add(cb.isNull(root.get("equipamento")));
            } else if (status != null) {
                filtros.add(cb.equal(root.get("status"), status));
            }
            contem(filtros, cb, root.get("codigo"), codigo);
            contem(filtros, cb, root.get("marca"), marca);
            contem(filtros, cb, root.get("modelo"), modelo);
            contem(filtros, cb, root.get("medida"), medida);
            if (!soEstoque && tem(equipamento)) {
                contem(filtros, cb, root.get("equipamento").get("codigo"), equipamento);
            }
            return juntar(cb, filtros);
        };
    }

    public static Specification<Equipamento> equipamentos(
            String codigo,
            String nome,
            String placa,
            TipoEquipamento tipo,
            StatusEquipamento status
    ) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            contem(filtros, cb, root.get("codigo"), codigo);
            contem(filtros, cb, root.get("nome"), nome);
            contem(filtros, cb, root.get("placa"), placa);
            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipo"), tipo));
            }
            if (status != null) {
                filtros.add(cb.equal(root.get("status"), status));
            }
            return juntar(cb, filtros);
        };
    }

    public static Specification<Movimentacao> movimentacoes(String pneu, String origem, String destino) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (tem(pneu)) {
                contem(filtros, cb, root.get("pneu").get("codigo"), pneu);
            }
            contem(filtros, cb, root.get("origem"), origem);
            contem(filtros, cb, root.get("destino"), destino);
            return juntar(cb, filtros);
        };
    }

    private static void contem(List<Predicate> filtros, CriteriaBuilder cb, Path<String> campo, String valor) {
        if (!tem(valor)) {
            return;
        }
        String texto = valor.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        filtros.add(cb.like(cb.lower(campo), "%" + texto + "%", '\\'));
    }

    private static boolean tem(String valor) {
        return valor != null && !valor.isBlank();
    }

    private static Predicate juntar(CriteriaBuilder cb, List<Predicate> filtros) {
        if (filtros.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(filtros.toArray(Predicate[]::new));
    }
}
