package urjc.isi.dao.implementaciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import urjc.isi.dao.interfaces.PersonasDAO;
import urjc.isi.entidades.Personas;

public class DirectoresDAOImpl extends GenericDAOImpl<Personas> implements PersonasDAO {

	public Personas fromResultSet(ResultSet rs) throws  SQLException{
		Personas persona = new Personas();

		persona.setId(rs.getString("idpersona"));
		persona.setFullNombre(rs.getString("fullnombre"));
		persona.setNacimiento(rs.getString("fnacimiento"));
		persona.setMuerte(rs.getString("fmuerte"));
		return persona;
	}

	@Override
	public void createTable() throws SQLException {
		Statement statement = c.createStatement();
		statement.executeUpdate("create table directores" + table);
		c.commit();
	}

	@Override
   public void dropTable() throws SQLException {
		Statement statement = c.createStatement();
		statement.executeUpdate("drop table if exists directores");
		c.commit();
	}

	@Override
	public void insert(Personas entity) {
	 	String sql = "INSERT INTO directores(idpersona,fullnombre,fnacimiento,fmuerte) VALUES(?,?,?,?)";

	  	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
	  		pstmt.setString(1, entity.getId());
	  		pstmt.setString(2, entity.getFullNombre());
	  		pstmt.setString(3, entity.getNacimiento());
	      	pstmt.setString(4, entity.getMuerte());
	  		pstmt.executeUpdate();
	    } catch (SQLException e) {
	  	    System.out.println(e.getMessage());
	  	}

	}

	@Override
	public void uploadTable(BufferedReader br) throws IOException, SQLException {
	    String s;
	    while ((s = br.readLine()) != null) {
	    	if(s.length()>0) {
		      Personas persona = new Personas(s);
		      insert(persona);
		      c.commit();
	    	}
	    }
	}

	@Override
	public List<Personas> selectAll() {
		List<Personas> personaslist = new ArrayList<>();
		  String sql = "SELECT * from directores";
		  try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			  ResultSet rs = pstmt.executeQuery();
			  c.commit();
			  while(rs.next()){
				  personaslist.add(fromResultSet(rs));
			  }
	    } catch (SQLException e) {
			  System.out.println(e.getMessage());
		  }
		  return personaslist;
	}

	@Override
	public Personas selectByID(String idpersona) {
		  String sql = "SELECT * from directores WHERE idpersona=" + idpersona;
		  Personas persona = new Personas();
		  try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			  ResultSet rs = pstmt.executeQuery();
			  c.commit();
			  persona = fromResultSet(rs);
	      } catch (SQLException e) {
			  System.out.println(e.getMessage());
		  }
		  return persona;
	}

	@Override
	public void deleteByID(String idpersona) {
		  String sql = "DELETE from directores WHERE idpersona=" + idpersona;
		  try (PreparedStatement pstmt = c.prepareStatement(sql)){
			  pstmt.executeUpdate();
			  c.commit();
		  } catch (SQLException e) {
			  System.out.println(e.getMessage());
		  }
	}

	@Override
	public Personas selectByName(String name) {
		 String sql = "SELECT * from directores WHERE fullnombre=" + name;
		  Personas persona = new Personas();
		  try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			  ResultSet rs = pstmt.executeQuery();
			  c.commit();
			  persona = fromResultSet(rs);
	      } catch (SQLException e) {
			  System.out.println(e.getMessage());
		  }
		  return persona;
	}
	
	@Override
	public List<Personas> selectPerByFechaNac(String fecha) {
		 List<Personas> actFechaNac = new ArrayList<>();
		 String sql = "SELECT * from directores WHERE fnacimiento=" + fecha;
		 try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			 ResultSet rs = pstmt.executeQuery();
			 c.commit();
			 while(rs.next()){
				 actFechaNac.add(fromResultSet(rs));
			 }
		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
		 return actFechaNac;
	}	
	
	@Override
	public List<Personas> selectPerMuertas() {
		 List<Personas> dirMuertos = new ArrayList<>();
		 String sql = "SELECT * from directores WHERE fmuerte < 2020";
		 try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			 ResultSet rs = pstmt.executeQuery();
			 c.commit();
			 while(rs.next()){
				 dirMuertos.add(fromResultSet(rs));
			 }
		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
		 return dirMuertos;
	}
	
	@Override
	public List<Personas> selectPerByIntervaloNac(String fechaIn, String fechaFin) {
		 List<Personas> dirFechaInter = new ArrayList<>();
		 String sql = "SELECT * from directores WHERE fnacimiento>" + fechaIn + " AND fnacimiento<" + fechaFin ;
		 try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			 ResultSet rs = pstmt.executeQuery();
			 c.commit();
			 while(rs.next()){
				 dirFechaInter.add(fromResultSet(rs));
			 }
		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
		 return dirFechaInter;
	}
}
