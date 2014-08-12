package com.clemble.casino.server.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.UUID;

import com.clemble.casino.server.connection.GraphPlayerConnections;
import com.clemble.casino.server.connection.GraphConnectionKey;
import com.clemble.casino.server.connection.repository.GraphPlayerConnectionsRepository;
import com.clemble.casino.server.connection.spring.PlayerConnectionSpringConfiguration;
import com.clemble.casino.server.connection.spring.ServerPlayerConnectionsSpringConfiguration;
import com.clemble.casino.server.spring.common.PropertiesSpringConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ServerPlayerConnectionsSpringConfiguration.GraphPlayerConnectionsSpringConfigurations.class, PropertiesSpringConfiguration.class})
public class GraphPlayerConnectionsRepositoryTest {

    @Autowired
    public GraphPlayerConnectionsRepository relationsRepository;

    @Test
    public void testSimpleSave(){
        // Step 1. Generating simple empty relation
        GraphPlayerConnections A = new GraphPlayerConnections();
        A.setPlayer(UUID.randomUUID().toString());
        // Step 2. Saving and fetching entity to test a value
        assertNull(A.getId());
        A = relationsRepository.save(A);
        assertNotNull(A.getId());
        // Step 3. Looking up relations
        GraphPlayerConnections found = relationsRepository.findByPlayer(A.getPlayer());
        Assert.assertEquals(found, A);
    }

    @Test
    public void testSaveWithOwned(){
        // Step 1. Generating simple empty relation
        GraphPlayerConnections relations = new GraphPlayerConnections();
        relations.setPlayer(UUID.randomUUID().toString());
        relations.getOwns().add(new GraphConnectionKey(new ConnectionKey("facebook", "asdsdasdwew")));
        // Step 2. Saving and fetching entity to test a value
        assertNull(relations.getId());
        relations = relationsRepository.save(relations);
        assertNotNull(relations.getId());
        // Step 3. Looking up relations
        GraphPlayerConnections found = relationsRepository.findByPlayer(relations.getPlayer());
        Assert.assertEquals(found, relations);
    }

    @Test
    public void testSave2WithOwned(){
        // Step 1. Generating simple empty relation
        GraphPlayerConnections relations = new GraphPlayerConnections();
        relations.setPlayer(UUID.randomUUID().toString());
        relations.getOwns().add(new GraphConnectionKey(new ConnectionKey("facebook", "asdsdasdwew")));
        // Step 2. Saving and fetching entity to test a value
        assertNull(relations.getId());
        relations = relationsRepository.save(relations);
        assertNotNull(relations.getId());
        // Step 3. Looking up relations
        GraphPlayerConnections relations2 = new GraphPlayerConnections();
        relations2.setPlayer(UUID.randomUUID().toString());
        relations2.getOwns().add(new GraphConnectionKey(new ConnectionKey("facebook", "asdsdasdwew")));
        assertNull(relations2.getId());
        relations2 = relationsRepository.save(relations2);
        assertNotNull(relations2.getId());
        Assert.assertEquals(relations2.getOwns().iterator().next().getId(), relations.getOwns().iterator().next().getId());
    }

    @Test
    @Transactional
    public void testConnectionRealisation() {
        // Step 1. Generating simple empty relation
        GraphPlayerConnections A = new GraphPlayerConnections();
        A.setPlayer("A");
        A.getOwns().add(new GraphConnectionKey(new ConnectionKey("f", "A")));
        A.getConnections().add(new GraphConnectionKey(new ConnectionKey("f", "B")));
        // Step 2. Saving and fetching entity to test a value
        assertNull(A.getId());
        A = relationsRepository.save(A);
        assertNotNull(A.getId());
        // Step 3. Looking up relations
        GraphPlayerConnections B = new GraphPlayerConnections();
        B.setPlayer("B");
        B.getOwns().add(new GraphConnectionKey(new ConnectionKey("f", "B")));
        B.getConnections().add(new GraphConnectionKey(new ConnectionKey("f", "A")));
        assertNull(B.getId());
        B = relationsRepository.save(B);
        assertNotNull(B.getId());
        // Step 4. Checking autodiscovery worked
        Iterator<GraphPlayerConnections> iterator = relationsRepository.findRelations("B").iterator();
        assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), A);
        iterator = relationsRepository.findRelations("A").iterator();
        assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), B);
    }
    
}
