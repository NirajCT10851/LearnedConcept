
----29/12/2021 morning
first download the zipkin jar and run 
java -jar zipkin.jar

adding 2 depemcy in 2 ms and apigateway
it help to trace id based on that
<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-sleuth-zipkin</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-sleuth</artifactId>
 </dependency>




 INFO [apigateway,2989560eede03e73,2989560eede03e73]
 INFO [orderms,2989560eede03e73,3f09c60966a98c9c]
  INFO [userms,,]
2989560eede03e73---> zipkin



--config server

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

2)done
@EnableConfigServer

3)
server.port=9999
spring.application.name=config-server
management.endpoints.web.exposure.include=*
# Provide Github uri below
spring.cloud.config.server.git.uri=https://github.com/NirajCT10851/propertiesFile.git
spring.cloud.config.server.git.clone-on-start=true
encrypt.key=MyEncryptionKey


--config client


1)
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

2)
@RestController
@RefreshScope

3)
 @Value("${my-property}")
   private String myProperty;
   @Value("${your-property}")
   private String yourProperty;
   @Value("${common.property}")
   private String commonProperty;
   @Value("${paymentPassword}")
   private String paymentPassword;
   @GetMapping("/properties")
   public Map<String, String> getProperties() {
      HashMap<String, String> properties = new HashMap<>();
      properties.put("myProperty", myProperty);
      properties.put("yourProperty", yourProperty);
      properties.put("commonProperty", commonProperty);
      properties.put("paymentPassword", paymentPassword);
      return properties;
   }
   
4)   


++++API calls:++++
 curl --location --request GET 'localhost:9999/config-client/default'
 curl --location --request GET 'localhost:8085/properties'


curl --location --request POST 'localhost:8085/actuator/refresh'


Eureka Server should be runnning.



below two api can be used to encrpt and decrpt
curl --location --request POST 'localhost:9999/decrypt' \
--header 'Content-Type: text/plain' \
--data-raw '25355a6c8a538ef3182270a9589dddb25321a8019886a179bc481f9d9ed7c741b5fbe493829d57a30046b785b9a3cb38'

curl --location --request POST 'localhost:9999/encrypt' \
--header 'Content-Type: text/plain' \
--data-raw 'This is a new password'



------------------------------------------
after lunch 

web client for ms commuication  bettwn them and it is latest.. rest template soon get depricated

 <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
	
	
	@Bean
@LoadBalanced
public WebClient.Builder webClientBuilder() {
   return WebClient.builder();
}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
@RestController
@RequestMapping("/")
public class UserResourceWebClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResourceWebClient.class);
    private final WebClient.Builder webClientBuilder;
//    @Autowired //Not-Mandatory
    public UserResourceWebClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/webclient-orders")
    public  Mono<List> callOrderms() {
        System.out.println("calling orderms");
        LOGGER.info("Calling orderms");
        Mono<List> listMono = webClientBuilder.build()
                .get()
                .uri("http://orderms/orders")
                .retrieve()
                .bodyToMono(List.class);
        return listMono;
    }

    @PostMapping("/webclient-orders")
    public  Mono<OrderTO> saveOrderInOrderms(@RequestParam String orderName) {
        OrderTO orderTO = new OrderTO();
        orderTO.setName(orderName);
        Mono<OrderTO> orderTOMono = webClientBuilder.build()
                .post()
                .uri("http://orderms/orders")
                .body(Mono.just(orderTO), OrderTO.class)
                .retrieve()
                .bodyToMono(OrderTO.class);
        return orderTOMono;
    }

    @DeleteMapping("/webclient-orders/{id}")
    public Mono<Void> deleteOrder(@PathVariable Long id) {
        System.out.println("DELETE API called");
        Mono<Void> voidMono = webClientBuilder.build()
                .delete()
                .uri("http://orderms/orders/" + id)
                .retrieve()
                .bodyToMono(Void.class);
        return voidMono;
    }

}




--spring Security 30/12/2021
Autharization-->>useid +pass
Authentication-->>Access level

Authorization based in api


 @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager udm = new InMemoryUserDetailsManager();
        UserDetails john = User.withUsername("john")
                .password("123")
                .roles("read") // Authorization
                .build();
        UserDetails mary = User.withUsername("mary")
                .password("123")
                .roles("write") // Authorization
                .build();
        UserDetails bek = User.withUsername("bek")
                .password("123")
                .roles("read", "write") // Authorization
                .build();
        udm.createUser(bek);
        
        udm.createUser(john);
        udm.createUser(mary);
        return udm;
    }
	
	
  @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable().authorizeRequests()
               /* .anyRequest()
                .authenticated();*/
                .mvcMatchers(HttpMethod.GET, "/hello").hasRole("read")
                .mvcMatchers(HttpMethod.POST, "/hello").hasRole("write")//single role onle
				.mvcMatchers(HttpMethod.POST, "/hello").hasAnyRole("write","read")//multiple role can be assigned
                .anyRequest()
                .authenticated();
    }
	
	method level security
	
	
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) in config class

@PreAuthorize("hasRole('ROLE_read')") //type 1
	@GetMapping("/secured")
	public String getSecured() {
	    System.out.println("GET-Secured called");
	    return "Hello";
	}

	@PostAuthorize("hasRole('ROLE_write')")  //type 2
	@PostMapping("/secured")
	public String postSecured() {
	    System.out.println("Post-Secured called");
	    return "Hello";
	}

	@Secured({"ROLE_admin", "ROLE_write"}) //type 3
	@DeleteMapping("/secured")
	public String deleteSecured() {
	    System.out.println("DELETE-Secured called");
	    return "Hello";
	}



Oath2 sso

1) Github Developer Settings: https://github.com/settings/apps
2) OAuth Apps
3) New OAuth App
4) Provide details + http://localhost:8080

Client ID
e2a5fff60afa91079043

Client secrets
8c92438fd52791a14b5662da76b49ce4c427438c

(sso project created in training workSpace2)
1) new spring boot : web, spring-security, oauth2-client


	
	
	@Bean

public WebClient.Builder webClientBuilder() {
   return WebClient.builder();
}
	
	public static void main(String[] args) {
		SpringApplication.run(SsoApplication.class, args);
	}
	

	   @Override
	   protected void configure(HttpSecurity http) throws Exception {
//	    http.httpBasic();
	      http.oauth2Login();
	      http.authorizeRequests().anyRequest().authenticated();
	   }

	   @Bean
	   public ClientRegistrationRepository clientRegistrationRepository() {
	      ClientRegistration clientRegistration = clientRegistration();
	      return new InMemoryClientRegistrationRepository(clientRegistration);
	   }

	   private ClientRegistration clientRegistration() {
	      return CommonOAuth2Provider.GITHUB
	            .getBuilder("github")
	            .clientId("e2a5fff60afa91079043")
	            .clientSecret("8c92438fd52791a14b5662da76b49ce4c427438c")
	            .build();
	   }


2) Add Code: clientId + secret
3) home.html file in resources->static directory
3) use new browser session
4) localhost:8080/home


