package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;


import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres()  {
		
		//Creation of the list of genre
		
		ArrayList<Genre> liste_genre= new ArrayList<>();            
		
		// Getting of the data base
		DataSource data=DataSourceFactory.getDataSource();
		
		// Connection with the data base
		try (Connection connection = data.getConnection()) {
		
			// Creation of a statement
			Statement statement =connection.createStatement();
			
			// Creation of a request 
			
			String sqlQuery="SELECT * FROM genre";
			
			// Execution of the request
			try (ResultSet resultSet=statement.executeQuery(sqlQuery)){
			
				while(resultSet.next()) {
					
					// We retrieve the result of the request
					
					Integer id_genre= resultSet.getInt("idgenre");   // Retrieving of the id of the genre 
					String name=resultSet.getString("name");         // Retrieving of the name of the genre 
					
			        Genre genre =new Genre (id_genre,name);          // Creation of the genre object 
			        liste_genre.add(genre);                          // Addition of the genre to the list 
				}
				return liste_genre;
				
			}
		
			catch(SQLException e) {
				System.out.println("Aucun genre  disponible.");
			
			}
		
			return liste_genre;
		} 
		catch (SQLException e1) {
			System.out.println("Connexion impossible.");
			
			
		}
		return liste_genre;
		
		
		
	}
	
	
	
			
	public Genre getGenre(String name) {
		Genre genre=new Genre();
		// Before stating to look for the genre we want, we are going to check if it is available on our date base 
		
		// Retrieving of all the genre available
		List<Genre> genres =this.listGenres();
		
		// Then we put all the name of each genre of the list in a Stream
		Stream<String> streamOfGenreName= genres.stream().map(P->P.getName());
		
		// after that we convert the stream into a list 
		List<String> liste_de_genre = streamOfGenreName.collect(Collectors.toList());
		
		// And then we check if the name we want is available
		if (liste_de_genre.contains(name)) {
		
			// Getting of the data base
			DataSource data=DataSourceFactory.getDataSource();
			
			// Connection with the data base
			try (Connection connection = data.getConnection()) {
		
				// Creation of a request 
				
				String sqlQuery="SELECT * FROM genre WHERE name=?";
				
				// Setting of the parameter of the request
				try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				    statement.setString(1, name);
				
					// Execution of the request
					ResultSet resultSet=statement.executeQuery();
					
					while(resultSet.next()) {
						
						// We retrieve the result of the request
						
						Integer id_genre= resultSet.getInt("idgenre");
					    String nom= resultSet.getString("name");
				        genre = new Genre (id_genre,nom);
				        
				       
					}
					return genre;
							
				}
			
				catch (SQLException e1) {
					System.out.println("Prepation of the request impossible.");	
				}
			
			 return genre;
			}
			catch (SQLException e2) {
				System.out.println("Connexion impossible");	
				
			}
			return genre;
	
	  }
		
	  else {
		// If the genre name we look for is unknown, the genre returned will be null
		genre=null;
		return genre;
	
	  }
	}
	
	

	public void addGenre(String name) {
		
		// Before adding a new genre, we are going to check if it's not already available on our database 
		/* DISCLAIMER: I wanted to use stream for this part but I could have used an sql query to retrieve the genre needed from the database, and according to the
		 * the resultSet and seen if the genre exists or not*/
		
		// Retrieving of all the genre available
		List<Genre> genres =this.listGenres();
		
		// Then we put all the name of each genre of the list in a Stream
		Stream<String> streamOfGenreName= genres.stream().map(P->P.getName());
		
		// after that we convert the stream into a list 
		List<String> liste_de_genre = streamOfGenreName.collect(Collectors.toList());
		
		// And then we check if the name we want to add is already in the list
		if (liste_de_genre.contains(name)) {
			Genre NewGenre=this.getGenre(name);
		}
		else {
		
			// Getting of the data base
			DataSource data=DataSourceFactory.getDataSource();
			
			// Connection with the data base
			try (Connection connection = data.getConnection()) {
		
				// getting of the size of the list, so we will know what will be the id of the next gender
				int taille=genres.size();
				// Creation of a request
				String sqlQuery="INSERT INTO genre(idgenre,name) VALUES(?,?)";
				
				// Setting of the parameter of the request
				try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				    statement.setInt(1, taille+1);            // We create a new ID for the new genre 
				    statement.setString(2, name);             // Setting of the name
				    
					// Execution of the request
					statement.executeUpdate();
						
				}
			
				catch (SQLException e1) {
					System.out.println("Prepation of the request impossible.");	
				}
			
			
			}
			catch (SQLException e2) {
				System.out.println("Connexion impossible");	
				
			}
			
	
	  }
	
		
	}
}
