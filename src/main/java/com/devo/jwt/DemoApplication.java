package com.devo.jwt;

import com.devo.jwt.models.AppRole;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.services.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication(/*exclude = SecurityAutoConfiguration.class*/)
public class DemoApplication implements CommandLineRunner{

	@Autowired
	IAccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 * Save admin user (userName : admin, password : admin) with roles ADMIN and USER
	 * Save user (userName : user, password : user) with role : USER
	 *
	 * @param args
	 */
	@Override
	public void run(String... args) {
		String adminUserName = "admin";
		String adminRole = "ADMIN";

		String simpleUserName = "user";
		String userRole = "USER";

		this.accountService.saveRole(AppRole.builder()
				.roleName(adminRole)
				.build());

		this.accountService.saveRole(AppRole.builder()
				.roleName(userRole)
				.build());

		this.accountService.saveUser(AppUser.builder()
						.userName(adminUserName)
						.password("admin")
						.firstName("admin")
						.lastName("lastName")
						.birth(LocalDate.of(2000, 12,20))
				.build());

		this.accountService.saveUser(AppUser.builder()
				.userName(simpleUserName)
				.password("user")
				.firstName("admin")
				.lastName("lastName")
				.birth(LocalDate.of(2000, 12,20))
				.build());

		this.accountService.addRoleToUser(adminUserName, adminRole);
		this.accountService.addRoleToUser(adminUserName, userRole);
		this.accountService.addRoleToUser(simpleUserName, userRole);
	}
}
