package com.ensa.hosmoaBank.models;

import java.io.IOException;
import java.util.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthorityDeserializer extends JsonDeserializer{

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) p.getCodec();
		JsonNode jsonNode = mapper.readTree(p);
		List<SimpleGrantedAuthority> granteAuthorothies = new LinkedList<>();
		
		Iterator<JsonNode> elements = jsonNode.elements();
		
		while(elements.hasNext()) {
			JsonNode next = elements.next();
			JsonNode authority = next.get("authority");
			granteAuthorothies.add(new SimpleGrantedAuthority(authority.asText()));
		}
		return granteAuthorothies;
	}

}
