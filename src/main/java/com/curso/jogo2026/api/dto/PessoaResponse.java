package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;
import java.time.LocalDate;

public record PessoaResponse(Long id, String nome, String email, String cpf, LocalDate dataCadastro, Status status) { }
