package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.FuncaoColaborador;
import java.time.LocalDate;

public record ColaboradorResponse(Long id, Long pessoaId, String nome, String email, String matricula,
                                  FuncaoColaborador funcao, LocalDate dataAdmissao) { }
