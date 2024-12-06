package com.skillspace.sgs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@MapperScan(basePackages = "com.skillspace.sgs.**")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SkillspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillspaceApplication.class, args);
	}

}
