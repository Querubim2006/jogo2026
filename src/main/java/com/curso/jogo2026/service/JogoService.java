
package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.domain.Fornecedor;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.DesenvolvedoraRepository;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import com.curso.jogo2026.repository.JogoRepository;
import com.curso.jogo2026.repository.FornecedorRepository;
import com.curso.jogo2026.specification.JogoSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final GeneroJogoRepository generoRepository;
    private final DesenvolvedoraRepository desenvolvedoraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EntityManager entityManager;

    public JogoService(
            JogoRepository jogoRepository,
            GeneroJogoRepository generoRepository,
            DesenvolvedoraRepository desenvolvedoraRepository,
            FornecedorRepository fornecedorRepository,
            EntityManager entityManager
    ) {
        this.jogoRepository = jogoRepository;
        this.generoRepository = generoRepository;
        this.desenvolvedoraRepository = desenvolvedoraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Jogo cadastrar(
            Jogo jogo,
            Long generoId,
            Long desenvolvedoraId
    ) {
        return cadastrar(jogo, generoId, desenvolvedoraId, null);
    }

    @Transactional
    public Jogo cadastrar(Jogo jogo, Long generoId, Long desenvolvedoraId, Long fornecedorId) {
        if (jogoRepository.existsByCodigoBarras(jogo.getCodigoBarras())) {
            throw new RecursoDuplicadoException(
                    "Código de barras já cadastrado"
            );
        }

        GeneroJogo genero = generoRepository.findById(generoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        genero.adicionarJogo(jogo);

        if (desenvolvedoraId != null) {
            Desenvolvedora desenvolvedora =
                    desenvolvedoraRepository.findById(desenvolvedoraId)
                            .orElseThrow(() ->
                                    new RecursoNaoEncontradoException(
                                            "Desenvolvedora não encontrada"
                                    )
                            );

            jogo.associarDesenvolvedora(desenvolvedora);
        }

        if (fornecedorId != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
            jogo.associarFornecedor(fornecedor);
        }

        return jogoRepository.save(jogo);
    }

    @Transactional(readOnly = true)
    public Jogo buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Page<Jogo> pesquisar(
            String titulo,
            Status status,
            Long generoId,
            Long desenvolvedoraId,
            Boolean abaixoEstoqueMinimo,
            int pagina,
            int tamanho,
            String ordenarPor,
            String direcao
    ) {
        if (pagina < 0) {
            throw new IllegalArgumentException(
                    "A página não pode ser negativa"
            );
        }

        if (tamanho < 1 || tamanho > 100) {
            throw new IllegalArgumentException(
                    "O tamanho da página deve estar entre 1 e 100"
            );
        }

        List<String> camposPermitidos = List.of(
                "id",
                "codigoBarras",
                "titulo",
                "saldoEstoque",
                "valorUnitario",
                "dataLancamento",
                "estoqueMinimo",
                "status"
        );

        String campoOrdenacao =
                ordenarPor == null || ordenarPor.isBlank()
                        ? "id"
                        : ordenarPor;

        if (!camposPermitidos.contains(campoOrdenacao)) {
            throw new IllegalArgumentException(
                    "Campo de ordenação inválido: " + campoOrdenacao
            );
        }

        String sentido =
                direcao == null || direcao.isBlank()
                        ? "ASC"
                        : direcao;

        Sort.Direction direcaoOrdenacao;

        try {
            direcaoOrdenacao = Sort.Direction.fromString(sentido);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "A direção deve ser ASC ou DESC"
            );
        }

        Sort ordenacao = Sort.by(
                direcaoOrdenacao,
                campoOrdenacao
        );

        if (!campoOrdenacao.equals("id")) {
            ordenacao = ordenacao.and(
                    Sort.by(direcaoOrdenacao, "id")
            );
        }

        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                ordenacao
        );

        Specification<Jogo> filtros =
                JogoSpecification.tituloContem(titulo)
                        .and(JogoSpecification.statusIgual(status))
                        .and(JogoSpecification.porGenero(generoId))
                        .and(JogoSpecification.porDesenvolvedora(
                                desenvolvedoraId))
                        .and(JogoSpecification.abaixoEstoqueMinimo(
                                abaixoEstoqueMinimo));

        Page<Jogo> resultado = jogoRepository.findAll(
                filtros,
                pageable
        );

        return resultado;
    }


    @Transactional
    public Jogo alterar(
            Long id,
            String titulo,
            BigDecimal valorUnitario,
            LocalDate dataLancamento,
            BigDecimal estoqueMinimo,
            Long generoId,
            Long desenvolvedoraId
    ) {
        return alterar(id, titulo, valorUnitario, dataLancamento, estoqueMinimo, generoId, desenvolvedoraId, null);
    }

    @Transactional
    public Jogo alterar(Long id, String titulo, BigDecimal valorUnitario,
                        LocalDate dataLancamento, BigDecimal estoqueMinimo,
                        Long generoId, Long desenvolvedoraId, Long fornecedorId) {
        Jogo jogoExistente = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        GeneroJogo genero = generoRepository.findById(generoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        Desenvolvedora desenvolvedora = desenvolvedoraId == null
                ? null
                : desenvolvedoraRepository.findById(desenvolvedoraId)
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Desenvolvedora não encontrada"));

        jogoExistente.alterarDados(
                jogoExistente.getCodigoBarras(),
                titulo,
                valorUnitario,
                dataLancamento,
                estoqueMinimo
        );

        GeneroJogo generoAnterior = jogoExistente.getGenero();

        if (!generoAnterior.getId().equals(genero.getId())) {
            jogoExistente.transferirPara(genero);
        }

        jogoExistente.associarDesenvolvedora(desenvolvedora);

        Fornecedor fornecedor = fornecedorId == null ? null
                : fornecedorRepository.findById(fornecedorId).orElseThrow(() ->
                        new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        jogoExistente.associarFornecedor(fornecedor);

        return jogoExistente;
    }

    @Transactional
    public Jogo registrarEntradaEstoque(
            Long id,
            BigDecimal quantidade
    ) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        entityManager.refresh(jogo, LockModeType.PESSIMISTIC_WRITE);
        jogo.receberEstoque(quantidade);

        return jogo;
    }

    @Transactional
    public Jogo registrarSaidaEstoque(
            Long id,
            BigDecimal quantidade
    ) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        entityManager.refresh(jogo, LockModeType.PESSIMISTIC_WRITE);
        jogo.retirarEstoque(quantidade);

        return jogo;
    }

    @Transactional
    public void excluir(Long id) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        jogoRepository.delete(jogo);
    }


    @Transactional
    public Jogo ativar(Long id) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        jogo.ativar();

        return jogo;
    }

    @Transactional
    public Jogo inativar(Long id) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );
        jogo.desativar();
        return jogo;
    }

    @Transactional
    public Jogo alterarStatus(Long id, Status status) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Jogo não encontrado com o ID: " + id));
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
        if (status == Status.ATIVO) {
            jogo.ativar();
        } else {
            jogo.desativar();
        }
        return jogo;
    }
}
