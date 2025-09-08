# Load balancing Spring boot application services networked with Consul

### Introduction 
These days, most of the Java applications are developed in Spring boot __as micro-services that communicate with different protocols/standards.

### Motivation
When incoming request for specific service grows__usually exponentially__ the application requires to scale up, mostly horizontally via adding new instances.

### Problem
How can the application coordinate/distribute the incoming request equally towards different instances of that specific target application?

### Solution
Re-architect your application (if not yet!) to route your input request in a reactive-style to the subsequent services.  You will be need relying on a configured [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) that queries [Consul](https://developer.hashicorp.com/consul) -supported micro-services to find alive instance and redirect incoming requests:

``` Client request -> API gateway ->  Chooses target instance (discovers via Consul) -> Forwards request```


#### Why did not choose Spring Cloud Netflix Eureka?
It is an architectural decision, quite up to the project owner. Need more arguments? Read [this](https://dev.to/isaactony/comparison-of-spring-cloud-with-eureka-vs-consulio-3hgm)

 
## Requirements
You need to have installed:
* Java 21+
* Docker

## How does it work?
All you need is to run:
```
docker-compose up --build
```
To check your instances running successfully head to ```http://localhost:8500/ui/dc1/nodes/645a85a794b6/health-checks``` on your local machine. You should be able to see:
![Consul Nodes Status](readme-images/consul-nodes.png#gh-light-mode-only)
### Test time
Once have the instances up running, make a call to ```localhost:8001/api/v1/test```:
```
curl -X GET http://yourserver.com/api/v1/test
```
We have developed an automated test makes call 10 times to check lb works fine:
```Java
@Autowired
private WebTestClient webTestClient;

@Test
void testLoadBalancingViaConsul() {
        List<String> responses = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            String body = webTestClient.get()
                    .uri("/api/v1/test")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();
            responses.add(body);
        });
    // Assumption: consul-demo-1 and consul-demo-2 represent different instances of consul-demo Spring boot app
    assertThat(responses.stream().filter(s -> s.contains("consul-demo-1")).toList())
            .hasSameSizeAs(responses.stream().filter(s -> s.contains("consul-demo-2")).toList());
}
```
You should be able to see this test passes valid.

## Contributing
Any contributions are welcome. Just drop an issue to start discussing, or submit a pull request.

## What next?
We plan to add new k8s configuration to enable more maintainable multi-instance subsequent services.
## License
[MIT](https://choosealicense.com/licenses/mit/)
