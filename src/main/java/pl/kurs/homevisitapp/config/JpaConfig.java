package pl.kurs.homevisitapp.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@PropertySource("classpath:application-${spring.profiles.active:prod}.properties")
@Configuration
public class JpaConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean createEmf(JpaVendorAdapter adapter, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setJpaVendorAdapter(adapter);
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("pl.kurs.homevisitapp.models");
        return emf;
    }

    @Bean
    public HibernateJpaVendorAdapter createAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.H2);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Bean
    public BasicDataSource createDataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl(env.getProperty("jdbc.url"));
        bds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        bds.setUsername(env.getProperty("jdbc.user"));
        bds.setPassword(env.getProperty("jdbc.pass"));
        bds.setMinIdle(5);
        bds.setMaxIdle(20);
        return bds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(BasicDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}

