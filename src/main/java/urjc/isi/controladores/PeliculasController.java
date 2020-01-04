package urjc.isi.controladores;

import static spark.Spark.*;

import java.sql.SQLException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import spark.Request;
import spark.Response;

import urjc.isi.entidades.Peliculas;
import urjc.isi.service.PeliculasService;

public class PeliculasController {

	private static PeliculasService ps;
	private static String adminkey = "1234";
	
	/**
	 * Constructor por defecto
	 */
	public PeliculasController() {
		ps = new PeliculasService();
	}
	
	/**
	 * Maneja las peticiones que llegan al endpoint /peliculas/uploadTable
	 * @param request
	 * @param response
	 * @return El formulario para subir el fichero con las pseudoqueries o una redireccion al endpoint /welcome
	 */
	public static String uploadTable(Request request, Response response) {
		if(!adminkey.equals(request.queryParams("key"))) {
			response.redirect("/welcome"); //Se necesita pasar un parametro (key) para poder subir la tabla
		}
		return "<form action='/peliculas/upload' method='post' enctype='multipart/form-data'>" 
			    + "    <input type='file' name='uploaded_films_file' accept='.txt'>"
			    + "    <button>Upload file</button>" + "</form>";
	}
	
	/**
	 * Metodo que se encarga de manejar las peticiones a /peliculas/upload
	 * @param request
	 * @param response
	 * @return Mensaje de estado sobre la subida de los registros
	 */
	public static String upload(Request request, Response response) {
		return ps.uploadTable(request);
	}
	
	/**
	 * Metodo encargado de manejar las peticiones a /peliculas/selectAll
	 * @param request
	 * @param response
	 * @return Listado de peliculas que estan en la tabla Peliculas de la base de datos en formato HTML o JSON
	 * @throws SQLException
	 */
	public static String selectAllPeliculas(Request request, Response response) throws SQLException {
		List<Peliculas> output;
		String result = "";
		String query = "";
		
		if(request.queryParams("actor")!= null) 
			output = ps.getAllPeliculasByActor(request.queryParams("actor"));
		else if(request.queryParams("time")!= null) {
			query = request.queryParams("time");
			String[] parts = query.split("-");
			if(parts.length == 2) {
				String time1 = parts[0];
				String time2 = parts[1];
				double t1 = Double.parseDouble(time1);
				double t2 = Double.parseDouble(time2);
				//result = t1 + "<br/>" + t2 + "<br/>" + result;
				output = ps.getAllPeliculasByDuration(t1,t2, "rango");
			}else {
				char FirstCaracteres = parts[0].charAt(0);
				String mayor = ">";
				String menor = "<";
				char signomayor = mayor.charAt(0);
				char signomenor = menor.charAt(0);
				if (FirstCaracteres == signomayor) {
					//result = "entramos en mayor:\n " + result; 
					String[] partsmayor = query.split(">");
					String time1 = partsmayor[1];
					double t1 = Double.parseDouble(time1);
					output = ps.getAllPeliculasByDuration(t1,0, "mayor");
				}else if(FirstCaracteres == signomenor) {
					//result = "entramos en menor:\n " + result; 
					String[] partsmenor = query.split("<");
					String time1 = partsmenor[1];
					double t1 = Double.parseDouble(time1);
					output = ps.getAllPeliculasByDuration(t1,0, "menor");
				}else {
					//result = "query erronea, pruebe a introducir ?time=num1-num2 ; donde num1 y num2 son números <br/>" + result;
					double t1 = Double.parseDouble(query);
					output = ps.getAllPeliculasByDuration(t1,0, "igual");
				}
			}
		}else 
			output = ps.getAllPeliculas();
		
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
		return result;
	}
	
	
	/**
	 * Metodo que se encarga de manejar todos los endpoints que cuelgan de /peliculasactores
	 */
	public void peliculasHandler() {
		//get("/crearTabla", AdminController::crearTablaPeliculas);
		get("/selectAll", PeliculasController::selectAllPeliculas);
		get("/uploadTable", PeliculasController::uploadTable);
		post("/upload", PeliculasController::upload);
	}
	
}