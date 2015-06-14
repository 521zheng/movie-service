package com.wakaleo.myflix.movies.features.steps;

import com.google.common.collect.ImmutableList;
import com.jayway.restassured.RestAssured;
import com.wakaleo.myflix.movies.MovieServiceApplication;
import com.wakaleo.myflix.movies.features.serenitysteps.MovieCatalog;
import com.wakaleo.myflix.movies.model.Movie;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.wakaleo.myflix.movies.features.steps.MovieComparators.byTitleAndDirector;
import static net.serenitybdd.rest.SerenityRest.rest;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(loader = SpringApplicationContextLoader.class,
        classes = MovieServiceApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SearchSteps {

    @Steps
    MovieCatalog theMovieCatalog;

    @Value("${local.server.port}")
    int port;

    List<Movie> matchingMovies;

    @Before
    public void configurePorts() {
        RestAssured.port = port;
    }

    @Given("the catalog has the following movies:")
    public void catalogMovies(List<Movie> movies) {
        theMovieCatalog.hasTheFollowingMovies(movies);
    }

    @When("I search for movies directed by (.*)")
    public void searchByDirector(String director) {
        Movie[] movies = rest().when().get("/movies/findByDirector/" + director).as(Movie[].class);
        matchingMovies = ImmutableList.copyOf(movies);
    }

    @Then("I should be presented with the following movies:")
    public void shouldSeeMovies(List<Movie> expectedMovies) {
        assertThat(matchingMovies).usingElementComparator(byTitleAndDirector()).containsOnlyElementsOf(expectedMovies);
    }
}
