package com.mosca.pneus.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.mosca.pneus.controller.Respostas;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.temporal.Temporal;
import java.util.Map;

@RestControllerAdvice
public class ValidacaoAdvice {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> tratar(ValidacaoException ex) {
        return Respostas.erro(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> corpo(HttpMessageNotReadableException ex) {
        return Respostas.erro(corpo(causaUtil(ex)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> argumento(MethodArgumentTypeMismatchException ex) {
        String valor = ex.getValue() == null ? "" : String.valueOf(ex.getValue());
        return Respostas.erro(invalido(ex.getName(), valor, ex.getRequiredType()));
    }

    private static Throwable causaUtil(Throwable ex) {
        InvalidFormatException formato = null;
        MismatchedInputException mapeado = null;
        JsonParseException parse = null;
        Throwable atual = ex;
        while (atual != null) {
            if (atual instanceof InvalidFormatException erro) {
                formato = erro;
            } else if (atual instanceof MismatchedInputException erro) {
                mapeado = erro;
            } else if (atual instanceof JsonParseException erro) {
                parse = erro;
            }
            atual = atual.getCause();
        }
        if (formato != null) {
            return formato;
        }
        if (mapeado != null) {
            return mapeado;
        }
        if (parse != null) {
            return parse;
        }
        return ex;
    }

    private static String corpo(Throwable causa) {
        if (causa instanceof InvalidFormatException formato) {
            return invalido(campo(formato), texto(formato.getValue()), formato.getTargetType());
        }
        if (causa instanceof MismatchedInputException mapeado) {
            if (mapeado.getPath() == null || mapeado.getPath().isEmpty()) {
                return "O corpo precisa ser um objeto JSON";
            }
            return invalido(campo(mapeado), "", mapeado.getTargetType());
        }
        if (causa instanceof JsonParseException parse) {
            String detalhe = parse.getOriginalMessage() == null ? "" : parse.getOriginalMessage();
            if (invisivel(detalhe)) {
                return "O JSON tem um caractere invisível. Apague o corpo e escreva de novo, sem colar de slide ou documento";
            }
            return "O JSON enviado não pôde ser lido. Confira vírgulas, aspas e chaves";
        }
        String mensagem = causa.getMessage() == null ? "" : causa.getMessage();
        if (mensagem.contains("Required request body is missing")) {
            return "Falta o corpo da requisição";
        }
        return "O corpo da requisição não pôde ser lido";
    }

    private static String invalido(String campo, String valor, Class<?> tipo) {
        if (valor == null || valor.isBlank()) {
            return falta(campo);
        }
        String mostrado = "\"" + valor + "\"";
        if (tipo != null && tipo.isEnum()) {
            String opcoes = opcoes(tipo);
            return switch (campo) {
                case "tipo" -> "Tipo inválido: " + mostrado + ". Use " + opcoes;
                case "status" -> "Status inválido: " + mostrado + ". Use " + opcoes;
                default -> "Valor inválido: " + mostrado + ". Use " + opcoes;
            };
        }
        if (tipo != null && Temporal.class.isAssignableFrom(tipo)) {
            return "Data de aquisição inválida: " + mostrado + ". Use o formato aaaa-mm-dd";
        }
        return switch (campo) {
            case "quantidadePosicoes" -> "Quantidade de posições inválida: " + mostrado + ". Informe um número";
            case "sulcoMm" -> "Sulco inválido: " + mostrado + ". Informe um número";
            case "km" -> "Quilometragem inválida: " + mostrado + ". Informe um número";
            case "posicao" -> "Posição inválida: " + mostrado + ". Informe um número";
            case "id" -> "Id inválido: " + mostrado + ". Informe um número";
            case "pneuId" -> "Pneu inválido: " + mostrado + ". Informe o id";
            case "posicaoOrigem" -> "Posição de origem inválida: " + mostrado + ". Informe um número";
            case "posicaoDestino" -> "Posição de destino inválida: " + mostrado + ". Informe um número";
            default -> "Valor inválido: " + mostrado;
        };
    }

    private static String falta(String campo) {
        return switch (campo) {
            case "codigo" -> "Falta o código";
            case "nome" -> "Falta o nome";
            case "tipo" -> "Falta o tipo";
            case "status" -> "Falta o status";
            case "quantidadePosicoes" -> "Falta a quantidade de posições";
            case "marca" -> "Falta a marca";
            case "modelo" -> "Falta o modelo";
            case "medida" -> "Falta a medida";
            case "sulcoMm" -> "Falta o sulco";
            case "km" -> "Falta a quilometragem";
            case "pneuId" -> "Informe o pneu";
            case "posicaoOrigem", "posicaoDestino" -> "Informe as duas posições";
            default -> "Falta um valor obrigatório";
        };
    }

    private static String campo(JsonMappingException ex) {
        String nome = "";
        for (JsonMappingException.Reference referencia : ex.getPath()) {
            if (referencia.getFieldName() != null) {
                nome = referencia.getFieldName();
            }
        }
        return nome;
    }

    private static String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private static String opcoes(Class<?> tipo) {
        Object[] valores = tipo.getEnumConstants();
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                texto.append(i == valores.length - 1 ? " ou " : ", ");
            }
            texto.append(valores[i]);
        }
        return texto.toString();
    }

    private static boolean invisivel(String mensagem) {
        int inicio = mensagem.indexOf("(code ");
        if (inicio < 0) {
            return false;
        }
        int fim = mensagem.indexOf(')', inicio);
        if (fim < 0) {
            return false;
        }
        String trecho = mensagem.substring(inicio + 6, fim).trim().split(" ")[0];
        try {
            int code = Integer.parseInt(trecho);
            return code > 127 || Character.isISOControl(code) || Character.getType(code) == Character.FORMAT;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
