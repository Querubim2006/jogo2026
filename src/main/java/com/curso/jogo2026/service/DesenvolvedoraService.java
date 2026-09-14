package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.DesenvolvedoraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesenvolvedoraService {

    private final DesenvolvedoraRepository desenvolvedoraRepository;

    public DesenvolvedoraService(
            DesenvolvedoraRepository desenvolvedoraRepository
    ) {
        this.desenvolvedoraRepository = desenvolvedoraRepository;
    }

    @Transactional
    public Desenvolvedora salvar(Desenvolvedora desenvolvedora) {
        if (desenvolvedoraRepository.existsByCnpj(
                desenvolvedora.getCnpj()
        )) {
            throw new RecursoDuplicadoException(
                    "Já existe uma desenvolvedora cadastrada com este CNPJ."
            );
        }

        return desenvolvedoraRepository.save(desenvolvedora);
    }

    @Transactional(readOnly = true)
    public List<Desenvolvedora> listarTodas() {
        return desenvolvedoraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Desenvolvedora buscarPorId(Long id) {
        return desenvolvedoraRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Desenvolvedora não encontrada com o ID: " + id
                        )
                );
    }

    @Transactional
    public Desenvolvedora alterar(
            Long id,
            String nomeFantasia,
            String cnpj
    ) {
        Desenvolvedora desenvolvedora = desenvolvedoraRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Desenvolvedora não encontrada com o ID: " + id
                        )
                );

        String cnpjLimpo = cnpj.replaceAll("\\D", "");

        if (!desenvolvedora.getCnpj().equals(cnpjLimpo)
                && desenvolvedoraRepository.existsByCnpj(cnpjLimpo)) {
            throw new RecursoDuplicadoException(
                    "Já existe uma desenvolvedora cadastrada com este CNPJ."
            );
        }

        desenvolvedora.alterarNomeFantasia(nomeFantasia);
        desenvolvedora.alterarCnpj(cnpj);

        return desenvolvedora;
    }

    @Transactional
    public void excluir(Long id) {
        Desenvolvedora desenvolvedora = desenvolvedoraRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Desenvolvedora não encontrada com o ID: " + id
                        )
                );

        desenvolvedoraRepository.delete(desenvolvedora);
    }
}