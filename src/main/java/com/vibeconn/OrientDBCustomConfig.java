package com.vibeconn;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.ext.orientdb.OrientTransactionFactory;
import com.syncleus.ferma.ext.orientdb.impl.OrientTransactionFactoryImpl;
import com.syncleus.ferma.tx.TxFactory;
import io.quarkus.arc.DefaultBean;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraph;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;
import org.apache.tinkerpop.gremlin.structure.Graph;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import static org.apache.tinkerpop.gremlin.orientdb.OrientGraph.*;

@ApplicationScoped
public class OrientDBCustomConfig {

    @Produces
    @DefaultBean
    OrientGraph graph(){
        Configuration configuration = new PropertiesConfiguration();
        configuration.setProperty(CONFIG_URL,"remote:localhost/testgremlin");
        configuration.setProperty(CONFIG_USER,"root");
        configuration.setProperty(CONFIG_PASS,"Vibfam@321");
//        configuration.setProperty(CONFIG_TRANSACTIONAL,true);
        configuration.setProperty(CONFIG_POOL_SIZE,40);
        return OrientGraph.open(configuration);
//        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/testgremlin","root","Vibfam@321").setupPool(1,10);
//        OrientGraph graph = new OrientGraph(factory,configuration);
//        return graph;
    }

    OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
    ODatabasePool pool =  new ODatabasePool(orient, "testgremlin", "root", "Vibfam@321");

    @RequestScoped
    @DefaultBean
    ODatabaseSession dbSession(){
        return pool.acquire();
    }

    @Singleton
    @DefaultBean
    FramedGraph framedGraph(OrientGraph graph){
        return new DelegatingFramedGraph<Graph>(graph);
    }

    @Singleton
    @DefaultBean
    TxFactory txFactory(){
        OrientGraphFactory graphFactory = new OrientGraphFactory("remote:localhost/testgremlin","root","Vibfam@321");
        OrientTransactionFactory graph = new OrientTransactionFactoryImpl(graphFactory,false,"com.vibeconn.models");
        graph.setupElementClasses();
        return graph;
    }

}
