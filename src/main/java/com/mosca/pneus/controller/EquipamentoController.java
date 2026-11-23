package com.mosca.pneus.controller;

import com.mosca.pneus.domain.Equipamento;
import com.mosca.pneus.service.EquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipamentos")
@Tag(name = "Equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    @Operation(summary = "Lista equipamentos")
    public List<Equipamento> listar() {
        return equipamentoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um equipamento")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Equipamento equipamento = equipamentoService.buscar(id);
        if (equipamento == null) {
            return Respostas.erro("Equipamento não encontrado");
        }
        return ResponseEntity.ok(equipamento);
    }

    @PostMapping
    @Operation(summary = "Cadastra um equipamento")
    public ResponseEntity<?> criar(@RequestBody Equipamento equipamento) {
        String erro = equipamentoService.criar(equipamento);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.created(URI.create("/api/equipamentos/" + equipamento.getId())).body(equipamento);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um equipamento")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Equipamento equipamento) {
        String erro = equipamentoService.atualizar(id, equipamento);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.ok(equipamentoService.buscar(id));
    }

    @PostMapping("/{id}/posicoes/{posicao}")
    @Operation(summary = "Monta um pneu em estoque na posição")
    public ResponseEntity<?> vincular(@PathVariable Long id, @PathVariable int posicao, @RequestBody Map<String, Long> body) {
        String erro = equipamentoService.vincular(id, posicao, body.get("pneuId"));
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.ok(equipamentoService.buscar(id));
    }

    @DeleteMapping("/{id}/posicoes/{posicao}")
    @Operation(summary = "Tira o pneu da posição e devolve ao estoque")
    public ResponseEntity<?> retirar(@PathVariable Long id, @PathVariable int posicao) {
        String erro = equipamentoService.retirar(id, posicao);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.ok(equipamentoService.buscar(id));
    }

    @PostMapping("/{id}/troca")
    @Operation(summary = "Troca a posição de pneus montados")
    public ResponseEntity<?> trocar(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        String erro = equipamentoService.trocar(id, body.get("posicaoOrigem"), body.get("posicaoDestino"));
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.ok(equipamentoService.buscar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um equipamento")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        String erro = equipamentoService.excluir(id);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.noContent().build();
    }
}
