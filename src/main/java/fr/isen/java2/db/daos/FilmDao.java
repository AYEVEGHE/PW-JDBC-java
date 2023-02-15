package fr.isen.java2.db.daos;

import java.sql.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	public List<Film> listFilms() {
		// Creation of the list film
		
		ArrayList<Film> list_film= new ArrayList<>();
	
		// Retrieving of the database and use of the class DataSourceFactory
		DataSource data=DataSourceFactory.getDataSource();
		
		// Connection with the database
		try (Connection connection = data.getConnection()) {
			
			// Creation of the statement
			Statement statement = connection.createStatement();
			
			// Creation of the request 
			String sqlQuery="SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre";
		
			// Execution of the request
			try (ResultSet resultSet=statement.executeQuery(sqlQuery)){
			   
				// Creation of the list
				
				while(resultSet.next()) {
					
					// We retrieve the different column of each line
				
					String genrenom= resultSet.getString("name");                           // Name of the genre 
					
					Integer id_genre= resultSet.getInt("idgenre");                          // id of the genre 
					Integer id_film= resultSet.getInt("idfilm");                            // id of the movie
				
			        String title = resultSet.getString("title");                            // title of the movie
			       
			        // Retrieving of the date 
			        
			        //conversion into the right class for the date
			   
			        Timestamp sqlDateAndTime = resultSet.getTimestamp("release_date");      // Retrieving of the date of the sql date on the format: date and hour
			        LocalDateTime date1 = sqlDateAndTime.toLocalDateTime();                 
			        LocalDate date2= date1.toLocalDate();                                   // Retrieving only the date, without the hour
			       
			        Integer duration=resultSet.getInt("duration");                          // Retrieve the duration of the movie
			        
			        String summary=resultSet.getString("summary");                          // Retrieve the summary of the movie
			   
			        String director= resultSet.getString("director");                       //  director of the movie
			       
	        		Genre genre =new Genre (id_genre,genrenom);                                 // Creation of the genre for the movie
	        		Film film= new Film(id_film,title,date2,genre,duration,director,summary);   // Creation of the movie 
		        	list_film.add(film);                                                        // Addition of the movie into the list 
		        	
			        	
			    }
				return list_film;
			}
				
				
			catch(SQLException e) {
				System.out.println("Aucun genre  disponible.");
			
			}
		}
		catch(SQLException e) {
			System.out.println("connexion avec la BDD impossible.");
		
		}
		
		return list_film;
		
	}
	
	

	public List<Film> listFilmsByGenre(String genreName) {
		
		// Creation of the list of films by genre 
		ArrayList<Film> list_film_by_genre= new ArrayList<>();
		
		GenreDao genre=new GenreDao();
		
		// Before starting to look for the genre we want, we are going to check if it exists in our database
		
		// Retrieving of all the genre available
		List<Genre> genres =genre.listGenres();
		
		// Then we put all the name of each genre of the list in a Stream
		Stream<String> streamOfGenreName= genres.stream().map(P->P.getName());
		
		// after that, we convert the stream into a list 
		List<String> liste_de_genre = streamOfGenreName.collect(Collectors.toList());
		
		/* DISCLAIMER: I wanted to use stream for this part but I could have used an sql query to retrieve the genre needed from the database, and according to the
		 * the resultSet and seen if the genre exists or not*/
		
		// And then we check if the name we want is in the list
		if (liste_de_genre.contains(genreName)) {
		
			// Getting of the data base
			DataSource data=DataSourceFactory.getDataSource();
			
			// Connection with the database
			try (Connection connection = data.getConnection()) {
		
				// Creation of a request 
				
				String sqlQuery="SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?";
				
				// Setting of the parameter of the request
				try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				    statement.setString(1, genreName);
				
					// Execution of the request
					ResultSet resultSet=statement.executeQuery();
					
					while(resultSet.next()) {
						
						// We retrieve the different column of each line
				
						Integer id_genre= resultSet.getInt("genre_id");      // genre id
						Integer id_film= resultSet.getInt("idfilm");         // film id
					
				        String title = resultSet.getString("title");         // title
				       
				        // Retrieving of the date 
				        
				        // conversion into the right class for the date
				       
				        
				        Timestamp sqlDateAndTime = resultSet.getTimestamp("release_date");                    // date and time from the sql 
				        LocalDateTime date1 = sqlDateAndTime.toLocalDateTime();                               // conversion into LocalDateTime
				        LocalDate date2= date1.toLocalDate();                                                 // conversion into LocalDate, so we just keep the date to create a movie
				      
				        Integer duration=resultSet.getInt("duration");                                        // duration of the film
				      
				        String summary=resultSet.getString("summary");                                        // Summary of the movie
				   
				        String director= resultSet.getString("director");                                     // Director of the movie
				       	Genre genre_film =new Genre (id_genre,genreName);                                     // Creation of the genre of the movie
		        		Film film= new Film(id_film,title,date2,genre_film,duration,director,summary);        // Creation of the film object 
			        	list_film_by_genre.add(film);                                                         // Addition of the film to the list 
			
					}
					return list_film_by_genre;
							
				}
			
				catch (SQLException e1) {
					System.out.println("Prepation of the request impossible.");	
				}
			
			 return list_film_by_genre;
			}
			catch (SQLException e2) {
				System.out.println("Connexion impossible");	
				
			}
			return list_film_by_genre;
	
	  }
		
	  else {
		  
		// If the genre name we look for is unknown, the genre returned will be null
		genre=null;
		return list_film_by_genre;
	
	  }
	}
	
	

	public Film addFilm(Film film) {
	
		// Before adding a new genre, we are going to check if it's not already available on our database 
		
		// Retrieving of all the genre available
		List<Film> list_film =this.listFilms();
		
		// Then we put all the id of each genre of the list in a Stream
		Stream<Integer> streamOfFilmId= list_film.stream().map(P->P.getId());
		
		// after that we convert the stream into a list 
		List<Integer> liste_de_film = streamOfFilmId.collect(Collectors.toList());
		
		// And then we check if the name we want to add is already in the list
		if (liste_de_film.contains(film.getId())) {
			System.out.println("Ce film est deja enregistré.");
			return film;
		}
		else {
		
			// Getting of the database
			DataSource data=DataSourceFactory.getDataSource();
			
			// Connection with the database
			try (Connection connection = data.getConnection()) {
		
				// Getting of the size of the list, so we will know what will be the id of the next gender
				
				// Creation of a request
				String sqlQuery="INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) VALUES(?,?,?,?,?,?,?)";
				
				// Setting of the parameter of the request
				try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
		
					statement.setInt(1, film.getId());                 // Setting of Id of the film

				    statement.setString(2, film.getTitle());           // Setting of the title of the movie
				
				    Date date= Date.valueOf(film.getReleaseDate());    // conversion from LocalDate to Date
			
				    statement.setDate(3,date);                         // Setting of the date
				  
				    statement.setInt(4, film.getGenre().getId());      // Setting of the film id
				
				    statement.setInt(5, film.getDuration());           // Setting of the film duration 
				  
				    statement.setString(6, film.getDirector());        // Setting of the film director
				   
				    statement.setString(7,film.getSummary());          // Setting of the film summary 
			
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
			
	    return film;
	  }
	
		
	}
		
	}

