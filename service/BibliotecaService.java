package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Emprestimo;
import model.Livro;
import model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BibliotecaService {
    private final String ARQUIVO_DADOS = "dados_biblioteca.json";
    private Map<String, Livro> livros = new HashMap<>();
    private Map<String, Usuario> usuarios = new HashMap<>();
    private List<Emprestimo> emprestimos = new ArrayList<>();
    private final ObjectMapper mapper;

    public BibliotecaService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Permite salvar LocalDate do Java 8
        carregarDados();
    }

    // --- Persistência de Dados ---
    private void salvarDados() {
        try {
            Map<String, Object> dados = new HashMap<>();
            dados.put("livros", livros);
            dados.put("usuarios", usuarios);
            dados.put("emprestimos", emprestimos);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO_DADOS), dados);
        } catch (IOException e) {
            System.out.println("❌ Erro ao salvar dados: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarDados() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) return;

        try {
            Map<String, Object> dados = mapper.readValue(arquivo, HashMap.class);
            
            // Reconstrução dos mapas e listas a partir do JSON estruturado
            Map<String, Object> livrosJson = (Map<String, Object>) dados.get("livros");
            if (livrosJson != null) {
                livrosJson.forEach((k, v) -> livros.put(k, mapper.convertValue(v, Livro.class)));
            }

            Map<String, Object> usuariosJson = (Map<String, Object>) dados.get("usuarios");
            if (usuariosJson != null) {
                usuariosJson.forEach((k, v) -> usuarios.put(k, mapper.convertValue(v, Usuario.class)));
            }

            List<Object> empsJson = (List<Object>) dados.get("emprestimos");
            if (empsJson != null) {
                emprestimos = empsJson.stream()
                        .map(e -> mapper.convertValue(e, Emprestimo.class))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            System.out.println("⚠️ Erro ao carregar dados. Iniciando sistema vazio.");
        }
    }

    // --- Lógica de Negócio ---
    public String cadastrarLivro(String titulo, String autor, String isbn, int totalCopias) {
        if (livros.containsKey(isbn)) {
            Livro l = livros.get(isbn);
            l.setTotalCopias(l.getTotalCopias() + totalCopias);
            l.setCopiasDisponiveis(l.getCopiasDisponiveis() + totalCopias);
            salvarDados();
            return "Quantidade atualizada para o livro já existente!";
        }
        livros.put(isbn, new Livro(titulo, autor, isbn, totalCopias));
        salvarDados();
        return "✅ Livro cadastrado com sucesso!";
    }

    public boolean cadastrarUsuario(String id, String nome, String email) {
        if (usuarios.containsKey(id)) return false;
        usuarios.put(id, new Usuario(id, nome, email));
        salvarDados();
        return true;
    }

    public List<Livro> buscarLivros(String termo) {
        String t = termo.toLowerCase();
        return livros.values().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(t) || 
                             l.getAutor().toLowerCase().contains(t) || 
                             l.getIsbn().equals(termo))
                .collect(Collectors.toList());
    }

    public String realizarEmprestimo(String isbn, String idUsuario) {
        if (!livros.containsKey(isbn)) return "❌ Livro não encontrado.";
        if (!usuarios.containsKey(idUsuario)) return "❌ Usuário não encontrado.";

        boolean jaPossui = emprestimos.stream()
                .anyMatch(e -> e.isAtivo() && e.getIdUsuario().equals(idUsuario) && e.getIsbnLivro().equals(isbn));
        if (jaPossui) return "⚠️ Usuário já está com uma cópia ativa desse livro.";

        Livro livro = livros.get(isbn);
        if (livro.emprestar()) {
            Emprestimo emp = new Emprestimo(isbn, idUsuario);
            emprestimos.add(emp);
            salvarDados();
            return "✅ Empréstimo realizado! Prazo: " + emp.getDataPrazo();
        }
        return "❌ Não há cópias disponíveis.";
    }

    public String realizarDevolucao(String isbn, String idUsuario) {
        for (Emprestimo emp : emprestimos) {
            if (emp.isAtivo() && emp.getIsbnLivro().equals(isbn) && emp.getIdUsuario().equals(idUsuario)) {
                emp.encerrarEmprestimo();
                livros.get(isbn).devolver();
                salvarDados();
                return "✅ Livro devolvido com sucesso!";
            }
        }
        return "❌ Nenhum empréstimo ativo encontrado para este usuário e livro.";
    }

    public List<String> obterPendencias() {
        List<String> relatorio = new ArrayList<>();
        for (Emprestimo emp : emprestimos) {
            if (emp.estaAtrasado()) {
                Usuario u = usuarios.get(emp.getIdUsuario());
                Livro l = livros.get(emp.getIsbnLivro());
                relatorio.add(String.format("👤 Membro: %s | 📖 Livro: %s | 📅 Prazo era: %s", 
                        u != null ? u.getNome() : "Desconhecido", 
                        l != null ? l.getTitulo() : "Desconhecido", 
                        emp.getDataPrazo()));
            }
        }
        return relatorio;
    }
}
