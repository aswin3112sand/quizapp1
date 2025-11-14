package com.example.quizapp.config;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

import java.util.List;

/**
 * Central place to keep the built-in quiz metadata and its starter question set.
 */
public final class DefaultQuizData {

    public static final String DEFAULT_SLUG = "general-knowledge";
    public static final String DEFAULT_TITLE = "General Knowledge";
    public static final String DEFAULT_DESCRIPTION = "Starter quiz from the GUVI tutorial";
    public static final int DEFAULT_DURATION_SECONDS = 600;

    private DefaultQuizData() {}

    public static Quiz buildDefaultQuiz() {
        return new Quiz(DEFAULT_TITLE, DEFAULT_SLUG, DEFAULT_DESCRIPTION, DEFAULT_DURATION_SECONDS);
    }

    public static List<Question> buildQuestions(Quiz quiz) {
        return List.of(
                new Question("Spring main concept?", List.of("DI", "JDBC", "React", "Docker"), 0, quiz),
                new Question("DI stands for?", List.of("Data Injection", "Dependency Injection", "Direct Input", "Delay Init"), 1, quiz),
                new Question("Spring container interface?", List.of("HttpClient", "ApplicationContext", "ObjectMapper", "Servlet"), 1, quiz),
                new Question("Default bean scope?", List.of("request", "session", "singleton", "prototype"), 2, quiz),
                new Question("Which annotation creates objects automatically?", List.of("@Controller", "@Autowired", "@Service", "@Component"), 3, quiz),
                new Question("Which stereotype is for business logic services?", List.of("@Service", "@Controller", "@RestController", "@Repository"), 0, quiz),
                new Question("Which stereotype targets the DAO/DB layer?", List.of("@Service", "@Repository", "@Value", "@Model"), 1, quiz),
                new Question("Annotation for REST API controller?", List.of("@Service", "@RestController", "@Bean", "@Entity"), 1, quiz),
                new Question("Stereotype for MVC controller?", List.of("@Controller", "@Table", "@Scope", "@Column"), 0, quiz),
                new Question("Typical bean lifecycle init method name?", List.of("init()", "start()", "begin()", "activate()"), 0, quiz),
                new Question("Inject value from properties file?", List.of("@Value", "@Config", "@Inject", "@Load"), 0, quiz),
                new Question("Spring Boot core annotation?", List.of("@SpringRun", "@SpringBootApplication", "@Boot", "@Starter"), 1, quiz),
                new Question("Embedded server used by default in Spring Boot?", List.of("WildFly", "Tomcat", "JBoss", "IIS"), 1, quiz),
                new Question("Spring Boot configuration file?", List.of("index.html", "app.js", "application.properties", "pom.json"), 2, quiz),
                new Question("Common build tool for Spring Boot?", List.of("Maven", "Photoshop", "Excel", "Python"), 0, quiz),
                new Question("Alternative config file for Spring Boot?", List.of("app.ini", "application.yml", "data.xml", "config.js"), 1, quiz),
                new Question("Starter dependency for web apps?", List.of("spring-boot-starter-test", "spring-boot-starter-web", "spring-boot-starter-jdbc", "spring-boot-starter-mail"), 1, quiz),
                new Question("Annotation to create REST API returning JSON?", List.of("@Entity", "@Bean", "@RestController", "@Qualifier"), 2, quiz),
                new Question("Annotation to map GET URLs?", List.of("@Mapping", "@GetMapping", "@JSON", "@URL"), 1, quiz),
                new Question("Annotation to mark a JPA entity?", List.of("@Entity", "@Table", "@Model", "@Service"), 0, quiz),
                new Question("Primary key annotation for JPA?", List.of("@Column", "@Id", "@Key", "@PK"), 1, quiz),
                new Question("Annotation for auto DB key generation?", List.of("@Auto", "@GeneratedValue", "@Random", "@Value"), 1, quiz),
                new Question("Base Spring Data repository interface?", List.of("CrudRepository", "MainRepository", "ListRepository", "EntityRepository"), 0, quiz),
                new Question("Class used to read config values programmatically?", List.of("ModelMapper", "Environment", "Logger", "HttpServlet"), 1, quiz),
                new Question("Method that boots Spring Boot app?", List.of("begin()", "start()", "main()", "boot()"), 2, quiz),
                new Question("Annotation to handle cross-origin requests?", List.of("@Cors", "@CrossOrigin", "@Allow", "@Origin"), 1, quiz),
                new Question("Annotation that enables Lombok logging?", List.of("@Slf4j", "@Log", "@Print", "@Console"), 0, quiz),
                new Question("Common testing framework for Spring Boot?", List.of("JUnit", "Excel", "Notepad", "MySQL"), 0, quiz),
                new Question("Default REST payload format?", List.of("XML", "JSON", "CSV", "Docx"), 1, quiz),
                new Question("Most used Spring Boot embedded server?", List.of("Apache", "Nginx", "Tomcat", "GlassFish"), 2, quiz)
        );
    }
}
