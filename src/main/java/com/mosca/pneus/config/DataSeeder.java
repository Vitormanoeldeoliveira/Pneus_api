package com.mosca.pneus.config;

import com.mosca.pneus.domain.Equipamento;
import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusEquipamento;
import com.mosca.pneus.domain.StatusPneu;
import com.mosca.pneus.domain.TipoEquipamento;
import com.mosca.pneus.repository.EquipamentoRepository;
import com.mosca.pneus.repository.PneuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final EquipamentoRepository equipamentoRepository;
    private final PneuRepository pneuRepository;

    public DataSeeder(EquipamentoRepository equipamentoRepository, PneuRepository pneuRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.pneuRepository = pneuRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (equipamentoRepository.count() > 0) {
            return;
        }

        Equipamento caminhao = equipamento(
                "CAM-042", "Volvo FH 540", TipoEquipamento.CAMINHAO, "RIO2A18", 6,
                StatusEquipamento.ATIVO, "Cavalo da linha Rio–BH"
        );
        Equipamento empilhadeira = equipamento(
                "EMP-007", "Yale 25", TipoEquipamento.EMPILHADEIRA, null, 4,
                StatusEquipamento.ATIVO, "Galpão 2"
        );
        Equipamento trator = equipamento(
                "TRA-015", "John Deere 7200", TipoEquipamento.TRATOR, null, 4,
                StatusEquipamento.MANUTENCAO, "Aguardando peça do eixo"
        );

        pneu("PN-1001", "Michelin", "X Multi Z", "295/80R22.5", "13.2", 48200, caminhao, 1);
        pneu("PN-1002", "Michelin", "X Multi Z", "295/80R22.5", "12.8", 51000, caminhao, 2);
        pneu("PN-1003", "Goodyear", "KMax", "295/80R22.5", "11.4", 62000, caminhao, 3);
        pneu("PN-1004", "Goodyear", "KMax", "295/80R22.5", "11.1", 64000, caminhao, 4);
        pneu("PN-1005", "Pirelli", "TR01", "295/80R22.5", "10.6", 71000, caminhao, 5);
        pneu("PN-1006", "Pirelli", "TR01", "295/80R22.5", "10.2", 73500, caminhao, 6);
        pneu("PN-1101", "Continental", "HDR2", "295/80R22.5", "16.0", 0, null, null);
        pneu("PN-1102", "Continental", "HDR2", "295/80R22.5", "15.8", 1200, null, null);
        pneu("PN-1103", "Bridgestone", "M858", "295/80R22.5", "9.4", 82000, null, null);

        pneu("PN-2001", "Solideal", "Magnum", "7.00-12", "14.0", 800, empilhadeira, 1);
        pneu("PN-2002", "Solideal", "Magnum", "7.00-12", "13.6", 900, empilhadeira, 2);
        pneu("PN-2003", "Solideal", "Magnum", "7.00-12", "13.1", 1100, empilhadeira, 3);
        pneu("PN-2004", "Solideal", "Magnum", "7.00-12", "12.4", 1400, empilhadeira, 4);

        pneu("PN-3001", "Firestone", "Performer", "480/70R28", "18.0", 2400, trator, 1);
        pneu("PN-3002", "Firestone", "Performer", "480/70R28", "17.5", 2400, trator, 2);
    }

    private Equipamento equipamento(
            String codigo,
            String nome,
            TipoEquipamento tipo,
            String placa,
            int posicoes,
            StatusEquipamento status,
            String observacao
    ) {
        Equipamento equipamento = new Equipamento();
        equipamento.setCodigo(codigo);
        equipamento.setNome(nome);
        equipamento.setTipo(tipo);
        equipamento.setPlaca(placa);
        equipamento.setQuantidadePosicoes(posicoes);
        equipamento.setStatus(status);
        equipamento.setObservacao(observacao);
        return equipamentoRepository.save(equipamento);
    }

    private Pneu pneu(
            String codigo,
            String marca,
            String modelo,
            String medida,
            String sulco,
            int km,
            Equipamento equipamento,
            Integer posicao
    ) {
        Pneu pneu = new Pneu();
        pneu.setCodigo(codigo);
        pneu.setMarca(marca);
        pneu.setModelo(modelo);
        pneu.setMedida(medida);
        pneu.setSulcoMm(new BigDecimal(sulco));
        pneu.setKm(km);
        pneu.setDataAquisicao(LocalDate.now().minusMonths(6));
        pneu.setStatus(StatusPneu.ESTOQUE);
        if (equipamento != null) {
            pneu.montar(equipamento, posicao);
        }
        return pneuRepository.save(pneu);
    }
}
