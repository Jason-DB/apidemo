# apidemo
spring boot后端采用spring Security+jwt+Swagger实现按钮权限级别api和自动api说明文档
工具VS Code助手Copilot(GPT 4o)，基本可达到100%代码可用。
建立项目时就注意提醒Copilot版本的兼容性，可以减少大量兼容问题，pom.xml：
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>apidemo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.5.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>javax.persistence-api</artifactId>
            <version>2.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
            <version>3.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
----------------------------------------------------------------------------------------
使用Spring Security初期用户名和密码配置：

Spring Security默认用户密码是通过BCryptPasswordEncoder加密的，所以初始访问时，需要密码明文加密后存储在MySQL中。这时需要新建一个运行java来生成密码才能确保盐值一致，比如PasswordEncoderUtil.java,用来将密码1变为加密后的值。，然后直接运行这个java文件，就在终端窗口输出了：
-----------------------------------------------------
package com.example.apidemo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//初始化密码，将生产的密码放到数据库中
public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}

========================================================================================
按钮级别的权限配置：
1.先在Permissions.java中配置权限的名称：
public class Permissions {
    public static final String RESET_PASSWORD = "ROLE_RESET_PASSWORD";
    public static final String CREATE_USER = "ROLE_CREATE_USER";
    public static final String UPDATE_USER = "ROLE_UPDATE_USER";
    public static final String ADMIN_UPDATE_USER = "ROLE_ADMIN_UPDATE_USER";
    public static final String DELETE_USER = "ROLE_DELETE_USER";
    public static final String ASSIGN_ROLE = "ROLE_ASSIGN_ROLE";
    public static final String ASSIGN_PERMISSION = "ROLE_ASSIGN_PERMISSION";
    public static final String MANAGE_ROLES = "ROLE_MANAGE_ROLES";
    public static final String MANAGE_PERMISSIONS = "ROLE_MANAGE_PERMISSIONS";
    public static final String VIEW_USERS = "ROLE_VIEW_USERS";
    public static final String ADMIN_VIEW_USERS = "ROLE_ADMIN_VIEW_USERS";
}

2.在每个控制器的上方加上权限命名：
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('" + Permissions.VIEW_USERS + "')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
3.在MySQL的权限表中将权限名称加入："ROLE_RESET_PASSWORD","ROLE_CREATE_USER"等

4.调整SecurityConfig.java中的配置项，将不需要权限的控制器如注册、登录排除。
=============================================================================================
 Swagger（OpenAPI）自动生成 API 的接口说明文档
1.使用 Springfox 集成 Swagger：
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
2.增加配置java文件：SwaggerConfig.java
3.在SecurityConfig.java中开放访问权限:
package com.example.apidemo.config;

import com.example.apidemo.filter.JwtRequestFilter;
import com.example.apidemo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/authenticate", "/api/user/register").permitAll()
            .antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-ui/**"
            ).permitAll()
            .anyRequest().authenticated()
            .and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
