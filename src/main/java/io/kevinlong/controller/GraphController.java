package io.kevinlong.controller;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.neo4j.driver.v1.Values.parameters;

@Named
@SessionScoped
public class GraphController implements AutoCloseable, Serializable {

    private Driver driver;
    private String actor;
    private String query;
    private Integer numResults;

    @Produces
    @Named
    private List< String > coactors;

    @PostConstruct
    public void populateMovie() {

        String user = "neo4j";
        String password = "hello";
        this.actor = "";

        coactors = new ArrayList<>();
        driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(user, password));

        query = "MATCH (tom:Person {name: {actor}})-[:ACTED_IN]->(m)<-[:ACTED_IN]-(coActors) RETURN coActors.name as coactors";

    }

    public void search() {

        coactors.clear();

        try ( Session session = driver.session() ) {
            StatementResult result = session.run(query, singletonMap("actor", this.actor));

            while ( result.hasNext() ) {
                coactors.add(result.next().get("coactors").asString());
            }

            PrimeFaces.current().ajax().update("searchOut");

        }
    }

    @Override
    public void close() {

        driver.close();
    }

    public List< String > getCoactors() {

        return coactors;
    }

    public void setCoactors(List< String > coactors) {

        this.coactors = coactors;
    }

    public String getActor() {

        return actor;
    }

    public void setActor(String actor) {

        this.actor = actor;
    }

    public Integer getNumResults() {

        return numResults;
    }

    public void setNumResults(Integer numResults) {

        this.numResults = numResults;
    }

}
