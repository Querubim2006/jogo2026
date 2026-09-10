package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.repository.DesenvolvedoraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesenvolvedoraService {

    private final DesenvolvedoraRepository desenvolvedoraRepository;

    public DesenvolvedoraService(DesenvolvedoraRepository desenvolvedoraRepository) {
        this.desenvolvedoraRepository = desenvolvedoraRepository;
    }

    @Transactional
    public Desenvolvedora salvar(Desenvolvedora desenvolvedora) {
        if (desenvolvedoraRepository.existsByCnpj(desenvolvedora.getCnpj())) {
            throw new IllegalArgumentException("Já existe uma desenvolvedora cadastrada com este CNPJ.");
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
                .orElseThrow(() -> new IllegalArgumentException("Desenvolvedora não encontrada com o ID: " + id));
    }
}
