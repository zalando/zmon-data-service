package de.zalando.zmon.dataservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by jmussler on 12/16/15.
 */

@RestController
@RequestMapping(value="/api/v1/mobile/")
public class MobileApiProxy {

    @Autowired
    TokenInfoService tokenInfoService;

    @Autowired
    DataServiceConfig config;


    @Autowired
    ObjectMapper mapper;

    public static class AlertHeader {
        public String name;
        public int id;
        public String team;
    }

    @RequestMapping(value="alert", method= RequestMethod.GET)
    public ResponseEntity<List<AlertHeader>> getAllAlerts(@RequestHeader(value = "Authorization", required = false) String oauthHeader) throws URISyntaxException, IOException {
        Optional<String> uid = tokenInfoService.lookupUid(oauthHeader);
        if (!uid.isPresent()) {
            return new ResponseEntity<>((List)null, HttpStatus.UNAUTHORIZED);
        }

        URI uri = new URIBuilder().setPath(config.getProxy_controller_base_url() + "/rest/api/v1/checks/all-active-alert-definitions").build();
        final String r = Request.Get(uri).useExpectContinue().execute().returnContent().asString();

        JsonNode node = mapper.readTree(r);
        List<AlertHeader> alerts = new ArrayList<>();

        Iterator<JsonNode> i = ((ArrayNode)node.get("alert_definitions")).iterator();
        while(i.hasNext()) {
            AlertHeader h = new AlertHeader();
            JsonNode n = i.next();
            h.id = n.get("alert_id").asInt();
            h.name = n.get("name").textValue();
            h.team = n.get("team").textValue();
            alerts.add(h);
        }

        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @RequestMapping(value="alert/{alert_id}", method=RequestMethod.GET)
    public ResponseEntity<JsonNode> getAlertDetails(@PathVariable(value="alert_id") int alertId, @RequestHeader(value = "Authorization", required = false) String oauthHeader) throws URISyntaxException, IOException {
        Optional<String> uid = tokenInfoService.lookupUid(oauthHeader);
        if (!uid.isPresent()) {
            return new ResponseEntity<>((JsonNode)null, HttpStatus.UNAUTHORIZED);
        }

        URI uri = new URIBuilder().setPath(config.getProxy_controller_base_url() + "/rest/alertDetails").addParameter("alert_id", ""+alertId).build();
        final String r = Request.Get(uri).useExpectContinue().execute().returnContent().asString();

        JsonNode node = mapper.readTree(r);
        return new ResponseEntity<>( node, HttpStatus.OK);
    }
}
