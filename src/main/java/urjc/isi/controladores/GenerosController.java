package urjc.isi.controladores;

import static spark.Spark.get;

import static spark.Spark.post;

import java.sql.SQLException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import spark.Request;
import spark.Response;
import urjc.isi.entidades.Generos;
import urjc.isi.service.GenerosService;

public class GenerosController {
	private static GenerosService gs;
	private static String adminkey = "1234";

	/**
	 * Constructor por defecto
	 */
	public GenerosController() {
		gs = new GenerosService();
	}

	/**
	 * Maneja las peticiones que llegan al endpoint /generos/uploadTable
	 * @param request
	 * @param response
	 * @return El formulario para subir el fichero con las pseudoqueries o una redireccion al endpoint /welcome
	 */
	public static String uploadTable(Request request, Response response) {
		if(!adminkey.equals(request.queryParams("key"))) {
			response.redirect("/welcome"); //Se necesita pasar un parametro (key) para poder subir la tabla
		}
		return "<form action='/generos/upload' method='post' enctype='multipart/form-data'>"
			    + "    <input type='file' name='uploaded_generos_file' accept='.txt'>"
			    + "    <button>Upload file</button>" + "</form>";
	}

	/**
	 * Metodo que se encarga de manejar las peticiones a /generos/upload
	 * @param request
	 * @param response
	 * @return Mensaje de estado sobre la subida de los registros
	 */
	public static String upload(Request request, Response response) {
		return gs.uploadTable(request);
	}

	/**
	 * Maneja las peticiones al endpoint /generos/selectAll
	 * @param request
	 * @param response
	 * @return La lista de generos que hay en la tabla Generos de la base de datos en formato HTML
	 * @throws SQLException
	 */
	public static String selectAllGeneros(Request request, Response response) throws SQLException {
		List<Generos> output = gs.getAllGeneros();
		String result = "";
		String titulo = "<h1> <em>Todos los géneros existentes</em></h1> <br>";
		if(request.queryParams("format")!= null && request.queryParams("format").equals("json")) {
			response.type("application/json");
			JsonObject json = new JsonObject();
			json.addProperty("status", "SUCCESS");
			json.addProperty("serviceMessage", "La peticion se manejo adecuadamente");
			JsonArray array = new JsonArray();
			for(int i = 0; i < output.size(); i++) {
				array.add(output.get(i).toJSONObject());;
			}
			json.add("output", array);
			result = json.toString();
		}else {
			for(int i = 0; i < output.size(); i++) {
			    result = result + output.get(i).toHTMLString() +"</br>";
			}
		}
		return titulo + result;
	}
	/**
	 * Metodo que se encarga de manejar todos los endpoints que cuelgan de /peliculas
	 * Metodo que se encarga de manejar las peticiones a /peliculas/searchByGenero
	 * @param request
	 * @param response
	 * @return Muestra los distintos generos obtenidos en base de datos y envia un formulario con la opcion correcta
	 */

	public static String searchByGenero(Request request, Response response) throws SQLException {
		List<Generos> output = gs.getAllGeneros();
		String base = "<h1> <em>Listado de peliculas por género </em></h1> <br> <strong>Eliga uno o varios género</strong>";
		String result = base + "<form action='/peliculas/selectAll' method='get' enctype='multipart/form-data'>" + "  <select name=\"genero\" size=\"20\"  multiple>\n";
		{
			for(int i = 0; i < output.size(); i++) {
				String[] tokens= output.get(i).toHTMLString().split("\\s");
					result = result + "<option value=\"" + tokens[1] + "\">" + tokens[1] + "</option>\n";
			}
				result = result + "  </select>\n" +
				"  <input type=\"submit\" value=\"Filtrar\">"
				+ "</form>";
		}
		return result;
	}

	/**
	 * Metodo que se encarga de manejar todos los endpoints que cuelgan de /peliculasactores
	 */
	public void peliculasHandler() {
		get("/selectAll", GenerosController::selectAllGeneros);
		get("/uploadTable", GenerosController::uploadTable);
		post("/upload", GenerosController::upload);
		get("/searchByGenero", GenerosController::searchByGenero);
	}

}
