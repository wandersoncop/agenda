package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import model.DAO;
import model.JavaBeans;


@WebServlet(urlPatterns = { "/Controller", "/main", "/insert", "/select", "/update", "/delete", "/report" })
public class Controller extends HttpServlet {
	

	private static final long serialVersionUID = 1L;
	DAO dao = new DAO();	
	JavaBeans contato = new JavaBeans();	
	public Controller() {
		super();
	}	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getServletPath();
		
		if (action.equals("/main")) {
			listarContatos(request, response);
		} else if (action.equals("/insert")) {
			novoContato(request, response);
		} else if (action.equals("/select")) {
			listarContato(request, response);
		} else if (action.equals("/update")) {
			editarContato(request, response);
		} else if (action.equals("/delete")) {
			removerContato(request, response);
		} else if (action.equals("/report")) {
			gerarRelatorio(request, response);
		} else {
			response.sendRedirect("index.html");
		}
	}

	
	// Listar contatos
	protected void listarContatos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// criando um objeto que ira receberos daos JavaBeans
		ArrayList<JavaBeans> lista = dao.listarContatos();
		// Encaminhar a lista de documento agenda.jsp
		request.setAttribute("contatos", lista);
		RequestDispatcher rd = request.getRequestDispatcher("agenda.jsp");
		rd.forward(request, response);
	}

	
	// Novo contato
	protected void novoContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// teste de recebimentodos dados do formulario
		System.out.println(request.getParameter("nome"));
		System.out.println(request.getParameter("fone"));
		System.out.println(request.getParameter("email"));
		// Setar as variáveis JavaBeans
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		// invocar o metodo inserirContato passando o objeto cotato
		dao.inserirContato(contato);
		// redirecionar para o documento agenda.jsp
		response.sendRedirect("main");
	}

	
	// Editar contato
	protected void listarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Recebimento do id do contato que sera editado
		String idcon = request.getParameter("idcon");
		// setar a variavel JavaBeans
		contato.setIdcon(idcon);
		// executar o metodo selecionar contato(DAO)
		dao.selecionarContato(contato);

		// Setar os atributos do formulario com o conteudo JavaBeans
		request.setAttribute("idcon", contato.getIdcon());
		request.setAttribute("nome", contato.getNome());
		request.setAttribute("fone", contato.getFone());
		request.setAttribute("email", contato.getEmail());
		// Encaminhar para o documento editar.jsp
		RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
		rd.forward(request, response);
	}


	protected void editarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// setar as variaveis JavaBeans
		contato.setIdcon(request.getParameter("idcon"));
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		// executar o metodo alterarContato
		dao.alterarContato(contato);
		// redirecionar para o documento agenda.jsp(atualizado as alterações)
		response.sendRedirect("main");

	}
	
	
	// Remover um contato
	protected void removerContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Recebimento do id do contato a ser excluido (validador.js)
		String idcon = request.getParameter("idcon");
		//setar a variavel idcom JavaBeans
		contato.setIdcon(idcon);
		//executar o metodo deletarContato(DAO)passando o objeto contato
		dao.deletarContato(contato);
		// redirecionar para o documento agenda.jsp(atualizado as alterações)
				response.sendRedirect("main");
		
		
	}
	
	
	// Gerar relatorio em PDF
		protected void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			Document documento = new Document();
			try {
				//Tipo de conteudo
				response.setContentType("apllication/pdf");
				// nome do ducumento
				response.addHeader("Content-Disposition", "inline; filename="+ "contatos.pdf");
				//criar o documento
				PdfWriter.getInstance(documento, response.getOutputStream());
				//Abrir o documento conteudo
				documento.open();
				documento.add(new Paragraph("Lista de contatos:"));
				documento.add(new Paragraph(" "));
				// Criar uma tabela o numero 3 significa a quantidade de colunas
				PdfPTable tabela = new PdfPTable(3);
				// cabeçalho da tabela no pdf
				PdfPCell col1 = new PdfPCell(new Paragraph("Nome"));
				PdfPCell col2 = new PdfPCell(new Paragraph("Fone"));
				PdfPCell col3 = new PdfPCell(new Paragraph("E-mail"));
				tabela.addCell(col1);
				tabela.addCell(col2);
				tabela.addCell(col3);
				//popular a tabela com os contatos
				ArrayList<JavaBeans>lista = dao.listarContatos();
				for(int i = 0; i < lista.size(); i++) {
					tabela.addCell(lista.get(i).getNome());
					tabela.addCell(lista.get(i).getFone());
					tabela.addCell(lista.get(i).getEmail());
				}
				documento.add(tabela);
				documento.close();
			} catch (Exception e) {
				System.out.println(e);
				documento.close();
			}
		}
}
