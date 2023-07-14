package com.example.translationcsv.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${application.enable-security}")
    private boolean enableSecurity;

    private static final String[] WHITELIST_ENDPOINT = new String[]{"/v3/api-docs/**", "/swagger-ui/**", "/sample_file/**", "/api/**"};

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("authorization");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHeaderNames((header) -> true);
        firewall.setAllowedHeaderValues((header) -> true);
        firewall.setAllowedParameterNames((parameter) -> true);
        return firewall;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/libs/**", "/custom/**", "/js/**", "/icon/**", "/images/**", "/favicon.ico/**", "/webjars/springfox-swagger-ui/**", "/swagger-ui.html/**", "/swagger-resources/**", "/v1/api-docs", "/v2/api-docs", "/v3/api-docs");
        web.httpFirewall(httpFirewall());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (enableSecurity) {
            LOGGER.info("Enable application security.");
            http.authorizeRequests().antMatchers(WHITELIST_ENDPOINT).permitAll().anyRequest().authenticated();

        } else {
            LOGGER.info("Disable application security.");
            http.authorizeRequests().anyRequest().permitAll();
        }

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
    }
}
