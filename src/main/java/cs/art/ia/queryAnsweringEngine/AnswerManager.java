package cs.art.ia.queryAnsweringEngine;

import cs.art.ia.model.QuerySPARQL;
import cs.art.ia.model.QuerySPARQLResult;
import it.uniroma2.art.owlart.model.ARTNode;
import it.uniroma2.art.owlart.model.ARTNodeFactory;
import it.uniroma2.art.owlart.model.impl.ARTLiteralEmptyImpl;
import it.uniroma2.art.owlart.model.impl.ARTNodeFactoryImpl;
import it.uniroma2.art.owlart.model.impl.ARTURIResourceEmptyImpl;
import it.uniroma2.art.owlart.models.OWLArtModelFactory;
import it.uniroma2.art.owlart.sesame2impl.factory.ARTModelFactorySesame2Impl;
import it.uniroma2.art.owlart.sesame2impl.models.conf.Sesame2ModelConfiguration;
import it.uniroma2.art.owlart.vocabulary.XmlSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnswerManager {

	private QueryManager mQueryBuilder;

	private OWLArtModelFactory<Sesame2ModelConfiguration> factory;


	/**
	 * Inizializza il sistema che gestisce le rispose alle query
	 */
	public AnswerManager() {

		mQueryBuilder = new QueryManager();
		factory = OWLArtModelFactory.createModelFactory(new ARTModelFactorySesame2Impl());
	}

	/**
	 * Flow di esecuzione della query fornita in input che è stata generata dalla valutazione delle stringa in input
	 * @param queries
	 * @return
	 * @throws Exception
	 */
	public List<QuerySPARQLResult> executeQuery(List<QuerySPARQL> queries) throws Exception {

		List<QuerySPARQLResult> querySPARQLResults = new ArrayList<QuerySPARQLResult>();

		if (!queries.isEmpty()) {

			String var = mQueryBuilder.determinateContest(queries);

			System.out.println("Var: " + var);

			List<String> sparqlQuery = mQueryBuilder.buildQuery(queries);

			for (String singleQuery : sparqlQuery) {

				System.out.println("\n"+singleQuery.toString());

				Map<String, List<ARTNode>> results = mQueryBuilder.executeQuery(singleQuery, factory, mQueryBuilder.yesOrNoQuery(queries));

				if (var.equals("")) {
					if ((results != null) && (results.size() > 0)) {
						ARTNode nodeASK = new ARTURIResourceEmptyImpl("true");
						QuerySPARQLResult querySPARQLResult = new QuerySPARQLResult(nodeASK);
						querySPARQLResults.add(querySPARQLResult);
						return querySPARQLResults;
					}
				}

				if (results.get(var) != null) {
					System.out.println("Result: " + results.get(var).toString());
					for (ARTNode node : results.get(var)) {
						System.out.println("ArtNode: " + node.getNominalValue());
						if (!var.equals("")) {
							QuerySPARQLResult querySPARQLResult = new QuerySPARQLResult(node);
							querySPARQLResults.add(querySPARQLResult);
							System.out.println("Risultato QuerySPARQL Nodo: " + node);
						}

					}
				}

			}
			//Questo controllo perchè nel caso di domande di tipo ask di cui non si ottiene il risultato questo controllo
			//non fa altro che assegnare la risposta false in caso di assenza di risultato
			if (var.equals("")&& querySPARQLResults.isEmpty()){
//				ARTNode nodeASK = new ARTURIResourceEmptyImpl("false");
				ARTNodeFactory nodeFactory=new ARTNodeFactoryImpl();
				ARTNode nodeASK=nodeFactory.createLiteral("false",XmlSchema.BOOLEAN);
				QuerySPARQLResult querySPARQLResult = new QuerySPARQLResult(nodeASK);
				querySPARQLResults.add(querySPARQLResult);
			}
		} else {
			System.out.println("Lista di query inviata nulla");
			throw new NullPointerException();
		}
		return querySPARQLResults;
	}

	/**
	 * Flow di esecuzione sfruttando il synonimer, della query fornita in input che è stata generata dalla valutazione delle stringa in input
	 * @param queries
	 * @param predicateSynonymer
	 * @return
	 * @throws Exception
	 */
	public List<QuerySPARQLResult> executeSynonymerQuery(List<QuerySPARQL> queries,List<String> predicateSynonymer) throws Exception {

        List<QuerySPARQLResult> queryResults = new ArrayList<QuerySPARQLResult>();

        if (!queries.isEmpty()) {


            String var = mQueryBuilder.determinateContest(queries);


            for (String pre : predicateSynonymer) {

                QuerySPARQL query = queries.get(0);

                List<String> sparqlQuery = mQueryBuilder.buildQuerySynonymer(query.getTripleRDF().getSubject(), pre, query.getTripleRDF().getObject());

                for (String singleQuery : sparqlQuery) {

					System.out.println("\n"+singleQuery.toString());

                    Map<String, List<ARTNode>> results = mQueryBuilder.executeQuery(singleQuery,factory, mQueryBuilder.yesOrNoQuery(queries));

                    if (var.equals("")) {
                        if ((results != null) && (results.size() > 0)) {
                            ARTNode nodeASK = new ARTURIResourceEmptyImpl("true");
                            QuerySPARQLResult queryResult = new QuerySPARQLResult(nodeASK);
                            queryResults.add(queryResult);
                            return queryResults;
                        }
                    }
                    // Estrai i valori della variabile principale dalla mappa dei
                    // risultati
                    if (results.get(var) != null) {
                        System.out.println("Result: " + results.get(var).toString());
                        for (ARTNode node : results.get(var)) {
                            System.out.println("ArtNode: " + node.getNominalValue());
                            if (!var.equals("")) {
                                QuerySPARQLResult queryResult = new QuerySPARQLResult(node);
                                queryResults.add(queryResult);
                                System.out.println("Risultato Query Nodo: " + node);
                                return queryResults;
                            }

                        }
                    }
                }

            }
        }else {
            System.out.println("Lista di query inviata nulla");
            throw new NullPointerException();
        }
		return queryResults;
	}


	public QueryManager getmQueryBuilder() {
		return mQueryBuilder;
	}
}