package br.ufscar.dc.dsw.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufscar.dc.dsw.DAO.EditoraDAO;
import br.ufscar.dc.dsw.DAO.LivrosDAO;
import br.ufscar.dc.dsw.domain.Capa;
import br.ufscar.dc.dsw.domain.Editora;
import br.ufscar.dc.dsw.domain.Livro;

@WebServlet(urlPatterns = "/livros/*")
public class LivroController extends HttpServlet {

    private static final long serialVersionUID = 1L; 
    private LivrosDAO dao;

    @Override
    public void init() {
        dao = new LivrosDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {           
        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastro(request, response);
                    break;
                case "/insercao":
                    insere(request, response);
                    break;
                case "/remocao":
                    remove(request, response);
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response);
                    break;
                case "/atualizacao":
                    atualize(request, response);
                    break;
                case "/pegarCapa":
                    pegarCapa(request, response);
                    break;
                default:
                    lista(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            throw new ServletException(e);
        }
    }

    private void pegarCapa(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Capa capa = dao.getCapaLivro(new Livro(id));
        
        // Verifique se o valor está sendo definido corretamente
        System.out.println("Caminho da Capa: " + capa.getCaminho());
        String baseURL = "https://laughing-computing-machine-7x95p9q6g4qhpxx-8081.app.github.dev/";
        request.setAttribute("caminhoCapa", baseURL + "capa" + id + ".jpg");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/livro/capaLivro.jsp");
        dispatcher.forward(request, response);
    }

    private void lista(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Livro> listaLivros = dao.getAll();
        request.setAttribute("listaLivros", listaLivros);
        request.setAttribute("contextPath", request.getContextPath().replace("/", ""));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/livro/lista.jsp");
        dispatcher.forward(request, response);
    }

    private Map<Long, String> getEditoras() {
        Map <Long,String> editoras = new HashMap<>();
        for (Editora editora: new EditoraDAO().getAll()) {
            editoras.put(editora.getId(), editora.getNome());
        }
        return editoras;
    }
    
    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("editoras", getEditoras());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/livro/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Livro livro = dao.get(id);
        request.setAttribute("livro", livro);
        request.setAttribute("editoras", getEditoras());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/livro/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insere(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        Integer ano = Integer.parseInt(request.getParameter("ano"));
        Float preco = Float.parseFloat(request.getParameter("preco"));
        
        Long editoraID = Long.parseLong(request.getParameter("editora"));
        Editora editora = new EditoraDAO().get(editoraID);
        
        Livro livro = new Livro(titulo, autor, ano, preco, editora);
        dao.insert(livro);
        response.sendRedirect("lista");
    }

    private void atualize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(request.getParameter("id"));
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        Integer ano = Integer.parseInt(request.getParameter("ano"));
        Float preco = Float.parseFloat(request.getParameter("preco"));
        
        Long editoraID = Long.parseLong(request.getParameter("editora"));
        Editora editora = new EditoraDAO().get(editoraID);
        
        Livro livro = new Livro(id, titulo, autor, ano, preco, editora);
        dao.update(livro);
        response.sendRedirect("lista");
    }

    private void remove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));

        Livro livro = new Livro(id);
        dao.delete(livro);
        response.sendRedirect("lista");
    }
}