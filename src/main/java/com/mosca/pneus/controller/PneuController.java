package com.mosca.pneus.controller;

import com.mosca.pneus.domain.Pneu;
import com.mosca.pneus.domain.StatusPneu;
import com.mosca.pneus.service.PneuService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pneus")
@Tag(name = "Pneus")
public class PneuController {

    private final PneuService pneuService;

    public PneuController(PneuService pneuService) {
        this.pneuService = pneuService;
    }

    @GetMapping
    @Operation(summary = "Lista pneus")
    public List<Pneu> listar(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String medida,
            @RequestParam(required = false) StatusPneu status,
            @RequestParam(required = false) String equipamento
    ) {
        return pneuService.listar(codigo, marca, modelo, medida, status, equipamento);
    }

    @GetMapping("/estoque")
    @Operation(summary = "Lista pneus em estoque")
    public List<Pneu> estoque(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String medida
    ) {
        return pneuService.estoque(codigo, marca, modelo, medida);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pneu")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Pneu pneu = pneuService.buscar(id);
        if (pneu == null) {
            return Respostas.erro("Pneu não encontrado");
        }
        return ResponseEntity.ok(pneu);
    }

    @PostMapping
    @Operation(summary = "Cadastra um pneu")
    public ResponseEntity<?> criar(@RequestBody Pneu pneu) {
        String erro = pneuService.criar(pneu);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.created(URI.create("/api/pneus/" + pneu.getId())).body(pneu);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pneu")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pneu pneu) {
        String erro = pneuService.atualizar(id, pneu);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.ok(pneuService.buscar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um pneu")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        String erro = pneuService.excluir(id);
        if (erro != null) {
            return Respostas.erro(erro);
        }
        return ResponseEntity.noContent().build();
    }
}
