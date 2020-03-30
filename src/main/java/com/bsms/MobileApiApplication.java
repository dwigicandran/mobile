package com.bsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bsms.cons.MbApiConstant;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MobileApiApplication {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public static void main(String args[]) {
		SpringApplication.run(MobileApiApplication.class, args);
	}
	
	@Bean
	public ObjectMapper getObjectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		// mapper.enableDefaultTyping();
		return mapper;
	}
		
	@Bean
	public ResourceBundleMessageSource messageSource(){
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("message-hl");
		return messageSource;
	}
	
	@Bean 
	public MbApiConstant getHlAdConstant(){
		return new MbApiConstant();
	}
	
	@Bean
	public DateFormat dateFormat() {
		return new SimpleDateFormat(MbApiConstant.TIME_FORMAT);
	}
	
}
