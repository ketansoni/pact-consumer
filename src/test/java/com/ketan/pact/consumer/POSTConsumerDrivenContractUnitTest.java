package com.ketan.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class POSTConsumerDrivenContractUnitTest {

    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2("data_platform", "localhost", 8081, this);


    @Pact(consumer = "DDO")
    public RequestResponsePact postPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        return builder.given("test POST").uponReceiving("POST REQUEST").method("POST")
                .headers(headers).body("{\"name\": \"Michael\"}").path("/pact").willRespondWith().status(201).toPact();
    }


    @Test
    @PactVerification()
    public void givenPost_whenSendRequest_shouldReturn201() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = "{\"name\": \"Michael\"}";

        // when
        ResponseEntity<String> postResponse = new RestTemplate().exchange(mockProvider.getUrl() + "/pact", HttpMethod.POST, new HttpEntity(jsonBody, httpHeaders), String.class);

        // then
        assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
    }

}
