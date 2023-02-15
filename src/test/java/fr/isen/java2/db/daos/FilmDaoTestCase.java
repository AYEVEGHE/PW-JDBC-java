package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDaoTestCase {
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS film (\r\n"
				+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListFilms() {
		// WHEN
		FilmDao filmDao = new FilmDao();
		List<Film> film = filmDao.listFilms();
		LocalDate date1=LocalDate.of(2015, 11, 26);
		LocalDate date2=LocalDate.of(2015, 11, 14); 
		LocalDate date3=LocalDate.of(2015, 12, 12);
	
		// THEN
		assertThat(film).hasSize(3);
		
		assertThat(film).extracting("id", "title","releaseDate","genre.id","duration","director","summary").containsOnly(tuple(1,"Title 1",date1, 1, 120, "director 1", "summary of the first film"), tuple(2, "My Title 2",date2, 2, 114, "director 2", "summary of the second film"),
				tuple(3,"Third title",date3, 2, 176, "director 3", "summary of the third film"));
	 
	 }
	
	 @Test
	 public void shouldListFilmsByGenre() {
		 FilmDao filmDao = new FilmDao();
		// WHEN
		List<Film> list_film = filmDao.listFilmsByGenre("Comedy");
		LocalDate date1=LocalDate.of(2015, 11, 14);
		LocalDate date2=LocalDate.of(2015, 12, 12); 
			
			
		// THEN
		assertThat(list_film).hasSize(2);
		
		assertThat(list_film).extracting("id", "title","releaseDate","genre.id","duration","director","summary").containsOnly(tuple(2, "My Title 2", date1,2, 114, "director 2", "summary of the second film"),
				tuple(3,"Third title",date2, 2, 176, "director 3", "summary of the third film"));
	}
		
	
	
	 @Test
	 public void shouldAddFilm() throws Exception {
		// WHEN 
		FilmDao filmDao = new FilmDao();
		Genre genre =new Genre (4,"Drame");
		LocalDate date= LocalDate.of(2015, 10, 12);
		Film film=new Film(4,"titre4",date, genre, 176, "director 4", "summary of the fourth film");
		filmDao.addFilm(film);
		
				
		// THEN
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM film WHERE title='titre4'");
		assertThat(resultSet.next()).isTrue();
		assertThat(resultSet.getInt("genre_id")).isNotNull();
		assertThat(resultSet.getString("title")).isEqualTo("titre4");
		assertThat(resultSet.next()).isFalse();
		resultSet.close();
		statement.close();
		connection.close();
	 }
}
