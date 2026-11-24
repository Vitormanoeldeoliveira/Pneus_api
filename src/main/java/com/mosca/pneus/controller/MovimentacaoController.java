package com.mosca.pneus.controller;

import com.mosca.pneus.domain.Movimentacao;
import com.mosca.pneus.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@Tag(name = "Movimentações")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    @Operation(summary = "Lista as saídas de pneu, da mais recente para a mais antiga")
    public List<Movimentacao> listar(
            @RequestParam(required = false) String pneu,
            @RequestParam(required = false) String origem,
            @RequestParam(required = false) String destino
    ) {
        return movimentacaoService.listar(pneu, origem, destino);
    }
}
