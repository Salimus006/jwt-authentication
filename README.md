### JWT demo


### Context de l'application
- Application web (spring boot)
- Utilise une base mysql conteneurisé (image mysql, conteneur : mysql_db)
- Authentification par jwt token
- Un fichier Dockerfile pour la création de l'image web
- Un fichier docker-compose qui permet de décrire les deux services (web et mysql_db)
- L'application est exposé sur le port 6868. 
- Open API accéssible via l'url : <a href="http://localhost:6868/swagger-ui/index.html" title="Open API">swagger</a>
- H2 (base de données embarquée est utilisée pour les tests)

### Dockerfile
- L'image est céée à partir de l'image openjdk:11-jre-slim
- Le point d'entrée de l'image : java -jar

### Docker-compose
- Deux services sont définis (web et mysql)
- le service web est exposé via le port 6868 avec back-jwt comme nom de conteneur
- Le service web dépend du service mysql_db
- Les deux services font partis du même sous-réseau jwt-network


### Build de l'image et démarrage des deux services
- Packager l'application : <code>mvn -DskipTests=true  package</code>
- Démarrage des deux services : <code>docker compose build</code> puis <code>docker compose up</code>
- Arrêt des services et suppression des conteneurs : <code>docker compose down</code>

### Config sécurité (Spring sécurity)
#### Prérecquis: 
- Ajout du starter spring-boot-starter-test
#### Config (voir package config test): 
- La class <code>CryptConfig</code> permet d'encoder/decoder les mdp via <code>BCryptPasswordEncoder</code>
- La class <code>JwtAuthenticationFilter</code> s'occupe de l'authentification et de la génération du JWT
- La class <code>JwtAuthorizationFilter</code> s'occupe de la validation du token 
- La class <code>UserDetailsServiceImpl</code> implément l'interface de Spring <code>UserDetailsService</code> et redéfinit la méthode <code>loadUserByUsername</code> appelée par spring pour charger les utilisateurs de la base
- La class <code>SecurityConfig</code> est une class de config qui hérite de la class de spring <code>WebSecurityConfigurerAdapter</code> et redéfinit les deux méthodes <code>configure</code> pour injecter notre <code>UserDetailsService</code> et <code>PassEncoder</code> d'un côté et injecter nos deux filtre <code>JwtAuthenticationFilter</code>, <code>JwtAuthorizationFilter</code