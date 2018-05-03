package com.ketan.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import groovy.ui.SystemOutputInterceptor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GETConsumerDrivenContractUnitTest {

    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2("data_platform", "localhost", 8081, this);

    @Pact(consumer = "Inview")
    public RequestResponsePact getPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        return builder.given("Results: Bob, Fred, Harry").uponReceiving("Fetching results").path("/results").method("GET")
                .willRespondWith().status(200).body("{\"count\":3,\"results\":[\"Bob\",\"Fred\",\"Harry\"]}").toPact();
    }

    @Test
    @PactVerification()
    public void givenGet_whenSendRequest_shouldReturn200WithProperHeaderAndBody(){
        // when
        ResponseEntity<String> response = new RestTemplate().getForEntity(mockProvider.getUrl() + "/results", String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("count", "3","results","[\"Bob\",\"Fred\",\"Harry\"]");
    }

}
